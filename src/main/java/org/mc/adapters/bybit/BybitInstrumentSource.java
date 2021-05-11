package org.mc.adapters.bybit;

import io.contek.invoker.bybit.api.ApiFactory;
import io.contek.invoker.bybit.api.rest.market.GetSymbols;
import io.contek.invoker.bybit.api.rest.market.MarketRestApi;
import org.mc.instruments.Instrument;
import org.mc.instruments.InstrumentSource;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Map;
import java.util.stream.Collectors;

public class BybitInstrumentSource implements InstrumentSource {

    @Override
    public Map<String, Instrument> getInstrumentsBySymbol() {
        MarketRestApi api = ApiFactory.getMainNetDefault().rest().market();
        GetSymbols.Response response = api.getSymbols().submit();

        // Filter out symbols that don't end with a digit (i.e. perpetuals)
        return response.result.stream().filter(t -> Character.isDigit(t.name.charAt(t.name.length() - 1)))
                .collect(Collectors.toMap(t -> t.name, t -> new Instrument(t.base_currency, t.quote_currency,
                        getExpiryDateFromSymbol(t.name))));
    }

    // The library used to access Bybit is missing the fields on its DTOs that convey futures expiry dates.
    // As a workaround, we can infer it from the expiry code based on a last-Friday-of-the-month rule.
    // Strategic fix: update library. Tactical fix: this!
    private LocalDate getExpiryDateFromSymbol(String symbol) {
        var year = 2000 + Integer.parseInt(symbol.substring(symbol.length() - 2));
        char monthCode = symbol.charAt(symbol.length() - 3);

        var month = switch (monthCode) {
            case 'H' -> 3;
            case 'M' -> 6;
            case 'U' -> 9;
            case 'Z' -> 12;
            default -> throw new IllegalStateException("Unexpected value: " + monthCode);
        };

        return LocalDate.of(year, month, 1).with(TemporalAdjusters.lastInMonth(DayOfWeek.FRIDAY));
    }
}
