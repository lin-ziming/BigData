package cn.tedu.serial;

import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class SerialDemo {

    // 创建对象
    @Test
    public void createUser() {

        // 方式一：先创建对象后给值
        User u1 = new User();
        u1.setName("Nancy");
        u1.setAge(22);
        u1.setGender("female");
        u1.setHeight(168.0);
        u1.setWeight(52.5);
        System.out.println(u1);

        // 方式二：在创建对象的时候给值
        User u2 = new User("Peter", 18, "male", 185.2, 70.4);
        System.out.println(u2);

        // 方式三：指定属性给值 - 适合于反射
        User u3 = new User();
        u3.put("name", "Helen");
        u3.put("age", 21);
        u3.put("gender", "female");
        u3.put("height", 165.8);
        u3.put("weight", 51.8);
        System.out.println(u3);

        // 方式四：编号给值 - 这种方式更适合于序列化
        User u4 = new User();
        u4.put(0, "Cindy");
        u4.put(1, 19);
        u4.put(2, "female");
        u4.put(3, 169.2);
        u4.put(4, 49.9);
        System.out.println(u4);

        // 方式五：建造者模式
        User u5 = User.newBuilder().setName("Mark").setAge(18).setGender("male")
                .setHeight(185.5).setWeight(65.8).build();
        System.out.println(u5);

        // 方式六：建造者模式
        User u6 = User.newBuilder(u5).setName("Mike").build();
        System.out.println(u6);

    }

    // 将json转化为User对象
    @Test
    public void jsonToUser() {
        // 给定json
        String json = "{\"name\": \"Helen\", \"age\": 21, \"gender\": \"female\", \"height\": 165.8, \"weight\": 51.8}";
        // json的解析
        String[] arr = json.substring(1, json.length() - 1)
                .replaceAll("\"", "").split(", ");
        // 构建对象
        User u = new User();
        // 填充属性值
        for (String s : arr) {
            String[] fields = s.split(": ");
            if (fields[1].matches("0|[1-9][0-9]*")) {
                u.put(fields[0], Integer.parseInt(fields[1]));
            } else if (fields[1].matches("0\\.[0-9]+|[1-9][0-9]*\\.[0-9]+")) {
                u.put(fields[0], Double.parseDouble(fields[1]));
            } else {
                u.put(fields[0], fields[1]);
            }
        }
        System.out.println(u.getName());
    }

    // 序列化
    @Test
    public void serial() throws IOException {
        // 创建对象
        User u1 = new User("Lee", 19, "male", 176.9, 63.7);
        User u2 = new User("Tom", 21, "male", 187.1, 73.9);
        User u3 = new User("Sam", 18, "male", 180.2, 66.2);
        // 构建序列化流
        DatumWriter<User> dw = new SpecificDatumWriter<>(User.class);
        // 构建文件流
        DataFileWriter<User> dfw = new DataFileWriter<>(dw);
        // 指定文件
        // dfw.create(u1.getSchema(), new File("D:/a.txt"));
        dfw.create(User.SCHEMA$, new File("C:/test/d.txt"));
        // 序列化
        dfw.append(u1);
        dfw.append(u2);
        dfw.append(u3);
        // 关流
        dfw.close();
    }

    // 反序列化
    @Test
    public void deSerial() throws IOException {
        // 构建反序列化流
        DatumReader<User> dr = new SpecificDatumReader<>(User.class);
        // 构建文件流
        DataFileReader<User> dfr = new DataFileReader<>(
                new File("C:/test/d.txt"), dr);
        // 为了方便获取多个对象，将这个流设计成了迭代模式
        while (dfr.hasNext()) {
            User u = dfr.next();
            System.out.println(u);
        }
        // 关流
        dfr.close();
    }

}
