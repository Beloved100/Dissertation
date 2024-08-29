package utils;

import java.awt.*;
import javax.swing.*;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYItemRenderer;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import org.jfree.util.ShapeUtilities;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 * Created by Theo Theodoridis.
 * Class    : ScatterPlotter
 * Version  : v1.0
 * Date     : © Copyright 26-01-2013
 * User     : ttheod
 * email    : ttheod@gmail.com
 * Comments : None.
 **/

public class ScatterPlotter extends ApplicationFrame
{
    public XYSeries series;
    private String title    = "";
    private String xLabel   = "";
    private String yLabel   = "";
    private String mrkLabel = "";

    private boolean legend = true;
    private Color mrkColor = Color.RED;

   /**
    * Method     : ScatterPlotter::ScatterPlotter()
    * Purpose    : Default ScatterPlotter class constructor.
    * Parameters : - winLabel : The window label.
    *              - title    : The chart title.
    *              - xLabel   : The X-label.
    *              - yLabel   : The Y-label.
    *              - mrkLabel : The marker label.
    *              - mrkColor : The marker color.
    *              - legend   : The data legend.
    * Returns    : Nothing.
    * Notes      : None.
    **/
    public ScatterPlotter(String winLabel, String title, String xLabel, String yLabel, String mrkLabel, Color mrkColor, boolean legend)
    {
        super(winLabel);

        this.legend   = legend;
        this.mrkColor = mrkColor;

        this.title    = title;
        this.xLabel   = xLabel;
        this.yLabel   = yLabel;
        this.mrkLabel = mrkLabel;

        JPanel jpanel = createDemoPanel();
        jpanel.setPreferredSize(new Dimension(640, 480));
        add(jpanel);
    }

   /**
    * Method     : ScatterPlotter::createDemoPanel()
    * Purpose    : To create a demo panel .
    * Parameters : None.
    * Returns    : Nothing.
    * Notes      : None.
    **/
    public JPanel createDemoPanel()
    {
        series = new XYSeries(mrkLabel);
        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        xySeriesCollection.addSeries(series);
        JFreeChart chart = ChartFactory.createScatterPlot(title, xLabel, yLabel, xySeriesCollection, PlotOrientation.HORIZONTAL, legend, true, false);

        Shape shape = ShapeUtilities.createRegularCross(5, 0.1f);
        XYPlot plot = (XYPlot)chart.getPlot();
        XYItemRenderer renderer = plot.getRenderer();
        renderer.setSeriesShape(0, shape);
        renderer.setSeriesPaint(0, mrkColor);
        return(new ChartPanel(chart));
    }
}
