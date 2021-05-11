package org.mc;

import org.junit.Test;
import org.mc.instruments.Instrument;
import org.mc.marketdata.Exchange;
import org.mc.marketdata.Quote;
import org.mc.marketdata.SpotVsFutureArbTrade;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class SpotVsFutureArbTradeTests {
    private Instrument spotInstrument = new Instrument("BTC", "USD");
    private Instrument futureInstrument = new Instrument("BTC", "USD", LocalDate.of(2021, 6, 25));

    @Test
    public void zeroPriceIsInvalid() {
        var trade = new SpotVsFutureArbTrade(
                new Quote(spotInstrument, 10, 0, Exchange.Binance),
                new Quote(futureInstrument, 10, 11, Exchange.Binance), 1);

        assertFalse(trade.isValid());

        trade = new SpotVsFutureArbTrade(
                new Quote(spotInstrument, 10, 10, Exchange.Binance),
                new Quote(futureInstrument, 0, 11, Exchange.Binance), 1);

        assertFalse(trade.isValid());
    }

    @Test
    public void expiryTodayIsInvalid() {
        var trade = new SpotVsFutureArbTrade(
                new Quote(spotInstrument, 10, 10, Exchange.Binance),
                new Quote(futureInstrument, 10, 11, Exchange.Binance), 0);

        assertFalse(trade.isValid());
    }

    @Test
    public void flatPremiumCalculation() {
        var trade = new SpotVsFutureArbTrade(
                new Quote(spotInstrument, 9, 10, Exchange.Binance),
                new Quote(futureInstrument, 15, 16, Exchange.Binance), 10);

        assertEquals(0.5, trade.getFlatPremium(), 1e-9);
    }

    @Test
    public void annualisedPremiumCalculation() {
        var trade = new SpotVsFutureArbTrade(
                new Quote(spotInstrument, 9, 10, Exchange.Binance),
                new Quote(futureInstrument, 11, 12, Exchange.Binance), 1);

        assertEquals(0.1 * 365, trade.getAnnualisedPremium(), 1e-9);

        trade = new SpotVsFutureArbTrade(
                new Quote(spotInstrument, 9, 10, Exchange.Binance),
                new Quote(futureInstrument, 15, 16, Exchange.Binance), 25);

        assertEquals(0.5 * (365.0 / 25), trade.getAnnualisedPremium(), 1e-9);
    }
}
