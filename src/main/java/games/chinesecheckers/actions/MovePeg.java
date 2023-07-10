package games.chinesecheckers.actions;

import core.AbstractGameState;
import core.actions.AbstractAction;
import games.chinesecheckers.CCGameState;
import games.chinesecheckers.components.CCNode;
import games.chinesecheckers.components.Peg;
import games.sushigo.actions.ChooseCard;

import java.util.Objects;

public class MovePeg extends AbstractAction {

    CCNode from;
    CCNode to;

    public MovePeg(CCNode from, CCNode to)
    {
        //System.out.println("Peg on: " + from + " moved to " + to);
        this.from = from;
        this.to = to;
    }

    @Override
    public boolean execute(AbstractGameState gs) {
        CCGameState state = (CCGameState) gs;

        ((CCNode) state.getStarBoard().getBoardNodes().get(to.getID())).setOccupiedPeg(from.getOccupiedPeg());
        ((CCNode) state.getStarBoard().getBoardNodes().get(from.getID())).setOccupiedPeg(null);

        return true;
    }

    @Override
    public AbstractAction copy() {
        return null; // immutable
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof MovePeg)) return false;
        MovePeg peg = (MovePeg) obj;
        return from == peg.from && to == peg.to;
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }

    @Override
    public String getString(AbstractGameState gameState) {
        return "Peg on Node: " + from.getID() + " move to " + "Node: " + to.getID();
    }
}
