package com.jingerbread;

import javacalculus.core.CALC;
import javacalculus.core.CalcParser;
import javacalculus.evaluator.CalcSUB;
import javacalculus.exception.CalcSyntaxException;
import javacalculus.struct.CalcDouble;
import javacalculus.struct.CalcObject;
import javacalculus.struct.CalcSymbol;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Slf4j
public class MathChart extends JFrame {

    private void initChart(String name, List<Pair<Double, Double>> data) {
        XYDataset dataset = generateDataSet(name, data);
        JFreeChart chart = createChart(name, dataset);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        chartPanel.setBackground(Color.white);
        add(chartPanel);

        pack();
        setTitle("Line chart");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private XYDataset generateDataSet(String intervalName, List<Pair<Double, Double>> data) {
        XYSeries series = new XYSeries(intervalName);
        data.forEach(p -> series.add(p.getKey(), p.getValue()));
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);

        return dataset;
    }

    private JFreeChart createChart(String name, XYDataset dataset) {

        JFreeChart chart = ChartFactory.createXYLineChart(
                name,
                "x",
                "y",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        XYPlot plot = chart.getXYPlot();

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.RED);
        renderer.setSeriesStroke(0, new BasicStroke(2.0f));

        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.white);

        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.BLACK);

        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.BLACK);

        chart.getLegend().setFrame(BlockBorder.NONE);

        chart.setTitle(new TextTitle(name,
                        new Font("Serif", java.awt.Font.BOLD, 18)
                )
        );

        return chart;

    }

    public static void main(String[] args)  {
        Scanner in = new Scanner(System.in);
        boolean end;
        do {
            try {
                calculateInt();
            } catch (Exception e) {
                log.error("Calculation failed: ", e);
            }
            log.info("If you want to exit: enter yes");
            String exit = in.nextLine();
            end = exit.equalsIgnoreCase("Yes");
        } while (!end);
    }

    private static void differentiate() {
        final MathChart mathChart = new MathChart();
        Scanner in = new Scanner(System.in);
        boolean end = true;
        do {
            try {
                List<Pair<Double, Double>> data = calculateDiff();
                mathChart.initChart("y", data);
                SwingUtilities.invokeLater(() -> {
                    mathChart.setVisible(true);
                });
                log.info("If you want to exit: enter yes");
                String exit = in.nextLine();
                end = exit.equalsIgnoreCase("Yes");
            } catch (Exception e) {
                log.error("Calculation failed: ", e);
            }
        } while(!end);
    }

    private static void calculateInt() throws Exception {
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

    /**
     * Get numeric value from ADD(numeric,C) string
     * @param valueWithC ADD(numeric,C)
     */
    private static double getValue(String valueWithC) {
        String patternString = "ADD\\((-?\\d\\.?\\d*),\\s?C\\)";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(valueWithC);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Can't parse interval " + valueWithC);
        }
        String start = matcher.group(1);

        return Double.valueOf(start);
    }

    private static List<Pair<Double, Double>> calculateDiff() throws Exception {
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
        Pair<Double, Double> interval = parseInterval(intervalInput);
        log.info("Entered interval: {}", interval);

        //calc function
        CalcObject function = new CalcParser().parse(expression).evaluate();
        List<Pair<Double, Double>> diffValues = new LinkedList<Pair<Double, Double>>();
        for (double i = interval.getKey(); i <= interval.getValue(); i += 0.1) {
            Double f1 = Double.parseDouble(CALC.SYM_EVAL(calc(function, "x", i)).toString());
            Double f0 = Double.parseDouble(CALC.SYM_EVAL(calc(function, "x", i - 0.01)).toString());
            diffValues.add(new Pair(i, (f1 - f0)/ 0.01));
        }

        log.info("Differentiate values {}", diffValues);

       return diffValues;
    }

    static Pair<Double, Double> parseInterval(String interval) {
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

    static CalcObject calc(CalcObject input, String var, double number) {
        CalcSymbol symbol = new CalcSymbol(var);
        CalcDouble value = new CalcDouble(number);
        return CalcSUB.numericSubstitute(input, symbol, value);
    }
}
