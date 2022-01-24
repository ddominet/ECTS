module com.example.ecoin {
    requires javafx.controls;
    requires javafx.fxml;

    requires java.sql;
    requires org.apache.commons.codec;
    requires org.bouncycastle.provider;
    requires json.simple;
    requires com.fasterxml.jackson.databind;
    requires gson;

    opens com.example.ecoin to javafx.fxml;
    exports com.example.ecoin;
}