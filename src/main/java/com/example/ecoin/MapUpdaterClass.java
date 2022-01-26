package com.example.ecoin;

import java.io.IOException;

public class MapUpdaterClass extends Thread {
    public void run(){
        ServerMap serverMap = new ServerMap();
        try {
            serverMap.begin(7777);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}