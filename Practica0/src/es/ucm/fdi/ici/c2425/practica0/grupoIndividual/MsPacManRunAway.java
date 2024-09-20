package es.ucm.fdi.ici.c2425.practica0.grupoIndividual;


import pacman.controllers.PacmanController;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class MsPacManRunAway extends PacmanController{

    @Override
    public MOVE getMove(Game game, long timeDue) {
    	GHOST g_nearly = GHOST.BLINKY;
    	
    	int distancia_max = 9000;
    	for(GHOST g: GHOST.values()) {
    		int distance = Math.abs(game.getPacmanCurrentNodeIndex() - game.getGhostCurrentNodeIndex(g));
    		
    		if(distance < distancia_max) {
    			g_nearly = g;
    			distancia_max = distance;
    		}
    	}
    	
		return game.getApproximateNextMoveAwayFromTarget(game.getPacmanCurrentNodeIndex(),
				game.getGhostCurrentNodeIndex(g_nearly), game.getPacmanLastMoveMade(), 
				DM.EUCLID);
    }

    public String getName() {
    	return "MsPacManRandom";
    }
    
}
