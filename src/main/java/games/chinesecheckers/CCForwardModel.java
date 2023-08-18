package games.chinesecheckers;

import core.AbstractGameState;
import core.CoreConstants;
import core.StandardForwardModel;
import core.actions.AbstractAction;
import core.components.BoardNode;
import core.components.GraphBoard;
import games.chinesecheckers.actions.MovePeg;
import games.chinesecheckers.components.CCNode;
import games.chinesecheckers.components.Peg;
import games.chinesecheckers.components.StarBoard;
import games.connect4.Connect4Constants;
import games.stratego.actions.Move;
import evaluation.metrics.Event;

import java.io.*;
import java.util.*;
import java.util.stream.IntStream;

import static core.CoreConstants.GameResult.*;

public class CCForwardModel extends StandardForwardModel {

    private static boolean isPlayingColour(Peg.Colour col, CCGameState state) {
        Map<Peg.Colour, Set<Peg.Colour>> playableColors = new HashMap<>();
        playableColors.put(Peg.Colour.purple, new HashSet<>(Arrays.asList(Peg.Colour.purple, Peg.Colour.red, Peg.Colour.neutral)));
        playableColors.put(Peg.Colour.blue, new HashSet<>(Arrays.asList(Peg.Colour.blue, Peg.Colour.orange, Peg.Colour.neutral)));
        playableColors.put(Peg.Colour.yellow, new HashSet<>(Arrays.asList(Peg.Colour.yellow, Peg.Colour.green, Peg.Colour.neutral)));
        playableColors.put(Peg.Colour.red, new HashSet<>(Arrays.asList(Peg.Colour.red, Peg.Colour.purple, Peg.Colour.neutral)));
        playableColors.put(Peg.Colour.orange, new HashSet<>(Arrays.asList(Peg.Colour.orange, Peg.Colour.blue, Peg.Colour.neutral)));
        playableColors.put(Peg.Colour.green, new HashSet<>(Arrays.asList(Peg.Colour.green, Peg.Colour.yellow, Peg.Colour.neutral)));

        Peg.Colour currentPlayerColour = state.getPlayerColour(state.getCurrentPlayer());
        return playableColors.getOrDefault(currentPlayerColour, Collections.emptySet()).contains(col);
    }

    @Override
    protected void _setup(AbstractGameState firstState) {
        // TODO: perform initialization of variables and game setup
        System.out.println("");
        CCParameters params = (CCParameters) firstState.getGameParameters();
        CCGameState state = (CCGameState) firstState;

        state.starBoard = new StarBoard();

        testing(state);
        if (state.getNPlayers() == 2) {
            //loadPegs2Player(state);
        }
        if (state.getNPlayers() == 3) {
            loadPegs3Player(state);
        }
        if (state.getNPlayers() == 4) {
            loadPegs4Player(state);
        }
        if (state.getNPlayers() == 6) {
            loadPegs6Player(state);
        }
    }

    @Override
    protected List<AbstractAction> _computeAvailableActions(AbstractGameState gameState) {
        CCGameState state = (CCGameState) gameState;
        List<AbstractAction> actions = new ArrayList<>();
        int player = gameState.getCurrentPlayer();

       // System.out.println("player id "+ player);

        loadPlayerActions(player, state, actions);
        return actions;
    }

    private static CCNode neighbourInDirection(CCNode node, int dir, CCGameState state) {
        for (CCNode neig : node.getNeighbours()) {
            if (node.getNeighbourSideMapping().get(neig) == dir) {
                return neig;
            }
        }
        return null;
    }

    private static boolean isPlayerPlacable(Peg.Colour col, Peg.Colour playerCol) {
        return col == playerCol ||
                col == Peg.Colour.values()[(playerCol.ordinal() + 3) % 6] || //opposite
                col == Peg.Colour.neutral;
    }

    private void loadPlayerActions(int player, CCGameState state, List<AbstractAction> actions){
        // Player Purple
        //player index to colour
        Peg.Colour playerCol = state.getPlayerColour(player);
        for (CCNode node : state.starBoard.getBoardNodes()) { // Check all Nodes
            if (node.getOccupiedPeg() != null && node.getOccupiedPeg().getColour() == playerCol) {
                exploreNodeAction(node, actions, state);
            }
        }

    }
    private static void repeatAction(CCNode node, List<AbstractAction> actions, Peg.Colour playerCol, CCGameState state) {
        HashSet<CCNode> visited = new HashSet<CCNode>();
        HashSet<CCNode> toVisit = new HashSet<CCNode>();
        toVisit.add(node);

        Peg.Colour oppositeCol = Peg.Colour.values()[(playerCol.ordinal() + 3) % 6];
        while (!toVisit.isEmpty()) {
            CCNode expNode = toVisit.iterator().next();
            visited.add(expNode);
            toVisit.remove(expNode);
            boolean canLeaveZone = expNode.getBaseColour() != oppositeCol;
            for (CCNode neighbour : expNode.getNeighbours()) {
                int side = expNode.getNeighbourSideMapping().get(neighbour);
                if (neighbour.isNodeOccupied()) {
                    CCNode stride = neighbourInDirection(neighbour, side, state);
                    if (stride != null && !stride.isNodeOccupied() &&
                            (canLeaveZone || stride.getBaseColour() == oppositeCol) &&
                            !visited.contains(stride)) {
                        toVisit.add(stride);
                    }
                }
            }
        }
        visited.remove(node);
        visited.removeIf(n -> (!isPlayerPlacable(n.getBaseColour(), playerCol)));
        for (CCNode v : visited) {
            MovePeg action = new MovePeg(node.getID(), v.getID());
            if(!actions.contains(action)){
                actions.add(action);
            }
        }
    }

    private static void exploreNodeAction(CCNode node, List<AbstractAction> actions, CCGameState state) {
        Peg.Colour playerCol = node.getOccupiedPeg().getColour();
        for (CCNode nei_0: node.getNeighbours()) {
            if (nei_0.isNodeOccupied()) {
                repeatAction(node, actions, playerCol, state);
            }
            else if (isPlayingColour(nei_0.getBaseColour(), state)) {
                if (node.getOccupiedPeg().getInDestination() == true) {
                    if (nei_0.getBaseColour() != Peg.Colour.neutral) {
                        MovePeg action = new MovePeg(node.getID(), nei_0.getID());
                        if(!actions.contains(action)){
                            actions.add(action);
                        }
                    }
                } else {
                    MovePeg action = new MovePeg(node.getID(), nei_0.getID());
                    if(!actions.contains(action)){
                        actions.add(action);
                    }
                }
            }
        }
    }

    @Override
    protected void endGame(AbstractGameState gs) {
//        CCGameState state = (CCGameState) gs;
//        state.setGameStatus(CoreConstants.GameResult.GAME_END);
//
//        state.setPlayerResult(WIN_GAME, 0);
//        state.setPlayerResult(LOSE_GAME, 1);
    }

    @Override
    protected void _afterAction(AbstractGameState currentState, AbstractAction action) {
        CCGameState state = (CCGameState) currentState;
        List<AbstractAction> actions = new ArrayList<>();

        for(int i = 0; i < ((CCGameState) currentState).getStarBoard().getSize(); i++){
            if(((CCGameState) currentState).getStarBoard().getBoardNodes().get(i).isNodeOccupied()){
                // Check if purple is in red base
                if(((CCGameState) currentState).getStarBoard().getBoardNodes().get(i).getOccupiedPeg().getColour() == Peg.Colour.purple){
                    if(((CCGameState) currentState).getStarBoard().getBoardNodes().get(i).getBaseColour() == Peg.Colour.red){
                        ((CCGameState) currentState).getStarBoard().getBoardNodes().get(i).getOccupiedPeg().setInDestination(true);
                    }
                }
                // Check if yellow was in green base
                if(((CCGameState) currentState).getStarBoard().getBoardNodes().get(i).getOccupiedPeg().getColour() == Peg.Colour.yellow){
                    if(((CCGameState) currentState).getStarBoard().getBoardNodes().get(i).getBaseColour() == Peg.Colour.green){
                        ((CCGameState) currentState).getStarBoard().getBoardNodes().get(i).getOccupiedPeg().setInDestination(true);
                    }
                }
                // Check if red was in purple base
                if(((CCGameState) currentState).getStarBoard().getBoardNodes().get(i).getOccupiedPeg().getColour() == Peg.Colour.red){
                    if(((CCGameState) currentState).getStarBoard().getBoardNodes().get(i).getBaseColour() == Peg.Colour.purple){
                        ((CCGameState) currentState).getStarBoard().getBoardNodes().get(i).getOccupiedPeg().setInDestination(true);
                    }
                }
                // Check if orange was in blue base
                if(((CCGameState) currentState).getStarBoard().getBoardNodes().get(i).getOccupiedPeg().getColour() == Peg.Colour.orange){
                    if(((CCGameState) currentState).getStarBoard().getBoardNodes().get(i).getBaseColour() == Peg.Colour.blue){
                        ((CCGameState) currentState).getStarBoard().getBoardNodes().get(i).getOccupiedPeg().setInDestination(true);
                    }
                }
                // Check if green was in purple yellow
                if(((CCGameState) currentState).getStarBoard().getBoardNodes().get(i).getOccupiedPeg().getColour() == Peg.Colour.green){
                    if(((CCGameState) currentState).getStarBoard().getBoardNodes().get(i).getBaseColour() == Peg.Colour.yellow){
                        ((CCGameState) currentState).getStarBoard().getBoardNodes().get(i).getOccupiedPeg().setInDestination(true);
                    }
                }
            }
        }
        if (checkWinConditionPurple((CCGameState) state)) {
            int nPlayers = state.getNPlayers();
            switch (nPlayers) {
                case 2:
                    state.setGameStatus(CoreConstants.GameResult.GAME_END);
                    state.setPlayerResult(WIN_GAME, 0);
                    state.setPlayerResult(LOSE_GAME, 1);
                    break;
                case 3:
                    // 0 == purple, 1 == yellow, 2 == orange
                    state.setGameStatus(CoreConstants.GameResult.GAME_END);
                    state.setPlayerResult(WIN_GAME, 0);
                    state.setPlayerResult(LOSE_GAME, 1);
                    state.setPlayerResult(LOSE_GAME, 2);
                    break;
                case 4:
                    state.setGameStatus(CoreConstants.GameResult.GAME_END);
                    state.setPlayerResult(WIN_GAME, 0);
                    state.setPlayerResult(LOSE_GAME, 1);
                    state.setPlayerResult(LOSE_GAME, 2);
                    state.setPlayerResult(LOSE_GAME, 3);
                    break;
                case 6:
                    state.setGameStatus(CoreConstants.GameResult.GAME_END);
                    state.setPlayerResult(WIN_GAME, 0);
                    state.setPlayerResult(LOSE_GAME, 1);
                    state.setPlayerResult(LOSE_GAME, 2);
                    state.setPlayerResult(LOSE_GAME, 3);
                    state.setPlayerResult(LOSE_GAME, 4);
                    state.setPlayerResult(LOSE_GAME, 5);
                    break;
            }
        }
        if (checkWinConditionYellow((CCGameState) state)) {
            int nPlayers = state.getNPlayers();
            switch (nPlayers) {
                case 3:
                    // 0 == purple, 1 == yellow, 2 == orange
                    state.setGameStatus(CoreConstants.GameResult.GAME_END);
                    state.setPlayerResult(WIN_GAME, 1);
                    state.setPlayerResult(LOSE_GAME, 0);
                    state.setPlayerResult(LOSE_GAME, 2);
                    break;
                case 4:
                    state.setGameStatus(CoreConstants.GameResult.GAME_END);
                    state.setPlayerResult(WIN_GAME, 1);
                    state.setPlayerResult(LOSE_GAME, 0);
                    state.setPlayerResult(LOSE_GAME, 2);
                    state.setPlayerResult(LOSE_GAME, 3);
                    break;
                case 6:
                    state.setGameStatus(CoreConstants.GameResult.GAME_END);
                    state.setPlayerResult(WIN_GAME, 2);
                    state.setPlayerResult(LOSE_GAME, 0);
                    state.setPlayerResult(LOSE_GAME, 1);
                    state.setPlayerResult(LOSE_GAME, 3);
                    state.setPlayerResult(LOSE_GAME, 4);
                    state.setPlayerResult(LOSE_GAME, 5);
                    break;
            }
        }
        if (checkWinConditionRed((CCGameState) state)) {
            int nPlayers = state.getNPlayers();
            switch (nPlayers) {
                case 2:
                    state.setGameStatus(CoreConstants.GameResult.GAME_END);
                    state.setPlayerResult(WIN_GAME, 1);
                    state.setPlayerResult(LOSE_GAME, 0);
                    break;
                case 4:
                    state.setGameStatus(CoreConstants.GameResult.GAME_END);
                    state.setPlayerResult(WIN_GAME, 2);
                    state.setPlayerResult(LOSE_GAME, 0);
                    state.setPlayerResult(LOSE_GAME, 1);
                    state.setPlayerResult(LOSE_GAME, 3);
                    break;
                case 6:
                    state.setGameStatus(CoreConstants.GameResult.GAME_END);
                    state.setPlayerResult(WIN_GAME, 0);
                    state.setPlayerResult(LOSE_GAME, 1);
                    state.setPlayerResult(LOSE_GAME, 2);
                    state.setPlayerResult(LOSE_GAME, 3);
                    state.setPlayerResult(LOSE_GAME, 4);
                    state.setPlayerResult(LOSE_GAME, 5);
            }
        }
        if (checkWinConditionOrange((CCGameState) state)) {
            int nPlayers = state.getNPlayers();
            switch (nPlayers) {
                case 3:
                    // 0 == purple, 1 == yellow, 2 == orange
                    state.setGameStatus(CoreConstants.GameResult.GAME_END);
                    state.setPlayerResult(WIN_GAME, 2);
                    state.setPlayerResult(LOSE_GAME, 0);
                    state.setPlayerResult(LOSE_GAME, 1);
                    break;
                case 6:
                    int[] playersToSetFor6 = {0, 1, 2, 3, 5};
                    for (int player : playersToSetFor6) {
                        state.setPlayerResult(LOSE_GAME, player);
                    }
                    break;
            }
        }
        if (checkWinConditionGreen((CCGameState) state)) {
            int nPlayers = state.getNPlayers();
            switch (nPlayers) {
                case 4:
                    state.setGameStatus(CoreConstants.GameResult.GAME_END);
                    state.setPlayerResult(WIN_GAME, 3);
                    state.setPlayerResult(LOSE_GAME, 0);
                    state.setPlayerResult(LOSE_GAME, 1);
                    state.setPlayerResult(LOSE_GAME, 2);
                    break;
                case 6:
                    int[] playersToSetFor6 = {0, 1, 2, 3, 5};
                    for (int player : playersToSetFor6) {
                        state.setPlayerResult(LOSE_GAME, player);
                    }
                    break;
            }
        }
        endPlayerTurn(state);
    }

    private boolean checkWinConditionPurple(CCGameState state) {
        int counter = 0;
        boolean PegIn = false;
        List<CCNode> nodes = state.getStarBoard().getBoardNodes();
        for(int i = 111; i <= 120; i++){
            if(nodes.get(i).isNodeOccupied() && nodes.get(i).getOccupiedPeg().getColour() == Peg.Colour.purple){
                PegIn = true;
            }
            if(nodes.get(i).isNodeOccupied()){
                counter++;
            }
        }
        if(counter >= 5 && PegIn){
            return true;
        }
        return false;
    }

    private boolean checkWinConditionRed(CCGameState state) {
        int counter = 0;
        boolean PegIn = false;
        List<CCNode> nodes = state.getStarBoard().getBoardNodes();
        for(int i = 0; i <= 9; i++){
            if(nodes.get(i).isNodeOccupied() && nodes.get(i).getOccupiedPeg().getColour() == Peg.Colour.red){
                PegIn = true;
            }
            if(nodes.get(i).isNodeOccupied()){
                counter++;
            }
        }
        if(counter >= 10 && PegIn){
            return true;
        }
        return false;
    }

    private boolean checkWinConditionYellow(CCGameState state) {
        int counter = 0;
        boolean PegIn = false;
        List<CCNode> nodes = state.getStarBoard().getBoardNodes();
        int[] indices = {10, 11, 12, 13, 23, 24, 25, 35, 36, 46};
        for (int i : indices) {
            if (nodes.get(i).isNodeOccupied()) {
                if (nodes.get(i).getOccupiedPeg().getColour() == Peg.Colour.yellow) {
                    PegIn = true;
                }
                counter++;
            }
        }
        if(counter >= 5 && PegIn){
            return true;
        }
        return false;
    }

    private boolean checkWinConditionOrange(CCGameState state) {
        int counter = 0;
        boolean PegIn = false;
        List<CCNode> nodes = state.getStarBoard().getBoardNodes();
        int[] indices = {19, 20, 21, 22, 32, 33, 34, 44, 45, 55};
        for (int i : indices) {
            if (nodes.get(i).isNodeOccupied()) {
                if (nodes.get(i).getOccupiedPeg().getColour() == Peg.Colour.orange) {
                    PegIn = true;
                }
                counter++;
            }
        }
        if(counter >= 5 && PegIn){
            return true;
        }
        return false;
    }

    private boolean checkWinConditionGreen(CCGameState state) {
        int counter = 0;
        boolean PegIn = false;
        List<CCNode> nodes = state.getStarBoard().getBoardNodes();
        int[] indices = {74, 84, 85, 95, 96, 97, 107, 108, 109, 110};
        for (int i : indices) {
            if (nodes.get(i).isNodeOccupied()) {
                if (nodes.get(i).getOccupiedPeg().getColour() == Peg.Colour.green) {
                    PegIn = true;
                }
                counter++;
            }
        }
        if(counter >= 10 && PegIn){
            return true;
        }
        return false;
    }

    private void loadPegs2Player(CCGameState state) {
        loadPegsPurple(state);
        loadPegsRed(state);
    }

    private void loadPegs3Player(CCGameState state) {
        loadPegsPurple(state);
        loadPegsYellow(state);
        loadPegsOrange(state);
    }

    private void loadPegs4Player(CCGameState state) {
        loadPegsPurple(state);
        loadPegsYellow(state);
        loadPegsRed(state);
        loadPegsGreen(state);
    }

    private void loadPegs6Player(CCGameState state){
        loadPegsPurple(state);
        loadPegsRed(state);
        loadPegsGreen(state);
        loadPegsOrange(state);
        loadPegsYellow(state);
        loadPegsBlue(state);
    }

    private void loadPegsPurple(CCGameState state){
        for(int i = 0; i <= 9; i++){
            ((CCNode) state.starBoard.getBoardNodes().get(i)).setOccupiedPeg(new Peg(Peg.Colour.purple, (CCNode)state.starBoard.getBoardNodes().get(i)));
        }
    }

    private void loadPegsRed(CCGameState state){
        for(int i = 111; i <= 120; i++){
            ((CCNode) state.starBoard.getBoardNodes().get(i)).setOccupiedPeg(new Peg(Peg.Colour.red, (CCNode)state.starBoard.getBoardNodes().get(i)));
        }
    }

    private void loadPegsGreen(CCGameState state){
            for(int i = 10; i <= 13; i++){
                ((CCNode) state.starBoard.getBoardNodes().get(i)).setOccupiedPeg(new Peg(Peg.Colour.green, (CCNode)state.starBoard.getBoardNodes().get(i)));
            }
            for(int i = 23; i <= 25; i++){
                ((CCNode) state.starBoard.getBoardNodes().get(i)).setOccupiedPeg(new Peg(Peg.Colour.green, (CCNode)state.starBoard.getBoardNodes().get(i)));
            }
            for(int i = 35; i <= 36; i++){
                ((CCNode) state.starBoard.getBoardNodes().get(i)).setOccupiedPeg(new Peg(Peg.Colour.green, (CCNode)state.starBoard.getBoardNodes().get(i)));
            }
            ((CCNode) state.starBoard.getBoardNodes().get(46)).setOccupiedPeg(new Peg(Peg.Colour.green, (CCNode)state.starBoard.getBoardNodes().get(46)));
    }
//
    private void loadPegsOrange(CCGameState state){
        ((CCNode) state.starBoard.getBoardNodes().get(65)).setOccupiedPeg(new Peg(Peg.Colour.orange, (CCNode)state.starBoard.getBoardNodes().get(65)));
        for(int i = 75; i <= 76; i++){
            ((CCNode) state.starBoard.getBoardNodes().get(i)).setOccupiedPeg(new Peg(Peg.Colour.orange, (CCNode)state.starBoard.getBoardNodes().get(i)));
        }
        for(int i = 86; i <= 88; i++){
            ((CCNode) state.starBoard.getBoardNodes().get(i)).setOccupiedPeg(new Peg(Peg.Colour.orange, (CCNode)state.starBoard.getBoardNodes().get(i)));
        }
        for(int i = 98; i <= 101; i++){
            ((CCNode) state.starBoard.getBoardNodes().get(i)).setOccupiedPeg(new Peg(Peg.Colour.orange, (CCNode)state.starBoard.getBoardNodes().get(i)));
        }
    }

    private void loadPegsYellow(CCGameState state){
        ((CCNode) state.starBoard.getBoardNodes().get(74)).setOccupiedPeg(new Peg(Peg.Colour.yellow, (CCNode)state.starBoard.getBoardNodes().get(74)));
        for(int i = 84; i <= 85; i++){
            ((CCNode) state.starBoard.getBoardNodes().get(i)).setOccupiedPeg(new Peg(Peg.Colour.yellow, (CCNode)state.starBoard.getBoardNodes().get(i)));
        }
        for(int i = 95; i <= 97; i++){
            ((CCNode) state.starBoard.getBoardNodes().get(i)).setOccupiedPeg(new Peg(Peg.Colour.yellow, (CCNode)state.starBoard.getBoardNodes().get(i)));
        }
        for(int i = 107; i <= 110; i++){
            ((CCNode) state.starBoard.getBoardNodes().get(i)).setOccupiedPeg(new Peg(Peg.Colour.yellow, (CCNode)state.starBoard.getBoardNodes().get(i)));
        }
    }
//
    private void loadPegsBlue(CCGameState state){
        for(int i = 19; i <= 22; i++){
            ((CCNode) state.starBoard.getBoardNodes().get(i)).setOccupiedPeg(new Peg(Peg.Colour.blue, (CCNode)state.starBoard.getBoardNodes().get(i)));
        }
        for(int i = 32; i <= 34; i++){
            ((CCNode) state.starBoard.getBoardNodes().get(i)).setOccupiedPeg(new Peg(Peg.Colour.blue, (CCNode)state.starBoard.getBoardNodes().get(i)));
        }
        for(int i = 44; i <= 45; i++){
            ((CCNode) state.starBoard.getBoardNodes().get(i)).setOccupiedPeg(new Peg(Peg.Colour.blue, (CCNode)state.starBoard.getBoardNodes().get(i)));
        }
        ((CCNode) state.starBoard.getBoardNodes().get(55)).setOccupiedPeg(new Peg(Peg.Colour.blue, (CCNode)state.starBoard.getBoardNodes().get(55)));
    }

    public void testing(CCGameState state){
        ((CCNode) state.starBoard.getBoardNodes().get(61)).setOccupiedPeg(new Peg(Peg.Colour.red, (CCNode)state.starBoard.getBoardNodes().get(61)));
        ((CCNode) state.starBoard.getBoardNodes().get(89)).setOccupiedPeg(new Peg(Peg.Colour.red, (CCNode)state.starBoard.getBoardNodes().get(89)));
        ((CCNode) state.starBoard.getBoardNodes().get(59)).setOccupiedPeg(new Peg(Peg.Colour.red, (CCNode)state.starBoard.getBoardNodes().get(59)));
        ((CCNode) state.starBoard.getBoardNodes().get(57)).setOccupiedPeg(new Peg(Peg.Colour.red, (CCNode)state.starBoard.getBoardNodes().get(57)));
        ((CCNode) state.starBoard.getBoardNodes().get(63)).setOccupiedPeg(new Peg(Peg.Colour.red, (CCNode)state.starBoard.getBoardNodes().get(63)));
        ((CCNode) state.starBoard.getBoardNodes().get(66)).setOccupiedPeg(new Peg(Peg.Colour.red, (CCNode)state.starBoard.getBoardNodes().get(66)));
        ((CCNode) state.starBoard.getBoardNodes().get(54)).setOccupiedPeg(new Peg(Peg.Colour.red, (CCNode)state.starBoard.getBoardNodes().get(54)));
        ((CCNode) state.starBoard.getBoardNodes().get(60)).setOccupiedPeg(new Peg(Peg.Colour.purple, (CCNode)state.starBoard.getBoardNodes().get(54)));
//
//        ((CCNode) state.starBoard.getBoardNodes().get(27)).setOccupiedPeg(new Peg(Peg.Colour.yellow, (CCNode)state.starBoard.getBoardNodes().get(27)));
//        ((CCNode) state.starBoard.getBoardNodes().get(26)).setOccupiedPeg(new Peg(Peg.Colour.yellow, (CCNode)state.starBoard.getBoardNodes().get(26)));
    }
}
