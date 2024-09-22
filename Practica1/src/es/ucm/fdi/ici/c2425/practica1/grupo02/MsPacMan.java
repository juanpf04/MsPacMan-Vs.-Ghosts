package es.ucm.fdi.ici.c2425.practica1.grupo02;

import pacman.controllers.PacmanController;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class MsPacMan extends PacmanController{

    @Override
    public MOVE getMove(Game game, long timeDue) {
        return MOVE.NEUTRAL;
    }
    
    public String getName() {
    	return "MsPacManNeutral";
    }

}
