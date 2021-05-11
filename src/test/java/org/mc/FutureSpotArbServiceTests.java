package org.mc;

import org.junit.Test;
import org.mc.adapters.binance.BinanceInstrumentSource;
import org.mc.adapters.binance.BinanceMarketDataSource;
import org.mc.adapters.bybit.BybitInstrumentSource;
import org.mc.adapters.bybit.BybitMarketDataSource;
import org.mc.marketdata.MarketDataSource;
import org.mc.services.SpotVsFutureArbTradeService;

public class FutureSpotArbServiceTests {

    MarketDataSource[] marketDataSources = new MarketDataSource[] {
            new BinanceMarketDataSource(new BinanceInstrumentSource()),
            new BybitMarketDataSource(new BybitInstrumentSource())
    };

    @Test
    public void getArbTrades() {
        var futureSpotArbService = new SpotVsFutureArbTradeService(marketDataSources);

        var results = futureSpotArbService.getSortedTrades(1);

        for (var entry : results) {
            System.out.println(entry);
        }
    }
}
