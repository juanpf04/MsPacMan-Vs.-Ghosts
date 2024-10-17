package es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.actions.flee_actions;

import es.ucm.fdi.ici.Action;
import es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.MsPacManInput;
import es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.SafePaths;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class MorePillsFleeAction implements Action{

    private SafePaths safePaths;

    public MorePillsFleeAction(SafePaths safePaths) {
        this.safePaths = safePaths;
    }

    @Override
    public MOVE execute(Game game) {
        MsPacManInput input = new MsPacManInput(game);
        return input.pathWithMorePills(safePaths);
    }

    @Override
    public String getActionId() {
        return "Path with more pills action from Flee";
    }
    
}
