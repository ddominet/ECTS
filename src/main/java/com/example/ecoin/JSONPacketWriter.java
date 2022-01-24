package com.example.ecoin;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;

/**
 * Klasa do wysyłanie zakodowanych danych JSON do streamu socketu
 * <pre>
 * ----------------------------------------------
 * 1|0|0|0|0|0|1|1|0|0|0|1|0|0|1|0|0|1|1|0|1|1|1|
 * ----------------------------------------------
 * |                    Dane                    |
 * ----------------------------------------------
 * </pre>
 */
public class JSONPacketWriter extends JSONWriter {

    public JSONPacketWriter(OutputStream outStrm, String encd) {
        super(outStrm, encd);
    }

    /**
     * <p>
     * Enkapsuluje dane w pakiet i wysyła do streamu.
     * Pierwsze 4 bajty to integer zapisujący długość przesyłanych danych.
     * Po odbiorze jest odczytywany i odbiornik odlicza dane przesłane przez tego nadawcę
     * </p>
     */
    public void write(String json) {writePacket(json);
    }

    private void writePacket(String json) {

        JSONObject jsonObj = null;
        DataOutputStream dataOutStream = null;
        byte jsonBytes[] = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String, String> map = mapper.readValue(json, Map.class);
            // kodujemy JSON w UTF-8
            jsonObj = new JSONObject(map);
            jsonBytes = jsonObj.toString().getBytes(super.encd);

            dataOutStream = new DataOutputStream(super.outStrm);

            // tworzymy pole długość nagłówka
            for (int i = 0; i < 4 - jsonBytes.length; i++)
                dataOutStream.write(0);

            // wypisujemy długość nagłówka jako bajty
            dataOutStream.write(ByteBuffer.allocate(4).putInt(jsonBytes.length)
                    .array());

            // wpisujemy dane
            dataOutStream.write(jsonBytes);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

