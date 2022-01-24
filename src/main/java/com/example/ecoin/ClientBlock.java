package com.example.ecoin;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;


public class ClientBlock extends Thread{


    public void begin(int port) {
        System.out.println("Connected!");
        while (true){
            try {
                Socket s = new Socket("0.0.0.0", port);
                OutputStream outputStream = s.getOutputStream();
                if(ControllerGUI.currentBlock != null){
                    System.out.println("Assigning new thread for this client");
                    Thread t = new ClientBlockHandler(s, outputStream, StringUtil.getJson(ControllerGUI.currentBlock));
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
class ClientBlockHandler extends Thread {

    final OutputStream outputStream;
    final Socket s;
    final String json;

    public ClientBlockHandler(Socket s, OutputStream outputStream, String json) {
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

