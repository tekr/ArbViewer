package org.mc.instruments;

import java.util.Map;

public interface InstrumentSource {

    Map<String, Instrument> getInstrumentsBySymbol();
}
