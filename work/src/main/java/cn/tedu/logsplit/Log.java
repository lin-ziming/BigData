package cn.tedu.logsplit;

import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Log implements Writable {
    private String IP = "";
    private String date;
    private String requestType = "";
    private String resource = "";
    private String protocol = "";
    private int status;

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void write(DataOutput out) throws IOException {
        out.writeUTF(IP);
        out.writeUTF(date);
        out.writeUTF(requestType);
        out.writeUTF(resource);
        out.writeUTF(protocol);
        out.writeInt(status);
    }

    public void readFields(DataInput in) throws IOException {
        this.IP = in.readUTF();
        this.date = in.readUTF();
        this.requestType = in.readUTF();
        this.resource = in.readUTF();
        this.protocol = in.readUTF();
        this.status = in.readInt();
    }
}
