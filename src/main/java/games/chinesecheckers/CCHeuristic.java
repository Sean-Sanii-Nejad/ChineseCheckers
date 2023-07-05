package games.chinesecheckers;

import core.AbstractGameState;
import core.AbstractParameters;
import core.CoreConstants;
import core.interfaces.IStateHeuristic;
import evaluation.TunableParameters;
import games.connect4.Connect4Heuristic;

public class CCHeuristic extends TunableParameters implements IStateHeuristic  {

    @Override
    protected AbstractParameters _copy() {
        return new CCHeuristic();
    }

    @Override
    protected boolean _equals(Object o) {
        return o instanceof CCHeuristic;
    }

    @Override
    public double evaluateState(AbstractGameState gs, int playerId) {
        CoreConstants.GameResult playerResult = gs.getPlayerResults()[playerId];
        if(playerResult == CoreConstants.GameResult.LOSE_GAME) {
            return -1;
        }
        if(playerResult == CoreConstants.GameResult.WIN_GAME) {
            return 1;
        }
        return 0;
    }

    @Override
    public Object instantiate() {
        return this._copy();
    }

    @Override
    public void _reset() {
    }
}
