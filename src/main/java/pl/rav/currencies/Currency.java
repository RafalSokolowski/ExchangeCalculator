package pl.rav.currencies;

public enum Currency {
    PLN("PLN"),
    EUR("EUR"),
    USD("USD"),
    CHF("CHF");

    private String currency;

    Currency(String currency) {
        this.currency = currency;
    }

    public String get() {
        return currency;
    }
}
