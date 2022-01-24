package com.example.ecoin;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Abstrakcyjna klasa do wpisywanie danych JSON do streamu
 */
public abstract class JSONWriter {

    protected OutputStream outStrm;
    protected String encd;

    protected JSONWriter(OutputStream outStrm, String encd) {
        this.outStrm = outStrm;
        this.encd = encd;
    }

    /**
     * wpisuje dane do streamu
     */
    public abstract void write(String json);

    /**
     * zamyka stream.
     */
    public void close() {
        try {
            outStrm.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

