package es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.actions.pills_actions;

import es.ucm.fdi.ici.Action;
import es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.MsPacManInput;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class MorePillsPillsAction implements Action{



    @Override
    public MOVE execute(Game game) {
        MsPacManInput input = new MsPacManInput(game);
        return input.pathWithMorePills(false);
    }

    
    @Override
    public String getActionId() {
        return "More pills action from Pills";
    }
    
}
