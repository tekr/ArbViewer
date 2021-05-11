package org.mc.adapters.bybit;

import io.contek.invoker.bybit.api.ApiFactory;
import io.contek.invoker.bybit.api.rest.market.GetTickers;
import io.contek.invoker.bybit.api.rest.market.MarketRestApi;
import org.mc.instruments.Instrument;
import org.mc.instruments.InstrumentSource;
import org.mc.marketdata.MarketDataSource;
import org.mc.marketdata.Quote;
import org.mc.marketdata.Exchange;

import java.util.List;
import java.util.Map;

public class BybitMarketDataSource implements MarketDataSource {

    private final Map<String, Instrument> instrumentsBySymbol;

    public BybitMarketDataSource(InstrumentSource instrumentSource) {
        instrumentsBySymbol = instrumentSource.getInstrumentsBySymbol();
    }

    @Override
    public List<Quote> getQuotes() {
        MarketRestApi api = ApiFactory.getMainNetDefault().rest().market();
        GetTickers.Response response = api.getTickers().submit();

        return response.result.stream().filter(t -> instrumentsBySymbol.containsKey(t.symbol)).map(t ->
                new Quote(instrumentsBySymbol.get(t.symbol), Double.parseDouble(t.bid_price),
                        Double.parseDouble(t.ask_price), Exchange.Bybit)).toList();
    }
}
