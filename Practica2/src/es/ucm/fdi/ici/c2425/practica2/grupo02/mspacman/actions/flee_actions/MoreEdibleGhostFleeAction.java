package es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.actions.flee_actions;

import es.ucm.fdi.ici.Action;
import es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.MsPacManInput;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class MoreEdibleGhostFleeAction implements Action {


    @Override
    public MOVE execute(Game game) {
        MsPacManInput input = new MsPacManInput(game);
        return input.pathWithMoreEdibleGhosts();
    }

    @Override
    public String getActionId() {
        return "Path with more edible ghosts action from Flee";
    }
    
}
