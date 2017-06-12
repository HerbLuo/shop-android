package cn.cloudself.weexshop.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 针对ipv4的工具
 * <p>
 * 目前功能有
 * <p>
 * ipv4字符串和long型数字的互转
 * 可用于数据库中，数据库中可采用 无符号整型存储
 *
 * @author unascribed
 * @author HerbLuo
 * @version 0.0.2.d
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class Ipv4Utils {

    /**
     * 将ip转化为整数，便于数据库存储
     * 未对传入string 进行验证(请确保ip合法，亦可使用 {@link #isIp(String)} 进行验证)
     * <p>
     * 相当于mysql里的INET_ATON
     *
     * @param ip 例: 192.168.1.1
     * @return 例: 3232235777
     */
    public static long ip2long(String ip) {
        String[] ips = ip.split("\\.");
        if (ips.length != 4) {
            return 0;
        }
        long result = Long.parseLong(ips[0]) << 24;
        result += Integer.parseInt(ips[1]) << 16;
        result += Integer.parseInt(ips[2]) << 8;
        result += Integer.parseInt(ips[3]);
        return result;
    }

    /**
     * 将整数转化为ip，便于数据库存储
     * 相当于mysql里的INET_NTOA
     *
     * @param ip 例: 3232235777
     * @return 例: 192.168.1.1
     */
    public static String long2ip(long ip) {
        String result = (ip >> 24) + ".";
        ip = ip % 16777216;
        result += (ip >> 16) + ".";
        ip = ip % 65536;
        result += (ip >> 8) + ".";
        ip = ip % 256;
        result += ip;
        return result;
    }

    /**
     * 从服务器获取当前主机的ip
     *
     * @return ip
     */
    public static String getCurrentIpFromServer() throws IOException {
        URL url = new URL("http://www.cloudself.cn/util/ip/");
        URLConnection connection = url.openConnection();
        connection.connect();

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String result = reader.readLine();

        Matcher matcher = ipJsonResolverRegex.matcher(result);

        return matcher.find() ? matcher.group(1) : "";
    }

    private static Pattern ipJsonResolverRegex = Pattern.compile("\\{\"ip\": ([\\d.]+)}");

    /**
     * 判断字符串是否为ip
     *
     * @param ip 字符串
     * @return .
     */
    public static boolean isIp(String ip) {

        String[] parts = ip.split("\\.");

        if (parts.length != 4) {
            return false;
        }

        for (String part : parts) {
            if (part.startsWith("0") && !part.equals("0")) {
                return false;
            }

            Integer num;
            try {
                num = Integer.valueOf(part);
            } catch (NumberFormatException e) {
                return false;
            }
            if (num == null || num < 0 || num > 255) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断ip 是否为为内网地址
     * <p>
     * the code snippets is copied from internet
     *
     * @param ip .
     * @return .
     */
    public static boolean isInternalIp(String ip) {
        if (!isIp(ip)) {
            throw new IllegalArgumentException("参数ip不符合规范");
        }

        byte[] addr = textToNumericFormatV4(ip);
        if (addr == null) {
            throw new RuntimeException("String ip 转数值数组失败，email to i@closx.com");
        }

        final byte b0 = addr[0];
        final byte b1 = addr[1];
        //10.x.x.x/8
        final byte SECTION_1 = 0x0A;
        //172.16.x.x/12
        final byte SECTION_2 = (byte) 0xAC;
        final byte SECTION_3 = (byte) 0x10;
        final byte SECTION_4 = (byte) 0x1F;
        //192.168.x.x/16
        final byte SECTION_5 = (byte) 0xC0;
        final byte SECTION_6 = (byte) 0xA8;
        switch (b0) {
            case SECTION_1:
                return true;
            case SECTION_2:
                if (b1 >= SECTION_3 && b1 <= SECTION_4) {
                    return true;
                }
            case SECTION_5:
                switch (b1) {
                    case SECTION_6:
                        return true;
                }
            default:
                return false;
        }
    }


    //
    // Source code recreated from a .class file by IntelliJ IDEA
    // (powered by Fernflower decompiler)
    // package sun.net.util bytecode version: 52.0 (Java 8)
    // IPAddressUtil.textToNumericFormatV4
    //
    private static byte[] textToNumericFormatV4(String var0) {
        byte[] var1 = new byte[4];
        long var2 = 0L;
        int var4 = 0;
        boolean var5 = true;
        int var6 = var0.length();
        if (var6 != 0 && var6 <= 15) {
            for (int var7 = 0; var7 < var6; ++var7) {
                char var8 = var0.charAt(var7);
                if (var8 == 46) {
                    if (var5 || var2 < 0L || var2 > 255L || var4 == 3) {
                        return null;
                    }

                    var1[var4++] = (byte) ((int) (var2 & 255L));
                    var2 = 0L;
                    var5 = true;
                } else {
                    int var9 = Character.digit(var8, 10);
                    if (var9 < 0) {
                        return null;
                    }

                    var2 *= 10L;
                    var2 += (long) var9;
                    var5 = false;
                }
            }

            if (!var5 && var2 >= 0L && var2 < 1L << (4 - var4) * 8) {
                switch (var4) {
                    case 0:
                        var1[0] = (byte) ((int) (var2 >> 24 & 255L));
                    case 1:
                        var1[1] = (byte) ((int) (var2 >> 16 & 255L));
                    case 2:
                        var1[2] = (byte) ((int) (var2 >> 8 & 255L));
                    case 3:
                        var1[3] = (byte) ((int) (var2 & 255L));
                    default:
                        return var1;
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

}
