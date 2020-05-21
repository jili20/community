package com.nowcoder.community;

import java.io.IOException;
/**
 * @author bing  @create 2020/5/20 7:46 下午
 */
public class WKTests {
    public static void main(String[] args) {
        String cmd = "/usr/local/bin/wkhtmltoimage --quality 75 https://www.nowcoder.com /Users/Bing/Code/data/wk-images/4.png";
        try {
            Runtime.getRuntime().exec(cmd);
            System.out.println("ok!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
