package com.example.ecoin;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;


public class ServerMap {
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
                Thread t = new ServerMapHandler(s, inputStream);
                t.start();



            } catch (Exception e) {
                e.printStackTrace();
                s.close();
            }
        }
    }
}


class ServerMapHandler extends Thread {

    final InputStream inputStream;
    final Socket s;

    public ServerMapHandler(Socket s, InputStream inputStream)
    {
        this.s = s;
        this.inputStream = inputStream;
    }


    @Override
    public void run() {
        while (true) {
            try {
                String encd = "UTF-8";
                JSONReader rdr = new JSONPacketReader(new ObjectInputStream(inputStream), encd);
                String retrievedJSON = rdr.read();
                rdr.close();
                System.out.println("Retrieved XML: " + retrievedJSON);
                try {
                    if(retrievedJSON == null){
                        System.out.println("Recieved NULL");
                        break;
                    }
                    else{
                        HashMap<String,Wallet> recieved = StringUtil.getMap(retrievedJSON);
                        System.out.println("Recieved MAP!!");
                        System.out.println(recieved);
                        System.out.println(recieved.get(org.apache.commons.codec.digest.DigestUtils.sha256Hex("aaa@bbb.ccc")).publicKey);


                    }
                } catch (Exception e) {
                    System.out.println("ERROR CHECK SERVERMAP");
                    Thread.sleep(1000);
                    e.printStackTrace();
                }

                break;


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            this.inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}




