package tasks;

import interpolation.CubicInterpolation;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.data.xy.*;
import table.DataTable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RectangularShape;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestTask extends JFrame{

    private CubicInterpolation cubicInterpolation;

    XYDataset mainDataset;
    IntervalXYDataset mainDeltaDataset;
    XYDataset diffDataset;
    IntervalXYDataset diffDeltaDataset;
    XYDataset doubleDiffDataset;
    IntervalXYDataset doubleDiffDeltaDataset;

    public TestTask() {
        initUI();
    }

    private void initUI() {
        cubicInterpolation = new CubicInterpolation();

        JPanel p = new JPanel();

        createDataset();
        JFreeChart chart = createChart(mainDataset);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        chartPanel.setBackground(Color.white);

        JFreeChart chartDelta = createDeltaChartWithDiff(mainDeltaDataset);
        ChartPanel chartPanelDelta = new ChartPanel(chartDelta);
        chartPanelDelta.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        chartPanelDelta.setBackground(Color.white);

        JFreeChart chartDiff = createChartWithDiff(diffDataset);
        ChartPanel chartPanel2 = new ChartPanel(chartDiff);
        chartPanel2.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        chartPanel2.setBackground(Color.white);

        JFreeChart chartDeltaDiff = createDeltaChartWithDiff(diffDeltaDataset);
        ChartPanel chartPanel3 = new ChartPanel(chartDeltaDiff);
        chartPanel3.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        chartPanel3.setBackground(Color.white);

        JFreeChart chartDoubleDiff = createChartWithDiff(doubleDiffDataset);
        ChartPanel chartPanel4 = new ChartPanel(chartDoubleDiff);
        chartPanel4.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        chartPanel4.setBackground(Color.white);

        JFreeChart chartDeltaDoubleDiff = createDeltaChartWithDiff(doubleDiffDeltaDataset);
        ChartPanel chartPanel5 = new ChartPanel(chartDeltaDoubleDiff);
        chartPanel5.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        chartPanel5.setBackground(Color.white);

        JButton button1 = new JButton("Гравик сплайна и решения");
        button1.setActionCommand("График сплайна и решения");
        p.add(button1, BorderLayout.WEST);

        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                p.add(chartPanel, BorderLayout.NORTH);
                pack();
            }
        });

        JButton buttonDelta = new JButton("Гравик разности сплайна и решения");
        buttonDelta.setActionCommand("График разности сплайна и решения");
        p.add(buttonDelta, BorderLayout.WEST);

        buttonDelta.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                p.add(chartPanelDelta, BorderLayout.NORTH);
                pack();
            }
        });

        JButton button2 = new JButton("Гравик первых производных");
        button2.setActionCommand("График первых производных");
        p.add(button2, BorderLayout.EAST);

        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                p.add(chartPanel2);
                pack();
            }
        });

        JButton button3 = new JButton("Гравик разности первых производных");
        button3.setActionCommand("График разности первых производных");
        p.add(button3, BorderLayout.EAST);

        button3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                p.add(chartPanel3);
                pack();
            }
        });

        JButton button4 = new JButton("Гравик вторых производных");
        button4.setActionCommand("График вторых производных");
        p.add(button4, BorderLayout.EAST);

        button4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                p.add(chartPanel4);
                pack();
            }
        });

        JButton button5 = new JButton("Гравик разности вторых производных");
        button5.setActionCommand("График разности вторых производных");
        p.add(button5, BorderLayout.EAST);

        button5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                p.add(chartPanel5);
                pack();
            }
        });

        getContentPane().add(p);

        pack();
        setTitle("Line chart");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    private double diff(double x){
        if (x < 0) {
            return (3*Math.pow(x, 2) + 6 * x);
        } else {
            return (-3*Math.pow(x, 2) + 6 * x);
        }
    }

    private double doubleDiff(double x){
        if (x < 0) {
            return (6 * x + 6);
        } else {
            return (-6 * x + 6);
        }
    }

    private void createDataset() {

        var series1 = new XYSeries("The exact solution");
        List<Double> yListWithSmallStep = new ArrayList<>();
        for (double i = -1; round(i, 5) <= 1; i += 0.00001) {
            if (round(i, 5) < 0) {
                series1.add(i, leftFunction(i));
                yListWithSmallStep.add(leftFunction(i));
            } else {
                series1.add(i, rightFunction(i));
                yListWithSmallStep.add(rightFunction(i));
            }
        }

        List<Double> xList = new ArrayList<>();
        List<Double> yList = new ArrayList<>();

        for (double i = -1; round(i, 1) <= 1; i += 0.5) {
            if (i < 0) {
                xList.add(round(i, 1));
                yList.add(leftFunction(round(i, 1)));
            } else {
                xList.add(round(i, 1));
                yList.add(rightFunction(round(i, 1)));
            }
        }

        var series2 = new XYSeries("The spline solution");
        List<Double> xListSpline = new ArrayList<>();
        for (double i = -1; round(i, 5) <= 1; i += 0.00001) {
            xListSpline.add(round(i, 5));
        }

        List<List<Double>> result = cubicInterpolation.createSpline(xList, yList, 0, 0, 0.5);
        List<Double> yListSpline = result.get(0);

        System.out.println(xListSpline.size() + ":" + yListSpline.size());

        for (int i = 0; i < yListSpline.size(); i++) {
            series2.add(xListSpline.get(i), yListSpline.get(i));
        }

        var dataset = new XYSeriesCollection();
        dataset.addSeries(series1);
        dataset.addSeries(series2);

        mainDataset = dataset;

        var datasetDelta = new XYSeriesCollection();

        var seriesDelta = new XYSeries("The delta");
        double xDeltaMax = 0.0;
        double yDeltaMax = 0.0;
        List<Double> mainDelta = new ArrayList<>();
        for (int i = 0; i < yListSpline.size(); i++) {
            try{
                mainDelta.add((Double)((yListSpline.get(i) - yListWithSmallStep.get(i))));
                if((Double)((yListSpline.get(i) - yListWithSmallStep.get(i))) > yDeltaMax){
                    xDeltaMax = xListSpline.get(i);

                    yDeltaMax = (Double)((yListSpline.get(i) - yListWithSmallStep.get(i)));
                }
                seriesDelta.add(xListSpline.get(i), (Double)((yListSpline.get(i) - yListWithSmallStep.get(i))));
            }catch(Exception e){

            }
        }

        datasetDelta.addSeries(seriesDelta);

        mainDeltaDataset = datasetDelta;

        List<Double> ySplineDiff = result.get(1);
        List<Double> yDiff = cubicInterpolation.getAnaliticDiff((x) -> diff(x), xListSpline);
        var series3 = new XYSeries("The diff");
        var series4 = new XYSeries("The spline diff");


        for (int i = 0; i < yDiff.size(); i++) {
            try {
                series3.add(xListSpline.get(i), ySplineDiff.get(i));
                series4.add(xListSpline.get(i), yDiff.get(i));
            }catch (Exception e){

            }
        }

        System.out.println(yDiff);
        System.out.println(ySplineDiff);

        var dataset2 = new XYSeriesCollection();
        dataset2.addSeries(series3);
        dataset2.addSeries(series4);

        diffDataset = dataset2;

        var dataset3 = new XYSeriesCollection();

        var series5 = new XYSeries("The delta diff");
        List<Double> diffDelta = new ArrayList<>();
        double xDeltaDiffMax = 0.0;
        double yDeltaDiffMax = 0.0;
        for (int i = 0; i < yDiff.size(); i++) {
            try {
                diffDelta.add((Double) ((ySplineDiff.get(i) - yDiff.get(i))));
                if((Double) ((ySplineDiff.get(i) - yDiff.get(i))) > yDeltaDiffMax){
                    xDeltaDiffMax = xListSpline.get(i);
                    yDeltaDiffMax = (Double) ((ySplineDiff.get(i) - yDiff.get(i)));
                }
                series5.add(xListSpline.get(i), (Double) ((ySplineDiff.get(i) - yDiff.get(i))));
            }catch (Exception e){

            }
        }

        dataset3.addSeries(series5);

        diffDeltaDataset = dataset3;

        //DOUBLE DIFF
        List<Double> ySplineDoubleDiff = result.get(2);
        List<Double> yDoubleDiff = cubicInterpolation.getAnaliticDiff((x) -> doubleDiff(x), xListSpline);
        var series6 = new XYSeries("The double diff");
        var series7 = new XYSeries("The double spline diff");


        for (int i = 0; i < yDoubleDiff.size(); i++) {
            try {
                series6.add(xListSpline.get(i), yDoubleDiff.get(i));
                series7.add(xListSpline.get(i), ySplineDoubleDiff.get(i));
            } catch (Exception e){

            }
        }

        System.out.println(yDoubleDiff);
        System.out.println(ySplineDoubleDiff);

        var dataset4 = new XYSeriesCollection();
        dataset4.addSeries(series6);
        dataset4.addSeries(series7);

        doubleDiffDataset = dataset4;

        var dataset5 = new XYSeriesCollection();

        var series8 = new XYSeries("The delta double diff");
        for (int i = 0; i < yDiff.size(); i++) {
            try {
                series8.add(xListSpline.get(i), (Double) ((ySplineDoubleDiff.get(i) - yDoubleDiff.get(i))));
            } catch (Exception e){

            }
        }

        dataset5.addSeries(series8);

        doubleDiffDeltaDataset = dataset5;

        String[] colNames = new String[]{"i","x_i","F(xi)","S(xi)","|F(xi)-S(xi)|","F'(xi)","S'(xi)","|F'(xi)-S'(xi)|"};
        Double[][] array = new Double[xListSpline.size()][8];

        for (int i = 0; i < xListSpline.size(); i++) {
            try {
                array[i] = new Double[]{(double)i, xListSpline.get(i), yListWithSmallStep.get(i), yListSpline.get(i),
                        Math.abs(yListWithSmallStep.get(i) - yListSpline.get(i)), yDiff.get(i), ySplineDiff.get(i),
                        Math.abs(yDiff.get(i) - ySplineDiff.get(i))};
            } catch (Exception e){

            }
        }

        DataTable dataTable = new DataTable(array, colNames);

        System.out.println("Справка");
        System.out.println("Сетка сплайна n = 20");
        System.out.println("Контрольная сетка сплайна n = 20000");

        System.out.println("Погрешность сплайна на контрольной сетке max |F(xj)- S(xj)| =: " + Collections.max(mainDelta) + " при x = " + xDeltaMax);
        System.out.println("Погрешность производной на контрольной сетке max |F'(xj)- S'(xj)|=" + Collections.max(diffDelta) + " при x = " + xDeltaDiffMax);

    }

    private double leftFunction(double x){
        return Math.pow(x, 3) + 3 * Math.pow(x, 2);
    }

    private double rightFunction(double x){
        return -Math.pow(x, 3) + 3 * Math.pow(x, 2);
    }

    private JFreeChart createChart(final XYDataset dataset) {

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Test Task",
                "x",
                "y",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        XYPlot plot = chart.getXYPlot();

        var renderer = new XYLineAndShapeRenderer();

        renderer.setSeriesPaint(0, Color.RED);
        renderer.setSeriesStroke(0, new BasicStroke(2.0f));
        renderer.setSeriesPaint(1, Color.BLUE);
        renderer.setSeriesStroke(1, new BasicStroke(3.0f));
        renderer.setDefaultShapesVisible(false);

        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.white);
        plot.setRangeGridlinesVisible(false);
        plot.setDomainGridlinesVisible(false);

        chart.getLegend().setFrame(BlockBorder.NONE);

        chart.setTitle(new TextTitle("Test Task",
                        new Font("Serif", Font.BOLD, 18)
                )
        );

        return chart;
    }

    private JFreeChart createChartWithDiff(final XYDataset dataset) {

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Test Task",
                "x",
                "y",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        XYPlot plot = chart.getXYPlot();

        var renderer = new XYLineAndShapeRenderer();

        renderer.setSeriesPaint(0, Color.RED);
        renderer.setSeriesStroke(0, new BasicStroke(2.0f));
        renderer.setSeriesPaint(1, Color.BLUE);
        renderer.setSeriesStroke(1, new BasicStroke(3.0f));
        renderer.setDefaultShapesVisible(false);

        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.white);
        plot.setRangeGridlinesVisible(false);
        plot.setDomainGridlinesVisible(false);

        chart.getLegend().setFrame(BlockBorder.NONE);

        chart.setTitle(new TextTitle("Test Task Diff",
                        new Font("Serif", Font.BOLD, 18)
                )
        );

        return chart;
    }

    private JFreeChart createDeltaChartWithDiff(final IntervalXYDataset dataset) {

        JFreeChart chart = ChartFactory.createXYBarChart("Test Task",
                "x",
                false,
                "y + 10^-9",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        XYPlot plot = chart.getXYPlot();

        var renderer = new XYBarRenderer();

        renderer.setSeriesPaint(0, Color.RED);
        renderer.setSeriesStroke(0, new BasicStroke(0.1f));
        renderer.setBarPainter(new XYBarPainter() {
            @Override
            public void paintBar(Graphics2D graphics2D, XYBarRenderer xyBarRenderer, int i, int i1, RectangularShape rectangularShape, RectangleEdge rectangleEdge) {
                rectangularShape.setFrame(rectangularShape.getX() + rectangularShape.getWidth()/2, rectangularShape.getY(), 0.1f, rectangularShape.getHeight());
                graphics2D.setColor(Color.RED);
                graphics2D.draw(rectangularShape);
            }

            @Override
            public void paintBarShadow(Graphics2D graphics2D, XYBarRenderer xyBarRenderer, int i, int i1, RectangularShape rectangularShape, RectangleEdge rectangleEdge, boolean b) {

            }
        });
        //renderer.setSeriesPaint(1, Color.BLUE);
        //renderer.setSeriesStroke(1, new BasicStroke(3.0f));
        //renderer.setDefaultShapesVisible(false);

        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.white);
        plot.setRangeGridlinesVisible(false);
        plot.setDomainGridlinesVisible(false);

        chart.getLegend().setFrame(BlockBorder.NONE);

        chart.setTitle(new TextTitle("Test Task Diff",
                        new Font("Serif", Font.BOLD, 18)
                )
        );

        return chart;
    }
}
