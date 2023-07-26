package test.games.chinesecheckers;

import core.Game;
import games.GameType;
import games.chinesecheckers.CCForwardModel;
import games.chinesecheckers.CCGameState;
import games.chinesecheckers.CCParameters;
import games.chinesecheckers.actions.MovePeg;
import games.chinesecheckers.components.CCNode;
import games.chinesecheckers.components.Peg;
import games.dominion.DominionConstants;
import games.dominion.DominionGameState;
import games.dominion.cards.CardType;
import org.junit.Test;

import static org.junit.Assert.*;

public class ActionTests {
    CCForwardModel fm = new CCForwardModel();
    Game game = GameType.ChineseCheckers.createGameInstance(4, new CCParameters(3));
    CCGameState state = (CCGameState) game.getGameState();

//    @Test
//    public void movingPegFunctionality(){
//        CCGameState state = (CCGameState) game.getGameState();
//        MovePeg movePeg = new MovePeg(((CCNode) state.getStarBoard().getBoardNodes().get(6)), ((CCNode) state.getStarBoard().getBoardNodes().get(15)));
//
//        fm.computeAvailableActions(state);
//        fm.next(state, movePeg);
//    }

    @Test
    public void startPegLocations(){
//        CCGameState state = (CCGameState) game.getGameState();
//        // PurplePlayer
//        for(int i = 0; i < state.PLAYER_PEGS; i++) {
//            assertEquals(Peg.Colour.purple,((CCNode)state.getStarBoard().getBoardNodes().get(i)).getOccupiedPeg().getColour());
//        }
//        // Red Player
//        for(int i = 111; i < state.PLAYER_PEGS; i++) {
//            assertEquals(Peg.Colour.red,((CCNode)state.getStarBoard().getBoardNodes().get(i)).getOccupiedPeg().getColour());
//        }
//        // Green Player
//        for(int i = 10; i <= 13; i++){
//            assertEquals(Peg.Colour.green,((CCNode)state.getStarBoard().getBoardNodes().get(i)).getOccupiedPeg().getColour());
//        }
//        for(int i = 23; i <= 25; i++){
//            assertEquals(Peg.Colour.green,((CCNode)state.getStarBoard().getBoardNodes().get(i)).getOccupiedPeg().getColour());
//        }
//        for(int i = 35; i <= 36; i++){
//            assertEquals(Peg.Colour.green,((CCNode)state.getStarBoard().getBoardNodes().get(i)).getOccupiedPeg().getColour());
//        }
//        assertEquals(Peg.Colour.green,((CCNode)state.getStarBoard().getBoardNodes().get(46)).getOccupiedPeg().getColour());
//        // Orange Player
//        assertEquals(Peg.Colour.orange,((CCNode)state.getStarBoard().getBoardNodes().get(65)).getOccupiedPeg().getColour());
//        for(int i = 75; i <= 76; i++){
//            assertEquals(Peg.Colour.orange,((CCNode)state.getStarBoard().getBoardNodes().get(i)).getOccupiedPeg().getColour());
//        }
//        for(int i = 86; i <= 88; i++){
//            assertEquals(Peg.Colour.orange,((CCNode)state.getStarBoard().getBoardNodes().get(i)).getOccupiedPeg().getColour());
//        }
//        for(int i = 98; i <= 101; i++){
//            assertEquals(Peg.Colour.orange,((CCNode)state.getStarBoard().getBoardNodes().get(i)).getOccupiedPeg().getColour());
//        }
//        // Yellow Player
//        assertEquals(Peg.Colour.yellow,((CCNode)state.getStarBoard().getBoardNodes().get(74)).getOccupiedPeg().getColour());
//        for(int i = 84; i <= 85; i++){
//            assertEquals(Peg.Colour.yellow,((CCNode)state.getStarBoard().getBoardNodes().get(i)).getOccupiedPeg().getColour());
//        }
//        for(int i = 93; i <= 97; i++){
//            assertEquals(Peg.Colour.yellow,((CCNode)state.getStarBoard().getBoardNodes().get(i)).getOccupiedPeg().getColour());
//        }
//        for(int i = 107; i <= 110; i++){
//            assertEquals(Peg.Colour.yellow,((CCNode)state.getStarBoard().getBoardNodes().get(i)).getOccupiedPeg().getColour());
//        }
//        // Blue Player
//        for(int i = 19; i <= 22; i++){
//            assertEquals(Peg.Colour.blue,((CCNode)state.getStarBoard().getBoardNodes().get(i)).getOccupiedPeg().getColour());
//        }
//        for(int i = 32; i <= 34; i++){
//            assertEquals(Peg.Colour.blue,((CCNode)state.getStarBoard().getBoardNodes().get(i)).getOccupiedPeg().getColour());
//        }
//        for(int i = 44; i <= 45; i++){
//            assertEquals(Peg.Colour.blue,((CCNode)state.getStarBoard().getBoardNodes().get(i)).getOccupiedPeg().getColour());
//        }
//        assertEquals(Peg.Colour.blue,((CCNode)state.getStarBoard().getBoardNodes().get(55)).getOccupiedPeg().getColour());
    }
}
