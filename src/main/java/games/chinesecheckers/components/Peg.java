package games.chinesecheckers.components;

import core.AbstractGameState;
import core.CoreConstants;
import core.components.BoardNode;
import core.components.Component;
import core.components.Token;
import games.chinesecheckers.CCGameState;
import games.stratego.components.Piece;

import java.util.Objects;

public class Peg extends Component {

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

    private Colour2 team2;
    private Colour3 team3;
    private Colour6 team6;

    private boolean inDestination = false;

    public Peg(){
        super(CoreConstants.ComponentType.TOKEN, "PEG");

    }

    public Peg(Colour6 team, CCNode occupiedNode){
        super(CoreConstants.ComponentType.TOKEN, "PEG");
        this.team6 = team;
        this.occupiedNode = occupiedNode;
    }
    public Peg(Colour2 team, CCNode occupiedNode){
        super(CoreConstants.ComponentType.TOKEN, "PEG");
        this.team2 = team;
        this.occupiedNode = occupiedNode;
    }
    public Peg(Colour3 team, CCNode occupiedNode){
        super(CoreConstants.ComponentType.TOKEN, "PEG");
        this.team3 = team;
        this.occupiedNode = occupiedNode;
    }

    public void setOccupiedNode(CCNode occupiedNode)
    {
        this.occupiedNode = occupiedNode;
    }

    public void setInDestination(boolean value) { inDestination = value; }

    public CCNode getOccupiedNode()
    {
        if(occupiedNode == null) return null;
        else return occupiedNode;
    }

    public boolean getInDestination() { return inDestination; }

    public Colour2 getColour2(){
        return team2;
    }
    public Colour3 getColour3(){
        return team3;
    }
    public Colour6 getColour6(){
        return team6;
    }

    @Override
    public Component copy() {
        Peg copy = new Peg();
        copy.setInDestination(getInDestination());
        copy.team2 = getColour2();
        copy.team3 = getColour3();
        copy.team6 = getColour6();
        return copy;
    }

    @Override
    public final int hashCode() {
        return Objects.hash(componentID, team2, team3, team6, occupiedNode, inDestination);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Peg) {
            Peg other = (Peg) o;
            return componentID == other.componentID && Objects.equals(occupiedNode, other.occupiedNode) && Objects.equals(team2, other.team2)  && Objects.equals(team3, other.team3) &&
                    Objects.equals(team6, other.team6) && inDestination == other.inDestination;
        }
        return false;
    }
}


