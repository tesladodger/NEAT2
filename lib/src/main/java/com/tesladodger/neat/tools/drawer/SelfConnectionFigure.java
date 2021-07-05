package com.tesladodger.neat.tools.drawer;

import java.awt.*;

import static com.tesladodger.neat.tools.drawer.NodeFigure.RADIUS;


class SelfConnectionFigure extends GenomeFigure {

    private final int x, y;

    private final float thickness;
    private final Color color;

    protected SelfConnectionFigure (float weight, int x, int y) {
        color = (weight <= 0) ? Color.RED : Color.BLUE;
        thickness = 6 * Math.abs(weight) / 10;
        this.x = x - RADIUS * 2;
        this.y = y - RADIUS - 10;
    }

    @Override
    void paintFigure (Graphics2D g) {
        g.setStroke(new BasicStroke(thickness));
        g.setColor(color);
        g.drawArc(x, y, RADIUS << 1, (RADIUS << 1) + 10, 15, 280);
    }
}
