package interpolation;

import table.DataTable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class CubicInterpolation {

    public List<Double> calculateSpline(double x0, double xi, double a, double b, double c, double d){
        List<Double> result = new ArrayList<>();
        for (double i = x0; round(i, 5) < round(xi, 5); i+=0.00001) {
            //System.out.println(x0 + ":" + xi + ":" + round(i, 5));
            double y = a
                    + b * (round(i, 5) - xi)
                    + (c / 2) * Math.pow(round(i, 5) - xi, 2)
                    + (d / 6) * Math.pow(round(i, 5) - xi, 3);
            result.add(y);
            //System.out.println(i + ":" + y);
        }

        //System.out.println(result);
        return result;
    }

    public List<Double> calculateDiffSpline(double x0, double xi, double a, double b, double c, double d){
        List<Double> result = new ArrayList<>();
        for (double i = x0; round(i, 5) < round(xi, 5); i+=0.00001) {
            //System.out.println(x0 + ":" + xi + ":" + round(i, 5));
            double y = b + (c) * (round(i, 5) - xi)
                    + (d / 2) * Math.pow(round(i, 5) - xi, 2);
            result.add(y);
            //System.out.println(i + ":" + y);
        }

        //System.out.println(result);
        return result;
    }

    public List<Double> calculateDoubleDiffSpline(double x0, double xi, double a, double b, double c, double d){
        List<Double> result = new ArrayList<>();
        for (double i = x0; round(i, 5) < round(xi, 5); i+=0.00001) {
            //System.out.println(x0 + ":" + xi + ":" + round(i, 5));
            double y = (c)
                    + (d)*(round(i, 5) - xi);
            result.add(y);
            //System.out.println(i + ":" + y);
        }

        //System.out.println(result);
        return result;
    }

    public List<Double> getAnaliticDiff(Function<Double, Double> fun, List<Double> xList){
        List<Double> result = new ArrayList<>();
        for(int i = 0; i < xList.size(); i++){
            result.add(fun.apply(xList.get(i)));
        }
        return result;
    }

    public List<Double> getDiff(List<Double> xList, List<Double> yList){
        List<Double> result = new ArrayList<>();
        result.add((yList.get(1) - yList.get(0))/(xList.get(1) - xList.get(0)));
        for (int i = 1; i < yList.size()-1; i++) {
            try {
                result.add((yList.get(i + 1) - yList.get(i - 1)) / ((xList.get(i + 1) - xList.get(i - 1))));
            }catch (Exception e){
                return result;
            }
        }
        result.add((yList.get(yList.size()-1) - yList.get(yList.size()-2))/(xList.get(xList.size()-1) - xList.get(xList.size()-2)));
        return result;
    }

    public List<Double> getDoubleDiff(List<Double> xList, List<Double> yList){
        return getDiff(xList, getDiff(xList, yList));
    }


    private List<Double> getC(double h, List<Double> yList, double lEGR, double rEGR){
        double c0 = lEGR;
        double cN = rEGR;

        List<Double> coef1 = new ArrayList<>();
        coef1.add(0.0);
        List<Double> coef2 = new ArrayList<>();
        coef2.add(1.0);
        List<Double> coef3 = new ArrayList<>();
        coef3.add(0.0);
        List<Double> coef4 = new ArrayList<>();
        coef4.add(lEGR);

        for (int i = 1; i < yList.size()-1; i++){
            coef1.add(h);
            coef2.add(4 * h);
            coef3.add(h);
            coef4.add(6 * ((yList.get(i+1) - 2 * yList.get(i) + yList.get(i-1))/h));
        }

        coef1.add(0.0);
        coef2.add(1.0);
        coef4.add(rEGR);

        List<Double> ci = new ArrayList<>();
        ci.add(coef2.get(0));
        List<Double> c_coef1 = new ArrayList<>();
        c_coef1.add(-coef3.get(0)/ci.get(0));
        List<Double> c_coef2 = new ArrayList<>();
        c_coef2.add(coef4.get(0)/ci.get(0));
        for (int i = 1; i < yList.size()-1; i++){
            ci.add(coef2.get(i) + coef1.get(i) * c_coef1.get(i-1));
            c_coef1.add(-coef3.get(i) / ci.get(i));
            c_coef2.add((coef4.get(i) - coef1.get(i) * c_coef2.get(i-1))/ci.get(i));
        }
        ci.add(coef2.get(coef2.size()-1) + coef1.get(coef1.size()-1) * c_coef1.get(c_coef1.size()-1));
        c_coef2.add((coef4.get(coef4.size()-1) - coef1.get(coef1.size()-1)
                * c_coef2.get(c_coef2.size()-1)) / ci.get(ci.size()-1));

        List<Double> c = new ArrayList<Double>(Collections.nCopies(yList.size(), (double)0));
        c.set(c.size()-1, c_coef2.get(c_coef2.size()-1));
        for (int i = yList.size()-2; i > -1; i--){
            c.set(i, c_coef1.get(i) * c.get(i+1) + c_coef2.get(i));
        }

        return c;
    }

    public List<Double> getA(List<Double> yList){
        List<Double> a = new ArrayList<>();
        for (int i = 1; i < yList.size(); i++) {
            a.add(yList.get(i));
        }
        return a;
    }

    public List<Double> getB(List<Double> yList, List<Double> ci, double h){
        List<Double> b = new ArrayList<>();
        for (int i = 1; i < ci.size(); i++) {
            b.add((yList.get(i) - yList.get(i-1)) / h + ci.get(i) * h / 3 + ci.get(i-1) * h / 6);
        }
        return b;
    }

    public List<Double> getD(List<Double> ci, double h){
        List<Double> d = new ArrayList<>();
        for (int i = 1; i < ci.size(); i++) {
            d.add((ci.get(i) - ci.get(i-1)) / h);
        }
        return d;
    }

    public List<List<Double>> createSpline(List<Double> xList, List<Double> yList, double lEGR, double rEGR, double h){
        List<Double> ci = getC(h, yList, lEGR, rEGR);
        List<Double> ai = getA(yList);
        List<Double> bi = getB(yList, ci, h);
        List<Double> di = getD(ci, h);

        String[] colNames = new String[]{"i","x_i-1","x_i","a_i","b_i","c_i", "d_i"};
        Double[][] array = new Double[xList.size()][7];
        List<List<Double>> result = new ArrayList<>();
        List<Double> resultMain = new ArrayList<>();
        List<Double> resultDiff = new ArrayList<>();
        List<Double> resultDoubleDiff = new ArrayList<>();
        for (int i = 1; i < xList.size(); i++) {
            resultMain.addAll(calculateSpline(xList.get(i)-h, xList.get(i), ai.get(i-1), bi.get(i-1), ci.get(i), di.get(i-1)));
            resultDiff.addAll(calculateDiffSpline(xList.get(i)-h, xList.get(i), ai.get(i-1), bi.get(i-1), ci.get(i), di.get(i-1)));
            resultDoubleDiff.addAll(calculateDoubleDiffSpline(xList.get(i)-h, xList.get(i), ai.get(i-1), bi.get(i-1), ci.get(i), di.get(i-1)));
            array[i-1] = new Double[]{Double.valueOf(i-1), xList.get(i)-h, xList.get(i), ai.get(i-1), bi.get(i-1), ci.get(i), di.get(i-1)};
        }

        DataTable dataTable = new DataTable(array, colNames);

        result.add(resultMain);
        result.add(resultDiff);
        result.add(resultDoubleDiff);
        return result;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

}
