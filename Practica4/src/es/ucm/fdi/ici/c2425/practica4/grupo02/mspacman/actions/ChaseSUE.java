package es.ucm.fdi.ici.c2425.practica4.grupo02.mspacman.actions;

import es.ucm.fdi.ici.Action;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class ChaseSUE implements Action {

	@Override
	public String getActionId() {
		return "ChaseSUE";
	}

	@Override
	public MOVE execute(Game game) {
		int posSue = game.getGhostCurrentNodeIndex(GHOST.SUE);

		if (posSue == -1) {
			return MOVE.NEUTRAL;
		}

		int pacmanNode = game.getPacmanCurrentNodeIndex();
		MOVE lastMove = game.getPacmanLastMoveMade();

		return game.getNextMoveTowardsTarget(pacmanNode, posSue, lastMove, DM.PATH);
	}

}
