package com.example.dmc;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.*;
import java.util.stream.IntStream;

public class BaseCore
{

    private final long polyRev=0xedb88320L;
    private long[] table=new long[256];
    private long[] ini5Table=new long[1000000];

    private static BaseCore instance=new BaseCore();

    public static BaseCore getInstance(){
        return instance;
    }

    private BaseCore(){
        this.initializer();
        this.mkini5Table();
    }

    private void initializer(){
        for (int i=0; i<256; i=i+1) {
            long ope=i;
            for (int bit=0;bit<8;bit=bit+1) {
                if ((ope&0x1)!=0) {
                    ope=ope>>1;
                    ope=ope^polyRev;
                }
                else {
                    ope=ope>>1;
                }
            }
            table[i]=ope;
        }
    }


    private long cal(String line) {
        char[] charlist=line.toCharArray();
        long superv=0xffffffffL;
        for(int i=0; i<charlist.length; i++) {
            long ope=(int) charlist[i];
            ope=(ope^superv)&0xff;
            superv=table[(int) ope]^(superv>>8);
        }
        return superv;
    }
    private void mkini5Table(){
        for (int i=0;i<1000000;i++) {
            ini5Table[i]=cal(Integer.toString(i));
        }
    }
    /*
    public void ini5Initializer() {
        for (int i=0; i<100000;i++){
            ini5Table[i]=cal(Integer.toString(i));
        }
    }
    */
    private int finder(long num) {                //give a number and return the index in crc32 table
        int result=-1;
        for(int i=0; i<256; i++) {
            if(num==(table[i]>>24)) {
                result=i;
            }
        }
        return result;
    }
    private ArrayList<Integer> matcher(long num) {                 //give a number,and try to match the counterpart in the initial pool,return the index in the pool
        ArrayList<Integer> result=new ArrayList<Integer>();
        for (int i=0;i<1000000;i++) {
            if (((ini5Table[i]>>28)==(num>>28))&
                    (((ini5Table[i]>>20)&0xf)==((num>>20)&0xf))&
                    (((ini5Table[i]>>12)&0xf)==((num>>12)&0xf))&
                    (((ini5Table[i]>>4)&0xf)==((num>>4)&0xf))){
                result.add(i);
            }
        }
        return result;
    }
    private String crc_any(long ini,long[] l4) {               //return
        long ope=ini;
        ArrayList<Character> num_set=new ArrayList<Character>();
        for(int i=0; i<l4.length; i++) {
            long index=l4[i];
            long order=index^(ope&0xff);
            if (order>0x39||order<0x30) {
                return "error";
            }
            char num=(char) order;
            num_set.add(num);
            ope=table[(int) index]^(ope>>8);
        }
        StringBuilder concent=new StringBuilder();
        for (int i=0; i<num_set.size(); i++) {
            concent.append(num_set.get(i));
        }
        String result=new String();
        result=concent.toString();
        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private ArrayList<String> crackl4(String line) {
        long ori=Long.parseUnsignedLong(line,16);
        ori=ori^0xffffffffL;
        long karma=ori;
        long[] last4={0,0,0,0};
        for (int i=0; i<4 ;i++) {
            long f2=karma>>24;
            long table_index=finder(f2);
            long table_var=table[(int) table_index];
            long karma6=karma^table_var;
            karma=karma6<<8;
            long adder=table_index^0x30;
            karma=karma^adder;
            karma=((karma>>4)<<4);
            last4[3-i]=table_index;
        }
        String l4_index="-1";
        ArrayList<Integer> f6_indexs=matcher(karma);
        ArrayList<String> l4_array=new ArrayList<String>();
        for (int i=0;i<f6_indexs.size();i++)
        {
            int temp_index=f6_indexs.get(i);
            long temp_f6=ini5Table[(int) temp_index];
            String temp_l4_index=crc_any(temp_f6,last4);
            l4_array.add(temp_l4_index);
        }
        ArrayList<String> legal=new ArrayList<String>();
        for (int i=0;i<l4_array.size();i++)
        {
            if (l4_array.get(i)!="error")
            {
                legal.add(f6_indexs.get(i)+l4_array.get(i));
            }
        }
        return legal;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ArrayList<String> initBaseCore(String codex){
        return crackl4(codex);
    }
}
