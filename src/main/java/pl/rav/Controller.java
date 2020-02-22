package pl.rav;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import pl.rav.currencies.Currency;
import pl.rav.currencies.ReadJSON;
import pl.rav.currencies.Util;

import java.util.Map;

public class Controller {

    @FXML
    private TextField formEURtoPLN;
    @FXML
    private TextField formUSDtoPLN;
    @FXML
    private TextField formCHFtoPLN;
    @FXML
    private Button convertButton;

    @FXML
    private ComboBox currencyFrom;
    @FXML
    private ComboBox currencyTo;
    @FXML
    private TextField valueConverted;

    private Map<String, Double> currencies = new ReadJSON(Util.KEY, Util.WRITE_PATH).collectNestedMapFromJson();

    public void initialize() {
//        ReadJSON readJSON = new ReadJSON(Util.KEY, Util.WRITE_PATH);
//        readJSON.collectNestedMapFromJson();
        double valuePLN = currencies.get(Currency.PLN.get());
        double valueUSD = currencies.get(Currency.USD.get());
        double valueCHF = currencies.get(Currency.CHF.get());

        double valueEURinPLN = valuePLN;
        double valueUSDinPLN = valuePLN / valueUSD;
        double valueCHFinPLN = valuePLN / valueCHF;

        formEURtoPLN.setText(Double.toString(valueEURinPLN));
        formUSDtoPLN.setText(Double.toString(valueUSDinPLN));
        formCHFtoPLN.setText(Double.toString(valueCHFinPLN));


    }

    @FXML
    public void convertButtonClicked(ActionEvent event) {
        String stringFrom = currencyFrom.getValue().toString();
        String stringTo = currencyTo.getValue().toString();

        double valueFrom;
        double valueTo;

        if ("EUR".equals(stringFrom)) {
            valueFrom = 1.0;
            valueTo = currencies.get(stringTo);
        } else if ("EUR".equals(stringTo)) {
            valueFrom = currencies.get(stringFrom);
            valueTo = 1.0;
        } else {
            valueFrom = currencies.get(stringFrom);
            valueTo = currencies.get(stringTo);
        }
        valueConverted.setText(Double.toString(valueTo / valueFrom));

    }

}
