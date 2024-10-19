package es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.actions.pills_actions;

import es.ucm.fdi.ici.Action;
import es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.MsPacManInput;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class SafeLongestPathPillsAction implements Action {


    @Override
    public MOVE execute(Game game) {
        MsPacManInput input = new MsPacManInput(game);
        double media = input.getMediaDistaciaSegura();
        return input.pacmanRequieresAction() ? input.moveToSafetyPath(media, false) : MOVE.NEUTRAL;
    }

    @Override
    public String getActionId() {
        return ("More than 1 safe longest path action");
    }
    
}
