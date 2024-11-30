package es.ucm.fdi.ici.c2425.practica4.grupo02.mspacman.actions;

import es.ucm.fdi.ici.Action;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class RunAwayFromBLINKY implements Action {

	@Override
	public String getActionId() {
		return "RunawayFromBLINKY";
	}

	@Override
	public MOVE execute(Game game) {
		int posBlinky = game.getGhostCurrentNodeIndex(GHOST.BLINKY);

		if (posBlinky == -1) {
			return MOVE.NEUTRAL;
		}

		int pacmanNode = game.getPacmanCurrentNodeIndex();
		MOVE lastMove = game.getPacmanLastMoveMade();

		try {
			return game.getNextMoveAwayFromTarget(pacmanNode, posBlinky, lastMove, DM.PATH);
		} catch (Exception e) {
			return MOVE.NEUTRAL;
		}
	}

}
