package org.mc.instruments;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public record Instrument(String baseCurrency, String quoteCurrency, LocalDate expiryDate) {

    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Instrument(String baseCurrency, String quoteCurrency) {
        this(baseCurrency, quoteCurrency, null);
    }

    public boolean isFuture() {
        return expiryDate != null;
    }

    // Treat all USD stable-coins as exactly equivalent to fiat. A *very* simplifying assumption..
    public String getPair() { return baseCurrency + "/" + (quoteCurrency.contains("USD") ? "USD" : quoteCurrency); }

    @Override
    public String toString() {
        return (isFuture() ? "Future" : "Spot") + " " + baseCurrency + "/" + quoteCurrency +
                (isFuture() ? " " + expiryDate.format(dateFormat) : "");
    }
}
