package org.mc.marketdata;

public record SpotVsFutureArbTrade(Quote buyQuote, Quote sellQuote, int daysToExpiry) {
    private static final double DaysInYear = 365;

    public static final SpotVsFutureArbTrade Invalid = new SpotVsFutureArbTrade(null, null, 0);

    public double getBuyPrice() {
        return buyQuote.ask();
    }

    public double getSellPrice() {
        return sellQuote.bid();
    }

    public double getAnnualisedPremium() {
        return DaysInYear / daysToExpiry * getFlatPremium();
    }

    public double getFlatPremium() {
        return getSellPrice() / getBuyPrice() - 1;
    }

    public boolean isValid() {
        return daysToExpiry > 0 && getBuyPrice() > 0 && getSellPrice() > 0 && getFlatPremium() > 0;
    }

    @Override
    public String toString() {
        return "Buy " + buyQuote.instrument() +
                " on " + buyQuote.exchange() +
                " @ " + getBuyPrice() +
                ", sell " + sellQuote.instrument() +
                " on " + sellQuote.exchange() +
                " @ " + getSellPrice() +
                " Days to expiry: " + daysToExpiry +
                String.format(" Annualised return: %.1f%%", getAnnualisedPremium() * 100);
    }
}
