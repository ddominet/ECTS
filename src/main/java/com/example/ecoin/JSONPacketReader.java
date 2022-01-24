package com.example.ecoin;

import java.io.DataInputStream;
import java.io.InputStream;

public class JSONPacketReader extends JSONReader {

    public JSONPacketReader(InputStream inStrm, String encd) {
        super(inStrm, encd);
    }
    public String read() {
        return readPacket();
    }
    private String readPacket() {

        DataInputStream dataInStream = null;
        int payloadLen;
        byte[] payloadBuf = null;

        try {

            dataInStream = new DataInputStream(super.inStrm);

            // czytamy pierwsze 4 bajty na, których zakodowana jest długość danych
            payloadLen = dataInStream.readInt();

            // czytamy dane
            int totalBytesRead = 0, bytesRead = 0, currBufPos = 0;
            payloadBuf = new byte[payloadLen];
            dataInStream.readFully(payloadBuf, 0, payloadLen);


            // Java wykrywa kodowanie (UTF-8)
            return new String(payloadBuf);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

