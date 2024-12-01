package es.ucm.fdi.ici.c2425.practica4.grupo02.mspacman.actions;

import es.ucm.fdi.ici.Action;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class ChasePINKY implements Action {

	@Override
	public String getActionId() {
		return "ChasePINKY";
	}

	@Override
	public MOVE execute(Game game) {
		int posPinky = game.getGhostCurrentNodeIndex(GHOST.PINKY);

		if (posPinky == -1) {
			return MOVE.NEUTRAL;
		}

		int pacmanNode = game.getPacmanCurrentNodeIndex();
		MOVE lastMove = game.getPacmanLastMoveMade();

		return game.getNextMoveTowardsTarget(pacmanNode, posPinky, lastMove, DM.PATH);
	}

}
