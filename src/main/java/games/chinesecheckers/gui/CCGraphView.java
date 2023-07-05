package games.chinesecheckers.gui;

import games.chinesecheckers.components.StarBoard;
import gui.IScreenHighlight;
import gui.views.ComponentView;
import org.apache.commons.math3.geometry.spherical.twod.Circle;

import java.awt.*;
import java.util.ArrayList;

import static gui.GUI.defaultItemSize;

public class CCGraphView extends ComponentView implements IScreenHighlight {

    public CCGraphView(StarBoard starBoard){
        super(starBoard, 100 * defaultItemSize, 100 * defaultItemSize);

    }

    //Circle circle

    @Override
    public void clearHighlights() {

    }

    @Override
    protected void paintComponent(Graphics g) {

    }
}
