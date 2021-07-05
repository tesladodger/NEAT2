package com.tesladodger.neat.tools.drawer;

import javax.swing.*;
import java.awt.*;


abstract class GenomeFigure extends JComponent {

    abstract void paintFigure (Graphics2D g);

    protected GenomeFigure () {}

    @Override
    public void paint (Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        paintFigure(g2d);
    }

}
