package cn.tedu.regex;

public class RegexTest {

    public static void main(String[] args) {

        String log = "192.168.120.23 -- [30/Apr/2018:20:25:32 +0800] \"GET /asf.avi HTTP/1.1\" 304 -";
        // 在正则表达式中，用()括起来的部分称之为捕获组
        // 在正则比到达时中，不获取是有编号的
        // 编号是从1开始的
        String[] arr = log.replaceAll(
                "(.*) -- \\[(.*) (.*)\\] \"(.*) (.*) (.*)\" (.*) -",
                "$1 $2 $3 $4 $5 $6 $7").split(" ");

        for (String s : arr) {
            System.out.println(s);
        }
    }

}
