package games.chinesecheckers.components;

import core.components.BoardNode;

public class CCNode extends BoardNode {

    public enum Base {
        purple,
        blue,
        yellow,
        red,
        orange,
        green,
        neutral;
    }

    Base colour;

    private Peg occupiedPeg;

    private int x;
    private int y;

    private int id;

    public CCNode(int x, int y){
        this.x = x;
        this.y = y;
    }

    public CCNode(int id){
        super(6, "Node", id);
        this.id = id;
        colour = Base.neutral;
    }

    public CCNode(int neighbours, String something, int id){
        super(neighbours, something, id);
        this.id = id;
        colour = Base.neutral;
    }

    public void setOccupiedPeg(Peg peg) {
        occupiedPeg = peg;
    }

    public void setColourNode(Base colour){
        this.colour = colour;
    }

    public void setCoordinates(int x, int y){
        this.x = x;
        this.y = y;
    }

    public Peg getOccupiedPeg(){
        if(occupiedPeg == null) return null;
            return occupiedPeg;
    }

    public Base getColour(){
        return colour;
    }

    public int getID() {
        return id;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public Base getBaseColour(){ return colour;}

    public boolean isNodeOccupied(){
        if(occupiedPeg == null) return false;
        else return true;
    }
}
