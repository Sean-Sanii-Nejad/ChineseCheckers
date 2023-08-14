package games.chinesecheckers;

import core.AbstractGameState;
import core.AbstractParameters;
import core.components.Component;
import core.components.GraphBoard;
import games.GameType;
import games.chinesecheckers.components.Peg;
import games.chinesecheckers.components.StarBoard;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CCGameState extends AbstractGameState {

    public int PLAYER_PEGS = 10;
    StarBoard starBoard;

    public CCGameState(AbstractParameters gameParameters, int nPlayers) {
        super(gameParameters, nPlayers);
    }

    public StarBoard getStarBoard()
    {
        return starBoard;
    }

    @Override
    protected GameType _getGameType() {
        return GameType.ChineseCheckers;
    }

    @Override
    protected List<Component> _getAllComponents() {
        // TODO: add all components to the list
        return new ArrayList<Component>() {{
            add(starBoard);
        }};
    }

    @Override
    protected AbstractGameState _copy(int playerId) {
        CCGameState copy = new CCGameState(gameParameters, getNPlayers());
        copy.starBoard = starBoard.copy();
        copy.PLAYER_PEGS = PLAYER_PEGS;

        return copy;
    }

    @Override
    protected double _getHeuristicScore(int playerId) {
        if (isNotTerminal()) {
            // TODO calculate an approximate value
            return new CCHeuristic().evaluateState(this, playerId);
        } else {
            // The game finished, we can instead return the actual result of the game for the given player.
            return getPlayerResults()[playerId].value;
        }
    }

    @Override
    public double getGameScore(int playerId) {
        // TODO: What is this player's score (if any)?
        return playerResults[playerId].value;
    }

    @Override
    protected boolean _equals(Object o) {
        // TODO: compare all variables in the state
        if (this == o) return true;
        if (!(o instanceof CCGameState)) return false;
        if (!super.equals(o)) return false;
        CCGameState that = (CCGameState) o;
        return PLAYER_PEGS == that.PLAYER_PEGS && Objects.equals(starBoard, that.starBoard);
    }

    @Override
    public int hashCode() {
        // TODO: include the hash code of all variables
        int result = Objects.hash(super.hashCode(), starBoard, PLAYER_PEGS);
        result = 31 * result;
        return result;
    }

    public Peg.Colour getPlayerColour(int player) {
        if (nPlayers == 2) {
            return Peg.Colour.values()[player * 3];
        }
        else if (nPlayers == 3) {
            return Peg.Colour.values()[player * 2];
        }
        else {
            System.err.println("unimplemented number of players");
            return Peg.Colour.values()[player];
        }
    }
}
