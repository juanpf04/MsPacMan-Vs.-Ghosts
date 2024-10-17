package es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.actions.pills_actions;

import es.ucm.fdi.ici.Action;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class ChooseAnyPillsAction implements Action{

    //Me queda esta por implementar, tengo que ver como alamacenar los caminos iguales para escoger cualquiera
    
    @Override
    public MOVE execute(Game game) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'execute'");
    }

    @Override
    public String getActionId() {
        return "Choose any pills action from Pills";
    }
}
