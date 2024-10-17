package es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.actions;
import es.ucm.fdi.ici.Action;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class ChaseActionPacMan implements Action{


    @Override
    public MOVE execute(Game game) {
        return MOVE.NEUTRAL;
    }

    @Override
    public String getActionId() {
       return "ChaseActionPacMan";
    }
    

}