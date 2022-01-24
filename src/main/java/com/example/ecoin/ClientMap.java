package com.example.ecoin;

import java.io.*;
import java.net.Socket;


public class ClientMap extends Thread{


    public void begin(String json, int port) {
        System.out.println("Connected!");
        while (true){
            try {
                Socket s = new Socket("0.0.0.0", port);
                OutputStream outputStream = s.getOutputStream();
                if(ControllerGUI.wallets != null){
                    System.out.println("Assigning new thread for this client");
                    Thread t = new ClientBlockHandler(s, outputStream, json);
                    t.start();
                }
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
class ClientMapHandler extends Thread {

    final OutputStream outputStream;
    final Socket s;
    final String json;

    public ClientMapHandler(Socket s, OutputStream outputStream, String json) {
        this.s = s;
        this.outputStream = outputStream;
        this.json = json;
    }

    @Override
    public void run() {
        while (true) {
            try {

                String encd = "UTF-8";
                System.out.println(json.getBytes("UTF-8").length);

                JSONWriter wrtr = new JSONPacketWriter(new ObjectOutputStream(outputStream), encd);
                wrtr.write(json);
                wrtr.close();
                break;


            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

