package evaluation.listeners;

import core.AbstractGameState;
import core.actions.AbstractAction;
import core.interfaces.IActionFeatureVector;
import core.interfaces.IStatisticLogger;
import evaluation.listeners.FeatureListener;
import evaluation.metrics.Event;

/**
 * This provides a generic way of recording training data from games. After each move is made, it will record a feature
 * vector of the current state (for each player?) and the current score.
 * When a game is finished, and we know the final result, the records for the game can be updated with this (i.e.
 * win/loss, score, ordinal position), and all the records written to file.
 */
public class ActionFeatureListener extends FeatureListener {

    IActionFeatureVector psiFn;

    public ActionFeatureListener(IStatisticLogger logger, IActionFeatureVector psi, Event.GameEvent frequency, boolean currentPlayerOnly) {
        super(logger, frequency, currentPlayerOnly);
        this.psiFn = psi;
    }

    @Override
    public String[] names() {
        return psiFn.names();
    }

    @Override
    public double[] extractFeatureVector(AbstractAction action, AbstractGameState state, int perspectivePlayer) {
        return psiFn.featureVector(action, state, perspectivePlayer);
    }

}
