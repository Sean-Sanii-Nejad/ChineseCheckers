package games.chinesecheckers.gui;

import core.AbstractGameState;
import core.AbstractPlayer;
import core.Game;
import core.actions.AbstractAction;
import core.actions.SetGridValueAction;
import core.components.Token;
import games.chinesecheckers.CCGameState;
import games.connect4.Connect4Constants;
import gui.AbstractGUIManager;
import gui.GamePanel;
import gui.IScreenHighlight;
import org.apache.commons.math3.geometry.spherical.twod.Circle;
import players.human.ActionController;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CCGUIManager extends AbstractGUIManager {

    CCGraphView view;

    public CCGUIManager(GamePanel parent, Game game, ActionController ac, int human) {
        super(parent, game, ac, human);
        if (game == null) return;

        // TODO: set up GUI components and add to `parent`
        CCGameState gameState = (CCGameState) game.getGameState();
        view = new CCGraphView(gameState.getStarBoard());

        this.width = 1000;
        this.height = 450;

        JPanel infoPanel = createGameStateInfoPanel("ChineseCheckers", gameState, width, defaultInfoPanelHeight);
        JComponent actionPanel = createActionPanel(new IScreenHighlight[0], width, defaultActionPanelHeight, false, true, null);

        // Debug Colours
//        infoPanel.setBackground(Color.green);
//        actionPanel.setBackground(Color.red);

        view.setForeground(Color.cyan);

        parent.setLayout(new BorderLayout());
        parent.add(infoPanel, BorderLayout.NORTH);
        parent.add(actionPanel, BorderLayout.SOUTH);
        parent.add(view, BorderLayout.CENTER);
        parent.setPreferredSize(new Dimension(width, height + defaultActionPanelHeight + defaultInfoPanelHeight + defaultCardHeight + 20));
        parent.revalidate();
        parent.setVisible(true);
        parent.repaint();
    }

    /**
     * Defines how many action button objects will be created and cached for usage if needed. Less is better, but
     * should not be smaller than the number of actions available to players in any game state.
     *
     * @return maximum size of the action space (maximum actions available to a player for any decision point in the game)
     */
    @Override
    public int getMaxActionSpace() {
        // TODO
        return 100;
    }

    /**
     * Updates all GUI elements given current game state and player that is currently acting.
     *
     * @param player    - current player acting.
     * @param gameState - current game state to be used in updating visuals.
     */
    @Override
    protected void _update(AbstractPlayer player, AbstractGameState gameState) {
    }
}
