package games.chinesecheckers;

import core.AbstractGameState;
import core.AbstractParameters;
import core.CoreConstants;
import core.interfaces.IStateHeuristic;
import evaluation.TunableParameters;
import games.chinesecheckers.components.CCNode;
import games.chinesecheckers.components.Peg;
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
        CCGameState state = (CCGameState) gs;

        int score = 0;
        if(playerId == 0){
            for(int i = 0; i < state.getStarBoard().getBoardNodes().size(); i++){
                CCNode node = state.getStarBoard().getBoardNodes().get(i);
                if(node.isNodeOccupied()){
                    if(node.getOccupiedPeg().getColour2() == Peg.Colour2.purple){
                        if(node.getOccupiedPeg().getInDestination()){
//                            System.out.println(score + " " + node.getID() +  " " + node.getOccupiedPeg().getColour2());
//                            score++;
                            if(score == 10){
                                //System.out.println(score);
                            }
                        }
//                        for(int x = 0; x < 10; x++){ // check if purple pegs are in purple base
//                            if(state.getStarBoard().getBoardNodes().get(i).getID() == x){
//                                score--;
//                            }
//                        }
                    }
                }
            }
        }

        if(playerId == 1){
            for(int i = 0; i < state.getStarBoard().getBoardNodes().size(); i++){
                if(state.getStarBoard().getBoardNodes().get(i).isNodeOccupied()){
                    if(state.getStarBoard().getBoardNodes().get(i).getOccupiedPeg().getColour2() == Peg.Colour2.red){
                        if(state.getStarBoard().getBoardNodes().get(i).getOccupiedPeg().getInDestination()){
                            score++;
                        }
                        for(int x = 111; x < 121; x++){ // check if red pegs are in red base
                            if(state.getStarBoard().getBoardNodes().get(i).getID() == x){
                                score--;
                            }
                        }
                    }
                }
            }
        }
        //System.out.println(score);
        return score;
    }

    @Override
    public Object instantiate() {
        return this._copy();
    }

    @Override
    public void _reset() {
    }
}
