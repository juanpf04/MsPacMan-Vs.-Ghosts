package es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.actions.pills_actions;

import es.ucm.fdi.ici.Action;
import es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.MsPacManInput;
import es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.SafePaths;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class MorePillsPillsAction implements Action{

    private SafePaths safePaths;   
    public MorePillsPillsAction(SafePaths safePaths) {
        this.safePaths = safePaths;
    }

    @Override
    public MOVE execute(Game game) {
        MsPacManInput input = new MsPacManInput(game);
        return input.pathWithMorePills(safePaths);
    }

    
    @Override
    public String getActionId() {
        return "More pills action from Pills";
    }
    
}
