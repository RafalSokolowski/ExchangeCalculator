package pl.rav.currencies;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

public class ReadJSON {

    private final String key;
    private final String writePath;
    private final Map<String, Double> currencies; // currency code , exchange to EUR (free of charges version accepts EUR as base currency only)

    public ReadJSON(String key, String writePath) {
        this.key = key;
        this.writePath = writePath;
        this.currencies = new HashMap<>();
    }

    private Optional<URL> connectToURL() {
        try {
            URL url = new URL("http://data.fixer.io/api/latest?access_key=" + key);
            return Optional.of(url);
        } catch (MalformedURLException e) {
            Util.logger.error("can not access to the FIXER.IO service... double check the url adress and access key (" + e.getMessage() + ")");
            return Optional.empty();
        }
    }

    private Optional<String> readJsonToString() {
        Optional<URL> url = connectToURL();
        if (connectToURL().isEmpty()) {
            Util.logger.error("URL is empty... verify the data");
            return Optional.empty(); // czy może lepiej tu throw exception ?
        }
        try {
            Scanner scanner = new Scanner(url.get().openStream()); // dlaczego tu podświetla get() ? ... przecież parę linijek wyżej jest sprawdzenie isEmpty()
            StringBuilder stringBuilderJSON = new StringBuilder();
            while (scanner.hasNext()) stringBuilderJSON.append(scanner.next());
            return Optional.of(stringBuilderJSON.toString());

        } catch (IOException e) {
            Util.logger.error("cannot open stream with data from fixer.io (" + e.getMessage() + ")");
            return Optional.empty(); // czy może lepiej tu throw exception ?
        }
    }

    public Map<String, Double> collectNestedMapFromJson() {
        Optional<String> jsonString = readJsonToString();
        if (jsonString.isEmpty())
            throw new IllegalArgumentException("Json was not read / decoded... check the data"); // czy może tu tylko return false;

        Util.logger.info(Util.ANSI_BLUE + "Received JSON: " + Util.ANSI_RESET + jsonString.get());

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode jsonNode = objectMapper.readValue(jsonString.get(), JsonNode.class);
            JsonNode jsonTreeRates = jsonNode.get("rates");
            Util.logger.info(Util.ANSI_BLUE + "Read nested JSON rates: " + Util.ANSI_RESET + jsonTreeRates);

            currencies.putAll(objectMapper.treeToValue(jsonTreeRates, Map.class)); // unchecked cast from Map to Map<String, Double> - jak to rozwiązać ?
            return currencies;

        } catch (JsonProcessingException e) {
            Util.logger.error("cannot access to nested json data (" + e.getMessage() + ")");
            throw new IllegalArgumentException (e.getMessage());
        }
    }

    public boolean writeCurrenciesToFile(String fileName) {
        if (currencies.isEmpty()) {
            Util.logger.error("currencies collection is empty... check loading JSON");
            return false;
        }

        File fileCurrencies = new File(writePath + "//" + fileName);
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(fileCurrencies, currencies);
            return true;
        } catch (IOException e) {
            Util.logger.error("cannot write to file (" + e.getMessage() + ")");
            return false;
        }
    }

    public boolean readCurrenciesFromFile(String fileName) {
        ObjectMapper mapper = new ObjectMapper();
        File fileCurrencies = new File(writePath + "//" + fileName);
        try {
            currencies.clear();
            currencies.putAll(mapper.readValue(fileCurrencies, Map.class));
            return true;
        } catch (IOException e) {
            Util.logger.error("cannot read the file from " + fileCurrencies.getAbsolutePath() + " (" + e.getMessage() + ")");
            return false;
        }
    }

    public Map<String, Double> getCurrencies() {
        return new HashMap<>(currencies);
    }
}
