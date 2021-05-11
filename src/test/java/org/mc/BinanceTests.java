package org.mc;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.general.ExchangeInfo;
import com.binance.api.client.domain.market.BookTicker;
import io.contek.invoker.binancedelivery.api.ApiFactory;
import io.contek.invoker.binancedelivery.api.rest.market.GetExchangeInfo;
import io.contek.invoker.binancedelivery.api.rest.market.GetTickerBookTicker;
import io.contek.invoker.binancedelivery.api.rest.market.MarketRestApi;
import org.junit.Test;
import org.mc.adapters.binance.BinanceInstrumentSource;

import java.util.Date;
import java.util.List;

public class BinanceTests {

    @Test
    public void testGetInstruments() {
        var instrumentSource = new BinanceInstrumentSource();

        var instrumentsBySymbol = instrumentSource.getInstrumentsBySymbol();
        System.out.println(instrumentsBySymbol);
    }

    @Test
    public void fetchBinanceTickers() {
        MarketRestApi api = ApiFactory.getMainNetDefault().rest().market();
        GetTickerBookTicker.Response response = api.getTickerBookTicker().submit();

        for (var entry : response.stream().toList()) {
            System.out.printf("%s - Bid: %f Ask: %f%n", entry.symbol, entry.bidPrice, entry.askPrice);
        }
    }

    @Test
    public void fetchBinanceInverseFuturesSymbols() {
        MarketRestApi api = ApiFactory.getMainNetDefault().rest().market();
        GetExchangeInfo.Response response = api.getExchangeInfo().submit();

        for (var entry : response.symbols.stream().toList()) {
            System.out.printf("Symbol: %s Base: %s Quote: %s Contract type: %s Underlying type: %s Delivery date: %s%n",
                    entry.symbol, entry.baseAsset, entry.quoteAsset, entry.contractType, entry.underlyingType, new Date(entry.deliveryDate));
        }
    }

    @Test
    public void fetchBinanceLinearFuturesSymbols() {
        var api = io.contek.invoker.binancefutures.api.ApiFactory.getMainNetDefault().rest().market();
        var response = api.getExchangeInfo().submit();

        for (var entry : response.symbols.stream().filter(s -> s.symbol.contains("_")).toList()) {
            System.out.printf("Symbol: %s Base: %s Quote: %s Extra: %s%n",
                    entry.symbol, entry.baseAsset, entry.quoteAsset, entry.filters);
        }
    }

    @Test
    public void fetchBinanceSpotTickers() {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance();
        BinanceApiRestClient client = factory.newRestClient();

        List<BookTicker> allBookTickers = client.getBookTickers();
        System.out.println(allBookTickers);
    }

    @Test
    public void fetchBinanceSpotSymbols() {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance();
        BinanceApiRestClient client = factory.newRestClient();

        ExchangeInfo exchangeInfo = client.getExchangeInfo();

        System.out.println(exchangeInfo.getSymbols());
    }
}
