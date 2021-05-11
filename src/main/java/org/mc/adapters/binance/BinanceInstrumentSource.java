package org.mc.adapters.binance;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.general.ExchangeInfo;
import io.contek.invoker.binancedelivery.api.ApiFactory;
import io.contek.invoker.binancedelivery.api.rest.market.GetExchangeInfo;
import io.contek.invoker.binancedelivery.api.rest.market.MarketRestApi;
import io.vavr.Tuple2;
import org.mc.instruments.InstrumentSource;
import org.mc.instruments.Instrument;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BinanceInstrumentSource implements InstrumentSource {

    @Override
    public Map<String, Instrument> getInstrumentsBySymbol() {
        return Stream.concat(getInverseFuturesInstrumentStream(),
                getSpotInstrumentStream()).collect(Collectors.toMap(Tuple2::_1, Tuple2::_2));
    }

    private Stream<Tuple2<String, Instrument>> getInverseFuturesInstrumentStream() {
        MarketRestApi api = ApiFactory.getMainNetDefault().rest().market();
        GetExchangeInfo.Response response = api.getExchangeInfo().submit();

        // Filter out perpetuals so we're just left with COIN futures
        return response.symbols.stream().filter(s -> !s.contractType.equals("PERPETUAL"))
                .map(s -> new Tuple2<>(s.symbol, new Instrument(s.baseAsset, s.quoteAsset,
                        LocalDate.ofInstant(new Date(s.deliveryDate).toInstant(), ZoneId.systemDefault()))));
    }

    private Stream<Tuple2<String, Instrument>> getSpotInstrumentStream() {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance();
        BinanceApiRestClient client = factory.newRestClient();

        ExchangeInfo exchangeInfo = client.getExchangeInfo();

        return exchangeInfo.getSymbols().stream()
                .map(s -> new Tuple2<>(s.getSymbol(), new Instrument(s.getBaseAsset(), s.getQuoteAsset())));
    }
}
