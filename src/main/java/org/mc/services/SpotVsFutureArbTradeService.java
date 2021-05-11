package org.mc.services;

import org.mc.marketdata.Quote;
import org.mc.marketdata.SpotVsFutureArbTrade;
import org.mc.marketdata.MarketDataSource;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;

public class SpotVsFutureArbTradeService {

    private final MarketDataSource[] marketDataSources;

    private final Stream<SpotVsFutureArbTrade> InvalidTradeStream =
            Arrays.stream(new SpotVsFutureArbTrade[] { SpotVsFutureArbTrade.Invalid });

    public SpotVsFutureArbTradeService(MarketDataSource... marketDataSources) {
        this.marketDataSources = marketDataSources;
    }

    public List<SpotVsFutureArbTrade> getSortedTrades(int minDaysToExpiry) {
        var allQuotes = Arrays.stream(marketDataSources).parallel().flatMap(mds -> mds.getQuotes().stream()).toList();

        var spotQuotesByPair = allQuotes.stream().filter(q -> !q.instrument().isFuture())
                .collect(groupingBy(q -> q.instrument().getPair()));

        var futuresQuotes = allQuotes.stream().filter(q -> q.instrument().isFuture());

        return futuresQuotes.flatMap(f -> {
            var spotQuotes = spotQuotesByPair.get(f.instrument().getPair());

            return spotQuotes != null ? spotQuotes.stream().flatMap(s -> {
                int daysToExpiry = (int) Duration.between(LocalDate.now().atStartOfDay(),
                        f.instrument().expiryDate().atStartOfDay()).toDays();

                // Simple way of allowing for contango or backwardation (though borrowing costs for
                // short-selling are not considered for simplicity)
                return Arrays.stream(new SpotVsFutureArbTrade[] {
                        new SpotVsFutureArbTrade(s, f, daysToExpiry),
                        new SpotVsFutureArbTrade(f, s, daysToExpiry)
                });
            }) : InvalidTradeStream;

        }).sorted((t1, t2) -> Double.compare(t2.getAnnualisedPremium(), t1.getAnnualisedPremium()))
                .filter(ar -> ar.isValid() && ar.daysToExpiry() >= minDaysToExpiry).toList();
    }
}
