package com.example.ecoin;

public class BlockSender extends Thread{
    public void run(){
        ClientBlock client = new ClientBlock();
        client.begin(8888);
    }

}

