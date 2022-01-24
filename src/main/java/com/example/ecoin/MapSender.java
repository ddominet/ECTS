package com.example.ecoin;

public class MapSender extends Thread{
    public void run(){
        ClientMap client = new ClientMap();
        client.begin(StringUtil.getJson(ControllerGUI.wallets), 7777);
    }
}
