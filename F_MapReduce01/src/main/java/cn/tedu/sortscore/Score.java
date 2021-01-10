package cn.tedu.sortscore;

import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Score implements WritableComparable<Score> {
    private String name = "";
    private int score;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    /**
     * 按照分数降序排序
     * @param o
     * @return
     */
    @Override
    public int compareTo(Score o) {
        return o.getScore() - this.score;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeUTF(name);
        out.writeInt(score);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        this.name = in.readUTF();
        this.score = in.readInt();
    }
}
