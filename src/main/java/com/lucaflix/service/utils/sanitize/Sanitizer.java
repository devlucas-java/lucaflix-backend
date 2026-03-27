package com.lucaflix.service.utils.sanitize;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

public class Sanitizer {

    public static String sanitize(String input) {
        if (input == null) return null;
        return Jsoup.clean(input, Safelist.none());
    }
}