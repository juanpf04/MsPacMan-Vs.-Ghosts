package es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.actions;

import es.ucm.fdi.ici.Action;
import es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.GhostsInfo;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class GoToGhostAction implements Action {

	GHOST ghost;
	GhostsInfo info;

	public GoToGhostAction(GHOST ghost, GhostsInfo info) {
		this.ghost = ghost;
		this.info = info;
	}

	@Override
	public MOVE execute(Game game) {
		if (game.doesGhostRequireAction(ghost))
			return game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(ghost),
					this.info.getNearestGhost(ghost), game.getGhostLastMoveMade(ghost), DM.PATH);

		return MOVE.NEUTRAL;
	}

	@Override
	public String getActionId() {
		return "go to ghost";
	}
}
