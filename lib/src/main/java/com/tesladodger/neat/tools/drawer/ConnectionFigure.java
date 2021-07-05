package com.tesladodger.neat.tools.drawer;

import java.awt.*;


class ConnectionFigure extends GenomeFigure {

    private final int x1;
    private final int y1;
    private final int x2;
    private final int y2;

    private final float thickness;
    private final Color color;

    protected ConnectionFigure (float weight, int x1, int y1, int x2, int y2) {
        color = (weight <= 0) ? Color.RED : Color.BLUE;
        thickness = 6 * Math.abs(weight) / 10;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    @Override
    void paintFigure (Graphics2D g) {
        g.setStroke(new BasicStroke(thickness));
        g.setColor(color);
        g.drawLine(x1, y1, x2, y2);
    }
}
