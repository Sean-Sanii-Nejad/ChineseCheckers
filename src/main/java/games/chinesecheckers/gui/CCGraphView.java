package games.chinesecheckers.gui;

import core.components.GraphBoard;
import games.catan.components.Graph;
import games.chinesecheckers.components.CCNode;
import games.chinesecheckers.components.Peg;
import games.chinesecheckers.components.StarBoard;
import gui.IScreenHighlight;
import gui.views.ComponentView;

import java.awt.*;
import java.util.ArrayList;

import static gui.GUI.defaultItemSize;

public class CCGraphView extends ComponentView implements IScreenHighlight {

    ArrayList<Rectangle> dots = new ArrayList<Rectangle>();
    StarBoard starBoard;

    public CCGraphView(StarBoard starBoard){
        super(starBoard, defaultItemSize, defaultItemSize);

        this.starBoard = starBoard;
        for(int i = 0; i < starBoard.getBoardNodes().size(); i++){
            dots.add(new Rectangle(((CCNode)starBoard.getBoardNodes().get(i)).getX(), ((CCNode)starBoard.getBoardNodes().get(i)).getY(), 10, 10));
            //System.out.println(((CCNode)starBoard.getBoardNodes().get(i)).getX() + " " + ((CCNode)starBoard.getBoardNodes().get(i)).getY());
        }
    }

    Color retrieveColour(CCNode node){
        if(((CCNode)starBoard.getBoardNodes().get(node.getID())).getBaseColour().name() == "purple"){
            return Color.magenta;
        }
        if(((CCNode)starBoard.getBoardNodes().get(node.getID())).getBaseColour().name() == "green"){
            return Color.green;
        }
        if(((CCNode)starBoard.getBoardNodes().get(node.getID())).getBaseColour().name() == "neutral"){
            return Color.black;
        }
        if(((CCNode)starBoard.getBoardNodes().get(node.getID())).getBaseColour().name() == "orange"){
            return Color.orange;
        }
        if(((CCNode)starBoard.getBoardNodes().get(node.getID())).getBaseColour().name() == "yellow"){
            return Color.yellow;
        }
        if(((CCNode)starBoard.getBoardNodes().get(node.getID())).getBaseColour().name() == "red"){
            return Color.red;
        }
        return Color.BLUE;
    }

    Color retrieveColourPeg(Peg peg){
        if(peg.getColour() == Peg.Colour.purple){
            return Color.magenta;
        }
        if(peg.getColour() == Peg.Colour.red){
            return Color.red.brighter();
        }
        return Color.blue;
    }

    void drawNodes(Graphics g){
        Graphics2D g2d = (Graphics2D) g;
        for(int i = 0; i < starBoard.getBoardNodes().size(); i++){
            g2d.setColor(retrieveColour((CCNode) starBoard.getBoardNodes().get(i)));

            int x = dots.get(i).x;
            int y = dots.get(i).y;
            int scale = 30;
            int size = 15;
            boolean shift = false;

            if(y % 2 != 0){
                shift = true;
            }

            x = x * scale;
            y = y * scale;

            if(shift){
                g2d.fillOval(x+72, y+23, size, size);
            }
            else {
                g2d.fillOval(x+57, y+23, size, size);
            }
        }
    }

    void drawPegs(Graphics g){
        Graphics2D g2d = (Graphics2D) g;
        for(int i = 0; i < starBoard.getBoardNodes().size(); i++){
            if(((CCNode)starBoard.getBoardNodes().get(i)).isNodeOccupied()){
                Peg peg = ((CCNode)starBoard.getBoardNodes().get(i)).getOccupiedPeg();
                g2d.setColor(retrieveColourPeg(peg));

                int x = dots.get(i).x;
                int y = dots.get(i).y;
                int scale = 30;
                int size = 20;
                boolean shift = false;

                if(y % 2 != 0){
                    shift = true;
                }

                x = x * scale;
                y = y * scale;

                if(shift){
                    g2d.fillOval(x+70, y+22, size, size);
                }
                else {
                    g2d.fillOval(x+55, y+22, size, size);
                }
            }
        }
    }

    void drawNumbers(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        Font font = new Font("Verdana", Font.BOLD, 13);
        g2d.setFont(font);

        for (int i = 0; i < starBoard.getBoardNodes().size(); i++) {
            g2d.setColor(retrieveColour((CCNode) starBoard.getBoardNodes().get(i)));

            int x = dots.get(i).x;
            int y = dots.get(i).y;
            int scale = 30;
            boolean shift = false;

            if (y % 2 != 0) {
                shift = true;
            }

            x = x * scale;
            y = y * scale;

            if (shift) {
                g2d.drawString(Integer.toString(i), x + 570, y + 28);
            } else {
                g2d.drawString(Integer.toString(i), x + 555, y + 28);
            }
        }
    }


    @Override
    public void clearHighlights() {

    }

    @Override
    protected void paintComponent(Graphics g) {
        drawNodes(g);
        drawPegs(g);
        drawNumbers(g);
    }
}
