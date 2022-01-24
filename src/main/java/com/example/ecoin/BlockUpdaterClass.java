package com.example.ecoin;

import java.io.IOException;

public class BlockUpdaterClass extends Thread {
    public void run(){
        ServerBlock serverBlock = new ServerBlock();
        try {
            serverBlock.begin(8888);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

