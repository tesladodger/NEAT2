package com.tesladodger.neat.tools.drawer;

import java.awt.*;


class NodeFigure extends GenomeFigure {

    protected final int x;
    protected final int y;
    public static int RADIUS = 15;

    protected final int id;
    protected final int layer;

    protected NodeFigure (int x, int y, int id, int layer) {
        this.x = x;
        this.y = y;
        this.id = id;
        this.layer = layer;
    }

    @Override
    void paintFigure (Graphics2D g) {
        g.setColor(Color.BLACK);
        g.fillOval(x-RADIUS, y-RADIUS, RADIUS << 1, RADIUS << 1);
        g.setColor(Color.WHITE);
        g.drawString("" + id, x-2, y+4);
    }
}
