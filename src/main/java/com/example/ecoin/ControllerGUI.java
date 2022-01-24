package com.example.ecoin;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;
import java.io.IOException;
import java.security.Security;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.regex.Pattern;

public class ControllerGUI {

    @FXML private Stage stage;
    @FXML private Scene scene;
    @FXML private Parent root;
    @FXML private TextField nameField;
    @FXML private TextField emailIdField;
    @FXML private TextField passwordField;
    @FXML private TextField emailFieldSI;
    @FXML private TextField passwordFieldSI;
    @FXML private Button submitButton;
    @FXML private Button submitButtonSI;
    @FXML private Button transferButton;
    @FXML private TextField amountField;
    @FXML private TextField recipientField;

    public static String currentUser;
    public static Wallet currentWallet;
    public static Block currentBlock;

    final Pattern EMAIL_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static HashMap<String, Wallet> wallets = new HashMap<String, Wallet>();

    //TODO: transfer method

    @FXML
    public void register(ActionEvent event) throws SQLException, IOException {

        Window owner = submitButton.getScene().getWindow();

        if (nameField.getText().isEmpty()) {
            showInfo(Alert.AlertType.ERROR, owner, "Błąd formularza!", "Podaj swoje imię");
            return;
        }

        if (emailIdField.getText().isEmpty()) {
            showInfo(Alert.AlertType.ERROR, owner, "Błąd formularza!", "Podaj adres email");
            return;
        }

        if (passwordField.getText().isEmpty()) {
            showInfo(Alert.AlertType.ERROR, owner, "Błąd formularza!", "Podaj hasło.");
            return;
        }

        if (passwordField.getText().length() < 8 ) {
            showInfo(Alert.AlertType.ERROR, owner, "Błąd formularza!", "Hasło musi składać się z przynajmniej 8 znaków.");
            return;
        }

        if (EMAIL_REGEX.matcher(emailIdField.getText()).find() == false){
            showInfo(Alert.AlertType.ERROR, owner, "Błąd formularza!", "Nieprawidłowy format adresu email.");
          return;
        }

        String fullName = nameField.getText();
        String emailId = emailIdField.getText();
        String password = passwordField.getText();
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        Wallet wallet = new Wallet();
        wallets.put(org.apache.commons.codec.digest.DigestUtils.sha256Hex(emailId), wallet);

        JdbcDao jdbcDao = new JdbcDao();
        jdbcDao.insert(fullName, emailId, password);

        showInfo(Alert.AlertType.CONFIRMATION, owner, "Gratulacje! Rejestracja zakończona pomyślnie.", "Aby uzyskać dostęp do swojego konta zaloguj się. ");

        Parent root = FXMLLoader.load((getClass().getResource("home.fxml")));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();

    }

    @FXML
    public void login(ActionEvent event) throws SQLException, IOException {

        Window owner = submitButtonSI.getScene().getWindow();

        if (emailFieldSI.getText().isEmpty()) {
            showInfo(Alert.AlertType.ERROR, owner, "Błąd formularza!", "Wprowadź swój adres email.");
            return;
        }
        if (passwordFieldSI.getText().isEmpty()) {
            showInfo(Alert.AlertType.ERROR, owner, "Błąd formularza!", "Wprowadź swoje hasło.");
            return;
        }

        String emailId = emailFieldSI.getText();
        String password = passwordFieldSI.getText();

        JdbcDao jdbcDao = new JdbcDao();
        boolean flag = jdbcDao.validate(emailId, password);

        if (!flag) {
            showInfo(Alert.AlertType.ERROR, owner, "Nieprawidłowe dane", "Spróbuj wprowadzić swoje dane ponownie.");
        } else {
            FXMLLoader fxmlLoader = new FXMLLoader(ApplicationGUI.class.getResource("panel.fxml"));
            root = fxmlLoader.load();
            PanelControllerGUI panelControler = fxmlLoader.getController();
            currentWallet = wallets.get(org.apache.commons.codec.digest.DigestUtils.sha256Hex(emailId));
            panelControler.setText(emailId, Float.toString(currentWallet.getBalance()));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
            currentUser = emailId;

        }
    }

    public void transfer(ActionEvent event) throws IOException {
        Window owner = transferButton.getScene().getWindow();

        if (recipientField.getText().isEmpty()) {
            showInfo(Alert.AlertType.ERROR, owner, "Błąd formularza!", "Wprowadź adres email odbiorcy.");
            return;
        }
        if (amountField.getText().isEmpty()) {
            showInfo(Alert.AlertType.ERROR, owner, "Błąd formularza!", "Wprowadź kwotę przelewu.");
            return;
        }

        if (recipientField.getText() == currentUser) {
            showInfo(Alert.AlertType.ERROR, owner, "Błąd formularza!", "Nie można zrealizować przelewu na własne konto.");
            return;
        }

        String recipient = recipientField.getText();
        String amount = amountField.getText();


        if (wallets.get(org.apache.commons.codec.digest.DigestUtils.sha256Hex(recipient)) == null) {
            showInfo(Alert.AlertType.ERROR, owner, "Błąd formularza!", "Taki odbiorca nie istnieje.");
            return;
        }

        try {
            Float.parseFloat(amount);
            if (Float.parseFloat(amount) > currentWallet.getBalance()) {
                showInfo(Alert.AlertType.ERROR, owner, "Błąd formularza!", "Nie wystarczająca ilość środków.");
                return;
            }
        } catch (NumberFormatException ex) {
            showInfo(Alert.AlertType.ERROR, owner, "Błąd formularza!", "Nieprawidłowy format danych w polu \"Kwota\".");
            return;
        }

        ControllerGUI.showInfo(Alert.AlertType.CONFIRMATION, owner, "Sukces", "Przelew został zlecony");

        Wallet recipientWallet = ControllerGUI.wallets.get(org.apache.commons.codec.digest.DigestUtils.sha256Hex(recipient));
        Block block = new Block(ControllerGUI.currentBlock.hash);
        ControllerGUI.currentBlock = block;
        block.addTransaction(currentWallet.sendFunds(recipientWallet.publicKey, Float.parseFloat(amount)));
        MainChain.addBlock(block);
        MainChain.isChainValid();

        FXMLLoader fxmlLoader = new FXMLLoader(ApplicationGUI.class.getResource("panel.fxml"));
        root = fxmlLoader.load();
        PanelControllerGUI panelController = fxmlLoader.getController();
        panelController.setText(ControllerGUI.currentUser, Float.toString(ControllerGUI.currentWallet.getBalance()));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    public static void showInfo(Alert.AlertType alertType, Window owner, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(owner);
        alert.show();
    }

    public void switchToHome(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load((getClass().getResource("home.fxml")));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    public void switchToRegistration(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load((getClass().getResource("registration.fxml")));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    public void switchToLogin(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load((getClass().getResource("login.fxml")));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    public void switchToPanel(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ApplicationGUI.class.getResource("panel.fxml"));
        root = fxmlLoader.load();
        PanelControllerGUI panelController = fxmlLoader.getController();
        panelController.setText(ControllerGUI.currentUser, Float.toString(ControllerGUI.currentWallet.getBalance()));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();

    }

}
