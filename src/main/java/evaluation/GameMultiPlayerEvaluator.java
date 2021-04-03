package evaluation;

import core.AbstractGameState;
import core.AbstractPlayer;
import core.Game;
import core.interfaces.IStatisticLogger;
import evodef.MultiSolutionEvaluator;
import evodef.SearchSpace;
import games.GameType;
import utilities.SummaryLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Game Evaluator is used for NTBEA optimisation of parameters. It implements the SolutionEvaluator interface.
 * On each NTBEA trial the evaluate(int[] settings) function is called with the set of parameters to try next.
 * The meaning of these settings is encapsulated in the AgentSearchSpace, as this will vary with whatever is being
 * optimised.
 */
public class GameMultiPlayerEvaluator implements MultiSolutionEvaluator {

    GameType game;
    ITPSearchSpace searchSpace;
    int nPlayers;
    int nEvals = 0;
    Random rnd;
    public boolean reportStatistics;
    public IStatisticLogger statsLogger = new SummaryLogger();

    /**
     * GameEvaluator
     *
     * @param game             The game that will be run for each trial. After each trial it is reset().
     * @param parametersToTune The ITunableParameters object that defines the parameter space we are optimising over.
     *                         This will vary with whatever is being optimised.
     * @param seed             Random seed to use
     */
    public <T> GameMultiPlayerEvaluator(GameType game, ITPSearchSpace parametersToTune,
                                        int nPlayers,
                                        long seed) {
        this.game = game;
        this.searchSpace = parametersToTune;
        this.nPlayers = nPlayers;
        this.rnd = new Random(seed);
    }

    @Override
    public void reset() {
        nEvals = 0;
    }


    /**
     * There should never be a need to call this method directly. It is called by the NTBEA framework as needed.
     *
     * @param settings is an integer array per player corresponding to the searchSpace.
     *                 The length of settings corresponds to searchSpace.nDims()
     *                 the value of settings[i] is a number in [0, searchSpace.nValues(i)]
     *                 the actual underlying parameter value can be found with searchSpace.value(i, settings[i])
     * @return Returns the game score for the agent being optimised
     */
    @Override
    public double[] evaluate(List<int[]> settings) {
/*        System.out.println(String.format("Starting evaluation %d of %s at %tT", nEvals,
                Arrays.toString(settings), System.currentTimeMillis()));*/

        List<AbstractPlayer> allPlayers = new ArrayList<>(nPlayers);

        for (int i = 0; i < nPlayers; i++) {
            AbstractPlayer tunedPlayer = (AbstractPlayer) searchSpace.getAgent(settings.get(i));
            if (reportStatistics) tunedPlayer.setStatsLogger(statsLogger);
            allPlayers.add(tunedPlayer);
        }

        Game newGame = game.createGameInstance(nPlayers);
        newGame.reset(allPlayers, rnd.nextLong());

        newGame.run();
        AbstractGameState finalState = newGame.getGameState();

        nEvals++;
        return finalState.getHeuristicScore(playerIndex);
    }

    /**
     * @return The searchSpace
     */
    @Override
    public SearchSpace searchSpace() {
        return searchSpace;
    }

    /**
     * @return The number of NTBEA iterations/trials that have been run so far
     */
    @Override
    public int nEvals() {
        return nEvals;
    }


}
