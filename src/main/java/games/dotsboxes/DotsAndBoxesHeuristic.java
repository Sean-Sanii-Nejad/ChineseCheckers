package games.dotsboxes;

import core.AbstractGameState;
import core.interfaces.IStateFeatureVector;
import core.interfaces.IStateHeuristic;
import evaluation.TunableParameters;
import utilities.Utils;

import java.util.Arrays;

public class DotsAndBoxesHeuristic extends TunableParameters implements IStateHeuristic, IStateFeatureVector {

    double POINT_ADVANTAGE = 0.05;
    double POINTS = 0.01;
    double THREE_BOXES = 0.0;
    double TWO_BOXES = 0.0;
    double ORDINAL = 0.0;

    String[] names = new String[]{"POINTS", "POINT_ADVANTAGE", "TWO_BOXES", "THREE_BOXES", "ORDINAL", "OUR_TURN", "FILLED_BOXES", "TURN"};

    public DotsAndBoxesHeuristic() {
        addTunableParameter(names[0], 0.01);
        addTunableParameter(names[1], 0.05);
        addTunableParameter(names[2], 0.0);
        addTunableParameter(names[3], 0.0);
        addTunableParameter(names[4], 0.0);
    }

    /**
     * Method that reloads all the locally stored values from currentValues
     * This is in case sub-classes decide to use the frankly more intuitive access via
     * params.paramName
     * instead of
     * params.getParameterValue("paramName")
     * (the latter is also more typo-prone if we hardcode strings everywhere)
     */
    @Override
    public void _reset() {
        POINTS = (double) getParameterValue(names[0]);
        POINT_ADVANTAGE = (double) getParameterValue(names[1]);
        TWO_BOXES = (double) getParameterValue(names[2]);
        THREE_BOXES = (double) getParameterValue(names[3]);
        ORDINAL = (double) getParameterValue(names[4]);
    }

    /**
     * Returns a score for the state that should be maximised by the player (the bigger, the better).
     * Ideally bounded between [-1, 1].
     *
     * @param gs       - game state to evaluate and score.
     * @param playerId
     * @return - value of given state.
     */
    @Override
    public double evaluateState(AbstractGameState gs, int playerId) {
        DBGameState state = (DBGameState) gs;
        Utils.GameResult playerResult = gs.getPlayerResults()[playerId];

        if (!state.isNotTerminal())
            return playerResult.value;

        double[] featureVector = featureVector(state, playerId);

        double maxPoints = 20.0;
        double totalCells = 10.0;

        double retValue = POINTS * featureVector[0] / maxPoints +
                POINT_ADVANTAGE * featureVector[1] / maxPoints +
                TWO_BOXES * featureVector[2] * (featureVector[5] - 0.5) / totalCells +
                THREE_BOXES * featureVector[3] * (featureVector[5] - 0.5) / totalCells +
                ORDINAL * featureVector[4] / state.getNPlayers();

        return retValue;
    }

    /**
     * Return a copy of this game parameters object, with the same parameters as in the original.
     *
     * @return - new game parameters object.
     */
    @Override
    protected DotsAndBoxesHeuristic _copy() {
        DotsAndBoxesHeuristic retValue = new DotsAndBoxesHeuristic();
        retValue.POINTS = POINTS;
        retValue.POINT_ADVANTAGE = POINT_ADVANTAGE;
        retValue.THREE_BOXES = THREE_BOXES;
        retValue.TWO_BOXES = TWO_BOXES;
        retValue.ORDINAL = ORDINAL;
        return retValue;
    }

    /**
     * Checks if the given object is the same as the current.
     *
     * @param o - other object to test equals for.
     * @return true if the two objects are equal, false otherwise
     */
    @Override
    protected boolean _equals(Object o) {
        if (o instanceof DotsAndBoxesHeuristic) {
            DotsAndBoxesHeuristic other = (DotsAndBoxesHeuristic) o;
            return other.POINT_ADVANTAGE == POINT_ADVANTAGE &&
                    other.ORDINAL == ORDINAL &&
                    other.TWO_BOXES == TWO_BOXES && other.THREE_BOXES == THREE_BOXES &&
                    other.POINTS == POINTS;
        }
        return false;
    }

    /**
     * @return Returns Tuned Parameters corresponding to the current settings
     * (will use all defaults if setParameterValue has not been called at all)
     */
    @Override
    public Object instantiate() {
        return this._copy();
    }

    @Override
    public double[] featureVector(AbstractGameState gs, int playerID) {
        double[] retValue = new double[names.length];
        DBGameState state = (DBGameState) gs;
        // POINTS
        retValue[0] = state.nCellsPerPlayer[playerID];

        // POINT_ADVANTAGE
        int ordinal = 1;
        int maxOtherScore = -1;
        for (int p = 0; p < state.getNPlayers(); p++) {
            if (p == playerID) continue;
            if (state.nCellsPerPlayer[p] > maxOtherScore) {
                maxOtherScore = state.nCellsPerPlayer[p];
                if (state.nCellsPerPlayer[p] > state.nCellsPerPlayer[playerID])
                    ordinal++;
            }
        }

        // POINT_ADVANTAGE
        retValue[1] = state.nCellsPerPlayer[playerID] - maxOtherScore;

        // CELLS
        int[] cellCountByEdges = new int[5];
        for (DBCell cell : state.cells) {
            int edges = state.countCompleteEdges(cell);
            cellCountByEdges[edges]++;
        }
        retValue[2] = cellCountByEdges[2];
        retValue[3] = cellCountByEdges[3];
        retValue[4] = ordinal;
        retValue[5] = state.getCurrentPlayer() == playerID ? 1 : 0;
        retValue[6] = cellCountByEdges[4];
        retValue[7] = state.getTurnOrder().getTurnCounter();

        return retValue;
    }

    @Override
    public String[] names() {
        return names;
    }
}
