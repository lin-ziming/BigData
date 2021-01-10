package cn.tedu.wordfile;

import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class WordFile implements Writable {
    private String word = "";
    private String file = "";

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public void write(DataOutput out) throws IOException {
        out.writeUTF(word);
        out.writeUTF(file);
    }

    public void readFields(DataInput in) throws IOException {
        this.word = in.readUTF();
        this.file = in.readUTF();
    }
}
