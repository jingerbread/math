package com.jingerbread.math;


import javacalculus.core.CALC;
import javacalculus.core.CalcParser;
import javacalculus.struct.CalcObject;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;

import static com.jingerbread.math.Utils.calc;
import static com.jingerbread.math.Utils.getValue;
import static com.jingerbread.math.Utils.parseInterval;

@Slf4j
public class Int {

    public void calculateInt() throws Exception {
        log.info("Enter expression, note use uppercase function names: ");
        Scanner in = new Scanner(System.in);
        String expression = in.nextLine();
        log.info("Integrate with respect to:");
        String variable = in.nextLine();

        // differentiate
        String command = "INT(" + expression + ", " + variable + ")";
        CalcParser parser = new CalcParser();
        CalcObject parsed = parser.parse(command);
        CalcObject intFunction = parsed.evaluate();

        // compute numerical value
        intFunction = CALC.SYM_EVAL(intFunction);

        log.info("Result:");
        log.info(intFunction.toString());

        log.info("Enter interval [0, 1]");
        String intervalInput = in.nextLine();
        Pair<Double, Double> interval = parseInterval(intervalInput);
        log.info("Entered interval: {}", interval);

        CalcObject F_a = calc(intFunction, "x", interval.getKey());
        CalcObject F_b = calc(intFunction, "x", interval.getValue());
        double f_a = getValue(CALC.SYM_EVAL(F_a).toString());
        double f_b = getValue(CALC.SYM_EVAL(F_b).toString());
        log.info("Calculate integral on interval {}: {}", interval, f_b - f_a);

        log.info("Enter step of numeric integration");
        double dx = Double.valueOf(in.nextLine());

        CalcObject function = new CalcParser().parse(expression).evaluate();
        double s = 0;
        for (double i = interval.getKey(); i <= interval.getValue(); i += dx) {
            Double f = Double.parseDouble(CALC.SYM_EVAL(calc(function, "x", i + dx/2)).toString());
            double ds = f * dx;
            s += ds;
        }
        log.info("Numeric calculate integral on interval [{}]: {}", interval, s);
    }
}
