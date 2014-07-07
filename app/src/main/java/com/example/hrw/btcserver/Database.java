package com.example.hrw.btcserver;

import android.util.Log;

/**
 * Created by hrw on 2014/7/7.
 */
public class Database {
    int[] data;

    public Database(){
        int[] data = {70,65,71,75,72,71,66,63,69,70,72,75,74,69,68,67,61
        ,65,69,70,72,69,68,74,72,73,75,71,68,64,69,72,68,65,73,68,69,64,74,72,72,71,66,63,69,
        70,72,75,74,69,74,72,73,74,71,65,71,75,72,71,71,68,64,69,64,74,73,75,71,65,71,63,69,
        70,72,75,61,65,69,70,69,70,72,75,74,73,68,69,64,74,6,3,69,70,72,73,75,71,68,64};
        this.data = data;
        Log.w("Database: ",String.valueOf(data.length)+" generated");
    }
    public int[] getData(int count){
        int [] dataTmp = new int[count];
        for(int i = 0;i < count;i++){
            dataTmp[i] = data[i];
        }
        return dataTmp;
    }
}
