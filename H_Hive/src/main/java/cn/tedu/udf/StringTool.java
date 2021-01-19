package cn.tedu.udf;

import org.apache.hadoop.hive.ql.exec.UDF;

/**
 * 自定义函数
 */
public class StringTool extends UDF {
    /**
     * 定义evaluate方法，将要执行的逻辑写在这个方法中
     */
    public int evaluate(String str, String sub){
        return str.indexOf(sub);
    }

    public String evaluate(String str, int n){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append(str);
        }
        return sb.toString();
    }
}
