package com.example.dmc;

import java.util.ArrayList;
import java.util.List;

public class BulletPool {
    private List<Bullet> mBullets=new ArrayList<>();

    public BulletPool(){

    }

    public List<Bullet> getBullets() {
        return mBullets;
    }

    public Bullet get(int pos){
        return mBullets.get(pos);
    }

    public int size(){
        return mBullets.size();
    }

    public void add(Bullet bullet){
        mBullets.add(bullet);
    }

    public Bullet[] toStore(){
        Bullet[] result=new Bullet[this.size()];
        for (int i=0;i<this.size();i++){
            result[i]=mBullets.get(i);
        }
        return result;
    }

    public BulletPool find(String s){
        BulletPool result=new BulletPool();
        for(int i=0;i<mBullets.size();i++){
            if (mBullets.get(i).getComment().contains(s)){
                result.add(mBullets.get(i));
            }
        }
        return result;
    }
}
