package org.mc.adapters.binance;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.market.BookTicker;
import io.contek.invoker.binancedelivery.api.ApiFactory;
import io.contek.invoker.binancedelivery.api.rest.market.GetTickerBookTicker;
import io.contek.invoker.binancedelivery.api.rest.market.MarketRestApi;
import org.mc.instruments.Instrument;
import org.mc.marketdata.Quote;
import org.mc.marketdata.Exchange;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class BinanceMarketDataSource implements org.mc.marketdata.MarketDataSource {

    private final Map<String, Instrument> instrumentsBySymbol;

    public BinanceMarketDataSource(BinanceInstrumentSource instrumentSource) {
        instrumentsBySymbol = instrumentSource.getInstrumentsBySymbol();
    }

    @Override
    public List<Quote> getQuotes() {
        return Stream.concat(getFuturesStream(), getSpotStream()).toList();
    }

    private Stream<Quote> getFuturesStream() {
        MarketRestApi api = ApiFactory.getMainNetDefault().rest().market();
        GetTickerBookTicker.Response response = api.getTickerBookTicker().submit();

        return response.stream().filter(t -> instrumentsBySymbol.containsKey(t.symbol))
                .map(t -> new Quote(instrumentsBySymbol.get(t.symbol), t.bidPrice, t.askPrice, Exchange.Binance));
    }

    private Stream<Quote> getSpotStream() {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance();
        BinanceApiRestClient client = factory.newRestClient();

        List<BookTicker> tickers = client.getBookTickers();

        return tickers.stream().filter(t -> instrumentsBySymbol.containsKey(t.getSymbol()))
                .map(t -> new Quote(instrumentsBySymbol.get(t.getSymbol()), Double.parseDouble(t.getBidPrice()),
                        Double.parseDouble(t.getAskPrice()), Exchange.Binance));
    }
}

