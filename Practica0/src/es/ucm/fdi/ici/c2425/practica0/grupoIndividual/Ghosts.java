package es.ucm.fdi.ici.c2425.practica0.grupoIndividual;

import java.util.EnumMap;
import java.util.Random;

import pacman.controllers.GhostController;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class Ghosts extends GhostController{

	@Override
	public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue) {
		EnumMap<GHOST, MOVE> sol = new EnumMap<GHOST, MOVE>(GHOST.class);
		int pacman = game.getPacmanCurrentNodeIndex();
		
		for(GHOST g: GHOST.values()) {
			if(game.doesGhostRequireAction(g)) {
				
				if(game.isGhostEdible(g) || pacmanCloseToPill(game)) {
					sol.put(g, game.getNextMoveAwayFromTarget(game.getGhostCurrentNodeIndex(g), pacman, DM.EUCLID));
				}
				else {
					if(Math.random() < 0.9) {
						sol.put(g, game.getNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(g), pacman, DM.EUCLID));
					}
					else {
						sol.put(g, getNextRandomMove());
					}
				}
			}
		}
		return sol;
	}
	
	private boolean pacmanCloseToPill(Game game) {
		
		for(int p: game.getActivePowerPillsIndices()) {
			if(game.getDistance(game.getPacmanCurrentNodeIndex(), p, DM.EUCLID) < 15.0) {
				return true;
			}
		}
		return false;
	}
	
	private MOVE getNextRandomMove() {
		MOVE[] movimientos = MOVE.values();
		Random random = new Random();
		
		return movimientos[random.nextInt(movimientos.length)];
	}

}
