package games.chinesecheckers.actions;

import core.AbstractGameState;
import core.actions.AbstractAction;
import games.chinesecheckers.CCGameState;
import games.chinesecheckers.components.CCNode;
import games.chinesecheckers.components.Peg;
import games.sushigo.actions.ChooseCard;

import java.util.Objects;

public class MovePeg extends AbstractAction {

    final int from;
    final int to;

    public MovePeg(int from, int to)
    {
        this.from = from;
        this.to = to;
    }

    @Override
    public boolean execute(AbstractGameState gs) {
        CCGameState state = (CCGameState) gs;

        CCNode nodeStart = state.getStarBoard().getBoardNodes().get(from);
        CCNode nodeDestination = state.getStarBoard().getBoardNodes().get(to);

        Peg peg = nodeStart.getOccupiedPeg();

        nodeStart.setOccupiedPeg(null);
        nodeDestination.setOccupiedPeg(peg);

        return true;
    }

    @Override
    public AbstractAction copy() {
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof MovePeg)) return false;
        MovePeg peg = (MovePeg) obj;
        return Objects.equals(from, peg.from) && Objects.equals(to, peg.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }

    @Override
    public String getString(AbstractGameState gameState) {
        return "Peg on Node: " + from + " move to " + "Node: " + to;
    }
}
