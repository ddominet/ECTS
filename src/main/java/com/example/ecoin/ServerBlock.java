package com.example.ecoin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import flexjson.JSONDeserializer;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;


public class ServerBlock {
    public void begin(int port) throws IOException {


        ServerSocket ss = new ServerSocket(port);
        while (true)
        {
            Socket s = null;
            try
            {

                s = ss.accept();
                System.out.println("A new client is connected : " + s);
                InputStream inputStream = s.getInputStream();

                System.out.println("Assigning new thread for this client");
                Thread t = new ServerBlockHandler(s, inputStream);
                t.start();



            } catch (Exception e) {
                e.printStackTrace();
                s.close();
            }
        }
    }
}



class ServerBlockHandler extends Thread {

    final InputStream inStrm;
    final Socket socks;

    public ServerBlockHandler(Socket socks, InputStream inStrm)
    {
        this.socks = socks;
        this.inStrm = inStrm;
    }


    @Override
    public void run() {
        while (true) {
            try {
                String encd = "UTF-8";
                JSONReader readder = new JSONPacketReader(new ObjectInputStream(inStrm), encd);
                String retrievedJSON1 = readder.read();
                readder.close();
                System.out.println("Retrieved XML: " + retrievedJSON1);
                if(retrievedJSON1 == null){
                    break;
                }
                else {
                    try {
                        if(socks.getLocalPort() == 8888){
                            Block test = StringUtil.getObject(retrievedJSON1);


                            System.out.println("START" + test + "STOP");


                        }

                    } catch (Exception e) {
                        System.out.println("ERROR CHECK SERVERBLOCK");
                        Thread.sleep(1000);
                        e.printStackTrace();
                    }
                }
                break;


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            this.inStrm.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}




