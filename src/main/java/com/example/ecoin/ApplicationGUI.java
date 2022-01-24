package com.example.ecoin;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.json.simple.JSONArray;

import java.io.IOException;
import java.security.Security;
import java.util.HashMap;

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

        //Create wallets:
        Wallet walletA = new Wallet();
        ControllerGUI.wallets.put(org.apache.commons.codec.digest.DigestUtils.sha256Hex("aaa@bbb.ccc"), walletA);
        Wallet walletB = new Wallet();
        ControllerGUI.wallets.put(org.apache.commons.codec.digest.DigestUtils.sha256Hex("test@test.test"), walletB);
        Wallet coinbase = new Wallet();

        if(MainChain.origin == true){
            MainChain.genesisTransaction = new Transaction(coinbase.publicKey, ControllerGUI.wallets.get(org.apache.commons.codec.digest.DigestUtils.sha256Hex("aaa@bbb.ccc")).publicKey, 100000f, null);
            MainChain.genesisTransaction.generateSignature(coinbase.privateKey);	 //manually sign the genesis transaction
            MainChain.genesisTransaction.transactionId = "0"; //manually set the transaction id
            MainChain.genesisTransaction.outputs.add(new TransactionOutput(MainChain.genesisTransaction.reciepient, MainChain.genesisTransaction.value, MainChain.genesisTransaction.transactionId)); //manually add the Transactions Output
            MainChain.UTXOs.put(MainChain.genesisTransaction.outputs.get(0).id, MainChain.genesisTransaction.outputs.get(0));
            System.out.println("Creating and Mining Genesis block... ");
            Block genesis = new Block("0");
            ControllerGUI.currentBlock = genesis;
            genesis.addTransaction(MainChain.genesisTransaction);
            MainChain.addBlock(genesis);
            MainChain.isChainValid();//its important to store our first transaction in the UTXOs list.
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





/*
        System.out.println(StringUtil.getJson(walletA));
        System.out.println("HEREEE______________________________________________");
        Wallet currwallet = ControllerGUI.wallets.get(org.apache.commons.codec.digest.DigestUtils.sha256Hex("aaa@bbb.ccc"));
        System.out.println(StringUtil.getJson(currwallet));
        System.out.println("HEREEE______________________________________________");
        String json = StringUtil.getJson(ControllerGUI.wallets);
        HashMap<String, Wallet> test_json = StringUtil.getMap(json);
        System.out.println("HERE BE DRAGONS");
        System.out.println((Block) StringUtil.getObject(json));

*/







        launch();
    }
}