package es.ucm.fdi.ici.c2425.practica0.grupoIndividual;

import java.util.EnumMap;

import pacman.controllers.GhostController;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class GhostAggresive extends GhostController{

	@Override
	public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue) {
		EnumMap<GHOST, MOVE> sol = new EnumMap<GHOST, MOVE>(GHOST.class);
		for(GHOST g: GHOST.values()) {
			
			MOVE m = game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(g), 
					game.getPacmanCurrentNodeIndex(), 
					game.getGhostLastMoveMade(g), 
					DM.EUCLID);
			
			sol.put(g, m);
		}
			
		return sol; 
	}
	
}
