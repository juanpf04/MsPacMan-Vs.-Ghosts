package es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.actions.chase_actions;

import es.ucm.fdi.ici.Action;
import es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.MsPacManInput;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class SafetyGhostChaseAction implements Action{

    @Override
    public MOVE execute(Game game) {
        MsPacManInput input = new MsPacManInput(game);
        return input.pacmanRequieresAction() ? input.moveToSafetyGhost() : MOVE.NEUTRAL;
    }
    
    @Override
    public String getActionId() {
        return "Safety ghost action from chase";
    }
}
