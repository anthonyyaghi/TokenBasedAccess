package app;

import app.util.DigitFieldListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class MenuController {
    @FXML
    private TextField shortTxTime;

    @FXML
    private TextField longTxTime;

    @FXML
    private TextField interArrTime;

    @FXML
    private TextField timeOutTime;

    @FXML
    private TextField txTime;

    @FXML
    private TextField switchOverTime;

    @FXML
    private CheckBox simpleTime;

    @FXML
    private CheckBox csvCheckBox;

    @FXML
    private ComboBox<Integer> nbQCombo;

    @FXML
    private TextField simDurationText;

    @FXML
    private ComboBox<Integer> tokenHolderCombo;

    @FXML
    private TextField initialValuesText;

    @FXML
    private CheckBox randomInitValues;

    @FXML
    void initialize() {
        nbQCombo.getItems().addAll(IntStream.range(2, 11).boxed().collect(Collectors.toList()));
        nbQCombo.getSelectionModel().select(0);
        nbQChanged();

        simDurationText.textProperty().addListener(new DigitFieldListener(simDurationText));
        interArrTime.textProperty().addListener(new DigitFieldListener(interArrTime));
        shortTxTime.textProperty().addListener(new DigitFieldListener(shortTxTime));
        longTxTime.textProperty().addListener(new DigitFieldListener(longTxTime));
        txTime.textProperty().addListener(new DigitFieldListener(txTime));
        timeOutTime.textProperty().addListener(new DigitFieldListener(timeOutTime));
        switchOverTime.textProperty().addListener(new DigitFieldListener(switchOverTime));
    }

    @FXML
    void nbQChanged() {
        tokenHolderCombo.getItems().clear();
        tokenHolderCombo.getItems().addAll(IntStream.range(0, nbQCombo.getSelectionModel().getSelectedItem()).boxed().collect(Collectors.toList()));
        tokenHolderCombo.getSelectionModel().select(0);
    }

    @FXML
    void startSimulation() {
        if (!settingsSet()) {
            displayWarning("Missing time values.");
            return;
        }
        Parent root;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("sim.fxml"));
            root = loader.load();

            SimController controller = loader.getController();

            SimulationSettings settings = new SimulationSettings();

            settings.setReplayDur(Double.parseDouble(simDurationText.getText()));
            settings.setNbQ(nbQCombo.getValue());
            settings.setTokenHolder(tokenHolderCombo.getValue());
            settings.setGenCsv(csvCheckBox.isSelected());
            settings.setInterArrTime(Double.parseDouble(interArrTime.getText()));
            settings.setTimeOutTime(Double.parseDouble(timeOutTime.getText()));
            settings.setSwitchOverTime(Double.parseDouble(switchOverTime.getText()));
            settings.setSimpleTime(simpleTime.isSelected());
            if (simpleTime.isSelected()) {
                settings.setTxTime(Double.parseDouble(txTime.getText()));
            } else {
                settings.setShortTxTime(Double.parseDouble(shortTxTime.getText()));
                settings.setLongTxTime(Double.parseDouble(longTxTime.getText()));
            }

            double[] initArr = getInitArr();
            if (!randomInitValues.isSelected() && initArr == null) {
                displayWarning("Invalid format for initial arrival clocks\n" +
                        "Starting simulation with random values");
                randomInitValues.setSelected(true);
            }else if (initArr!= null && initArr.length != nbQCombo.getValue()) {
                displayWarning("The number of arrival clocks must match the number of queues.\n" +
                        "Starting simulation with random values.");
                initArr = null;
                randomInitValues.setSelected(true);
            }
            settings.setInitArr(initArr);
            settings.setRandomInit(randomInitValues.isSelected());

            controller.load(settings);

            Stage stage = new Stage();
            stage.setTitle("Real-time Simulation");
            stage.setScene(new Scene(root, 800, 800));
            stage.setResizable(false);
            stage.setOnHidden(e -> controller.shutdown());
            stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private double[] getInitArr() {
        try {
            return Stream.of(initialValuesText.getText().split(",")).mapToDouble(v -> Double.parseDouble(v)).toArray();
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private boolean settingsSet() {
        if (interArrTime.getText().isEmpty()) return false;
        if (timeOutTime.getText().isEmpty()) return false;
        if (switchOverTime.getText().isEmpty()) return false;

        if (simpleTime.isSelected()) {
            if (txTime.getText().isEmpty()) return false;
        } else {
            if (shortTxTime.getText().isEmpty()) return false;
            if (longTxTime.getText().isEmpty()) return false;
        }

        return true;
    }

    private void displayWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
