module com.cdomenech.gestorcomandas_ui {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;
    requires java.sql;
    requires javafx.graphics;
    requires javafx.base;

    opens com.cdomenech.gestorcomandas_ui to javafx.fxml;
    exports com.cdomenech.gestorcomandas_ui;
}
