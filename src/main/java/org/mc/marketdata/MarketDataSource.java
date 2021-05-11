package org.mc.marketdata;

import java.util.List;

public interface MarketDataSource {

    List<Quote> getQuotes();
}
