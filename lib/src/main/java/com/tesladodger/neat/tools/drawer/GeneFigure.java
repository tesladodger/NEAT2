package com.tesladodger.neat.tools.drawer;

import java.awt.*;


class GeneFigure extends GenomeFigure {

    private static final int thickness = 3;
    private static final Color color = Color.BLACK;

    protected static final int w = 60;
    protected static final int h = 80;

    private final int x;

    private final int id;
    private final int in, out;
    private final double weight;
    private final boolean enabled;

    /**
     * @param x coordinate;
     * @param id connection innovation number;
     * @param in node id;
     * @param out node id;
     * @param weight connection weight;
     * @param enabled connection enabled or not;
     */
    protected GeneFigure (int x, int id, int in, int out, double weight, boolean enabled) {
        this.x = x;
        this.id = id;
        this.in = in;
        this.out = out;
        this.weight = weight;
        this.enabled = enabled;
    }

    @Override
    void paintFigure (Graphics2D g) {
        g.setStroke(new BasicStroke(thickness));
        g.setColor(color);
        g.drawRect(x, 0, w, h);
        g.drawString(id+"", x+(w/2f), h*.2f);
        g.drawString(in + " -> " + out, x+8, h*.4f);
        g.drawString(String.format("%.2f", weight), x+8, h*.6f);
        if (!enabled) {
            g.drawString("disabled", x+8, h*.8f);
        }
    }
}
