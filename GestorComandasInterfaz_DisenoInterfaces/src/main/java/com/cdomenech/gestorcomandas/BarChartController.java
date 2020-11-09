package com.cdomenech.gestorcomandas;

import com.cdomenech.database.GestorComandas;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Cristina Domenech Moreno, Javier Torres Sevilla
 */
public class BarChartController implements Initializable {

    @FXML
    private BarChart<String, Double> barChart;

    private XYChart.Series<String, Double> series;
    GestorComandas DB;
    @FXML
    private Button btnComandasScene;
    @FXML
    private Button btnMetricasScene;
    @FXML
    private Button btnAcercaDe;

    public BarChartController() throws IOException {
        this.DB = new GestorComandas();
    }
    /**
     * Set the data in the Bar Chart
     */
    private void updateChart() {
        try {
            series = DB.getBarChartData();
            barChart.getData().add(series);
        } catch (IOException ex) {
            Logger.getLogger(BarChartController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        updateChart();
    }
    /**
     * Method that switch to Comanda Scene
     * 
     * @param event 
     */
    @FXML
    private void switchComandaScene(ActionEvent event) {
        try {
            App.setRoot("allOrderTableView");
        } catch (IOException ex) {
            Logger.getLogger(AllOrderTableController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void switchMetricasScene(ActionEvent event) {
    }
    /**
     * Method that open "Acerca de" window
     * 
     * @param event 
     */
    @FXML
    private void acercaDeWindow(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("acercaDeView.fxml"));

            Parent root = fxmlLoader.load();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setResizable(false); // perfect size -> 495,750
            stage.setTitle("Acerca de...");
            Image image = new Image("images/restaurant.png");
            stage.getIcons().add(image);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException ex) {
            Logger.getLogger(AllOrderTableController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
