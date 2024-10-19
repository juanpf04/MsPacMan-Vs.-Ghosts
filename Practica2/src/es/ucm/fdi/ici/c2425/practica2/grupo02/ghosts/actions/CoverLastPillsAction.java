package es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.actions;

import es.ucm.fdi.ici.Action;
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
		Action action = new GoToAction(this.ghost, this.getNearestPill(game, this.ghost));
		return action.execute(game);
	}

	private int getNearestPill(Game game, GHOST ghost) {
		int ghostIndex = game.getGhostCurrentNodeIndex(ghost);
		MOVE lastMove = game.getGhostLastMoveMade(ghost);

		int nearestPill = 0;
		int minDistance = Integer.MAX_VALUE;

		for (int pill : game.getActivePillsIndices()) {
			int distance = game.getShortestPathDistance(ghostIndex, pill, lastMove);
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
