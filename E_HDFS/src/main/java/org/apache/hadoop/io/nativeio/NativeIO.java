package org.apache.hadoop.io.nativeio;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.CommonConfigurationKeys;
import org.apache.hadoop.fs.HardLink;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.SecureIOUtils.AlreadyExistsException;
import org.apache.hadoop.util.NativeCodeLoader;
import org.apache.hadoop.util.Shell;
import org.apache.hadoop.util.PerformanceAdvisory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sun.misc.Unsafe;

import com.google.common.annotations.VisibleForTesting;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class NativeIO {
  public static class POSIX {
    // Flags for open() call from bits/fcntl.h
    public static final int O_RDONLY   =    00;
    public static final int O_WRONLY   =    01;
    public static final int O_RDWR     =    02;
    public static final int O_CREAT    =  0100;
    public static final int O_EXCL     =  0200;
    public static final int O_NOCTTY   =  0400;
    public static final int O_TRUNC    = 01000;
    public static final int O_APPEND   = 02000;
    public static final int O_NONBLOCK = 04000;
    public static final int O_SYNC   =  010000;
    public static final int O_ASYNC  =  020000;
    public static final int O_FSYNC = O_SYNC;
    public static final int O_NDELAY = O_NONBLOCK;

    public static final int POSIX_FADV_NORMAL = 0;
    public static final int POSIX_FADV_RANDOM = 1;
    public static final int POSIX_FADV_SEQUENTIAL = 2;
    public static final int POSIX_FADV_WILLNEED = 3;
    public static final int POSIX_FADV_DONTNEED = 4;
    public static final int POSIX_FADV_NOREUSE = 5;


    public static final int SYNC_FILE_RANGE_WAIT_BEFORE = 1;
    public static final int SYNC_FILE_RANGE_WRITE = 2;

    public static final int SYNC_FILE_RANGE_WAIT_AFTER = 4;

    private static final Log LOG = LogFactory.getLog(NativeIO.class);

    private static boolean nativeLoaded = false;
    private static boolean fadvisePossible = true;
    private static boolean syncFileRangePossible = true;

    static final String WORKAROUND_NON_THREADSAFE_CALLS_KEY =
      "hadoop.workaround.non.threadsafe.getpwuid";
    static final boolean WORKAROUND_NON_THREADSAFE_CALLS_DEFAULT = true;

    private static long cacheTimeout = -1;

    private static CacheManipulator cacheManipulator = new CacheManipulator();

    public static CacheManipulator getCacheManipulator() {
      return cacheManipulator;
    }

    public static void setCacheManipulator(CacheManipulator cacheManipulator) {
      POSIX.cacheManipulator = cacheManipulator;
    }

    @VisibleForTesting
    public static class CacheManipulator {
      public void mlock(String identifier, ByteBuffer buffer,
          long len) throws IOException {
        POSIX.mlock(buffer, len);
      }

      public long getMemlockLimit() {
        return NativeIO.getMemlockLimit();
      }

      public long getOperatingSystemPageSize() {
        return NativeIO.getOperatingSystemPageSize();
      }

      public void posixFadviseIfPossible(String identifier,
        FileDescriptor fd, long offset, long len, int flags)
            throws NativeIOException {
        POSIX.posixFadviseIfPossible(identifier, fd, offset,
            len, flags);
      }

      public boolean verifyCanMlock() {
        return NativeIO.isAvailable();
      }
    }

    @VisibleForTesting
    public static class NoMlockCacheManipulator extends CacheManipulator {
      public void mlock(String identifier, ByteBuffer buffer,
          long len) throws IOException {
        LOG.info("mlocking " + identifier);
      }

      public long getMemlockLimit() {
        return 1125899906842624L;
      }

      public long getOperatingSystemPageSize() {
        return 4096;
      }

      public boolean verifyCanMlock() {
        return true;
      }
    }

    static {
      if (NativeCodeLoader.isNativeCodeLoaded()) {
        try {
          Configuration conf = new Configuration();
          workaroundNonThreadSafePasswdCalls = conf.getBoolean(
            WORKAROUND_NON_THREADSAFE_CALLS_KEY,
            WORKAROUND_NON_THREADSAFE_CALLS_DEFAULT);

          initNative();
          nativeLoaded = true;

          cacheTimeout = conf.getLong(
            CommonConfigurationKeys.HADOOP_SECURITY_UID_NAME_CACHE_TIMEOUT_KEY,
            CommonConfigurationKeys.HADOOP_SECURITY_UID_NAME_CACHE_TIMEOUT_DEFAULT) *
            1000;
          LOG.debug("Initialized cache for IDs to User/Group mapping with a " +
            " cache timeout of " + cacheTimeout/1000 + " seconds.");

        } catch (Throwable t) {
          PerformanceAdvisory.LOG.debug("Unable to initialize NativeIO libraries", t);
        }
      }
    }

    public static boolean isAvailable() {
      return NativeCodeLoader.isNativeCodeLoaded() && nativeLoaded;
    }

    private static void assertCodeLoaded() throws IOException {
      if (!isAvailable()) {
        throw new IOException("NativeIO was not loaded");
      }
    }

    public static native FileDescriptor open(String path, int flags, int mode) throws IOException;
    private static native Stat fstat(FileDescriptor fd) throws IOException;

    private static native void chmodImpl(String path, int mode) throws IOException;

    public static void chmod(String path, int mode) throws IOException {
      if (!Shell.WINDOWS) {
        chmodImpl(path, mode);
      } else {
        try {
          chmodImpl(path, mode);
        } catch (NativeIOException nioe) {
          if (nioe.getErrorCode() == 3) {
            throw new NativeIOException("No such file or directory",
                Errno.ENOENT);
          } else {
            LOG.warn(String.format("NativeIO.chmod error (%d): %s",
                nioe.getErrorCode(), nioe.getMessage()));
            throw new NativeIOException("Unknown error", Errno.UNKNOWN);
          }
        }
      }
    }

    static native void posix_fadvise(
      FileDescriptor fd, long offset, long len, int flags) throws NativeIOException;

    static native void sync_file_range(
      FileDescriptor fd, long offset, long nbytes, int flags) throws NativeIOException;

    static void posixFadviseIfPossible(String identifier,
        FileDescriptor fd, long offset, long len, int flags)
        throws NativeIOException {
      if (nativeLoaded && fadvisePossible) {
        try {
          posix_fadvise(fd, offset, len, flags);
        } catch (UnsupportedOperationException uoe) {
          fadvisePossible = false;
        } catch (UnsatisfiedLinkError ule) {
          fadvisePossible = false;
        }
      }
    }

    public static void syncFileRangeIfPossible(
        FileDescriptor fd, long offset, long nbytes, int flags)
        throws NativeIOException {
      if (nativeLoaded && syncFileRangePossible) {
        try {
          sync_file_range(fd, offset, nbytes, flags);
        } catch (UnsupportedOperationException uoe) {
          syncFileRangePossible = false;
        } catch (UnsatisfiedLinkError ule) {
          syncFileRangePossible = false;
        }
      }
    }

    static native void mlock_native(
        ByteBuffer buffer, long len) throws NativeIOException;

    static void mlock(ByteBuffer buffer, long len)
        throws IOException {
      assertCodeLoaded();
      if (!buffer.isDirect()) {
        throw new IOException("Cannot mlock a non-direct ByteBuffer");
      }
      mlock_native(buffer, len);
    }
    
    public static void munmap(MappedByteBuffer buffer) {
      if (buffer instanceof sun.nio.ch.DirectBuffer) {
        sun.misc.Cleaner cleaner =
            ((sun.nio.ch.DirectBuffer)buffer).cleaner();
        cleaner.clean();
      }
    }

    private static native long getUIDforFDOwnerforOwner(FileDescriptor fd) throws IOException;
    private static native String getUserName(long uid) throws IOException;

    public static class Stat {
      private int ownerId, groupId;
      private String owner, group;
      private int mode;

      public static final int S_IFMT = 0170000;      /* type of file */
      public static final int   S_IFIFO  = 0010000;  /* named pipe (fifo) */
      public static final int   S_IFCHR  = 0020000;  /* character special */
      public static final int   S_IFDIR  = 0040000;  /* directory */
      public static final int   S_IFBLK  = 0060000;  /* block special */
      public static final int   S_IFREG  = 0100000;  /* regular */
      public static final int   S_IFLNK  = 0120000;  /* symbolic link */
      public static final int   S_IFSOCK = 0140000;  /* socket */
      public static final int   S_IFWHT  = 0160000;  /* whiteout */
      public static final int S_ISUID = 0004000;  /* set user id on execution */
      public static final int S_ISGID = 0002000;  /* set group id on execution */
      public static final int S_ISVTX = 0001000;  /* save swapped text even after use */
      public static final int S_IRUSR = 0000400;  /* read permission, owner */
      public static final int S_IWUSR = 0000200;  /* write permission, owner */
      public static final int S_IXUSR = 0000100;  /* execute/search permission, owner */

      Stat(int ownerId, int groupId, int mode) {
        this.ownerId = ownerId;
        this.groupId = groupId;
        this.mode = mode;
      }
      
      Stat(String owner, String group, int mode) {
        if (!Shell.WINDOWS) {
          this.owner = owner;
        } else {
          this.owner = stripDomain(owner);
        }
        if (!Shell.WINDOWS) {
          this.group = group;
        } else {
          this.group = stripDomain(group);
        }
        this.mode = mode;
      }
      
      @Override
      public String toString() {
        return "Stat(owner='" + owner + "', group='" + group + "'" +
          ", mode=" + mode + ")";
      }

      public String getOwner() {
        return owner;
      }
      public String getGroup() {
        return group;
      }
      public int getMode() {
        return mode;
      }
    }

    public static Stat getFstat(FileDescriptor fd) throws IOException {
      Stat stat = null;
      if (!Shell.WINDOWS) {
        stat = fstat(fd); 
        stat.owner = getName(IdCache.USER, stat.ownerId);
        stat.group = getName(IdCache.GROUP, stat.groupId);
      } else {
        try {
          stat = fstat(fd);
        } catch (NativeIOException nioe) {
          if (nioe.getErrorCode() == 6) {
            throw new NativeIOException("The handle is invalid.",
                Errno.EBADF);
          } else {
            LOG.warn(String.format("NativeIO.getFstat error (%d): %s",
                nioe.getErrorCode(), nioe.getMessage()));
            throw new NativeIOException("Unknown error", Errno.UNKNOWN);
          }
        }
      }
      return stat;
    }

    private static String getName(IdCache domain, int id) throws IOException {
      Map<Integer, CachedName> idNameCache = (domain == IdCache.USER)
        ? USER_ID_NAME_CACHE : GROUP_ID_NAME_CACHE;
      String name;
      CachedName cachedName = idNameCache.get(id);
      long now = System.currentTimeMillis();
      if (cachedName != null && (cachedName.timestamp + cacheTimeout) > now) {
        name = cachedName.name;
      } else {
        name = (domain == IdCache.USER) ? getUserName(id) : getGroupName(id);
        if (LOG.isDebugEnabled()) {
          String type = (domain == IdCache.USER) ? "UserName" : "GroupName";
          LOG.debug("Got " + type + " " + name + " for ID " + id +
            " from the native implementation");
        }
        cachedName = new CachedName(name, now);
        idNameCache.put(id, cachedName);
      }
      return name;
    }

    static native String getUserName(int uid) throws IOException;
    static native String getGroupName(int uid) throws IOException;

    private static class CachedName {
      final long timestamp;
      final String name;

      public CachedName(String name, long timestamp) {
        this.name = name;
        this.timestamp = timestamp;
      }
    }

    private static final Map<Integer, CachedName> USER_ID_NAME_CACHE =
      new ConcurrentHashMap<Integer, CachedName>();

    private static final Map<Integer, CachedName> GROUP_ID_NAME_CACHE =
      new ConcurrentHashMap<Integer, CachedName>();

    private enum IdCache { USER, GROUP }

    public final static int MMAP_PROT_READ = 0x1; 
    public final static int MMAP_PROT_WRITE = 0x2; 
    public final static int MMAP_PROT_EXEC = 0x4; 

    public static native long mmap(FileDescriptor fd, int prot,
        boolean shared, long length) throws IOException;

    public static native void munmap(long addr, long length)
        throws IOException;
  }

  private static boolean workaroundNonThreadSafePasswdCalls = false;


  public static class Windows {
    public static final long GENERIC_READ = 0x80000000L;
    public static final long GENERIC_WRITE = 0x40000000L;

    public static final long FILE_SHARE_READ = 0x00000001L;
    public static final long FILE_SHARE_WRITE = 0x00000002L;
    public static final long FILE_SHARE_DELETE = 0x00000004L;

    public static final long CREATE_NEW = 1;
    public static final long CREATE_ALWAYS = 2;
    public static final long OPEN_EXISTING = 3;
    public static final long OPEN_ALWAYS = 4;
    public static final long TRUNCATE_EXISTING = 5;

    public static final long FILE_BEGIN = 0;
    public static final long FILE_CURRENT = 1;
    public static final long FILE_END = 2;
    
    public static final long FILE_ATTRIBUTE_NORMAL = 0x00000080L;

    public static void createDirectoryWithMode(File path, int mode)
        throws IOException {
      createDirectoryWithMode0(path.getAbsolutePath(), mode);
    }

    private static native void createDirectoryWithMode0(String path, int mode)
        throws NativeIOException;

    public static native FileDescriptor createFile(String path,
        long desiredAccess, long shareMode, long creationDisposition)
        throws IOException;

    public static FileOutputStream createFileOutputStreamWithMode(File path,
        boolean append, int mode) throws IOException {
      long desiredAccess = GENERIC_WRITE;
      long shareMode = FILE_SHARE_READ | FILE_SHARE_WRITE;
      long creationDisposition = append ? OPEN_ALWAYS : CREATE_ALWAYS;
      return new FileOutputStream(createFileWithMode0(path.getAbsolutePath(),
          desiredAccess, shareMode, creationDisposition, mode));
    }

    private static native FileDescriptor createFileWithMode0(String path,
        long desiredAccess, long shareMode, long creationDisposition, int mode)
        throws NativeIOException;

    public static native long setFilePointer(FileDescriptor fd,
        long distanceToMove, long moveMethod) throws IOException;

    private static native String getOwner(FileDescriptor fd) throws IOException;

    public static enum AccessRight {
      ACCESS_READ (0x0001),      // FILE_READ_DATA
      ACCESS_WRITE (0x0002),     // FILE_WRITE_DATA
      ACCESS_EXECUTE (0x0020);   // FILE_EXECUTE

      private final int accessRight;
      AccessRight(int access) {
        accessRight = access;
      }

      public int accessRight() {
        return accessRight;
      }
    };

    private static native boolean access0(String path, int requestedAccess);

    public static boolean access(String path, AccessRight desiredAccess)
        throws IOException {
      return true;
    }

    public static native void extendWorkingSetSize(long delta) throws IOException;

    static {
      if (NativeCodeLoader.isNativeCodeLoaded()) {
        try {
          initNative();
          nativeLoaded = true;
        } catch (Throwable t) {
          PerformanceAdvisory.LOG.debug("Unable to initialize NativeIO libraries", t);
        }
      }
    }
  }

  private static final Log LOG = LogFactory.getLog(NativeIO.class);

  private static boolean nativeLoaded = false;

  static {
    if (NativeCodeLoader.isNativeCodeLoaded()) {
      try {
        initNative();
        nativeLoaded = true;
      } catch (Throwable t) {
        PerformanceAdvisory.LOG.debug("Unable to initialize NativeIO libraries", t);
      }
    }
  }

  public static boolean isAvailable() {
    return NativeCodeLoader.isNativeCodeLoaded() && nativeLoaded;
  }

  private static native void initNative();

  static long getMemlockLimit() {
    return isAvailable() ? getMemlockLimit0() : 0;
  }

  private static native long getMemlockLimit0();
  
  static long getOperatingSystemPageSize() {
    try {
      Field f = Unsafe.class.getDeclaredField("theUnsafe");
      f.setAccessible(true);
      Unsafe unsafe = (Unsafe)f.get(null);
      return unsafe.pageSize();
    } catch (Throwable e) {
      LOG.warn("Unable to get operating system page size.  Guessing 4096.", e);
      return 4096;
    }
  }

  private static class CachedUid {
    final long timestamp;
    final String username;
    public CachedUid(String username, long timestamp) {
      this.timestamp = timestamp;
      this.username = username;
    }
  }
  private static final Map<Long, CachedUid> uidCache =
      new ConcurrentHashMap<Long, CachedUid>();
  private static long cacheTimeout;
  private static boolean initialized = false;
  
  private static String stripDomain(String name) {
    int i = name.indexOf('\\');
    if (i != -1)
      name = name.substring(i + 1);
    return name;
  }

  public static String getOwner(FileDescriptor fd) throws IOException {
    ensureInitialized();
    if (Shell.WINDOWS) {
      String owner = Windows.getOwner(fd);
      owner = stripDomain(owner);
      return owner;
    } else {
      long uid = POSIX.getUIDforFDOwnerforOwner(fd);
      CachedUid cUid = uidCache.get(uid);
      long now = System.currentTimeMillis();
      if (cUid != null && (cUid.timestamp + cacheTimeout) > now) {
        return cUid.username;
      }
      String user = POSIX.getUserName(uid);
      LOG.info("Got UserName " + user + " for UID " + uid
          + " from the native implementation");
      cUid = new CachedUid(user, now);
      uidCache.put(uid, cUid);
      return user;
    }
  }

  public static FileInputStream getShareDeleteFileInputStream(File f)
      throws IOException {
    if (!Shell.WINDOWS) {
      return new FileInputStream(f);
    } else {
      FileDescriptor fd = Windows.createFile(
          f.getAbsolutePath(),
          Windows.GENERIC_READ,
          Windows.FILE_SHARE_READ |
              Windows.FILE_SHARE_WRITE |
              Windows.FILE_SHARE_DELETE,
          Windows.OPEN_EXISTING);
      return new FileInputStream(fd);
    }
  }

  public static FileInputStream getShareDeleteFileInputStream(File f, long seekOffset)
      throws IOException {
    if (!Shell.WINDOWS) {
      RandomAccessFile rf = new RandomAccessFile(f, "r");
      if (seekOffset > 0) {
        rf.seek(seekOffset);
      }
      return new FileInputStream(rf.getFD());
    } else {
      FileDescriptor fd = Windows.createFile(
          f.getAbsolutePath(),
          Windows.GENERIC_READ,
          Windows.FILE_SHARE_READ |
              Windows.FILE_SHARE_WRITE |
              Windows.FILE_SHARE_DELETE,
          Windows.OPEN_EXISTING);
      if (seekOffset > 0)
        Windows.setFilePointer(fd, seekOffset, Windows.FILE_BEGIN);
      return new FileInputStream(fd);
    }
  }

  public static FileOutputStream getCreateForWriteFileOutputStream(File f, int permissions)
      throws IOException {
    if (!Shell.WINDOWS) {
      try {
        FileDescriptor fd = POSIX.open(f.getAbsolutePath(),
            POSIX.O_WRONLY | POSIX.O_CREAT
                | POSIX.O_EXCL, permissions);
        return new FileOutputStream(fd);
      } catch (NativeIOException nioe) {
        if (nioe.getErrno() == Errno.EEXIST) {
          throw new AlreadyExistsException(nioe);
        }
        throw nioe;
      }
    } else {
      try {
        FileDescriptor fd = Windows.createFile(f.getCanonicalPath(),
            Windows.GENERIC_WRITE,
            Windows.FILE_SHARE_DELETE
                | Windows.FILE_SHARE_READ
                | Windows.FILE_SHARE_WRITE,
            Windows.CREATE_NEW);
        POSIX.chmod(f.getCanonicalPath(), permissions);
        return new FileOutputStream(fd);
      } catch (NativeIOException nioe) {
        if (nioe.getErrorCode() == 80) {
          throw new AlreadyExistsException(nioe);
        }
        throw nioe;
      }
    }
  }

  private synchronized static void ensureInitialized() {
    if (!initialized) {
      cacheTimeout =
          new Configuration().getLong("hadoop.security.uid.cache.secs",
              4*60*60) * 1000;
      LOG.info("Initialized cache for UID to User mapping with a cache" +
          " timeout of " + cacheTimeout/1000 + " seconds.");
      initialized = true;
    }
  }
  
  public static void renameTo(File src, File dst)
      throws IOException {
    if (!nativeLoaded) {
      if (!src.renameTo(dst)) {
        throw new IOException("renameTo(src=" + src + ", dst=" +
          dst + ") failed.");
      }
    } else {
      renameTo0(src.getAbsolutePath(), dst.getAbsolutePath());
    }
  }

  public static void link(File src, File dst) throws IOException {
    if (!nativeLoaded) {
      HardLink.createHardLink(src, dst);
    } else {
      link0(src.getAbsolutePath(), dst.getAbsolutePath());
    }
  }

  private static native void renameTo0(String src, String dst)
      throws NativeIOException;

  private static native void link0(String src, String dst)
      throws NativeIOException;

  public static void copyFileUnbuffered(File src, File dst) throws IOException {
    if (nativeLoaded && Shell.WINDOWS) {
      copyFileUnbuffered0(src.getAbsolutePath(), dst.getAbsolutePath());
    } else {
      FileInputStream fis = null;
      FileOutputStream fos = null;
      FileChannel input = null;
      FileChannel output = null;
      try {
        fis = new FileInputStream(src);
        fos = new FileOutputStream(dst);
        input = fis.getChannel();
        output = fos.getChannel();
        long remaining = input.size();
        long position = 0;
        long transferred = 0;
        while (remaining > 0) {
          transferred = input.transferTo(position, remaining, output);
          remaining -= transferred;
          position += transferred;
        }
      } finally {
        IOUtils.cleanup(LOG, output);
        IOUtils.cleanup(LOG, fos);
        IOUtils.cleanup(LOG, input);
        IOUtils.cleanup(LOG, fis);
      }
    }
  }

  private static native void copyFileUnbuffered0(String src, String dst)
      throws NativeIOException;
}
