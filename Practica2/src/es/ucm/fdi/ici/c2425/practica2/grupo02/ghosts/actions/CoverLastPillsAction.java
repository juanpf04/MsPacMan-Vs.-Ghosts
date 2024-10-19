package es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.actions;

import es.ucm.fdi.ici.Action;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class CoverLastPillsAction implements Action {

	private GHOST ghost;

	public CoverLastPillsAction(GHOST ghost) {
		this.ghost = ghost;
	}

	@Override
	public MOVE execute(Game game) {
		Action action = new GoToAction(this.ghost, this.getNearestPill(this.ghost, game));
		return action.execute(game);
	}

	private int getNearestPill(GHOST ghost, Game game) {
		int ghostIndex = game.getGhostCurrentNodeIndex(ghost);
		MOVE lastMove = game.getGhostLastMoveMade(ghost);

		int nearestPill = 0;
		double minDistance = Double.MAX_VALUE;

		for (int pill : game.getActivePillsIndices()) {
			double distance = game.getDistance(ghostIndex, pill, lastMove, DM.PATH);
			if (distance < minDistance) {
				minDistance = distance;
				nearestPill = pill;
			}
		}

		return nearestPill;
	}

	@Override
	public String getActionId() {
		return "Cover last pills";
	}
}
