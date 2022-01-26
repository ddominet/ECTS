package com.example.ecoin;

import com.google.gson.Gson;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


import java.io.IOException;
import java.security.Security;


public class ApplicationGUI extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        Parent root = FXMLLoader.load((getClass().getResource("home.fxml")));
        Scene scene1 = new Scene(root);

        Image icon = new Image("217853.png");
        stage.getIcons().add(icon);

        stage.setTitle("eWaluta");

        String cssStyling = this.getClass().getResource("style.css").toExternalForm();
        scene1.getStylesheets().add(cssStyling);

        stage.setScene(scene1);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) throws IOException {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        //Tworzymy:
        Wallet walletA = new Wallet();
        ControllerGUI.wallets.put(org.apache.commons.codec.digest.DigestUtils.sha256Hex("aaa@bbb.ccc"), walletA);
        Wallet walletB = new Wallet();
        ControllerGUI.wallets.put(org.apache.commons.codec.digest.DigestUtils.sha256Hex("test@test.test"), walletB);
        Wallet coinbase = new Wallet();

        if(Chain.origin == true){

            // Tworzymy genesis transaction
            Chain.genesisTransaction = new Transaction(coinbase.publicKey, ControllerGUI.wallets.get(org.apache.commons.codec.digest.DigestUtils.sha256Hex("aaa@bbb.ccc")).publicKey, 100000f, null);
            Chain.genesisTransaction.generateSignature(coinbase.privateKey);	 //manually sign the genesis transaction
            Chain.genesisTransaction.transactionId = "0"; //manually set the transaction id
            Chain.genesisTransaction.outputs.add(new TransactionOutput(Chain.genesisTransaction.reciepient, Chain.genesisTransaction.value, Chain.genesisTransaction.transactionId)); //manually add the Transactions Output
            Chain.UTXOs.put(Chain.genesisTransaction.outputs.get(0).id, Chain.genesisTransaction.outputs.get(0));
            System.out.println("Creating and Mining Genesis block... ");
            Block genesis = new Block("0");
            ControllerGUI.currentBlock = genesis;
            genesis.addTransaction(Chain.genesisTransaction);
            Chain.addBlock(genesis);
            Chain.isChainValid();//przechowujemy pierwszą transakcję w liście UTXO


        }



        //STARTING P2P for MAPS

        BlockUpdaterClass blockupdater = new BlockUpdaterClass();
        blockupdater.start();

        MapUpdaterClass mapupdater = new MapUpdaterClass();
        mapupdater.start();

        BlockSender blocksender = new BlockSender();
        blocksender.start();

        MapSender mapsender = new MapSender();
        mapsender.start();




        launch();
    }
}