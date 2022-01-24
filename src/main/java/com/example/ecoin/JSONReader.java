package com.example.ecoin;

import java.io.IOException;
import java.io.InputStream;

/**
 * Abstrakcyjna klasa do czytania danych JSON z streamu
 */
public abstract class JSONReader {

    protected InputStream inStrm;
    protected String encd;

    protected JSONReader(InputStream inStrm, String encd) {
        this.inStrm = inStrm;
        this.encd = encd;
    }

    /**
     * Czyta dane z streamu
     */
    public abstract String read();

    /**
     * Zamyka stream
     */
    public void close() {
        try {
            inStrm.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

