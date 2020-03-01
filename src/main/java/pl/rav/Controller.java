package pl.rav;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import pl.rav.currencies.Currency;
import pl.rav.currencies.ReadJSON;
import pl.rav.currencies.Util;

import java.io.File;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Controller {

/*
 * Ogolen uwagi: gneralnie bardzo ładny kod, dobry podział wewnatrz klasowy na funkcje które są jednoznaczne, brakuje mi tutaj podizału MVC :/ nie jest on widoczny, bo praktycnzie go nie ma. 
 * w JavaFX moc Frameworka polega na MVC, Z klasą util sa jakies problemy, bo jest przygotowana w taki spsob ze osoba która wspiera kod, musi sobie sama reorganizowac importy. sprawia wrazenie ze brakuje jakiegos elementu
 * z którego korzystałes u siebie lokalnie. build path ? Generalnie oceniajac krytycznie dalbym 9/10 Calosc dziala bardzo fajnie, MVC jest wazny i stad taka niska nota! :D
 * Pozdrawiam ! 
 * 
 * 
 */
	
	
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

    @FXML
    private TextField keyValue;
    @FXML
    private TextArea messageBox;

    private Map<String, Double> currencies;

    public void initialize() {
//        ReadJSON readJSON = new ReadJSON(Util.KEY, Util.WRITE_PATH);
//        readJSON.collectNestedMapFromJson();
        try {
            readCurrencies(Util.KEY);
            setMessagesToJavaFX("Application was initialized and 170 currencies were uploaded", Util.LogType.INFO, "initialize");
        } catch (Exception e) {
            setMessagesToJavaFX("Application was NOT initialized, something went wrong... check the error details", Util.LogType.ERROR, e, "initialize");
        }


        currencyFrom.getItems().addAll(Util.CURRENCIES); // nie rozumiem tego warninga :(
        currencyTo.getItems().addAll(Util.CURRENCIES);   // nie rozumiem tego warninga :(

        /*
         * Możesz czytać Objects w Collection<?> taki sam sposób jak z Collection. 
         * Ale nie możesz dodać Objects do Collection<?>(kompilator zabrania tego), podczas gdy do Collection możesz.
         * Jeżeli po wydaniu Java 5 kompilator przetłumaczył każdy Collectiondo Collection<?>, 
         * a następnie kod napisany wcześniej nie byłoby już skompilować, a tym samym zniszczy 
         * kompatybilność wsteczną.
         */

    }

    private void readCurrencies(String key) {
        this.currencies = new ReadJSON(key, Util.WRITE_PATH).collectNestedMapFromJson();

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
    public void convertButtonClicked() {
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
        setMessagesToJavaFX(stringFrom + " was converted to " + stringTo, Util.LogType.INFO, "convertButtonClicked");

    }

    @FXML
    public void openURL() {
        try {
            java.awt.Desktop.getDesktop().browse(java.net.URI.create("https://fixer.io/documentation")); // zerÅ¼niÄ™te z StackOF :( ... jak to moÅ¼na w FX Å‚adnie ?
            setMessagesToJavaFX("API documentation was opened in your default web-browser... navigate there for more info", Util.LogType.INFO, "openURL");
        } catch (Exception e) {
        	
        	// Ja bym loggowaw ten sposób, a Logger utworzył na wstewpie kazdej klasy, tak jak Ty zrobiłes każdy contribbutor będzie musiał sam sobie importowac lib. 
//        	Logger log = Logger.getLogger(getClass().getName());
//        	log.log(Level.INFO, "Can not open fixer.io :/ (" + e.getMessage() + ")");
        	
            Util.logger.error("Can not open fixer.io :/ (" + e.getMessage() + ")");
            setMessagesToJavaFX("API documentation was not opened... check internet connection", Util.LogType.ERROR, "openURL");
        }
    }

    @FXML
    public void openFile() {
        try {
            java.awt.Desktop.getDesktop().open(new File("src/main/resources/pl/rav/type_of_currencies.txt")); // zerÅ¼niÄ™te z StackOF :( ... jak to moÅ¼na w FX Å‚adnie ?
            setMessagesToJavaFX("File was opened in txt format, check the \"type_of_currencies.txt\"", Util.LogType.INFO, "openFile");
        } catch (Exception e) {
            setMessagesToJavaFX("File was not opened... something wrong with the path", Util.LogType.ERROR, e, "openFile");
        }

    }

    @FXML
    private void keyChange() {
        try {
            readCurrencies(keyValue.getText());
            setMessagesToJavaFX("Key was changed successfully", Util.LogType.INFO, "keyChange");
        } catch (Exception e) {
            setMessagesToJavaFX("Something wrong with the key to the fixer.io service , value was NOT UPDATED... double check the key", Util.LogType.ERROR, e, "KeyChange");
        }
    }

    // nie wiem jak bezpoÅ›rednio uÅ¼yÄ‡ log4j2 do przekazywania info do TextArea w JavaFX (pewnie jakaÅ› konfiguracja log4j2.xml ?)
    private void setMessagesToJavaFX(String messageToPresent, Util.LogType typeOfMessage, Exception exception, String methodName) {
        messageBox.setText("");
        if ("info".equalsIgnoreCase(typeOfMessage.get())) {
            Util.logger.info(messageToPresent + " (" + exception.getMessage() + ")");
        } else if ("error".equalsIgnoreCase(typeOfMessage.get())) {
            Util.logger.error(messageToPresent + " (" + exception.getMessage() + ")");
        }
        messageBox.setText("[" + typeOfMessage + "] (" + methodName + "): " + messageToPresent + " (" + exception.getMessage() + ")");
    }

    private void setMessagesToJavaFX(String messageToPresent, Util.LogType typeOfMessage, String methodName) {
        messageBox.setText("");
        if ("info".equalsIgnoreCase(typeOfMessage.get())) {
            Util.logger.info(messageToPresent);
        } else if ("error".equalsIgnoreCase(typeOfMessage.get())) {
            Util.logger.error(messageToPresent);
        }
        messageBox.setText("[" + typeOfMessage + "] (" + methodName + "): " + messageToPresent);
    }
}
