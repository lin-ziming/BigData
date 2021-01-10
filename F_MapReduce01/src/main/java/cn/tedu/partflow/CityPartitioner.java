package cn.tedu.partflow;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

public class CityPartitioner extends Partitioner<Text,Flow> {
    /**
     * @param text
     * @param flow
     * @param numReduceTasks
     * @return 返回值实际上是分区号
     */
    @Override
    public int getPartition(Text text, Flow flow, int numReduceTasks) {
        // 获取城市/地区
        String city = flow.getCity();
        if (city.equals("bj")) return 0;
        else if(city.equals("sh")) return 1;
        else return 2;
    }
}
