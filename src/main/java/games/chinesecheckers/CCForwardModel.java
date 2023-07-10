package games.chinesecheckers;

import core.AbstractGameState;
import core.CoreConstants;
import core.StandardForwardModel;
import core.actions.AbstractAction;
import core.components.BoardNode;
import core.components.GridBoard;
import games.chinesecheckers.actions.MovePeg;
import games.chinesecheckers.components.CCNode;
import games.chinesecheckers.components.Peg;
import games.chinesecheckers.components.StarBoard;
import games.stratego.StrategoGameState;
import games.tictactoe.TicTacToeGameState;
import gametemplate.actions.GTAction;
import org.apache.hadoop.shaded.org.checkerframework.checker.units.qual.C;
import org.apache.hadoop.yarn.webapp.hamlet2.Hamlet;

import java.io.*;
import java.util.*;

public class CCForwardModel extends StandardForwardModel {

    @Override
    protected void _setup(AbstractGameState firstState) {
        // TODO: perform initialization of variables and game setup
        System.out.println("");
        CCParameters params = (CCParameters) firstState.getGameParameters();
        CCGameState state = (CCGameState) firstState;
        state.starBoard = new StarBoard();

        //loadStarBoard("src/main/java/games/chinesecheckers/board_adj.csv", state);
        loadStarBoardManually(state);
        if(state.getNPlayers() == 2){
            loadPegs2Player(state);
        }

        loadNodeBaseColours(state);
//        if(state.getNPlayers() == 3){
//            loadPegs3Player(state);
//        }
//        testing(state);

        //loadXYCoordinatesStarBoard("src/main/java/games/chinesecheckers/boardMask.txt", state);

        // Testing
//        System.out.println(state.starBoard.getBoardNodes().get(0).getNeighbours());
//        Peg peg = ((CCNode)state.starBoard.getBoardNodes().get(0)).getOccupiedPeg();
//        if(peg != null) System.out.println(peg.getColour());
//        else System.out.println("No Occupied Peg on node");
//        System.out.println(((CCNode) state.starBoard.getBoardNodes().get(50)).getOccupiedPeg().getOccupiedNode());
//        System.out.println(state.starBoard.getBoardNodes().get(51).getNeighbourSideMapping().get(state.starBoard.getBoardNodes().get(27)));
//        System.out.println(((CCNode)state.starBoard.getBoardNodes().get(0)).getX() + " " + ((CCNode)state.starBoard.getBoardNodes().get(0)).getY());

    }

    @Override
    protected List<AbstractAction> _computeAvailableActions(AbstractGameState gameState) {
        CCGameState state = (CCGameState) gameState;
        List<AbstractAction> actions = new ArrayList<>();
        int player = gameState.getCurrentPlayer();

        System.out.println("Player: " +player);

        if(state.getNPlayers() == 2){
            load2PlayerActions(player, state, actions);
        }
        if (state.getNPlayers() == 3){
            load3PlayerActions(player, state, actions);
        }
//        if (state.getNPlayers() == 4){
//            load4PlayerActions(player, state, actions);
//        }
//        if (state.getNPlayers() == 6){
//            load6PlayerActions(player, state, actions);
//        }
        return actions;
    }

    @Override
    protected void endGame(AbstractGameState gs) {
        super.endGame(gs);
    }

    @Override
    protected void _afterAction(AbstractGameState currentState, AbstractAction action) {
        CCGameState state = (CCGameState) currentState;
        List<AbstractAction> actions = new ArrayList<>();

        if(!checkWinConditionPurple(state)){
            endPlayerTurn(currentState);
        }
        else {
            currentState.setPlayerResult(CoreConstants.GameResult.WIN_GAME, Peg.Colour2.purple.ordinal());
            currentState.setPlayerResult(CoreConstants.GameResult.LOSE_GAME, Peg.Colour2.red.ordinal());
            currentState.setGameStatus(CoreConstants.GameResult.GAME_END);
            endGame(state);
        }
    }

    private void load2PlayerActions(int player, CCGameState state, List<AbstractAction> actions){
        // Player Purple
        //System.out.println(((CCNode)state.starBoard.getBoardNodes().get(13)).getColour());
        BoardNode temp = null;
        if (player == Peg.Colour2.purple.ordinal()) {
            for (BoardNode node : state.starBoard.getBoardNodes()) { // Check all Nodes
                if (((CCNode) node).getOccupiedPeg() != null && ((CCNode) node).getOccupiedPeg().getColour2() == Peg.Colour2.purple) { // Check colour is purple & not null
                    for (BoardNode neighbourNode : node.getNeighbours()) { // loop neighbors of that Node
                        if (((CCNode)neighbourNode).isNodeOccupied()){ // If Neighbour has Peg on it
                            temp = node;
                            repeatAction(node, temp, actions, CCNode.Base.purple, CCNode.Base.red);
                        } else if(((CCNode) neighbourNode).getColour() == CCNode.Base.purple || ((CCNode) neighbourNode).getColour() == CCNode.Base.red || ((CCNode) neighbourNode).getColour() == CCNode.Base.neutral) {
                            actions.add(new MovePeg((CCNode) node, (CCNode) neighbourNode));
                        }
                    }
                }
            }
        }
        // Player Red
        temp = null;
        if (player == Peg.Colour2.red.ordinal()) {
            for (BoardNode node : state.starBoard.getBoardNodes()) { // Check all Nodes
                if (((CCNode) node).getOccupiedPeg() != null && ((CCNode) node).getOccupiedPeg().getColour2() == Peg.Colour2.red) { // Check colour is purple & not null
                    for (BoardNode neighbourNode : node.getNeighbours()) { // loop neighbors of that Node
                        if (((CCNode)neighbourNode).isNodeOccupied()){ // If Neighbour has Peg on it
                            temp = node;
                            repeatAction(node, temp, actions, CCNode.Base.red, CCNode.Base.purple);
                        } else if(((CCNode) neighbourNode).getColour() == CCNode.Base.red || ((CCNode) neighbourNode).getColour() == CCNode.Base.purple || ((CCNode) neighbourNode).getColour() == CCNode.Base.neutral) {
                            actions.add(new MovePeg((CCNode) node, (CCNode) neighbourNode));
                        }
                    }
                }
            }
        }
    }

    private void repeatAction(BoardNode node, BoardNode temp, List<AbstractAction> actions, CCNode.Base colour, CCNode.Base colour_opp){
        int processedNodes = 0;
        while(processedNodes < 125) {
            for(BoardNode neighbourNode : node.getNeighbours()){
                processedNodes++;
                if(((CCNode)neighbourNode).isNodeOccupied()){
                    for(BoardNode neighbourNode1 : neighbourNode.getNeighbours()){
                        if(node.getNeighbours().contains(neighbourNode) && neighbourNode.getNeighbours().contains(neighbourNode1)){
                            if(node.getNeighbourSideMapping().get(neighbourNode).intValue() == neighbourNode.getNeighbourSideMapping().get(neighbourNode1).intValue()){
                                if(!((CCNode)neighbourNode1).isNodeOccupied()) {
                                    MovePeg action = new MovePeg((CCNode) temp, (CCNode) neighbourNode1);
                                    node = neighbourNode1;
                                    if(((CCNode) neighbourNode1).getBaseColour() == colour || ((CCNode) neighbourNode1).getBaseColour() == colour_opp  || ((CCNode) neighbourNode1).getBaseColour() == CCNode.Base.neutral){
                                        if(!actions.contains(action)){
                                            actions.add(action);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void load3PlayerActions(int player, CCGameState state, List<AbstractAction> actions) {
        // Player Red
        if(player == Peg.Colour3.red.ordinal()){
            int index = 0;
            for(BoardNode node : state.starBoard.getBoardNodes()){
                CCNode nodeSpecial = (CCNode) node;
                if(nodeSpecial.getOccupiedPeg() != null){
                    if(nodeSpecial.getOccupiedPeg().getColour3() == Peg.Colour3.red){
                        Iterator<BoardNode> iterator = state.starBoard.getBoardNodes().get(index).getNeighbours().iterator();
                        while(iterator.hasNext()) {
                            CCNode ccNode = (CCNode) iterator.next();
                            if (ccNode.isNodeOccupied()) {
                                continue;
                            }
                            actions.add(new MovePeg((CCNode)state.starBoard.getBoardNodes().get(index), ccNode));
                        }
                    }
                }
                index++;
            }
        }

        // Player Blue
        if(player == Peg.Colour3.blue.ordinal()){
            int index = 0;
            for(BoardNode node : state.starBoard.getBoardNodes()){
                CCNode nodeSpecial = (CCNode) node;
                if(nodeSpecial.getOccupiedPeg() != null){
                    if(nodeSpecial.getOccupiedPeg().getColour3() == Peg.Colour3.blue){
                        Iterator<BoardNode> iterator = state.starBoard.getBoardNodes().get(index).getNeighbours().iterator();
                        while(iterator.hasNext()) {
                            CCNode ccNode = (CCNode) iterator.next();
                            if (ccNode.isNodeOccupied()) {
                                continue;
                            }
                            actions.add(new MovePeg((CCNode)state.starBoard.getBoardNodes().get(index), ccNode));
                        }
                    }
                }
                index++;
            }
        }

        // Player Green
        if(player == Peg.Colour3.green.ordinal()){
            int index = 0;
            for(BoardNode node : state.starBoard.getBoardNodes()){
                CCNode nodeSpecial = (CCNode) node;
                if(nodeSpecial.getOccupiedPeg() != null){
                    if(nodeSpecial.getOccupiedPeg().getColour3() == Peg.Colour3.green){
                        Iterator<BoardNode> iterator = state.starBoard.getBoardNodes().get(index).getNeighbours().iterator();
                        while(iterator.hasNext()) {
                            CCNode ccNode = (CCNode) iterator.next();
                            if (ccNode.isNodeOccupied()) {
                                // Check its neighbours -- Check if it's a straight line to jump
                                continue;
                            }
                            actions.add(new MovePeg((CCNode)state.starBoard.getBoardNodes().get(index), ccNode));
                        }
                    }
                }
                index++;
            }
        }
    }

    private void load4PlayerActions(int player, CCGameState state, List<AbstractAction> actions) {
    }

    private void load6PlayerActions(int player, CCGameState state, List<AbstractAction> actions) {
    }

    // Infinite Loop Crash - Apparently all players are terminal, but game state is not
    private boolean checkWinConditionPurple(CCGameState state) {
        int counter = 0;
        boolean checkOwn = false;
        for(int i = 111; i <= 120; i++){
            if(((CCNode) state.starBoard.getBoardNodes().get(i)).isNodeOccupied()){
                counter++;
                if(((CCNode) state.starBoard.getBoardNodes().get(i)).getOccupiedPeg().getColour2() == Peg.Colour2.purple){
                    checkOwn = true;
                }
            }
        }
        if(counter >= 9 && checkOwn == true){
            state.setGameStatus(CoreConstants.GameResult.GAME_END);
            state.setPlayerResult(CoreConstants.GameResult.WIN_GAME, Peg.Colour2.purple.ordinal());
            state.setPlayerResult(CoreConstants.GameResult.LOSE_GAME, Peg.Colour2.red.ordinal());
            return true;
        }
        return false;
    }

    // Currently Broken - Does not set XY Coordinates
    private void loadXYCoordinatesStarBoard(String filePath, CCGameState state){
        File file = new File(filePath);
        int index = 0;
        int indexX = 0;
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                for (int i = 0; i < line.length(); i++) {
                    char character = line.charAt(i);
                    indexX++;
                    if(character == 1){
                        ((CCNode)state.starBoard.getBoardNodes().get(index)).setCoordinates(indexX, i);
                        index++;
                    }
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadStarBoard(String csvFile, CCGameState state) {
        for(int i = 0; i < 121; i++) {state.starBoard.getBoardNodes().add(new CCNode(i));}
        BufferedReader reader = null;
        String line = "";
        int index = 0;
        try {
            reader = new BufferedReader(new FileReader(csvFile));
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                for (String value : values) {
                    state.starBoard.getBoardNodes().get(index).addNeighbour(state.starBoard.getBoardNodes().get(Integer.parseInt(value)),0);
                }
                index++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void loadPegs2Player(CCGameState state) {
        loadPegsPurple(state);
        loadPegsRed(state);
    }

    private void loadPegs3Player(CCGameState state) {
        loadPegsGreen(state);
        loadPegsBlue(state);
        loadPegsRed(state);
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
            ((CCNode) state.starBoard.getBoardNodes().get(i)).setColourNode(CCNode.Base.purple);
        }
        // Load Red Nodes
        for(int i = 111; i <= 120; i++){
            ((CCNode) state.starBoard.getBoardNodes().get(i)).setColourNode(CCNode.Base.red);
        }
        // Load Green Nodes
        for(int i = 10; i <= 13; i++){
            ((CCNode) state.starBoard.getBoardNodes().get(i)).setColourNode(CCNode.Base.green);
        }
        for(int i = 23; i <= 25; i++){
            ((CCNode) state.starBoard.getBoardNodes().get(i)).setColourNode(CCNode.Base.green);
        }
        for(int i = 35; i <= 36; i++){
            ((CCNode) state.starBoard.getBoardNodes().get(i)).setColourNode(CCNode.Base.green);
        }
        ((CCNode) state.starBoard.getBoardNodes().get(46)).setColourNode(CCNode.Base.green);
        // Load Blue Nodes
        for(int i = 19; i <= 22; i++){
            ((CCNode) state.starBoard.getBoardNodes().get(i)).setColourNode(CCNode.Base.blue);
        }
        for(int i = 32; i <= 34; i++){
            ((CCNode) state.starBoard.getBoardNodes().get(i)).setColourNode(CCNode.Base.blue);
        }
        for(int i = 44; i <= 45; i++){
            ((CCNode) state.starBoard.getBoardNodes().get(i)).setColourNode(CCNode.Base.blue);
        }
        ((CCNode) state.starBoard.getBoardNodes().get(55)).setColourNode(CCNode.Base.blue);
        // Load Orange Nodes
        ((CCNode) state.starBoard.getBoardNodes().get(65)).setColourNode(CCNode.Base.orange);
        for(int i = 75; i <= 76; i++){
            ((CCNode) state.starBoard.getBoardNodes().get(i)).setColourNode(CCNode.Base.orange);
        }
        for(int i = 86; i <= 88; i++){
            ((CCNode) state.starBoard.getBoardNodes().get(i)).setColourNode(CCNode.Base.orange);
        }
        for(int i = 98; i <= 101; i++){
            ((CCNode) state.starBoard.getBoardNodes().get(i)).setColourNode(CCNode.Base.orange);
        }
        // Load Yellow Nodes
        ((CCNode) state.starBoard.getBoardNodes().get(74)).setColourNode(CCNode.Base.yellow);
        for(int i = 84; i <= 85; i++){
            ((CCNode) state.starBoard.getBoardNodes().get(i)).setColourNode(CCNode.Base.yellow);
        }
        for(int i = 95; i <= 97; i++){
            ((CCNode) state.starBoard.getBoardNodes().get(i)).setColourNode(CCNode.Base.yellow);
        }
        for(int i = 107; i <= 110; i++){
            ((CCNode) state.starBoard.getBoardNodes().get(i)).setColourNode(CCNode.Base.yellow);
        }
    }

    private void loadPegsPurple(CCGameState state){
        if(state.getNPlayers() == 2){
            for(int i = 0; i <= 9; i++){
                ((CCNode) state.starBoard.getBoardNodes().get(i)).setOccupiedPeg(new Peg(Peg.Colour2.purple, (CCNode)state.starBoard.getBoardNodes().get(i)));
            }
        }
        if(state.getNPlayers() == 3){
            // Not Included
        }
        if(state.getNPlayers() == 4){

        }
        if(state.getNPlayers() == 6){

        }
    }

    private void loadPegsRed(CCGameState state){
        if(state.getNPlayers() == 2){
            for(int i = 111; i <= 120; i++){
                ((CCNode) state.starBoard.getBoardNodes().get(i)).setOccupiedPeg(new Peg(Peg.Colour2.red, (CCNode)state.starBoard.getBoardNodes().get(i)));
            }
        }
        if(state.getNPlayers() == 3){
            for(int i = 111; i <= 120; i++){
                ((CCNode) state.starBoard.getBoardNodes().get(i)).setOccupiedPeg(new Peg(Peg.Colour3.red, (CCNode)state.starBoard.getBoardNodes().get(i)));
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
                ((CCNode) state.starBoard.getBoardNodes().get(i)).setOccupiedPeg(new Peg(Peg.Colour3.green, (CCNode)state.starBoard.getBoardNodes().get(i)));
            }
            for(int i = 23; i <= 25; i++){
                ((CCNode) state.starBoard.getBoardNodes().get(i)).setOccupiedPeg(new Peg(Peg.Colour3.green, (CCNode)state.starBoard.getBoardNodes().get(i)));
            }
            for(int i = 35; i <= 36; i++){
                ((CCNode) state.starBoard.getBoardNodes().get(i)).setOccupiedPeg(new Peg(Peg.Colour3.green, (CCNode)state.starBoard.getBoardNodes().get(i)));
            }
            ((CCNode) state.starBoard.getBoardNodes().get(46)).setOccupiedPeg(new Peg(Peg.Colour3.green, (CCNode)state.starBoard.getBoardNodes().get(46)));
        }
        if(state.getNPlayers() == 4){

        }
        if(state.getNPlayers() == 6){

        }
    }
//
//    private void loadPegsOrange(CCGameState state){
//        ((CCNode) state.starBoard.getBoardNodes().get(65)).setOccupiedPeg(new Peg(Peg.Colour.orange, (CCNode)state.starBoard.getBoardNodes().get(65)));
//        for(int i = 75; i <= 76; i++){
//            ((CCNode) state.starBoard.getBoardNodes().get(i)).setOccupiedPeg(new Peg(Peg.Colour.orange, (CCNode)state.starBoard.getBoardNodes().get(i)));
//        }
//        for(int i = 86; i <= 88; i++){
//            ((CCNode) state.starBoard.getBoardNodes().get(i)).setOccupiedPeg(new Peg(Peg.Colour.orange, (CCNode)state.starBoard.getBoardNodes().get(i)));
//        }
//        for(int i = 98; i <= 101; i++){
//            ((CCNode) state.starBoard.getBoardNodes().get(i)).setOccupiedPeg(new Peg(Peg.Colour.orange, (CCNode)state.starBoard.getBoardNodes().get(i)));
//        }
//    }
//
//    private void loadPegsYellow(CCGameState state){
//        ((CCNode) state.starBoard.getBoardNodes().get(74)).setOccupiedPeg(new Peg(Peg.Colour.yellow, (CCNode)state.starBoard.getBoardNodes().get(74)));
//        for(int i = 84; i <= 85; i++){
//            ((CCNode) state.starBoard.getBoardNodes().get(i)).setOccupiedPeg(new Peg(Peg.Colour.yellow, (CCNode)state.starBoard.getBoardNodes().get(i)));
//        }
//        for(int i = 93; i <= 97; i++){
//            ((CCNode) state.starBoard.getBoardNodes().get(i)).setOccupiedPeg(new Peg(Peg.Colour.yellow, (CCNode)state.starBoard.getBoardNodes().get(i)));
//        }
//        for(int i = 107; i <= 110; i++){
//            ((CCNode) state.starBoard.getBoardNodes().get(i)).setOccupiedPeg(new Peg(Peg.Colour.yellow, (CCNode)state.starBoard.getBoardNodes().get(i)));
//        }
//    }
//
    private void loadPegsBlue(CCGameState state){
        if(state.getNPlayers() == 2){
            // Not included
        }
        if(state.getNPlayers() == 3){

            for(int i = 19; i <= 22; i++){
                ((CCNode) state.starBoard.getBoardNodes().get(i)).setOccupiedPeg(new Peg(Peg.Colour3.blue, (CCNode)state.starBoard.getBoardNodes().get(i)));
            }
            for(int i = 32; i <= 34; i++){
                ((CCNode) state.starBoard.getBoardNodes().get(i)).setOccupiedPeg(new Peg(Peg.Colour3.blue, (CCNode)state.starBoard.getBoardNodes().get(i)));
            }
            for(int i = 44; i <= 45; i++){
                ((CCNode) state.starBoard.getBoardNodes().get(i)).setOccupiedPeg(new Peg(Peg.Colour3.blue, (CCNode)state.starBoard.getBoardNodes().get(i)));
            }
            ((CCNode) state.starBoard.getBoardNodes().get(55)).setOccupiedPeg(new Peg(Peg.Colour3.blue, (CCNode)state.starBoard.getBoardNodes().get(55)));
        }
        if(state.getNPlayers() == 4){

        }
        if(state.getNPlayers() == 6){

        }
    }

    public void testing(CCGameState state){

        ((CCNode) state.starBoard.getBoardNodes().get(56)).setOccupiedPeg(new Peg(Peg.Colour2.purple, (CCNode)state.starBoard.getBoardNodes().get(56)));
        ((CCNode) state.starBoard.getBoardNodes().get(46)).setOccupiedPeg(new Peg(Peg.Colour2.red, (CCNode)state.starBoard.getBoardNodes().get(46)));
        ((CCNode) state.starBoard.getBoardNodes().get(24)).setOccupiedPeg(new Peg(Peg.Colour2.red, (CCNode)state.starBoard.getBoardNodes().get(24)));
        ((CCNode) state.starBoard.getBoardNodes().get(13)).setOccupiedPeg(new Peg(Peg.Colour2.red, (CCNode)state.starBoard.getBoardNodes().get(13)));


//        ((CCNode) state.starBoard.getBoardNodes().get(57)).setOccupiedPeg(new Peg(Peg.Colour2.red, (CCNode)state.starBoard.getBoardNodes().get(57)));
//        ((CCNode) state.starBoard.getBoardNodes().get(59)).setOccupiedPeg(new Peg(Peg.Colour2.red, (CCNode)state.starBoard.getBoardNodes().get(59)));
//        ((CCNode) state.starBoard.getBoardNodes().get(61)).setOccupiedPeg(new Peg(Peg.Colour2.red, (CCNode)state.starBoard.getBoardNodes().get(61)));
//        ((CCNode) state.starBoard.getBoardNodes().get(63)).setOccupiedPeg(new Peg(Peg.Colour2.red, (CCNode)state.starBoard.getBoardNodes().get(63)));
    }

    private void loadStarBoardManually(CCGameState state) {
        for(int i = 0; i < 121; i++) {state.starBoard.getBoardNodes().add(new CCNode(i));}
        ((CCNode)state.starBoard.getBoardNodes().get(0)).setCoordinates(6, 0);
        state.starBoard.getBoardNodes().get(0).addNeighbour(state.starBoard.getBoardNodes().get(1),3);
        state.starBoard.getBoardNodes().get(0).addNeighbour(state.starBoard.getBoardNodes().get(2),2);

        ((CCNode)state.starBoard.getBoardNodes().get(1)).setCoordinates(5, 1);
        state.starBoard.getBoardNodes().get(1).addNeighbour(state.starBoard.getBoardNodes().get(3), 3);
        state.starBoard.getBoardNodes().get(1).addNeighbour(state.starBoard.getBoardNodes().get(4), 2);
        state.starBoard.getBoardNodes().get(1).addNeighbour(state.starBoard.getBoardNodes().get(2), 1);
        state.starBoard.getBoardNodes().get(1).addNeighbour(state.starBoard.getBoardNodes().get(0), 0);

        ((CCNode)state.starBoard.getBoardNodes().get(2)).setCoordinates(6, 1);
        state.starBoard.getBoardNodes().get(2).addNeighbour(state.starBoard.getBoardNodes().get(4),3);
        state.starBoard.getBoardNodes().get(2).addNeighbour(state.starBoard.getBoardNodes().get(5),2);
        state.starBoard.getBoardNodes().get(2).addNeighbour(state.starBoard.getBoardNodes().get(0),5);
        state.starBoard.getBoardNodes().get(2).addNeighbour(state.starBoard.getBoardNodes().get(1),4);

        ((CCNode)state.starBoard.getBoardNodes().get(3)).setCoordinates(5, 2);
        state.starBoard.getBoardNodes().get(3).addNeighbour(state.starBoard.getBoardNodes().get(6),3);
        state.starBoard.getBoardNodes().get(3).addNeighbour(state.starBoard.getBoardNodes().get(7),2);
        state.starBoard.getBoardNodes().get(3).addNeighbour(state.starBoard.getBoardNodes().get(4),1);
        state.starBoard.getBoardNodes().get(3).addNeighbour(state.starBoard.getBoardNodes().get(1),0);

        ((CCNode)state.starBoard.getBoardNodes().get(4)).setCoordinates(6, 2);
        state.starBoard.getBoardNodes().get(4).addNeighbour(state.starBoard.getBoardNodes().get(7),3);
        state.starBoard.getBoardNodes().get(4).addNeighbour(state.starBoard.getBoardNodes().get(8),2);
        state.starBoard.getBoardNodes().get(4).addNeighbour(state.starBoard.getBoardNodes().get(5),1);
        state.starBoard.getBoardNodes().get(4).addNeighbour(state.starBoard.getBoardNodes().get(2),0);
        state.starBoard.getBoardNodes().get(4).addNeighbour(state.starBoard.getBoardNodes().get(1),5);
        state.starBoard.getBoardNodes().get(4).addNeighbour(state.starBoard.getBoardNodes().get(3),4);

        ((CCNode)state.starBoard.getBoardNodes().get(5)).setCoordinates(7, 2);
        state.starBoard.getBoardNodes().get(5).addNeighbour(state.starBoard.getBoardNodes().get(8),3);
        state.starBoard.getBoardNodes().get(5).addNeighbour(state.starBoard.getBoardNodes().get(9),2);
        state.starBoard.getBoardNodes().get(5).addNeighbour(state.starBoard.getBoardNodes().get(2),5);
        state.starBoard.getBoardNodes().get(5).addNeighbour(state.starBoard.getBoardNodes().get(4),4);

        ((CCNode)state.starBoard.getBoardNodes().get(6)).setCoordinates(4, 3);
        state.starBoard.getBoardNodes().get(6).addNeighbour(state.starBoard.getBoardNodes().get(14),3);
        state.starBoard.getBoardNodes().get(6).addNeighbour(state.starBoard.getBoardNodes().get(15),2);
        state.starBoard.getBoardNodes().get(6).addNeighbour(state.starBoard.getBoardNodes().get(7),1);
        state.starBoard.getBoardNodes().get(6).addNeighbour(state.starBoard.getBoardNodes().get(3),0);

        ((CCNode)state.starBoard.getBoardNodes().get(7)).setCoordinates(5, 3);
        state.starBoard.getBoardNodes().get(7).addNeighbour(state.starBoard.getBoardNodes().get(15),3);
        state.starBoard.getBoardNodes().get(7).addNeighbour(state.starBoard.getBoardNodes().get(16),2);
        state.starBoard.getBoardNodes().get(7).addNeighbour(state.starBoard.getBoardNodes().get(8),1);
        state.starBoard.getBoardNodes().get(7).addNeighbour(state.starBoard.getBoardNodes().get(4),0);
        state.starBoard.getBoardNodes().get(7).addNeighbour(state.starBoard.getBoardNodes().get(3),5);
        state.starBoard.getBoardNodes().get(7).addNeighbour(state.starBoard.getBoardNodes().get(6),4);

        ((CCNode)state.starBoard.getBoardNodes().get(8)).setCoordinates(6, 3);
        state.starBoard.getBoardNodes().get(8).addNeighbour(state.starBoard.getBoardNodes().get(16),3);
        state.starBoard.getBoardNodes().get(8).addNeighbour(state.starBoard.getBoardNodes().get(17),2);
        state.starBoard.getBoardNodes().get(8).addNeighbour(state.starBoard.getBoardNodes().get(9),1);
        state.starBoard.getBoardNodes().get(8).addNeighbour(state.starBoard.getBoardNodes().get(5),0);
        state.starBoard.getBoardNodes().get(8).addNeighbour(state.starBoard.getBoardNodes().get(4),5);
        state.starBoard.getBoardNodes().get(8).addNeighbour(state.starBoard.getBoardNodes().get(7),4);

        ((CCNode)state.starBoard.getBoardNodes().get(9)).setCoordinates(7, 3);
        state.starBoard.getBoardNodes().get(9).addNeighbour(state.starBoard.getBoardNodes().get(17),3);
        state.starBoard.getBoardNodes().get(9).addNeighbour(state.starBoard.getBoardNodes().get(18),2);
        state.starBoard.getBoardNodes().get(9).addNeighbour(state.starBoard.getBoardNodes().get(5),5);
        state.starBoard.getBoardNodes().get(9).addNeighbour(state.starBoard.getBoardNodes().get(8),4);

        ((CCNode)state.starBoard.getBoardNodes().get(10)).setCoordinates(0, 4);
        state.starBoard.getBoardNodes().get(10).addNeighbour(state.starBoard.getBoardNodes().get(11),1);
        state.starBoard.getBoardNodes().get(10).addNeighbour(state.starBoard.getBoardNodes().get(23),2);

        ((CCNode)state.starBoard.getBoardNodes().get(11)).setCoordinates(1, 4);
        state.starBoard.getBoardNodes().get(11).addNeighbour(state.starBoard.getBoardNodes().get(12),1);
        state.starBoard.getBoardNodes().get(11).addNeighbour(state.starBoard.getBoardNodes().get(24),2);
        state.starBoard.getBoardNodes().get(11).addNeighbour(state.starBoard.getBoardNodes().get(23),3);
        state.starBoard.getBoardNodes().get(11).addNeighbour(state.starBoard.getBoardNodes().get(10),4);

        ((CCNode)state.starBoard.getBoardNodes().get(12)).setCoordinates(2, 4);
        state.starBoard.getBoardNodes().get(12).addNeighbour(state.starBoard.getBoardNodes().get(13),1);
        state.starBoard.getBoardNodes().get(12).addNeighbour(state.starBoard.getBoardNodes().get(25),2);
        state.starBoard.getBoardNodes().get(12).addNeighbour(state.starBoard.getBoardNodes().get(24),3);
        state.starBoard.getBoardNodes().get(12).addNeighbour(state.starBoard.getBoardNodes().get(11),4);

        ((CCNode)state.starBoard.getBoardNodes().get(13)).setCoordinates(3, 4);
        state.starBoard.getBoardNodes().get(13).addNeighbour(state.starBoard.getBoardNodes().get(14),1);
        state.starBoard.getBoardNodes().get(13).addNeighbour(state.starBoard.getBoardNodes().get(26),2);
        state.starBoard.getBoardNodes().get(13).addNeighbour(state.starBoard.getBoardNodes().get(25),3);
        state.starBoard.getBoardNodes().get(13).addNeighbour(state.starBoard.getBoardNodes().get(12),4);

        ((CCNode)state.starBoard.getBoardNodes().get(14)).setCoordinates(4, 4);
        state.starBoard.getBoardNodes().get(14).addNeighbour(state.starBoard.getBoardNodes().get(6),0);
        state.starBoard.getBoardNodes().get(14).addNeighbour(state.starBoard.getBoardNodes().get(15),1);
        state.starBoard.getBoardNodes().get(14).addNeighbour(state.starBoard.getBoardNodes().get(27),2);
        state.starBoard.getBoardNodes().get(14).addNeighbour(state.starBoard.getBoardNodes().get(26),3);
        state.starBoard.getBoardNodes().get(14).addNeighbour(state.starBoard.getBoardNodes().get(13),4);

        ((CCNode)state.starBoard.getBoardNodes().get(15)).setCoordinates(5, 4);
        state.starBoard.getBoardNodes().get(15).addNeighbour(state.starBoard.getBoardNodes().get(7),0);
        state.starBoard.getBoardNodes().get(15).addNeighbour(state.starBoard.getBoardNodes().get(16),1);
        state.starBoard.getBoardNodes().get(15).addNeighbour(state.starBoard.getBoardNodes().get(28),2);
        state.starBoard.getBoardNodes().get(15).addNeighbour(state.starBoard.getBoardNodes().get(27),3);
        state.starBoard.getBoardNodes().get(15).addNeighbour(state.starBoard.getBoardNodes().get(14),4);
        state.starBoard.getBoardNodes().get(15).addNeighbour(state.starBoard.getBoardNodes().get(6),5);

        ((CCNode)state.starBoard.getBoardNodes().get(16)).setCoordinates(6, 4);
        state.starBoard.getBoardNodes().get(16).addNeighbour(state.starBoard.getBoardNodes().get(8),0);
        state.starBoard.getBoardNodes().get(16).addNeighbour(state.starBoard.getBoardNodes().get(17),1);
        state.starBoard.getBoardNodes().get(16).addNeighbour(state.starBoard.getBoardNodes().get(29),2);
        state.starBoard.getBoardNodes().get(16).addNeighbour(state.starBoard.getBoardNodes().get(28),3);
        state.starBoard.getBoardNodes().get(16).addNeighbour(state.starBoard.getBoardNodes().get(15),4);
        state.starBoard.getBoardNodes().get(16).addNeighbour(state.starBoard.getBoardNodes().get(7),5);

        ((CCNode)state.starBoard.getBoardNodes().get(17)).setCoordinates(7, 4);
        state.starBoard.getBoardNodes().get(17).addNeighbour(state.starBoard.getBoardNodes().get(9),0);
        state.starBoard.getBoardNodes().get(17).addNeighbour(state.starBoard.getBoardNodes().get(18),1);
        state.starBoard.getBoardNodes().get(17).addNeighbour(state.starBoard.getBoardNodes().get(30),2);
        state.starBoard.getBoardNodes().get(17).addNeighbour(state.starBoard.getBoardNodes().get(29),3);
        state.starBoard.getBoardNodes().get(17).addNeighbour(state.starBoard.getBoardNodes().get(16),4);
        state.starBoard.getBoardNodes().get(17).addNeighbour(state.starBoard.getBoardNodes().get(8),5);

        ((CCNode)state.starBoard.getBoardNodes().get(18)).setCoordinates(8, 4);
        state.starBoard.getBoardNodes().get(18).addNeighbour(state.starBoard.getBoardNodes().get(19),1);
        state.starBoard.getBoardNodes().get(18).addNeighbour(state.starBoard.getBoardNodes().get(31),2);
        state.starBoard.getBoardNodes().get(18).addNeighbour(state.starBoard.getBoardNodes().get(30),3);
        state.starBoard.getBoardNodes().get(18).addNeighbour(state.starBoard.getBoardNodes().get(17),4);
        state.starBoard.getBoardNodes().get(18).addNeighbour(state.starBoard.getBoardNodes().get(9),5);

        ((CCNode)state.starBoard.getBoardNodes().get(19)).setCoordinates(9, 4);
        state.starBoard.getBoardNodes().get(19).addNeighbour(state.starBoard.getBoardNodes().get(20),1);
        state.starBoard.getBoardNodes().get(19).addNeighbour(state.starBoard.getBoardNodes().get(32),2);
        state.starBoard.getBoardNodes().get(19).addNeighbour(state.starBoard.getBoardNodes().get(31),3);
        state.starBoard.getBoardNodes().get(19).addNeighbour(state.starBoard.getBoardNodes().get(18),4);

        ((CCNode)state.starBoard.getBoardNodes().get(20)).setCoordinates(10, 4);
        state.starBoard.getBoardNodes().get(20).addNeighbour(state.starBoard.getBoardNodes().get(21),1);
        state.starBoard.getBoardNodes().get(20).addNeighbour(state.starBoard.getBoardNodes().get(33),2);
        state.starBoard.getBoardNodes().get(20).addNeighbour(state.starBoard.getBoardNodes().get(32),3);
        state.starBoard.getBoardNodes().get(20).addNeighbour(state.starBoard.getBoardNodes().get(19),4);

        ((CCNode)state.starBoard.getBoardNodes().get(21)).setCoordinates(11, 4);
        state.starBoard.getBoardNodes().get(21).addNeighbour(state.starBoard.getBoardNodes().get(22),1);
        state.starBoard.getBoardNodes().get(21).addNeighbour(state.starBoard.getBoardNodes().get(34),2);
        state.starBoard.getBoardNodes().get(21).addNeighbour(state.starBoard.getBoardNodes().get(33),3);
        state.starBoard.getBoardNodes().get(21).addNeighbour(state.starBoard.getBoardNodes().get(20),4);

        ((CCNode)state.starBoard.getBoardNodes().get(22)).setCoordinates(12, 4);
        state.starBoard.getBoardNodes().get(22).addNeighbour(state.starBoard.getBoardNodes().get(34),3);
        state.starBoard.getBoardNodes().get(22).addNeighbour(state.starBoard.getBoardNodes().get(21),4);

        ((CCNode)state.starBoard.getBoardNodes().get(23)).setCoordinates(0, 5);
        state.starBoard.getBoardNodes().get(23).addNeighbour(state.starBoard.getBoardNodes().get(11),0);
        state.starBoard.getBoardNodes().get(23).addNeighbour(state.starBoard.getBoardNodes().get(24),1);
        state.starBoard.getBoardNodes().get(23).addNeighbour(state.starBoard.getBoardNodes().get(35),2);
        state.starBoard.getBoardNodes().get(23).addNeighbour(state.starBoard.getBoardNodes().get(10),5);

        ((CCNode)state.starBoard.getBoardNodes().get(24)).setCoordinates(1, 5);
        state.starBoard.getBoardNodes().get(24).addNeighbour(state.starBoard.getBoardNodes().get(12),0);
        state.starBoard.getBoardNodes().get(24).addNeighbour(state.starBoard.getBoardNodes().get(25),1);
        state.starBoard.getBoardNodes().get(24).addNeighbour(state.starBoard.getBoardNodes().get(36),2);
        state.starBoard.getBoardNodes().get(24).addNeighbour(state.starBoard.getBoardNodes().get(36),3);
        state.starBoard.getBoardNodes().get(24).addNeighbour(state.starBoard.getBoardNodes().get(23),4);
        state.starBoard.getBoardNodes().get(24).addNeighbour(state.starBoard.getBoardNodes().get(10),5);

        ((CCNode)state.starBoard.getBoardNodes().get(25)).setCoordinates(2, 5);
        state.starBoard.getBoardNodes().get(25).addNeighbour(state.starBoard.getBoardNodes().get(13),0);
        state.starBoard.getBoardNodes().get(25).addNeighbour(state.starBoard.getBoardNodes().get(26),1);
        state.starBoard.getBoardNodes().get(25).addNeighbour(state.starBoard.getBoardNodes().get(37),2);
        state.starBoard.getBoardNodes().get(25).addNeighbour(state.starBoard.getBoardNodes().get(36),3);
        state.starBoard.getBoardNodes().get(25).addNeighbour(state.starBoard.getBoardNodes().get(24),4);
        state.starBoard.getBoardNodes().get(25).addNeighbour(state.starBoard.getBoardNodes().get(11),5);

        ((CCNode)state.starBoard.getBoardNodes().get(26)).setCoordinates(3, 5);
        state.starBoard.getBoardNodes().get(26).addNeighbour(state.starBoard.getBoardNodes().get(14),0);
        state.starBoard.getBoardNodes().get(26).addNeighbour(state.starBoard.getBoardNodes().get(27),1);
        state.starBoard.getBoardNodes().get(26).addNeighbour(state.starBoard.getBoardNodes().get(38),2);
        state.starBoard.getBoardNodes().get(26).addNeighbour(state.starBoard.getBoardNodes().get(37),3);
        state.starBoard.getBoardNodes().get(26).addNeighbour(state.starBoard.getBoardNodes().get(25),4);
        state.starBoard.getBoardNodes().get(26).addNeighbour(state.starBoard.getBoardNodes().get(13),5);

        ((CCNode)state.starBoard.getBoardNodes().get(27)).setCoordinates(4, 5);
        state.starBoard.getBoardNodes().get(27).addNeighbour(state.starBoard.getBoardNodes().get(15),0);
        state.starBoard.getBoardNodes().get(27).addNeighbour(state.starBoard.getBoardNodes().get(28),1);
        state.starBoard.getBoardNodes().get(27).addNeighbour(state.starBoard.getBoardNodes().get(39),2);
        state.starBoard.getBoardNodes().get(27).addNeighbour(state.starBoard.getBoardNodes().get(38),3);
        state.starBoard.getBoardNodes().get(27).addNeighbour(state.starBoard.getBoardNodes().get(26),4);
        state.starBoard.getBoardNodes().get(27).addNeighbour(state.starBoard.getBoardNodes().get(14),5);

        ((CCNode)state.starBoard.getBoardNodes().get(28)).setCoordinates(5, 5);
        state.starBoard.getBoardNodes().get(28).addNeighbour(state.starBoard.getBoardNodes().get(16),0);
        state.starBoard.getBoardNodes().get(28).addNeighbour(state.starBoard.getBoardNodes().get(29),1);
        state.starBoard.getBoardNodes().get(28).addNeighbour(state.starBoard.getBoardNodes().get(40),2);
        state.starBoard.getBoardNodes().get(28).addNeighbour(state.starBoard.getBoardNodes().get(39),3);
        state.starBoard.getBoardNodes().get(28).addNeighbour(state.starBoard.getBoardNodes().get(27),4);
        state.starBoard.getBoardNodes().get(28).addNeighbour(state.starBoard.getBoardNodes().get(15),5);

        ((CCNode)state.starBoard.getBoardNodes().get(29)).setCoordinates(6, 5);
        state.starBoard.getBoardNodes().get(29).addNeighbour(state.starBoard.getBoardNodes().get(17),0);
        state.starBoard.getBoardNodes().get(29).addNeighbour(state.starBoard.getBoardNodes().get(30),1);
        state.starBoard.getBoardNodes().get(29).addNeighbour(state.starBoard.getBoardNodes().get(41),2);
        state.starBoard.getBoardNodes().get(29).addNeighbour(state.starBoard.getBoardNodes().get(40),3);
        state.starBoard.getBoardNodes().get(29).addNeighbour(state.starBoard.getBoardNodes().get(28),4);
        state.starBoard.getBoardNodes().get(29).addNeighbour(state.starBoard.getBoardNodes().get(16),5);

        ((CCNode)state.starBoard.getBoardNodes().get(30)).setCoordinates(7, 5);
        state.starBoard.getBoardNodes().get(30).addNeighbour(state.starBoard.getBoardNodes().get(18),0);
        state.starBoard.getBoardNodes().get(30).addNeighbour(state.starBoard.getBoardNodes().get(31),1);
        state.starBoard.getBoardNodes().get(30).addNeighbour(state.starBoard.getBoardNodes().get(42),2);
        state.starBoard.getBoardNodes().get(30).addNeighbour(state.starBoard.getBoardNodes().get(41),3);
        state.starBoard.getBoardNodes().get(30).addNeighbour(state.starBoard.getBoardNodes().get(29),4);
        state.starBoard.getBoardNodes().get(30).addNeighbour(state.starBoard.getBoardNodes().get(17),5);

        ((CCNode)state.starBoard.getBoardNodes().get(31)).setCoordinates(8, 5);
        state.starBoard.getBoardNodes().get(31).addNeighbour(state.starBoard.getBoardNodes().get(19),0);
        state.starBoard.getBoardNodes().get(31).addNeighbour(state.starBoard.getBoardNodes().get(32),1);
        state.starBoard.getBoardNodes().get(31).addNeighbour(state.starBoard.getBoardNodes().get(43),2);
        state.starBoard.getBoardNodes().get(31).addNeighbour(state.starBoard.getBoardNodes().get(42),3);
        state.starBoard.getBoardNodes().get(31).addNeighbour(state.starBoard.getBoardNodes().get(30),4);
        state.starBoard.getBoardNodes().get(31).addNeighbour(state.starBoard.getBoardNodes().get(18),5);

        ((CCNode)state.starBoard.getBoardNodes().get(32)).setCoordinates(9, 5);
        state.starBoard.getBoardNodes().get(32).addNeighbour(state.starBoard.getBoardNodes().get(20),0);
        state.starBoard.getBoardNodes().get(32).addNeighbour(state.starBoard.getBoardNodes().get(33),1);
        state.starBoard.getBoardNodes().get(32).addNeighbour(state.starBoard.getBoardNodes().get(44),2);
        state.starBoard.getBoardNodes().get(32).addNeighbour(state.starBoard.getBoardNodes().get(43),3);
        state.starBoard.getBoardNodes().get(32).addNeighbour(state.starBoard.getBoardNodes().get(31),4);
        state.starBoard.getBoardNodes().get(32).addNeighbour(state.starBoard.getBoardNodes().get(19),5);

        ((CCNode)state.starBoard.getBoardNodes().get(33)).setCoordinates(10, 5);
        state.starBoard.getBoardNodes().get(33).addNeighbour(state.starBoard.getBoardNodes().get(21),0);
        state.starBoard.getBoardNodes().get(33).addNeighbour(state.starBoard.getBoardNodes().get(34),1);
        state.starBoard.getBoardNodes().get(33).addNeighbour(state.starBoard.getBoardNodes().get(45),2);
        state.starBoard.getBoardNodes().get(33).addNeighbour(state.starBoard.getBoardNodes().get(44),3);
        state.starBoard.getBoardNodes().get(33).addNeighbour(state.starBoard.getBoardNodes().get(32),4);
        state.starBoard.getBoardNodes().get(33).addNeighbour(state.starBoard.getBoardNodes().get(20),5);

        ((CCNode)state.starBoard.getBoardNodes().get(34)).setCoordinates(11, 5);
        state.starBoard.getBoardNodes().get(34).addNeighbour(state.starBoard.getBoardNodes().get(22),0);
        state.starBoard.getBoardNodes().get(34).addNeighbour(state.starBoard.getBoardNodes().get(45),3);
        state.starBoard.getBoardNodes().get(34).addNeighbour(state.starBoard.getBoardNodes().get(33),4);
        state.starBoard.getBoardNodes().get(34).addNeighbour(state.starBoard.getBoardNodes().get(21),5);

        ((CCNode)state.starBoard.getBoardNodes().get(35)).setCoordinates(1, 6);
        state.starBoard.getBoardNodes().get(35).addNeighbour(state.starBoard.getBoardNodes().get(24),0);
        state.starBoard.getBoardNodes().get(35).addNeighbour(state.starBoard.getBoardNodes().get(36),1);
        state.starBoard.getBoardNodes().get(35).addNeighbour(state.starBoard.getBoardNodes().get(46),2);
        state.starBoard.getBoardNodes().get(35).addNeighbour(state.starBoard.getBoardNodes().get(23),5);

        ((CCNode)state.starBoard.getBoardNodes().get(36)).setCoordinates(2, 6);
        state.starBoard.getBoardNodes().get(36).addNeighbour(state.starBoard.getBoardNodes().get(25),0);
        state.starBoard.getBoardNodes().get(36).addNeighbour(state.starBoard.getBoardNodes().get(37),1);
        state.starBoard.getBoardNodes().get(36).addNeighbour(state.starBoard.getBoardNodes().get(47),2);
        state.starBoard.getBoardNodes().get(36).addNeighbour(state.starBoard.getBoardNodes().get(46),3);
        state.starBoard.getBoardNodes().get(36).addNeighbour(state.starBoard.getBoardNodes().get(35),4);
        state.starBoard.getBoardNodes().get(36).addNeighbour(state.starBoard.getBoardNodes().get(24),5);

        ((CCNode)state.starBoard.getBoardNodes().get(37)).setCoordinates(3, 6);
        state.starBoard.getBoardNodes().get(37).addNeighbour(state.starBoard.getBoardNodes().get(26),0);
        state.starBoard.getBoardNodes().get(37).addNeighbour(state.starBoard.getBoardNodes().get(38),1);
        state.starBoard.getBoardNodes().get(37).addNeighbour(state.starBoard.getBoardNodes().get(48),2);
        state.starBoard.getBoardNodes().get(37).addNeighbour(state.starBoard.getBoardNodes().get(47),3);
        state.starBoard.getBoardNodes().get(37).addNeighbour(state.starBoard.getBoardNodes().get(36),4);
        state.starBoard.getBoardNodes().get(37).addNeighbour(state.starBoard.getBoardNodes().get(25),5);

        ((CCNode)state.starBoard.getBoardNodes().get(38)).setCoordinates(4, 6);
        state.starBoard.getBoardNodes().get(38).addNeighbour(state.starBoard.getBoardNodes().get(27),0);
        state.starBoard.getBoardNodes().get(38).addNeighbour(state.starBoard.getBoardNodes().get(39),1);
        state.starBoard.getBoardNodes().get(38).addNeighbour(state.starBoard.getBoardNodes().get(49),2);
        state.starBoard.getBoardNodes().get(38).addNeighbour(state.starBoard.getBoardNodes().get(48),3);
        state.starBoard.getBoardNodes().get(38).addNeighbour(state.starBoard.getBoardNodes().get(37),4);
        state.starBoard.getBoardNodes().get(38).addNeighbour(state.starBoard.getBoardNodes().get(26),5);

        ((CCNode)state.starBoard.getBoardNodes().get(39)).setCoordinates(5, 6);
        state.starBoard.getBoardNodes().get(39).addNeighbour(state.starBoard.getBoardNodes().get(28),0);
        state.starBoard.getBoardNodes().get(39).addNeighbour(state.starBoard.getBoardNodes().get(40),1);
        state.starBoard.getBoardNodes().get(39).addNeighbour(state.starBoard.getBoardNodes().get(50),2);
        state.starBoard.getBoardNodes().get(39).addNeighbour(state.starBoard.getBoardNodes().get(49),3);
        state.starBoard.getBoardNodes().get(39).addNeighbour(state.starBoard.getBoardNodes().get(38),4);
        state.starBoard.getBoardNodes().get(39).addNeighbour(state.starBoard.getBoardNodes().get(27),5);

        ((CCNode)state.starBoard.getBoardNodes().get(40)).setCoordinates(6, 6);
        state.starBoard.getBoardNodes().get(40).addNeighbour(state.starBoard.getBoardNodes().get(29),0);
        state.starBoard.getBoardNodes().get(40).addNeighbour(state.starBoard.getBoardNodes().get(41),1);
        state.starBoard.getBoardNodes().get(40).addNeighbour(state.starBoard.getBoardNodes().get(51),2);
        state.starBoard.getBoardNodes().get(40).addNeighbour(state.starBoard.getBoardNodes().get(50),3);
        state.starBoard.getBoardNodes().get(40).addNeighbour(state.starBoard.getBoardNodes().get(39),4);
        state.starBoard.getBoardNodes().get(40).addNeighbour(state.starBoard.getBoardNodes().get(28),5);

        ((CCNode)state.starBoard.getBoardNodes().get(41)).setCoordinates(7, 6);
        state.starBoard.getBoardNodes().get(41).addNeighbour(state.starBoard.getBoardNodes().get(30),0);
        state.starBoard.getBoardNodes().get(41).addNeighbour(state.starBoard.getBoardNodes().get(42),1);
        state.starBoard.getBoardNodes().get(41).addNeighbour(state.starBoard.getBoardNodes().get(52),2);
        state.starBoard.getBoardNodes().get(41).addNeighbour(state.starBoard.getBoardNodes().get(51),3);
        state.starBoard.getBoardNodes().get(41).addNeighbour(state.starBoard.getBoardNodes().get(40),4);
        state.starBoard.getBoardNodes().get(41).addNeighbour(state.starBoard.getBoardNodes().get(29),5);

        ((CCNode)state.starBoard.getBoardNodes().get(42)).setCoordinates(8, 6);
        state.starBoard.getBoardNodes().get(42).addNeighbour(state.starBoard.getBoardNodes().get(31),0);
        state.starBoard.getBoardNodes().get(42).addNeighbour(state.starBoard.getBoardNodes().get(43),1);
        state.starBoard.getBoardNodes().get(42).addNeighbour(state.starBoard.getBoardNodes().get(53),2);
        state.starBoard.getBoardNodes().get(42).addNeighbour(state.starBoard.getBoardNodes().get(52),3);
        state.starBoard.getBoardNodes().get(42).addNeighbour(state.starBoard.getBoardNodes().get(41),4);
        state.starBoard.getBoardNodes().get(42).addNeighbour(state.starBoard.getBoardNodes().get(30),5);

        ((CCNode)state.starBoard.getBoardNodes().get(43)).setCoordinates(9, 6);
        state.starBoard.getBoardNodes().get(43).addNeighbour(state.starBoard.getBoardNodes().get(32),0);
        state.starBoard.getBoardNodes().get(43).addNeighbour(state.starBoard.getBoardNodes().get(44),1);
        state.starBoard.getBoardNodes().get(43).addNeighbour(state.starBoard.getBoardNodes().get(54),2);
        state.starBoard.getBoardNodes().get(43).addNeighbour(state.starBoard.getBoardNodes().get(53),3);
        state.starBoard.getBoardNodes().get(43).addNeighbour(state.starBoard.getBoardNodes().get(42),4);
        state.starBoard.getBoardNodes().get(43).addNeighbour(state.starBoard.getBoardNodes().get(31),5);

        ((CCNode)state.starBoard.getBoardNodes().get(44)).setCoordinates(10, 6);
        state.starBoard.getBoardNodes().get(44).addNeighbour(state.starBoard.getBoardNodes().get(33),0);
        state.starBoard.getBoardNodes().get(44).addNeighbour(state.starBoard.getBoardNodes().get(45),1);
        state.starBoard.getBoardNodes().get(44).addNeighbour(state.starBoard.getBoardNodes().get(55),2);
        state.starBoard.getBoardNodes().get(44).addNeighbour(state.starBoard.getBoardNodes().get(54),3);
        state.starBoard.getBoardNodes().get(44).addNeighbour(state.starBoard.getBoardNodes().get(43),4);
        state.starBoard.getBoardNodes().get(44).addNeighbour(state.starBoard.getBoardNodes().get(32),5);

        ((CCNode)state.starBoard.getBoardNodes().get(45)).setCoordinates(11, 6);
        state.starBoard.getBoardNodes().get(45).addNeighbour(state.starBoard.getBoardNodes().get(34),0);
        state.starBoard.getBoardNodes().get(45).addNeighbour(state.starBoard.getBoardNodes().get(55),3);
        state.starBoard.getBoardNodes().get(45).addNeighbour(state.starBoard.getBoardNodes().get(44),4);
        state.starBoard.getBoardNodes().get(45).addNeighbour(state.starBoard.getBoardNodes().get(33),5);

        ((CCNode)state.starBoard.getBoardNodes().get(46)).setCoordinates(1, 7);
        state.starBoard.getBoardNodes().get(46).addNeighbour(state.starBoard.getBoardNodes().get(36),0);
        state.starBoard.getBoardNodes().get(46).addNeighbour(state.starBoard.getBoardNodes().get(47),1);
        state.starBoard.getBoardNodes().get(46).addNeighbour(state.starBoard.getBoardNodes().get(56),2);
        state.starBoard.getBoardNodes().get(46).addNeighbour(state.starBoard.getBoardNodes().get(35),5);

        ((CCNode)state.starBoard.getBoardNodes().get(47)).setCoordinates(2, 7);
        state.starBoard.getBoardNodes().get(47).addNeighbour(state.starBoard.getBoardNodes().get(37),0);
        state.starBoard.getBoardNodes().get(47).addNeighbour(state.starBoard.getBoardNodes().get(48),1);
        state.starBoard.getBoardNodes().get(47).addNeighbour(state.starBoard.getBoardNodes().get(57),2);
        state.starBoard.getBoardNodes().get(47).addNeighbour(state.starBoard.getBoardNodes().get(56),3);
        state.starBoard.getBoardNodes().get(47).addNeighbour(state.starBoard.getBoardNodes().get(46),4);
        state.starBoard.getBoardNodes().get(47).addNeighbour(state.starBoard.getBoardNodes().get(36),5);

        ((CCNode)state.starBoard.getBoardNodes().get(48)).setCoordinates(3, 7);
        state.starBoard.getBoardNodes().get(48).addNeighbour(state.starBoard.getBoardNodes().get(38),0);
        state.starBoard.getBoardNodes().get(48).addNeighbour(state.starBoard.getBoardNodes().get(49),1);
        state.starBoard.getBoardNodes().get(48).addNeighbour(state.starBoard.getBoardNodes().get(58),2);
        state.starBoard.getBoardNodes().get(48).addNeighbour(state.starBoard.getBoardNodes().get(57),3);
        state.starBoard.getBoardNodes().get(48).addNeighbour(state.starBoard.getBoardNodes().get(47),4);
        state.starBoard.getBoardNodes().get(48).addNeighbour(state.starBoard.getBoardNodes().get(37),5);

        ((CCNode)state.starBoard.getBoardNodes().get(49)).setCoordinates(4, 7);
        state.starBoard.getBoardNodes().get(49).addNeighbour(state.starBoard.getBoardNodes().get(39),0);
        state.starBoard.getBoardNodes().get(49).addNeighbour(state.starBoard.getBoardNodes().get(50),1);
        state.starBoard.getBoardNodes().get(49).addNeighbour(state.starBoard.getBoardNodes().get(59),2);
        state.starBoard.getBoardNodes().get(49).addNeighbour(state.starBoard.getBoardNodes().get(58),3);
        state.starBoard.getBoardNodes().get(49).addNeighbour(state.starBoard.getBoardNodes().get(48),4);
        state.starBoard.getBoardNodes().get(49).addNeighbour(state.starBoard.getBoardNodes().get(38),5);

        ((CCNode)state.starBoard.getBoardNodes().get(50)).setCoordinates(5, 7);
        state.starBoard.getBoardNodes().get(50).addNeighbour(state.starBoard.getBoardNodes().get(40),0);
        state.starBoard.getBoardNodes().get(50).addNeighbour(state.starBoard.getBoardNodes().get(51),1);
        state.starBoard.getBoardNodes().get(50).addNeighbour(state.starBoard.getBoardNodes().get(60),2);
        state.starBoard.getBoardNodes().get(50).addNeighbour(state.starBoard.getBoardNodes().get(59),3);
        state.starBoard.getBoardNodes().get(50).addNeighbour(state.starBoard.getBoardNodes().get(49),4);
        state.starBoard.getBoardNodes().get(50).addNeighbour(state.starBoard.getBoardNodes().get(39),5);

        ((CCNode)state.starBoard.getBoardNodes().get(51)).setCoordinates(6, 7);
        state.starBoard.getBoardNodes().get(51).addNeighbour(state.starBoard.getBoardNodes().get(41),0);
        state.starBoard.getBoardNodes().get(51).addNeighbour(state.starBoard.getBoardNodes().get(52),1);
        state.starBoard.getBoardNodes().get(51).addNeighbour(state.starBoard.getBoardNodes().get(61),2);
        state.starBoard.getBoardNodes().get(51).addNeighbour(state.starBoard.getBoardNodes().get(60),3);
        state.starBoard.getBoardNodes().get(51).addNeighbour(state.starBoard.getBoardNodes().get(50),4);
        state.starBoard.getBoardNodes().get(51).addNeighbour(state.starBoard.getBoardNodes().get(40),5);

        ((CCNode)state.starBoard.getBoardNodes().get(52)).setCoordinates(7, 7);
        state.starBoard.getBoardNodes().get(52).addNeighbour(state.starBoard.getBoardNodes().get(42),0);
        state.starBoard.getBoardNodes().get(52).addNeighbour(state.starBoard.getBoardNodes().get(53),1);
        state.starBoard.getBoardNodes().get(52).addNeighbour(state.starBoard.getBoardNodes().get(62),2);
        state.starBoard.getBoardNodes().get(52).addNeighbour(state.starBoard.getBoardNodes().get(61),3);
        state.starBoard.getBoardNodes().get(52).addNeighbour(state.starBoard.getBoardNodes().get(51),4);
        state.starBoard.getBoardNodes().get(52).addNeighbour(state.starBoard.getBoardNodes().get(41),5);

        ((CCNode)state.starBoard.getBoardNodes().get(53)).setCoordinates(8, 7);
        state.starBoard.getBoardNodes().get(53).addNeighbour(state.starBoard.getBoardNodes().get(43),0);
        state.starBoard.getBoardNodes().get(53).addNeighbour(state.starBoard.getBoardNodes().get(54),1);
        state.starBoard.getBoardNodes().get(53).addNeighbour(state.starBoard.getBoardNodes().get(63),2);
        state.starBoard.getBoardNodes().get(53).addNeighbour(state.starBoard.getBoardNodes().get(62),3);
        state.starBoard.getBoardNodes().get(53).addNeighbour(state.starBoard.getBoardNodes().get(52),4);
        state.starBoard.getBoardNodes().get(53).addNeighbour(state.starBoard.getBoardNodes().get(42),5);

        ((CCNode)state.starBoard.getBoardNodes().get(54)).setCoordinates(9, 7);
        state.starBoard.getBoardNodes().get(54).addNeighbour(state.starBoard.getBoardNodes().get(44),0);
        state.starBoard.getBoardNodes().get(54).addNeighbour(state.starBoard.getBoardNodes().get(55),1);
        state.starBoard.getBoardNodes().get(54).addNeighbour(state.starBoard.getBoardNodes().get(64),2);
        state.starBoard.getBoardNodes().get(54).addNeighbour(state.starBoard.getBoardNodes().get(63),3);
        state.starBoard.getBoardNodes().get(54).addNeighbour(state.starBoard.getBoardNodes().get(53),4);
        state.starBoard.getBoardNodes().get(54).addNeighbour(state.starBoard.getBoardNodes().get(43),5);

        ((CCNode)state.starBoard.getBoardNodes().get(55)).setCoordinates(10, 7);
        state.starBoard.getBoardNodes().get(55).addNeighbour(state.starBoard.getBoardNodes().get(45),0);
        state.starBoard.getBoardNodes().get(55).addNeighbour(state.starBoard.getBoardNodes().get(64),3);
        state.starBoard.getBoardNodes().get(55).addNeighbour(state.starBoard.getBoardNodes().get(54),4);
        state.starBoard.getBoardNodes().get(55).addNeighbour(state.starBoard.getBoardNodes().get(44),5);

        ((CCNode)state.starBoard.getBoardNodes().get(56)).setCoordinates(2, 8);
        state.starBoard.getBoardNodes().get(56).addNeighbour(state.starBoard.getBoardNodes().get(47),0);
        state.starBoard.getBoardNodes().get(56).addNeighbour(state.starBoard.getBoardNodes().get(57),1);
        state.starBoard.getBoardNodes().get(56).addNeighbour(state.starBoard.getBoardNodes().get(66),2);
        state.starBoard.getBoardNodes().get(56).addNeighbour(state.starBoard.getBoardNodes().get(65),3);
        state.starBoard.getBoardNodes().get(56).addNeighbour(state.starBoard.getBoardNodes().get(46),5);

        ((CCNode)state.starBoard.getBoardNodes().get(57)).setCoordinates(3, 8);
        state.starBoard.getBoardNodes().get(57).addNeighbour(state.starBoard.getBoardNodes().get(48),0);
        state.starBoard.getBoardNodes().get(57).addNeighbour(state.starBoard.getBoardNodes().get(58),1);
        state.starBoard.getBoardNodes().get(57).addNeighbour(state.starBoard.getBoardNodes().get(67),2);
        state.starBoard.getBoardNodes().get(57).addNeighbour(state.starBoard.getBoardNodes().get(66),3);
        state.starBoard.getBoardNodes().get(57).addNeighbour(state.starBoard.getBoardNodes().get(56),4);
        state.starBoard.getBoardNodes().get(57).addNeighbour(state.starBoard.getBoardNodes().get(47),5);

        ((CCNode)state.starBoard.getBoardNodes().get(58)).setCoordinates(4, 8);
        state.starBoard.getBoardNodes().get(58).addNeighbour(state.starBoard.getBoardNodes().get(49),0);
        state.starBoard.getBoardNodes().get(58).addNeighbour(state.starBoard.getBoardNodes().get(59),1);
        state.starBoard.getBoardNodes().get(58).addNeighbour(state.starBoard.getBoardNodes().get(68),2);
        state.starBoard.getBoardNodes().get(58).addNeighbour(state.starBoard.getBoardNodes().get(67),3);
        state.starBoard.getBoardNodes().get(58).addNeighbour(state.starBoard.getBoardNodes().get(57),4);
        state.starBoard.getBoardNodes().get(58).addNeighbour(state.starBoard.getBoardNodes().get(48),5);

        ((CCNode)state.starBoard.getBoardNodes().get(59)).setCoordinates(5, 8);
        state.starBoard.getBoardNodes().get(59).addNeighbour(state.starBoard.getBoardNodes().get(50),0);
        state.starBoard.getBoardNodes().get(59).addNeighbour(state.starBoard.getBoardNodes().get(60),1);
        state.starBoard.getBoardNodes().get(59).addNeighbour(state.starBoard.getBoardNodes().get(69),2);
        state.starBoard.getBoardNodes().get(59).addNeighbour(state.starBoard.getBoardNodes().get(68),3);
        state.starBoard.getBoardNodes().get(59).addNeighbour(state.starBoard.getBoardNodes().get(58),4);
        state.starBoard.getBoardNodes().get(59).addNeighbour(state.starBoard.getBoardNodes().get(49),5);

        ((CCNode)state.starBoard.getBoardNodes().get(60)).setCoordinates(6, 8);
        state.starBoard.getBoardNodes().get(60).addNeighbour(state.starBoard.getBoardNodes().get(51),0);
        state.starBoard.getBoardNodes().get(60).addNeighbour(state.starBoard.getBoardNodes().get(61),1);
        state.starBoard.getBoardNodes().get(60).addNeighbour(state.starBoard.getBoardNodes().get(70),2);
        state.starBoard.getBoardNodes().get(60).addNeighbour(state.starBoard.getBoardNodes().get(69),3);
        state.starBoard.getBoardNodes().get(60).addNeighbour(state.starBoard.getBoardNodes().get(59),4);
        state.starBoard.getBoardNodes().get(60).addNeighbour(state.starBoard.getBoardNodes().get(50),5);

        ((CCNode)state.starBoard.getBoardNodes().get(61)).setCoordinates(7, 8);
        state.starBoard.getBoardNodes().get(61).addNeighbour(state.starBoard.getBoardNodes().get(52),0);
        state.starBoard.getBoardNodes().get(61).addNeighbour(state.starBoard.getBoardNodes().get(62),1);
        state.starBoard.getBoardNodes().get(61).addNeighbour(state.starBoard.getBoardNodes().get(71),2);
        state.starBoard.getBoardNodes().get(61).addNeighbour(state.starBoard.getBoardNodes().get(70),3);
        state.starBoard.getBoardNodes().get(61).addNeighbour(state.starBoard.getBoardNodes().get(60),4);
        state.starBoard.getBoardNodes().get(61).addNeighbour(state.starBoard.getBoardNodes().get(51),5);

        ((CCNode)state.starBoard.getBoardNodes().get(62)).setCoordinates(8, 8);
        state.starBoard.getBoardNodes().get(62).addNeighbour(state.starBoard.getBoardNodes().get(53),0);
        state.starBoard.getBoardNodes().get(62).addNeighbour(state.starBoard.getBoardNodes().get(63),1);
        state.starBoard.getBoardNodes().get(62).addNeighbour(state.starBoard.getBoardNodes().get(72),2);
        state.starBoard.getBoardNodes().get(62).addNeighbour(state.starBoard.getBoardNodes().get(71),3);
        state.starBoard.getBoardNodes().get(62).addNeighbour(state.starBoard.getBoardNodes().get(61),4);
        state.starBoard.getBoardNodes().get(62).addNeighbour(state.starBoard.getBoardNodes().get(52),5);

        ((CCNode)state.starBoard.getBoardNodes().get(63)).setCoordinates(9, 8);
        state.starBoard.getBoardNodes().get(63).addNeighbour(state.starBoard.getBoardNodes().get(54),0);
        state.starBoard.getBoardNodes().get(63).addNeighbour(state.starBoard.getBoardNodes().get(64),1);
        state.starBoard.getBoardNodes().get(63).addNeighbour(state.starBoard.getBoardNodes().get(73),2);
        state.starBoard.getBoardNodes().get(63).addNeighbour(state.starBoard.getBoardNodes().get(72),3);
        state.starBoard.getBoardNodes().get(63).addNeighbour(state.starBoard.getBoardNodes().get(62),4);
        state.starBoard.getBoardNodes().get(63).addNeighbour(state.starBoard.getBoardNodes().get(53),5);

        ((CCNode)state.starBoard.getBoardNodes().get(64)).setCoordinates(10, 8);
        state.starBoard.getBoardNodes().get(64).addNeighbour(state.starBoard.getBoardNodes().get(55),0);
        state.starBoard.getBoardNodes().get(64).addNeighbour(state.starBoard.getBoardNodes().get(74),2);
        state.starBoard.getBoardNodes().get(64).addNeighbour(state.starBoard.getBoardNodes().get(73),3);
        state.starBoard.getBoardNodes().get(64).addNeighbour(state.starBoard.getBoardNodes().get(63),4);
        state.starBoard.getBoardNodes().get(64).addNeighbour(state.starBoard.getBoardNodes().get(54),5);

        ((CCNode)state.starBoard.getBoardNodes().get(65)).setCoordinates(1, 9);
        state.starBoard.getBoardNodes().get(65).addNeighbour(state.starBoard.getBoardNodes().get(56),0);
        state.starBoard.getBoardNodes().get(65).addNeighbour(state.starBoard.getBoardNodes().get(66),1);
        state.starBoard.getBoardNodes().get(65).addNeighbour(state.starBoard.getBoardNodes().get(76),2);
        state.starBoard.getBoardNodes().get(65).addNeighbour(state.starBoard.getBoardNodes().get(75),3);

        ((CCNode)state.starBoard.getBoardNodes().get(66)).setCoordinates(2, 9);
        state.starBoard.getBoardNodes().get(66).addNeighbour(state.starBoard.getBoardNodes().get(57),0);
        state.starBoard.getBoardNodes().get(66).addNeighbour(state.starBoard.getBoardNodes().get(67),1);
        state.starBoard.getBoardNodes().get(66).addNeighbour(state.starBoard.getBoardNodes().get(77),2);
        state.starBoard.getBoardNodes().get(66).addNeighbour(state.starBoard.getBoardNodes().get(76),3);
        state.starBoard.getBoardNodes().get(66).addNeighbour(state.starBoard.getBoardNodes().get(65),4);
        state.starBoard.getBoardNodes().get(66).addNeighbour(state.starBoard.getBoardNodes().get(56),5);

        ((CCNode)state.starBoard.getBoardNodes().get(67)).setCoordinates(3, 9);
        state.starBoard.getBoardNodes().get(67).addNeighbour(state.starBoard.getBoardNodes().get(58),0);
        state.starBoard.getBoardNodes().get(67).addNeighbour(state.starBoard.getBoardNodes().get(68),1);
        state.starBoard.getBoardNodes().get(67).addNeighbour(state.starBoard.getBoardNodes().get(78),2);
        state.starBoard.getBoardNodes().get(67).addNeighbour(state.starBoard.getBoardNodes().get(77),3);
        state.starBoard.getBoardNodes().get(67).addNeighbour(state.starBoard.getBoardNodes().get(66),4);
        state.starBoard.getBoardNodes().get(67).addNeighbour(state.starBoard.getBoardNodes().get(57),5);

        ((CCNode)state.starBoard.getBoardNodes().get(68)).setCoordinates(4, 9);
        state.starBoard.getBoardNodes().get(68).addNeighbour(state.starBoard.getBoardNodes().get(59),0);
        state.starBoard.getBoardNodes().get(68).addNeighbour(state.starBoard.getBoardNodes().get(69),1);
        state.starBoard.getBoardNodes().get(68).addNeighbour(state.starBoard.getBoardNodes().get(79),2);
        state.starBoard.getBoardNodes().get(68).addNeighbour(state.starBoard.getBoardNodes().get(78),3);
        state.starBoard.getBoardNodes().get(68).addNeighbour(state.starBoard.getBoardNodes().get(67),4);
        state.starBoard.getBoardNodes().get(68).addNeighbour(state.starBoard.getBoardNodes().get(58),5);

        ((CCNode)state.starBoard.getBoardNodes().get(69)).setCoordinates(5, 9);
        state.starBoard.getBoardNodes().get(69).addNeighbour(state.starBoard.getBoardNodes().get(60),0);
        state.starBoard.getBoardNodes().get(69).addNeighbour(state.starBoard.getBoardNodes().get(70),1);
        state.starBoard.getBoardNodes().get(69).addNeighbour(state.starBoard.getBoardNodes().get(80),2);
        state.starBoard.getBoardNodes().get(69).addNeighbour(state.starBoard.getBoardNodes().get(79),3);
        state.starBoard.getBoardNodes().get(69).addNeighbour(state.starBoard.getBoardNodes().get(68),4);
        state.starBoard.getBoardNodes().get(69).addNeighbour(state.starBoard.getBoardNodes().get(59),5);

        ((CCNode)state.starBoard.getBoardNodes().get(70)).setCoordinates(6, 9);
        state.starBoard.getBoardNodes().get(70).addNeighbour(state.starBoard.getBoardNodes().get(61),0);
        state.starBoard.getBoardNodes().get(70).addNeighbour(state.starBoard.getBoardNodes().get(71),1);
        state.starBoard.getBoardNodes().get(70).addNeighbour(state.starBoard.getBoardNodes().get(81),2);
        state.starBoard.getBoardNodes().get(70).addNeighbour(state.starBoard.getBoardNodes().get(80),3);
        state.starBoard.getBoardNodes().get(70).addNeighbour(state.starBoard.getBoardNodes().get(69),4);
        state.starBoard.getBoardNodes().get(70).addNeighbour(state.starBoard.getBoardNodes().get(60),5);

        ((CCNode)state.starBoard.getBoardNodes().get(71)).setCoordinates(7, 9);
        state.starBoard.getBoardNodes().get(71).addNeighbour(state.starBoard.getBoardNodes().get(62),0);
        state.starBoard.getBoardNodes().get(71).addNeighbour(state.starBoard.getBoardNodes().get(72),1);
        state.starBoard.getBoardNodes().get(71).addNeighbour(state.starBoard.getBoardNodes().get(82),2);
        state.starBoard.getBoardNodes().get(71).addNeighbour(state.starBoard.getBoardNodes().get(81),3);
        state.starBoard.getBoardNodes().get(71).addNeighbour(state.starBoard.getBoardNodes().get(70),4);
        state.starBoard.getBoardNodes().get(71).addNeighbour(state.starBoard.getBoardNodes().get(61),5);

        ((CCNode)state.starBoard.getBoardNodes().get(72)).setCoordinates(8, 9);
        state.starBoard.getBoardNodes().get(72).addNeighbour(state.starBoard.getBoardNodes().get(63),0);
        state.starBoard.getBoardNodes().get(72).addNeighbour(state.starBoard.getBoardNodes().get(73),1);
        state.starBoard.getBoardNodes().get(72).addNeighbour(state.starBoard.getBoardNodes().get(83),2);
        state.starBoard.getBoardNodes().get(72).addNeighbour(state.starBoard.getBoardNodes().get(82),3);
        state.starBoard.getBoardNodes().get(72).addNeighbour(state.starBoard.getBoardNodes().get(71),4);
        state.starBoard.getBoardNodes().get(72).addNeighbour(state.starBoard.getBoardNodes().get(62),5);

        ((CCNode)state.starBoard.getBoardNodes().get(73)).setCoordinates(9, 9);
        state.starBoard.getBoardNodes().get(73).addNeighbour(state.starBoard.getBoardNodes().get(64),0);
        state.starBoard.getBoardNodes().get(73).addNeighbour(state.starBoard.getBoardNodes().get(74),1);
        state.starBoard.getBoardNodes().get(73).addNeighbour(state.starBoard.getBoardNodes().get(84),2);
        state.starBoard.getBoardNodes().get(73).addNeighbour(state.starBoard.getBoardNodes().get(83),3);
        state.starBoard.getBoardNodes().get(73).addNeighbour(state.starBoard.getBoardNodes().get(72),4);
        state.starBoard.getBoardNodes().get(73).addNeighbour(state.starBoard.getBoardNodes().get(63),5);

        ((CCNode)state.starBoard.getBoardNodes().get(74)).setCoordinates(10, 9);
        state.starBoard.getBoardNodes().get(74).addNeighbour(state.starBoard.getBoardNodes().get(85),2);
        state.starBoard.getBoardNodes().get(74).addNeighbour(state.starBoard.getBoardNodes().get(84),3);
        state.starBoard.getBoardNodes().get(74).addNeighbour(state.starBoard.getBoardNodes().get(73),4);
        state.starBoard.getBoardNodes().get(74).addNeighbour(state.starBoard.getBoardNodes().get(64),5);

        ((CCNode)state.starBoard.getBoardNodes().get(75)).setCoordinates(1, 10);
        state.starBoard.getBoardNodes().get(75).addNeighbour(state.starBoard.getBoardNodes().get(65),0);
        state.starBoard.getBoardNodes().get(75).addNeighbour(state.starBoard.getBoardNodes().get(76),1);
        state.starBoard.getBoardNodes().get(75).addNeighbour(state.starBoard.getBoardNodes().get(87),2);
        state.starBoard.getBoardNodes().get(75).addNeighbour(state.starBoard.getBoardNodes().get(86),3);

        ((CCNode)state.starBoard.getBoardNodes().get(76)).setCoordinates(2, 10);
        state.starBoard.getBoardNodes().get(76).addNeighbour(state.starBoard.getBoardNodes().get(66),0);
        state.starBoard.getBoardNodes().get(76).addNeighbour(state.starBoard.getBoardNodes().get(77),1);
        state.starBoard.getBoardNodes().get(76).addNeighbour(state.starBoard.getBoardNodes().get(88),2);
        state.starBoard.getBoardNodes().get(76).addNeighbour(state.starBoard.getBoardNodes().get(87),3);
        state.starBoard.getBoardNodes().get(76).addNeighbour(state.starBoard.getBoardNodes().get(75),4);
        state.starBoard.getBoardNodes().get(76).addNeighbour(state.starBoard.getBoardNodes().get(65),5);

        ((CCNode)state.starBoard.getBoardNodes().get(77)).setCoordinates(3, 10);
        state.starBoard.getBoardNodes().get(77).addNeighbour(state.starBoard.getBoardNodes().get(67),0);
        state.starBoard.getBoardNodes().get(77).addNeighbour(state.starBoard.getBoardNodes().get(78),1);
        state.starBoard.getBoardNodes().get(77).addNeighbour(state.starBoard.getBoardNodes().get(89),2);
        state.starBoard.getBoardNodes().get(77).addNeighbour(state.starBoard.getBoardNodes().get(88),3);
        state.starBoard.getBoardNodes().get(77).addNeighbour(state.starBoard.getBoardNodes().get(76),4);
        state.starBoard.getBoardNodes().get(77).addNeighbour(state.starBoard.getBoardNodes().get(66),5);

        ((CCNode)state.starBoard.getBoardNodes().get(78)).setCoordinates(4, 10);
        state.starBoard.getBoardNodes().get(78).addNeighbour(state.starBoard.getBoardNodes().get(68),0);
        state.starBoard.getBoardNodes().get(78).addNeighbour(state.starBoard.getBoardNodes().get(79),1);
        state.starBoard.getBoardNodes().get(78).addNeighbour(state.starBoard.getBoardNodes().get(90),2);
        state.starBoard.getBoardNodes().get(78).addNeighbour(state.starBoard.getBoardNodes().get(89),3);
        state.starBoard.getBoardNodes().get(78).addNeighbour(state.starBoard.getBoardNodes().get(77),4);
        state.starBoard.getBoardNodes().get(78).addNeighbour(state.starBoard.getBoardNodes().get(67),5);

        ((CCNode)state.starBoard.getBoardNodes().get(79)).setCoordinates(5, 10);
        state.starBoard.getBoardNodes().get(79).addNeighbour(state.starBoard.getBoardNodes().get(69),0);
        state.starBoard.getBoardNodes().get(79).addNeighbour(state.starBoard.getBoardNodes().get(80),1);
        state.starBoard.getBoardNodes().get(79).addNeighbour(state.starBoard.getBoardNodes().get(91),2);
        state.starBoard.getBoardNodes().get(79).addNeighbour(state.starBoard.getBoardNodes().get(90),3);
        state.starBoard.getBoardNodes().get(79).addNeighbour(state.starBoard.getBoardNodes().get(78),4);
        state.starBoard.getBoardNodes().get(79).addNeighbour(state.starBoard.getBoardNodes().get(68),5);

        ((CCNode)state.starBoard.getBoardNodes().get(80)).setCoordinates(6, 10);
        state.starBoard.getBoardNodes().get(80).addNeighbour(state.starBoard.getBoardNodes().get(70),0);
        state.starBoard.getBoardNodes().get(80).addNeighbour(state.starBoard.getBoardNodes().get(81),1);
        state.starBoard.getBoardNodes().get(80).addNeighbour(state.starBoard.getBoardNodes().get(92),2);
        state.starBoard.getBoardNodes().get(80).addNeighbour(state.starBoard.getBoardNodes().get(91),3);
        state.starBoard.getBoardNodes().get(80).addNeighbour(state.starBoard.getBoardNodes().get(79),4);
        state.starBoard.getBoardNodes().get(80).addNeighbour(state.starBoard.getBoardNodes().get(69),5);

        ((CCNode)state.starBoard.getBoardNodes().get(81)).setCoordinates(7, 10);
        state.starBoard.getBoardNodes().get(81).addNeighbour(state.starBoard.getBoardNodes().get(71),0);
        state.starBoard.getBoardNodes().get(81).addNeighbour(state.starBoard.getBoardNodes().get(82),1);
        state.starBoard.getBoardNodes().get(81).addNeighbour(state.starBoard.getBoardNodes().get(93),2);
        state.starBoard.getBoardNodes().get(81).addNeighbour(state.starBoard.getBoardNodes().get(92),3);
        state.starBoard.getBoardNodes().get(81).addNeighbour(state.starBoard.getBoardNodes().get(80),4);
        state.starBoard.getBoardNodes().get(81).addNeighbour(state.starBoard.getBoardNodes().get(70),5);

        ((CCNode)state.starBoard.getBoardNodes().get(82)).setCoordinates(8, 10);
        state.starBoard.getBoardNodes().get(82).addNeighbour(state.starBoard.getBoardNodes().get(72),0);
        state.starBoard.getBoardNodes().get(82).addNeighbour(state.starBoard.getBoardNodes().get(83),1);
        state.starBoard.getBoardNodes().get(82).addNeighbour(state.starBoard.getBoardNodes().get(94),2);
        state.starBoard.getBoardNodes().get(82).addNeighbour(state.starBoard.getBoardNodes().get(93),3);
        state.starBoard.getBoardNodes().get(82).addNeighbour(state.starBoard.getBoardNodes().get(81),4);
        state.starBoard.getBoardNodes().get(82).addNeighbour(state.starBoard.getBoardNodes().get(71),5);

        ((CCNode)state.starBoard.getBoardNodes().get(83)).setCoordinates(9, 10);
        state.starBoard.getBoardNodes().get(83).addNeighbour(state.starBoard.getBoardNodes().get(73),0);
        state.starBoard.getBoardNodes().get(83).addNeighbour(state.starBoard.getBoardNodes().get(84),1);
        state.starBoard.getBoardNodes().get(83).addNeighbour(state.starBoard.getBoardNodes().get(95),2);
        state.starBoard.getBoardNodes().get(83).addNeighbour(state.starBoard.getBoardNodes().get(94),3);
        state.starBoard.getBoardNodes().get(83).addNeighbour(state.starBoard.getBoardNodes().get(83),4);
        state.starBoard.getBoardNodes().get(83).addNeighbour(state.starBoard.getBoardNodes().get(72),5);

        ((CCNode)state.starBoard.getBoardNodes().get(84)).setCoordinates(10, 10);
        state.starBoard.getBoardNodes().get(84).addNeighbour(state.starBoard.getBoardNodes().get(74),0);
        state.starBoard.getBoardNodes().get(84).addNeighbour(state.starBoard.getBoardNodes().get(85),1);
        state.starBoard.getBoardNodes().get(84).addNeighbour(state.starBoard.getBoardNodes().get(96),2);
        state.starBoard.getBoardNodes().get(84).addNeighbour(state.starBoard.getBoardNodes().get(95),3);
        state.starBoard.getBoardNodes().get(84).addNeighbour(state.starBoard.getBoardNodes().get(83),4);
        state.starBoard.getBoardNodes().get(84).addNeighbour(state.starBoard.getBoardNodes().get(73),5);

        ((CCNode)state.starBoard.getBoardNodes().get(85)).setCoordinates(11, 10);
        state.starBoard.getBoardNodes().get(85).addNeighbour(state.starBoard.getBoardNodes().get(97),2);
        state.starBoard.getBoardNodes().get(85).addNeighbour(state.starBoard.getBoardNodes().get(96),3);
        state.starBoard.getBoardNodes().get(85).addNeighbour(state.starBoard.getBoardNodes().get(84),4);
        state.starBoard.getBoardNodes().get(85).addNeighbour(state.starBoard.getBoardNodes().get(74),5);

        ((CCNode)state.starBoard.getBoardNodes().get(86)).setCoordinates(0, 11);
        state.starBoard.getBoardNodes().get(86).addNeighbour(state.starBoard.getBoardNodes().get(75),0);
        state.starBoard.getBoardNodes().get(86).addNeighbour(state.starBoard.getBoardNodes().get(87),1);
        state.starBoard.getBoardNodes().get(86).addNeighbour(state.starBoard.getBoardNodes().get(99),2);
        state.starBoard.getBoardNodes().get(86).addNeighbour(state.starBoard.getBoardNodes().get(98),3);

        ((CCNode)state.starBoard.getBoardNodes().get(87)).setCoordinates(1, 11);
        state.starBoard.getBoardNodes().get(87).addNeighbour(state.starBoard.getBoardNodes().get(75),0);
        state.starBoard.getBoardNodes().get(87).addNeighbour(state.starBoard.getBoardNodes().get(87),1);
        state.starBoard.getBoardNodes().get(87).addNeighbour(state.starBoard.getBoardNodes().get(99),2);
        state.starBoard.getBoardNodes().get(87).addNeighbour(state.starBoard.getBoardNodes().get(98),3);
        state.starBoard.getBoardNodes().get(87).addNeighbour(state.starBoard.getBoardNodes().get(99),4);
        state.starBoard.getBoardNodes().get(87).addNeighbour(state.starBoard.getBoardNodes().get(98),5);

        ((CCNode)state.starBoard.getBoardNodes().get(88)).setCoordinates(2, 11);
        state.starBoard.getBoardNodes().get(88).addNeighbour(state.starBoard.getBoardNodes().get(77),0);
        state.starBoard.getBoardNodes().get(88).addNeighbour(state.starBoard.getBoardNodes().get(89),1);
        state.starBoard.getBoardNodes().get(88).addNeighbour(state.starBoard.getBoardNodes().get(101),2);
        state.starBoard.getBoardNodes().get(88).addNeighbour(state.starBoard.getBoardNodes().get(100),3);
        state.starBoard.getBoardNodes().get(88).addNeighbour(state.starBoard.getBoardNodes().get(87),4);
        state.starBoard.getBoardNodes().get(88).addNeighbour(state.starBoard.getBoardNodes().get(76),5);

        ((CCNode)state.starBoard.getBoardNodes().get(89)).setCoordinates(3, 11);
        state.starBoard.getBoardNodes().get(89).addNeighbour(state.starBoard.getBoardNodes().get(78),0);
        state.starBoard.getBoardNodes().get(89).addNeighbour(state.starBoard.getBoardNodes().get(90),1);
        state.starBoard.getBoardNodes().get(89).addNeighbour(state.starBoard.getBoardNodes().get(102),2);
        state.starBoard.getBoardNodes().get(89).addNeighbour(state.starBoard.getBoardNodes().get(101),3);
        state.starBoard.getBoardNodes().get(89).addNeighbour(state.starBoard.getBoardNodes().get(88),4);
        state.starBoard.getBoardNodes().get(89).addNeighbour(state.starBoard.getBoardNodes().get(77),5);

        ((CCNode)state.starBoard.getBoardNodes().get(90)).setCoordinates(4, 11);
        state.starBoard.getBoardNodes().get(90).addNeighbour(state.starBoard.getBoardNodes().get(79),0);
        state.starBoard.getBoardNodes().get(90).addNeighbour(state.starBoard.getBoardNodes().get(91),1);
        state.starBoard.getBoardNodes().get(90).addNeighbour(state.starBoard.getBoardNodes().get(103),2);
        state.starBoard.getBoardNodes().get(90).addNeighbour(state.starBoard.getBoardNodes().get(102),3);
        state.starBoard.getBoardNodes().get(90).addNeighbour(state.starBoard.getBoardNodes().get(89),4);
        state.starBoard.getBoardNodes().get(90).addNeighbour(state.starBoard.getBoardNodes().get(78),5);

        ((CCNode)state.starBoard.getBoardNodes().get(91)).setCoordinates(5, 11);
        state.starBoard.getBoardNodes().get(91).addNeighbour(state.starBoard.getBoardNodes().get(80),0);
        state.starBoard.getBoardNodes().get(91).addNeighbour(state.starBoard.getBoardNodes().get(92),1);
        state.starBoard.getBoardNodes().get(91).addNeighbour(state.starBoard.getBoardNodes().get(104),2);
        state.starBoard.getBoardNodes().get(91).addNeighbour(state.starBoard.getBoardNodes().get(103),3);
        state.starBoard.getBoardNodes().get(91).addNeighbour(state.starBoard.getBoardNodes().get(90),4);
        state.starBoard.getBoardNodes().get(91).addNeighbour(state.starBoard.getBoardNodes().get(79),5);

        ((CCNode)state.starBoard.getBoardNodes().get(92)).setCoordinates(6, 11);
        state.starBoard.getBoardNodes().get(92).addNeighbour(state.starBoard.getBoardNodes().get(81),0);
        state.starBoard.getBoardNodes().get(92).addNeighbour(state.starBoard.getBoardNodes().get(93),1);
        state.starBoard.getBoardNodes().get(92).addNeighbour(state.starBoard.getBoardNodes().get(105),2);
        state.starBoard.getBoardNodes().get(92).addNeighbour(state.starBoard.getBoardNodes().get(104),3);
        state.starBoard.getBoardNodes().get(92).addNeighbour(state.starBoard.getBoardNodes().get(91),4);
        state.starBoard.getBoardNodes().get(92).addNeighbour(state.starBoard.getBoardNodes().get(80),5);

        ((CCNode)state.starBoard.getBoardNodes().get(93)).setCoordinates(7, 11);
        state.starBoard.getBoardNodes().get(93).addNeighbour(state.starBoard.getBoardNodes().get(82),0);
        state.starBoard.getBoardNodes().get(93).addNeighbour(state.starBoard.getBoardNodes().get(94),1);
        state.starBoard.getBoardNodes().get(93).addNeighbour(state.starBoard.getBoardNodes().get(106),2);
        state.starBoard.getBoardNodes().get(93).addNeighbour(state.starBoard.getBoardNodes().get(105),3);
        state.starBoard.getBoardNodes().get(93).addNeighbour(state.starBoard.getBoardNodes().get(92),4);
        state.starBoard.getBoardNodes().get(93).addNeighbour(state.starBoard.getBoardNodes().get(81),5);

        ((CCNode)state.starBoard.getBoardNodes().get(94)).setCoordinates(8, 11);
        state.starBoard.getBoardNodes().get(94).addNeighbour(state.starBoard.getBoardNodes().get(83),0);
        state.starBoard.getBoardNodes().get(94).addNeighbour(state.starBoard.getBoardNodes().get(95),1);
        state.starBoard.getBoardNodes().get(94).addNeighbour(state.starBoard.getBoardNodes().get(107),2);
        state.starBoard.getBoardNodes().get(94).addNeighbour(state.starBoard.getBoardNodes().get(106),3);
        state.starBoard.getBoardNodes().get(94).addNeighbour(state.starBoard.getBoardNodes().get(93),4);
        state.starBoard.getBoardNodes().get(94).addNeighbour(state.starBoard.getBoardNodes().get(82),5);

        ((CCNode)state.starBoard.getBoardNodes().get(95)).setCoordinates(9, 11);
        state.starBoard.getBoardNodes().get(95).addNeighbour(state.starBoard.getBoardNodes().get(84),0);
        state.starBoard.getBoardNodes().get(95).addNeighbour(state.starBoard.getBoardNodes().get(96),1);
        state.starBoard.getBoardNodes().get(95).addNeighbour(state.starBoard.getBoardNodes().get(108),2);
        state.starBoard.getBoardNodes().get(95).addNeighbour(state.starBoard.getBoardNodes().get(107),3);
        state.starBoard.getBoardNodes().get(95).addNeighbour(state.starBoard.getBoardNodes().get(94),4);
        state.starBoard.getBoardNodes().get(95).addNeighbour(state.starBoard.getBoardNodes().get(83),5);

        ((CCNode)state.starBoard.getBoardNodes().get(96)).setCoordinates(10, 11);
        state.starBoard.getBoardNodes().get(96).addNeighbour(state.starBoard.getBoardNodes().get(85),0);
        state.starBoard.getBoardNodes().get(96).addNeighbour(state.starBoard.getBoardNodes().get(97),1);
        state.starBoard.getBoardNodes().get(96).addNeighbour(state.starBoard.getBoardNodes().get(109),2);
        state.starBoard.getBoardNodes().get(96).addNeighbour(state.starBoard.getBoardNodes().get(108),3);
        state.starBoard.getBoardNodes().get(96).addNeighbour(state.starBoard.getBoardNodes().get(95),4);
        state.starBoard.getBoardNodes().get(96).addNeighbour(state.starBoard.getBoardNodes().get(84),5);

        ((CCNode)state.starBoard.getBoardNodes().get(97)).setCoordinates(11, 11);
        state.starBoard.getBoardNodes().get(97).addNeighbour(state.starBoard.getBoardNodes().get(110),2);
        state.starBoard.getBoardNodes().get(97).addNeighbour(state.starBoard.getBoardNodes().get(109),3);
        state.starBoard.getBoardNodes().get(97).addNeighbour(state.starBoard.getBoardNodes().get(96),4);
        state.starBoard.getBoardNodes().get(97).addNeighbour(state.starBoard.getBoardNodes().get(85),5);

        ((CCNode)state.starBoard.getBoardNodes().get(98)).setCoordinates(0, 12);
        state.starBoard.getBoardNodes().get(98).addNeighbour(state.starBoard.getBoardNodes().get(86),0);
        state.starBoard.getBoardNodes().get(98).addNeighbour(state.starBoard.getBoardNodes().get(99),1);

        ((CCNode)state.starBoard.getBoardNodes().get(99)).setCoordinates(1, 12);
        state.starBoard.getBoardNodes().get(99).addNeighbour(state.starBoard.getBoardNodes().get(87),0);
        state.starBoard.getBoardNodes().get(99).addNeighbour(state.starBoard.getBoardNodes().get(100),1);
        state.starBoard.getBoardNodes().get(99).addNeighbour(state.starBoard.getBoardNodes().get(98),4);
        state.starBoard.getBoardNodes().get(99).addNeighbour(state.starBoard.getBoardNodes().get(86),5);

        ((CCNode)state.starBoard.getBoardNodes().get(100)).setCoordinates(2, 12);
        state.starBoard.getBoardNodes().get(100).addNeighbour(state.starBoard.getBoardNodes().get(88),0);
        state.starBoard.getBoardNodes().get(100).addNeighbour(state.starBoard.getBoardNodes().get(101),1);
        state.starBoard.getBoardNodes().get(100).addNeighbour(state.starBoard.getBoardNodes().get(99),4);
        state.starBoard.getBoardNodes().get(100).addNeighbour(state.starBoard.getBoardNodes().get(87),5);;

        ((CCNode)state.starBoard.getBoardNodes().get(101)).setCoordinates(3, 12);
        state.starBoard.getBoardNodes().get(101).addNeighbour(state.starBoard.getBoardNodes().get(89),0);
        state.starBoard.getBoardNodes().get(101).addNeighbour(state.starBoard.getBoardNodes().get(102),1);
        state.starBoard.getBoardNodes().get(101).addNeighbour(state.starBoard.getBoardNodes().get(100),4);
        state.starBoard.getBoardNodes().get(101).addNeighbour(state.starBoard.getBoardNodes().get(88),5);;

        ((CCNode)state.starBoard.getBoardNodes().get(102)).setCoordinates(4, 12);
        state.starBoard.getBoardNodes().get(102).addNeighbour(state.starBoard.getBoardNodes().get(90),0);
        state.starBoard.getBoardNodes().get(102).addNeighbour(state.starBoard.getBoardNodes().get(103),1);
        state.starBoard.getBoardNodes().get(102).addNeighbour(state.starBoard.getBoardNodes().get(111),2);
        state.starBoard.getBoardNodes().get(102).addNeighbour(state.starBoard.getBoardNodes().get(101),4);
        state.starBoard.getBoardNodes().get(102).addNeighbour(state.starBoard.getBoardNodes().get(89),5);;

        ((CCNode)state.starBoard.getBoardNodes().get(103)).setCoordinates(5, 12);
        state.starBoard.getBoardNodes().get(103).addNeighbour(state.starBoard.getBoardNodes().get(91),0);
        state.starBoard.getBoardNodes().get(103).addNeighbour(state.starBoard.getBoardNodes().get(104),1);
        state.starBoard.getBoardNodes().get(103).addNeighbour(state.starBoard.getBoardNodes().get(112),2);
        state.starBoard.getBoardNodes().get(103).addNeighbour(state.starBoard.getBoardNodes().get(111),3);
        state.starBoard.getBoardNodes().get(103).addNeighbour(state.starBoard.getBoardNodes().get(102),4);
        state.starBoard.getBoardNodes().get(103).addNeighbour(state.starBoard.getBoardNodes().get(90),5);;

        ((CCNode)state.starBoard.getBoardNodes().get(104)).setCoordinates(6, 12);
        state.starBoard.getBoardNodes().get(104).addNeighbour(state.starBoard.getBoardNodes().get(92),0);
        state.starBoard.getBoardNodes().get(104).addNeighbour(state.starBoard.getBoardNodes().get(105),1);
        state.starBoard.getBoardNodes().get(104).addNeighbour(state.starBoard.getBoardNodes().get(113),2);
        state.starBoard.getBoardNodes().get(104).addNeighbour(state.starBoard.getBoardNodes().get(112),3);
        state.starBoard.getBoardNodes().get(104).addNeighbour(state.starBoard.getBoardNodes().get(103),4);
        state.starBoard.getBoardNodes().get(104).addNeighbour(state.starBoard.getBoardNodes().get(91),5);;

        ((CCNode)state.starBoard.getBoardNodes().get(105)).setCoordinates(7, 12);
        state.starBoard.getBoardNodes().get(105).addNeighbour(state.starBoard.getBoardNodes().get(93),0);
        state.starBoard.getBoardNodes().get(105).addNeighbour(state.starBoard.getBoardNodes().get(106),1);
        state.starBoard.getBoardNodes().get(105).addNeighbour(state.starBoard.getBoardNodes().get(114),2);
        state.starBoard.getBoardNodes().get(105).addNeighbour(state.starBoard.getBoardNodes().get(113),3);
        state.starBoard.getBoardNodes().get(105).addNeighbour(state.starBoard.getBoardNodes().get(104),4);
        state.starBoard.getBoardNodes().get(105).addNeighbour(state.starBoard.getBoardNodes().get(92),5);;

        ((CCNode)state.starBoard.getBoardNodes().get(106)).setCoordinates(8, 12);
        state.starBoard.getBoardNodes().get(106).addNeighbour(state.starBoard.getBoardNodes().get(94),0);
        state.starBoard.getBoardNodes().get(106).addNeighbour(state.starBoard.getBoardNodes().get(107),1);
        state.starBoard.getBoardNodes().get(106).addNeighbour(state.starBoard.getBoardNodes().get(114),3);
        state.starBoard.getBoardNodes().get(106).addNeighbour(state.starBoard.getBoardNodes().get(105),4);
        state.starBoard.getBoardNodes().get(106).addNeighbour(state.starBoard.getBoardNodes().get(93),5);;

        ((CCNode)state.starBoard.getBoardNodes().get(107)).setCoordinates(9, 12);
        state.starBoard.getBoardNodes().get(107).addNeighbour(state.starBoard.getBoardNodes().get(95),0);
        state.starBoard.getBoardNodes().get(107).addNeighbour(state.starBoard.getBoardNodes().get(108),1);
        state.starBoard.getBoardNodes().get(107).addNeighbour(state.starBoard.getBoardNodes().get(106),4);
        state.starBoard.getBoardNodes().get(107).addNeighbour(state.starBoard.getBoardNodes().get(94),5);;

        ((CCNode)state.starBoard.getBoardNodes().get(108)).setCoordinates(10, 12);
        state.starBoard.getBoardNodes().get(108).addNeighbour(state.starBoard.getBoardNodes().get(96),0);
        state.starBoard.getBoardNodes().get(108).addNeighbour(state.starBoard.getBoardNodes().get(109),1);
        state.starBoard.getBoardNodes().get(108).addNeighbour(state.starBoard.getBoardNodes().get(107),4);
        state.starBoard.getBoardNodes().get(108).addNeighbour(state.starBoard.getBoardNodes().get(95),5);;

        ((CCNode)state.starBoard.getBoardNodes().get(109)).setCoordinates(11, 12);
        state.starBoard.getBoardNodes().get(109).addNeighbour(state.starBoard.getBoardNodes().get(97),0);
        state.starBoard.getBoardNodes().get(109).addNeighbour(state.starBoard.getBoardNodes().get(110),1);
        state.starBoard.getBoardNodes().get(109).addNeighbour(state.starBoard.getBoardNodes().get(108),4);
        state.starBoard.getBoardNodes().get(109).addNeighbour(state.starBoard.getBoardNodes().get(96),5);;

        ((CCNode)state.starBoard.getBoardNodes().get(110)).setCoordinates(12, 12);
        state.starBoard.getBoardNodes().get(110).addNeighbour(state.starBoard.getBoardNodes().get(109),4);
        state.starBoard.getBoardNodes().get(110).addNeighbour(state.starBoard.getBoardNodes().get(97),5);;

        ((CCNode)state.starBoard.getBoardNodes().get(111)).setCoordinates(4, 13);
        state.starBoard.getBoardNodes().get(111).addNeighbour(state.starBoard.getBoardNodes().get(103),0);
        state.starBoard.getBoardNodes().get(111).addNeighbour(state.starBoard.getBoardNodes().get(112),1);
        state.starBoard.getBoardNodes().get(111).addNeighbour(state.starBoard.getBoardNodes().get(115),2);
        state.starBoard.getBoardNodes().get(111).addNeighbour(state.starBoard.getBoardNodes().get(102),5);;

        ((CCNode)state.starBoard.getBoardNodes().get(112)).setCoordinates(5, 13);
        state.starBoard.getBoardNodes().get(112).addNeighbour(state.starBoard.getBoardNodes().get(104),0);
        state.starBoard.getBoardNodes().get(112).addNeighbour(state.starBoard.getBoardNodes().get(113),1);
        state.starBoard.getBoardNodes().get(112).addNeighbour(state.starBoard.getBoardNodes().get(116),2);
        state.starBoard.getBoardNodes().get(112).addNeighbour(state.starBoard.getBoardNodes().get(115),3);
        state.starBoard.getBoardNodes().get(112).addNeighbour(state.starBoard.getBoardNodes().get(111),4);
        state.starBoard.getBoardNodes().get(112).addNeighbour(state.starBoard.getBoardNodes().get(103),5);;

        ((CCNode)state.starBoard.getBoardNodes().get(113)).setCoordinates(6, 13);
        state.starBoard.getBoardNodes().get(113).addNeighbour(state.starBoard.getBoardNodes().get(105),0);
        state.starBoard.getBoardNodes().get(113).addNeighbour(state.starBoard.getBoardNodes().get(114),1);
        state.starBoard.getBoardNodes().get(113).addNeighbour(state.starBoard.getBoardNodes().get(117),2);
        state.starBoard.getBoardNodes().get(113).addNeighbour(state.starBoard.getBoardNodes().get(116),3);
        state.starBoard.getBoardNodes().get(113).addNeighbour(state.starBoard.getBoardNodes().get(112),4);
        state.starBoard.getBoardNodes().get(113).addNeighbour(state.starBoard.getBoardNodes().get(104),5);;

        ((CCNode)state.starBoard.getBoardNodes().get(114)).setCoordinates(7, 13);
        state.starBoard.getBoardNodes().get(114).addNeighbour(state.starBoard.getBoardNodes().get(106),0);
        state.starBoard.getBoardNodes().get(114).addNeighbour(state.starBoard.getBoardNodes().get(117),3);
        state.starBoard.getBoardNodes().get(114).addNeighbour(state.starBoard.getBoardNodes().get(113),4);
        state.starBoard.getBoardNodes().get(114).addNeighbour(state.starBoard.getBoardNodes().get(105),5);;

        ((CCNode)state.starBoard.getBoardNodes().get(115)).setCoordinates(5, 14);
        state.starBoard.getBoardNodes().get(115).addNeighbour(state.starBoard.getBoardNodes().get(112),0);
        state.starBoard.getBoardNodes().get(115).addNeighbour(state.starBoard.getBoardNodes().get(116),1);
        state.starBoard.getBoardNodes().get(115).addNeighbour(state.starBoard.getBoardNodes().get(118),2);
        state.starBoard.getBoardNodes().get(115).addNeighbour(state.starBoard.getBoardNodes().get(111),5);;

        ((CCNode)state.starBoard.getBoardNodes().get(116)).setCoordinates(6, 14);
        state.starBoard.getBoardNodes().get(116).addNeighbour(state.starBoard.getBoardNodes().get(113),0);
        state.starBoard.getBoardNodes().get(116).addNeighbour(state.starBoard.getBoardNodes().get(117),1);
        state.starBoard.getBoardNodes().get(116).addNeighbour(state.starBoard.getBoardNodes().get(119),2);
        state.starBoard.getBoardNodes().get(116).addNeighbour(state.starBoard.getBoardNodes().get(118),3);
        state.starBoard.getBoardNodes().get(116).addNeighbour(state.starBoard.getBoardNodes().get(115),4);
        state.starBoard.getBoardNodes().get(116).addNeighbour(state.starBoard.getBoardNodes().get(112),5);;

        ((CCNode)state.starBoard.getBoardNodes().get(117)).setCoordinates(7, 14);
        state.starBoard.getBoardNodes().get(117).addNeighbour(state.starBoard.getBoardNodes().get(114),0);
        state.starBoard.getBoardNodes().get(117).addNeighbour(state.starBoard.getBoardNodes().get(119),3);
        state.starBoard.getBoardNodes().get(117).addNeighbour(state.starBoard.getBoardNodes().get(116),4);
        state.starBoard.getBoardNodes().get(117).addNeighbour(state.starBoard.getBoardNodes().get(113),5);;

        ((CCNode)state.starBoard.getBoardNodes().get(118)).setCoordinates(5, 15);
        state.starBoard.getBoardNodes().get(118).addNeighbour(state.starBoard.getBoardNodes().get(116),0);
        state.starBoard.getBoardNodes().get(118).addNeighbour(state.starBoard.getBoardNodes().get(119),1);
        state.starBoard.getBoardNodes().get(118).addNeighbour(state.starBoard.getBoardNodes().get(120),2);
        state.starBoard.getBoardNodes().get(118).addNeighbour(state.starBoard.getBoardNodes().get(115),5);;

        ((CCNode)state.starBoard.getBoardNodes().get(119)).setCoordinates(6, 15);
        state.starBoard.getBoardNodes().get(119).addNeighbour(state.starBoard.getBoardNodes().get(117),0);
        state.starBoard.getBoardNodes().get(119).addNeighbour(state.starBoard.getBoardNodes().get(120),3);
        state.starBoard.getBoardNodes().get(119).addNeighbour(state.starBoard.getBoardNodes().get(118),4);
        state.starBoard.getBoardNodes().get(119).addNeighbour(state.starBoard.getBoardNodes().get(116),5);;

        ((CCNode)state.starBoard.getBoardNodes().get(120)).setCoordinates(6, 16);
        state.starBoard.getBoardNodes().get(120).addNeighbour(state.starBoard.getBoardNodes().get(119),0);
        state.starBoard.getBoardNodes().get(120).addNeighbour(state.starBoard.getBoardNodes().get(118),5);;
    }
}
