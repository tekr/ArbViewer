package org.mc.marketdata;

import org.mc.instruments.Instrument;

public record Quote(Instrument instrument, double bid, double ask, Exchange exchange) {
}
