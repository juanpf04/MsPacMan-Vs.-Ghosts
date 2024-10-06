package es.ucm.fdi.ici.c2425.practica1.grupo02;

import pacman.game.Game;

/**
 * Class with useful methods for the MsPacMan and Ghosts
 */
class Methods {

	/**
	 * Get the closest power pill to a given position
	 * 
	 * @param game The game
	 * @param node The node index
	 * @return The closest power pill
	 */
	static int getClosestPowerPill(Game game, int node) {
		int closest = -1;
		int currDist = Integer.MAX_VALUE;
		for (int powerPill : game.getActivePowerPillsIndices()) {
			int pathDistance = game.getShortestPathDistance(node, powerPill);
			if (pathDistance < currDist) {
				currDist = pathDistance;
				closest = powerPill;
			}
		}
		return closest;
	}
}
