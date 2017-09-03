package com.jingerbread.math;


import javacalculus.core.CALC;
import javacalculus.core.CalcParser;
import javacalculus.struct.CalcObject;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

@Slf4j
public class Diff {

    public List<Pair<Double, Double>> calculateDiff() throws Exception {
        log.info("Enter expression, note use uppercase function names: ");
        Scanner in = new Scanner(System.in);
        String expression = in.nextLine();
        log.info("Differentiate with respect to:");
        String variable = in.nextLine();

        // differentiate
        String command = "DIFF(" + expression + ", " + variable + ")";
        CalcParser parser = new CalcParser();
        CalcObject parsed = parser.parse(command);
        CalcObject diff = parsed.evaluate();

        // compute numerical value
        diff = CALC.SYM_EVAL(diff);

        log.info("Result:");
        log.info(diff.toString());

        log.info("Enter interval [0, 1]");
        String intervalInput = in.nextLine();
        Pair<Double, Double> interval = Utils.parseInterval(intervalInput);
        log.info("Entered interval: {}", interval);

        //calc function
        CalcObject function = new CalcParser().parse(expression).evaluate();
        List<Pair<Double, Double>> diffValues = new LinkedList<Pair<Double, Double>>();
        for (double i = interval.getKey(); i <= interval.getValue(); i += 0.1) {
            Double f1 = Double.parseDouble(CALC.SYM_EVAL(Utils.calc(function, "x", i)).toString());
            Double f0 = Double.parseDouble(CALC.SYM_EVAL(Utils.calc(function, "x", i - 0.01)).toString());
            diffValues.add(new Pair(i, (f1 - f0)/ 0.01));
        }

        log.info("Differentiate values {}", diffValues);

        return diffValues;
    }
}
