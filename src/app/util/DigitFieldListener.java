package app.util;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

public class DigitFieldListener implements ChangeListener<String> {
    TextField field;

    public DigitFieldListener(TextField field) {
        this.field = field;
    }

    @Override
    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        if (!field.getText().matches("^\\d*\\.\\d+|\\d+\\.\\d*|\\d*")) {
            field.setText(oldValue);
        }
    }
}