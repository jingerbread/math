package com.jingerbread.math;


import javacalculus.evaluator.CalcSUB;
import javacalculus.struct.CalcDouble;
import javacalculus.struct.CalcObject;
import javacalculus.struct.CalcSymbol;
import javafx.util.Pair;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static Pair<Double, Double> parseInterval(String interval) {
        String patternString = "\\[(-?\\d\\.?\\d*),\\s?(-?\\d\\.?\\d*)\\]";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(interval);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Can't parse interval " + interval);
        }
        String start = matcher.group(1);
        String end = matcher.group(2);

        return new Pair(Double.valueOf(start), Double.valueOf(end));
    }

    public static CalcObject calc(CalcObject input, String var, double number) {
        CalcSymbol symbol = new CalcSymbol(var);
        CalcDouble value = new CalcDouble(number);
        return CalcSUB.numericSubstitute(input, symbol, value);
    }

    /**
     * Get numeric value from ADD(numeric,C) string
     * @param valueWithC ADD(numeric,C)
     */
    public static double getValue(String valueWithC) {
        String patternString = "ADD\\((-?\\d\\.?\\d*),\\s?C\\)";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(valueWithC);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Can't parse interval " + valueWithC);
        }
        String start = matcher.group(1);

        return Double.valueOf(start);
    }
}
