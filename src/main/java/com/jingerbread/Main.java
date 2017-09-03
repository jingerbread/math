package com.jingerbread;


import com.jingerbread.math.Chart;
import com.jingerbread.math.Diff;
import com.jingerbread.math.Int;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.util.List;
import java.util.Scanner;

@Slf4j
public class Main {

    public static void main(String[] args)  {
        Scanner in = new Scanner(System.in);
        boolean end;
        do {
            log.info("Choose differentiate or integrate, enter diff or int");
            String choose = in.nextLine();
            switch (choose.toLowerCase()) {
                case "int":
                    integrate();
                    break;
                case "diff":
                    differentiate();
                    break;
                default:
                    log.error("unsupported operation. exit");
                    return;
            }
            log.info("If you want to exit: enter yes");
            String exit = in.nextLine();
            end = exit.equalsIgnoreCase("Yes");
        } while (!end);
    }

    public static void integrate() {
        Int integrator = new Int();
        try {
            integrator.calculateInt();
        } catch (Exception e) {
            log.error("Calculation failed: ", e);
        }
    }

    public static void differentiate() {
        final Chart chart = new Chart();
        Diff diff = new Diff();
        try {
            List<Pair<Double, Double>> data = diff.calculateDiff();
            chart.initChart("y", data);
            SwingUtilities.invokeLater(() -> {
                chart.setVisible(true);
            });
        } catch (Exception e) {
            log.error("Calculation failed: ", e);
        }
    }
}
