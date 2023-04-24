package com.example.dmc;

import android.os.Message;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class InterfaceBili {
    public static String interfaceLink(int cid,int quality) throws NoSuchAlgorithmException {
        String entropy = "rbMCKn@KuamXWlPMoJGsKcbiJKUfkPF_8dABscJntvqhRSETg";
        char[] entropy_ch=entropy.toCharArray();
        StringBuilder temp=new StringBuilder();
        for (char ch:entropy_ch){
            temp.insert(0,Character.toChars(ch+2));
        }
        String[] buffer=temp.toString().split(":");
        String params=String.format("appkey=%s&cid=%s&otype=json&qn=%s&quality=%s&type=",buffer[0],cid,quality,quality);
        MessageDigest md=MessageDigest.getInstance("MD5");
        md.update((params+buffer[1]).getBytes(StandardCharsets.UTF_8));
        byte[] digest=md.digest();
        String checksum=getChecksum(digest);
        String result=String.format("https://interface.bilibili.com/v2/playurl?%s&sign=%s",params,checksum);
        return result;
    }

    private static String getChecksum(byte[] bytes){
        StringBuilder sb=new StringBuilder();
        for (byte b:bytes){
            //sb.append(Integer.toHexString(b&0xff));
            sb.append(String.format("%02x",b));
        }
        return sb.toString();
    }
}
