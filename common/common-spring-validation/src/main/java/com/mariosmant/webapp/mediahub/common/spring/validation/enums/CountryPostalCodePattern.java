package com.mariosmant.webapp.mediahub.common.spring.validation.enums;

public enum CountryPostalCodePattern {

    US("US", "^[0-9]{5}(?:-[0-9]{4})?$"),
    CA("CA", "^[A-Za-z]\\d[A-Za-z][ -]?\\d[A-Za-z]\\d$"),
    GB("GB", "^[A-Z]{1,2}[0-9][0-9A-Z]?[ ]?[0-9][A-Z]{2}$"),
    IE("IE", "^[A-Za-z]\\d{2}[A-Za-z0-9]{4}$"), // Ireland Eircode

    DE("DE", "^[0-9]{5}$"),
    FR("FR", "^[0-9]{5}$"),
    IT("IT", "^[0-9]{5}$"),
    ES("ES", "^[0-9]{5}$"),
    PT("PT", "^[0-9]{4}-[0-9]{3}$"),
    NL("NL", "^[0-9]{4}[ ]?[A-Za-z]{2}$"),
    BE("BE", "^[0-9]{4}$"),
    AT("AT", "^[0-9]{4}$"),
    CH("CH", "^[0-9]{4}$"),
    GR("GR", "^[0-9]{5}$"),
    DK("DK", "^[0-9]{4}$"),
    SE("SE", "^[0-9]{3}[ ]?[0-9]{2}$"),
    NO("NO", "^[0-9]{4}$"),
    FI("FI", "^[0-9]{5}$"),
    PL("PL", "^[0-9]{2}-[0-9]{3}$"),
    CZ("CZ", "^[0-9]{3}[ ]?[0-9]{2}$"),
    SK("SK", "^[0-9]{3}[ ]?[0-9]{2}$"),
    HU("HU", "^[0-9]{4}$"),
    SI("SI", "^[0-9]{4}$"),
    HR("HR", "^[0-9]{5}$"),
    RO("RO", "^[0-9]{6}$"),
    BG("BG", "^[0-9]{4}$"),
    RS("RS", "^[0-9]{5}$"),
    BA("BA", "^[0-9]{5}$"),
    MK("MK", "^[0-9]{4}$"),
    AL("AL", "^[0-9]{4}$"),
    TR("TR", "^[0-9]{5}$"),

    RU("RU", "^[0-9]{6}$"),
    UA("UA", "^[0-9]{5}$"),

    CN("CN", "^[0-9]{6}$"),
    JP("JP", "^[0-9]{3}-[0-9]{4}$"),
    KR("KR", "^[0-9]{5}$"),
    IN("IN", "^[0-9]{6}$"),
    SG("SG", "^[0-9]{6}$"),
    MY("MY", "^[0-9]{5}$"),
    TH("TH", "^[0-9]{5}$"),
    PH("PH", "^[0-9]{4}$"),
    VN("VN", "^[0-9]{6}$"),

    AU("AU", "^[0-9]{4}$"),
    NZ("NZ", "^[0-9]{4}$");

    private final String countryCode;
    private final String regex;

    CountryPostalCodePattern(String countryCode, String regex) {
        this.countryCode = countryCode;
        this.regex = regex;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getRegex() {
        return regex;
    }
}
