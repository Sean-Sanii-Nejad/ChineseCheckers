package games.chinesecheckers.components;

import core.components.BoardNode;
import core.components.Token;
import games.stratego.components.Piece;

public class Peg  {

    public enum Colour2 {
        purple,
        red;
    }

    public enum Colour3 {
        red,
        blue,
        green;
    }
    public enum Colour6 {
        purple,
        blue,
        yellow,
        red,
        orange,
        green;
    }

    CCNode occupiedNode;

    private int x;
    private int y;

    private Colour2 team2;
    private Colour3 team3;
    private Colour6 team6;

    private boolean inDestination = false;

    public Peg(){

    }

    public Peg(Colour6 team, CCNode occupiedNode){
        this.team6 = team;
        this.occupiedNode = occupiedNode;
    }
    public Peg(Colour2 team, CCNode occupiedNode){
        this.team2 = team;
        this.occupiedNode = occupiedNode;
    }
    public Peg(Colour3 team, CCNode occupiedNode){
        this.team3 = team;
        this.occupiedNode = occupiedNode;
    }

    public void setOccupiedNode(CCNode occupiedNode)
    {
        this.occupiedNode = occupiedNode;
    }

    public CCNode getOccupiedNode()
    {
        if(occupiedNode == null) return null;
        else return occupiedNode;
    }

    public Colour2 getColour2(){
        return team2;
    }
    public Colour3 getColour3(){
        return team3;
    }
    public Colour6 getColour6(){
        return team6;
    }
}


