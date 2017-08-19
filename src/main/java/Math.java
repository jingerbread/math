import com.sun.tools.javac.util.Pair;
import javacalculus.core.CALC;
import javacalculus.core.CalcParser;
import javacalculus.evaluator.CalcSUB;
import javacalculus.struct.CalcDouble;
import javacalculus.struct.CalcObject;
import javacalculus.struct.CalcSymbol;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Slf4j
public class Math {

    public static void main(String[] args) throws Exception {
        String patternString = "\\[(\\d\\.?\\d*),\\s?(\\d\\.?\\d*)\\]";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher("[0.1, 0.9]");
        boolean start = matcher.matches();
        log.info("" + start + ": " + matcher.groupCount() + matcher.group(1));

        Math math = new Math();
        boolean end = true;
        do {
            try {
               end = math.calculate();
            } catch (Exception e) {
                log.error("Calculation failed: ", e);
            }
        } while(!end);
    }

    private boolean calculate() throws Exception {
        log.info("Enter expression, note use uppercase function names: ");
        Scanner in = new Scanner(System.in);
        String expression = in.nextLine();
        log.info("Differentiate with respect to:");
        String variable = in.nextLine();
        // differentiate
        String command = "DIFF(" + expression + ", " + variable + ")";
        CalcParser parser = new CalcParser();
        CalcObject parsed = parser.parse(command);
        CalcObject result = parsed.evaluate();

        // compute numerical value
        result = CALC.SYM_EVAL(result);

        log.info("Result:");
        log.info(result.toString());

        log.info("Enter interval [0, 1]");
        String intervalInput = in.nextLine();
        Pair<Double, Double> interval = parseInterval(intervalInput);
        log.info("Entered interval: {}", interval);

        CalcObject value = calc(result, "x", 0.0);
        log.info("Calc in x {}: {}", "0.0", CALC.SYM_EVAL(value));

        log.info("If you want to exit: enter yes");
        String exit = in.nextLine();
        return exit.equalsIgnoreCase("Yes");
    }

    static Pair<Double, Double> parseInterval(String interval) {
        String patternString = "\\[(\\d\\.?\\d*),\\s?(\\d\\.?\\d*)\\]";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(interval);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Can't parse interval " + interval);
        }
        String start = matcher.group(1);
        String end = matcher.group(2);

        return Pair.of(Double.valueOf(start), Double.valueOf(end));
    }

    static CalcObject calc(CalcObject input, String var, double number) {
        CalcSymbol symbol = new CalcSymbol(var);
        CalcDouble value = new CalcDouble(number);
        return CalcSUB.numericSubstitute(input, symbol, value);
    }
}
