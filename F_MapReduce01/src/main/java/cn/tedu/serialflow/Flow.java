package cn.tedu.serialflow;

import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Flow implements Writable {
    private String phone = "";
    private String city = "";
    private String name = "";
    private int flow;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFlow() {
        return flow;
    }

    public void setFlow(int flow) {
        this.flow = flow;
    }

    /**
     * 序列化方法
     * 在序列化的时候，只需要将有必要的属性来依次写出即可
     * 有必要的属性 - 在实际生产过程中，一个类中可能会包含几十个属性
     * 在实际生产过程中，收集信息的时候，秉持”多收集“原则
     * 例如：在注册网址、APP的时候，会有必填和选填
     * @param out
     * @throws IOException
     */
    @Override
    public void write(DataOutput out) throws IOException {
        out.writeUTF(phone);
        out.writeUTF(city);
        out.writeUTF(name);
        out.writeInt(flow);
    }

    /**
     * 反序列化方法
     * 按照什么顺序往外写的，就按照什么顺序读回来
     * @param in
     * @throws IOException
     */
    @Override
    public void readFields(DataInput in) throws IOException {
        this.phone = in.readUTF();
        this.city = in.readUTF();
        this.name = in.readUTF();
        this.flow = in.readInt();
    }
}
