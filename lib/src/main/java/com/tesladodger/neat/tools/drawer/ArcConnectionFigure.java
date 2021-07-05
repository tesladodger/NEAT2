package com.tesladodger.neat.tools.drawer;

import java.awt.*;
import java.awt.geom.QuadCurve2D;


class ArcConnectionFigure extends GenomeFigure {

    private int x1, y1, x2, y2, x3, y3;

    private static final double DIST_TO_LINE = 100;

    private final int thickness;
    private final Color color;

    protected ArcConnectionFigure (double weight, int x1, int y1, int x2, int y2) {
        color = (weight <= 0) ? Color.RED : Color.BLUE;
        thickness = (int) (6f * Math.abs(weight) / 10f);
        if (x1 == x2) {
            calculateVerticalArc(x1, y1, y2);
        } else {
            calculateArc(x1, y1, x2, y2);
        }
    }

    private void calculateVerticalArc (double x, double Ay, double Cy) {
        x1 = (int) x;
        y1 = (int) Ay;
        x2 = (int) (x + DIST_TO_LINE);
        y2 = (int) ((Ay + Cy) * .5f);
        x3 = (int) x;
        y3 = (int) Cy;
    }

    private void calculateArc (double Ax, double Ay, double Cx, double Cy) {
        // find midpoint D between A and B
        double Dx = (Ax + Cx) * .5f;
        double Dy = (Ay + Cy) * .5f;

        // find slope of AC
        double slopeAC = (Cy - Ay) / (Cx - Ax);

        // find slope of BD, perpendicular to AC
        double slopeBD = - (1 / slopeAC);

        // angle BD
        double angleBD = Math.atan(slopeBD);

        // find B
        double Bx = Dx + DIST_TO_LINE * Math.cos(angleBD);
        double By = Dy + DIST_TO_LINE * Math.sin(angleBD);

        x1 = (int) Ax;
        y1 = (int) Ay;
        x2 = (int) Bx;
        y2 = (int) By;
        x3 = (int) Cx;
        y3 = (int) Cy;
    }

    @Override
    void paintFigure (Graphics2D g) {
        g.setStroke(new BasicStroke((float) thickness));
        g.setColor(color);
        QuadCurve2D.Double curve = new QuadCurve2D.Double(x1, y1, x2, y2, x3, y3);
        g.draw(curve);
    }
}
