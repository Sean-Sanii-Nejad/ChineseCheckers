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
        if(state.getNPlayers() == 2){
            return col == Peg.Colour.red ||
                    col == Peg.Colour.purple ||
                    col == Peg.Colour.neutral;
        }
        if(state.getNPlayers() == 3){
            return col == Peg.Colour.purple ||
                    col == Peg.Colour.orange ||
                    col == Peg.Colour.yellow ||
                    col == Peg.Colour.neutral;
        }
        return false;
    }

    @Override
    protected void _setup(AbstractGameState firstState) {
        // TODO: perform initialization of variables and game setup
        System.out.println("");
        CCParameters params = (CCParameters) firstState.getGameParameters();
        CCGameState state = (CCGameState) firstState;

        state.starBoard = new StarBoard();

        if (state.getNPlayers() == 2) {
            loadPegs2Player(state);
        }
        if (state.getNPlayers() == 3) {
            loadPegs3Player(state);
        }
        loadNodeBaseColours(state);
    }

    @Override
    protected List<AbstractAction> _computeAvailableActions(AbstractGameState gameState) {
        CCGameState state = (CCGameState) gameState;
        List<AbstractAction> actions = new ArrayList<>();
        int player = gameState.getCurrentPlayer();

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
                // Check if red was in purple base
                if(((CCGameState) currentState).getStarBoard().getBoardNodes().get(i).getOccupiedPeg().getColour() == Peg.Colour.red){
                    if(((CCGameState) currentState).getStarBoard().getBoardNodes().get(i).getBaseColour() == Peg.Colour.purple){
                        ((CCGameState) currentState).getStarBoard().getBoardNodes().get(i).getOccupiedPeg().setInDestination(true);
                    }
                }
            }
        }
        endPlayerTurn(state);
        checkWinConditionPurple((CCGameState) state);
        checkWinConditionRed((CCGameState) state);
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
        if(counter >= 10 && PegIn){
            state.setGameStatus(CoreConstants.GameResult.GAME_END);
            state.setPlayerResult(WIN_GAME, 0);
            state.setPlayerResult(LOSE_GAME, 1);
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
            state.setGameStatus(CoreConstants.GameResult.GAME_END);
            state.setPlayerResult(WIN_GAME, 1);
            state.setPlayerResult(LOSE_GAME, 0);
            return true;
        }
        return false;
    }

    private boolean checkWinConditionGreen(CCGameState state) {
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
        if(counter >= 10 && PegIn){
            state.setGameStatus(CoreConstants.GameResult.GAME_END);
            state.setPlayerResult(WIN_GAME, 1);
            state.setPlayerResult(LOSE_GAME, 0);
            state.setPlayerResult(LOSE_GAME, 2);
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
        if(counter >= 10 && PegIn){
            state.setGameStatus(CoreConstants.GameResult.GAME_END);
            state.setPlayerResult(WIN_GAME, 2);
            state.setPlayerResult(LOSE_GAME, 0);
            state.setPlayerResult(LOSE_GAME, 1);
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
//        loadPegsYellow(state);
        loadPegsRed(state);
        loadPegsPurple(state);
//        loadPegsGreen(state);
    }

    private void loadPegs6Player(CCGameState state){
        loadPegsPurple(state);
        loadPegsRed(state);
//        loadPegsGreen(state);
//        loadPegsOrange(state);
//        loadPegsYellow(state);
//        loadPegsBlue(state);
    }

    private void loadNodeBaseColours(CCGameState state){
        // Load Purple Nodes
        for(int i = 0; i <= 9; i++){
            ((CCNode) state.starBoard.getBoardNodes().get(i)).setColourNode(Peg.Colour.purple);
        }
        // Load Red Nodes
        for(int i = 111; i <= 120; i++){
            ((CCNode) state.starBoard.getBoardNodes().get(i)).setColourNode(Peg.Colour.red);
        }
        // Load Green Nodes
        for(int i = 10; i <= 13; i++){
            ((CCNode) state.starBoard.getBoardNodes().get(i)).setColourNode(Peg.Colour.green);
        }
        for(int i = 23; i <= 25; i++){
            ((CCNode) state.starBoard.getBoardNodes().get(i)).setColourNode(Peg.Colour.green);
        }
        for(int i = 35; i <= 36; i++){
            ((CCNode) state.starBoard.getBoardNodes().get(i)).setColourNode(Peg.Colour.green);
        }
        ((CCNode) state.starBoard.getBoardNodes().get(46)).setColourNode(Peg.Colour.green);
        // Load Blue Nodes
        for(int i = 19; i <= 22; i++){
            ((CCNode) state.starBoard.getBoardNodes().get(i)).setColourNode(Peg.Colour.blue);
        }
        for(int i = 32; i <= 34; i++){
            ((CCNode) state.starBoard.getBoardNodes().get(i)).setColourNode(Peg.Colour.blue);
        }
        for(int i = 44; i <= 45; i++){
            ((CCNode) state.starBoard.getBoardNodes().get(i)).setColourNode(Peg.Colour.blue);
        }
        ((CCNode) state.starBoard.getBoardNodes().get(55)).setColourNode(Peg.Colour.blue);
        // Load Orange Nodes
        ((CCNode) state.starBoard.getBoardNodes().get(65)).setColourNode(Peg.Colour.orange);
        for(int i = 75; i <= 76; i++){
            ((CCNode) state.starBoard.getBoardNodes().get(i)).setColourNode(Peg.Colour.orange);
        }
        for(int i = 86; i <= 88; i++){
            ((CCNode) state.starBoard.getBoardNodes().get(i)).setColourNode(Peg.Colour.orange);
        }
        for(int i = 98; i <= 101; i++){
            ((CCNode) state.starBoard.getBoardNodes().get(i)).setColourNode(Peg.Colour.orange);
        }
        // Load Yellow Nodes
        ((CCNode) state.starBoard.getBoardNodes().get(74)).setColourNode(Peg.Colour.yellow);
        for(int i = 84; i <= 85; i++){
            ((CCNode) state.starBoard.getBoardNodes().get(i)).setColourNode(Peg.Colour.yellow);
        }
        for(int i = 95; i <= 97; i++){
            ((CCNode) state.starBoard.getBoardNodes().get(i)).setColourNode(Peg.Colour.yellow);
        }
        for(int i = 107; i <= 110; i++){
            ((CCNode) state.starBoard.getBoardNodes().get(i)).setColourNode(Peg.Colour.yellow);
        }
    }

    private void loadPegsPurple(CCGameState state){
        for(int i = 0; i <= 9; i++){
            ((CCNode) state.starBoard.getBoardNodes().get(i)).setOccupiedPeg(new Peg(Peg.Colour.purple, (CCNode)state.starBoard.getBoardNodes().get(i)));
        }
    }

    private void loadPegsRed(CCGameState state){
        if(state.getNPlayers() == 2){
            for(int i = 111; i <= 120; i++){
                ((CCNode) state.starBoard.getBoardNodes().get(i)).setOccupiedPeg(new Peg(Peg.Colour.red, (CCNode)state.starBoard.getBoardNodes().get(i)));
            }
        }
        if(state.getNPlayers() == 3){
            for(int i = 111; i <= 120; i++){
                ((CCNode) state.starBoard.getBoardNodes().get(i)).setOccupiedPeg(new Peg(Peg.Colour.red, (CCNode)state.starBoard.getBoardNodes().get(i)));
            }
        }
        if(state.getNPlayers() == 4){

        }
        if(state.getNPlayers() == 6){

        }
    }

    private void loadPegsGreen(CCGameState state){
        if(state.getNPlayers() == 2){
            // Not included
        }
        if(state.getNPlayers() == 3){
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
        if(state.getNPlayers() == 4){

        }
        if(state.getNPlayers() == 6){

        }
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
        if(state.getNPlayers() == 2){
            // Not included
        }
        if(state.getNPlayers() == 3){

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
        if(state.getNPlayers() == 4){

        }
        if(state.getNPlayers() == 6){

        }
    }

    public void testing(CCGameState state){
        ((CCNode) state.starBoard.getBoardNodes().get(61)).setOccupiedPeg(new Peg(Peg.Colour.red, (CCNode)state.starBoard.getBoardNodes().get(61)));
        ((CCNode) state.starBoard.getBoardNodes().get(89)).setOccupiedPeg(new Peg(Peg.Colour.red, (CCNode)state.starBoard.getBoardNodes().get(89)));
        ((CCNode) state.starBoard.getBoardNodes().get(59)).setOccupiedPeg(new Peg(Peg.Colour.red, (CCNode)state.starBoard.getBoardNodes().get(59)));
        ((CCNode) state.starBoard.getBoardNodes().get(57)).setOccupiedPeg(new Peg(Peg.Colour.red, (CCNode)state.starBoard.getBoardNodes().get(57)));
        ((CCNode) state.starBoard.getBoardNodes().get(63)).setOccupiedPeg(new Peg(Peg.Colour.red, (CCNode)state.starBoard.getBoardNodes().get(63)));
        ((CCNode) state.starBoard.getBoardNodes().get(66)).setOccupiedPeg(new Peg(Peg.Colour.red, (CCNode)state.starBoard.getBoardNodes().get(66)));
        ((CCNode) state.starBoard.getBoardNodes().get(54)).setOccupiedPeg(new Peg(Peg.Colour.red, (CCNode)state.starBoard.getBoardNodes().get(54)));

        ((CCNode) state.starBoard.getBoardNodes().get(60)).setOccupiedPeg(new Peg(Peg.Colour.purple, (CCNode)state.starBoard.getBoardNodes().get(60)));
    }
}
