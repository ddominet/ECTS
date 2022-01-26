package com.example.ecoin;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

public class PanelControllerGUI {

    @FXML private Stage stage;
    @FXML private Scene scene;
    @FXML private Parent root;
    @FXML private Label userIDLabel;
    @FXML private Label walletBallanceLabel;
    @FXML private Button refreshButton;


    public void setText(String email, String balance){
        userIDLabel.setText(email);
        walletBallanceLabel.setText(balance);
    }

    public void switchToHome(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load((getClass().getResource("home.fxml")));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    public void switchToTransfer(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load((getClass().getResource("transfer.fxml")));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    public void refresh(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ApplicationGUI.class.getResource("panel.fxml"));
        root = fxmlLoader.load();
        userIDLabel.setText(ControllerGUI.currentUser);
        walletBallanceLabel.setText(Float.toString(ControllerGUI.currentWallet.getBalance()));
        PanelControllerGUI panelController = fxmlLoader.getController();
        panelController.setText(ControllerGUI.currentUser, Float.toString(ControllerGUI.currentWallet.getBalance()));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }
}