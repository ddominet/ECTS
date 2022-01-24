package com.example.ecoin;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Type;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


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

class BlockInstanceCreator extends Block {
    public String hash;
    public String previousHash;
    public String merkleRoot;
    public ArrayList<Transaction> transactions = new ArrayList<Transaction>(); //our data will be a simple message.
    public long timeStamp; //as number of milliseconds since 1/1/1970.
    public int nonce;

    public BlockInstanceCreator(String previousHash) {
        super(previousHash);
    }
}


class ServerBlockHandler extends Thread {

    final InputStream inputStream;
    final Socket s;

    public ServerBlockHandler(Socket s, InputStream inputStream)
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
                if(retrievedJSON == null){
                    break;
                }
                else {
                    try {
                        Gson gson = new Gson();
                        Block block = gson.fromJson(retrievedJSON, (Type) new BlockInstanceCreator("0"));
                        ControllerGUI.currentBlock = block;


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
            this.inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}




