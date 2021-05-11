package org.mc;

import io.contek.invoker.bybit.api.ApiFactory;
import io.contek.invoker.bybit.api.rest.market.GetSymbols;
import io.contek.invoker.bybit.api.rest.market.GetTickers;
import io.contek.invoker.bybit.api.rest.market.MarketRestApi;
import org.junit.Test;
import org.mc.adapters.bybit.BybitInstrumentSource;

public class BybitTests {

    @Test
    public void getSymbols() {
        MarketRestApi api = ApiFactory.getMainNetDefault().rest().market();
        GetSymbols.Response response = api.getSymbols().submit();

        for (var entry : response.result) {
            System.out.printf("Name: %s Base: %s Quote: %s%n", entry.name, entry.base_currency, entry.quote_currency);
        }
    }

    @Test
    public void getTickers() {
        MarketRestApi api = ApiFactory.getMainNetDefault().rest().market();
        GetTickers.Response response = api.getTickers().submit();

        for (var entry : response.result) {
            System.out.printf("%s %s%n", entry.symbol, entry.next_funding_time);
        }
    }

    @Test
    public void getTickersForSymbols() {
        MarketRestApi api = ApiFactory.getMainNetDefault().rest().market();
        GetTickers.Response response = api.getTickers().submit();

        var futures = response.result.stream().filter(t -> t.next_funding_time.isBlank()).toList();

        for (var entry : futures) {
            System.out.printf("%s %s%n", entry.symbol, entry.next_funding_time);
        }
    }

    @Test
    public void testGetInstruments() {
        var source = new BybitInstrumentSource();
        var result = source.getInstrumentsBySymbol();

        System.out.println(result);
    }
}
