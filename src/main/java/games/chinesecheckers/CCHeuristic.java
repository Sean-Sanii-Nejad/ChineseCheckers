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
//        if(playerId == 0){
//            for(int i = 0; i < state.getStarBoard().getBoardNodes().size(); i++){
//                CCNode node = state.getStarBoard().getBoardNodes().get(i);
//                if(node.isNodeOccupied()){
//                    if(node.getOccupiedPeg().getColour() == Peg.Colour.purple){
//                        if(node.getOccupiedPeg().getInDestination()){
//                            score++;
//                            System.out.println(score + " " + node.getID() +  " " + node.getOccupiedPeg().getColour());
//
//                            if(score == 10){
//                                System.out.println(score);
//                            }
//                        }
//                    }
//                }
//            }
//        }

        if(playerId == 0){
            for(int i = 0; i < state.getStarBoard().getBoardNodes().size(); i++){
                CCNode node = state.getStarBoard().getBoardNodes().get(i);
                if(node.isNodeOccupied()){
                    if(node.getOccupiedPeg().getColour() == Peg.Colour.red){
                        if(node.getOccupiedPeg().getInDestination()){
                            score++;
                        }
                    }
                }
            }
        }
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
