package es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.actions;

import java.util.ArrayList;
import java.util.List;

import es.ucm.fdi.ici.Action;
import es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.GhostsInfo;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class DisperseAction implements Action {

	GHOST ghost;
	GhostsInfo info;

	public DisperseAction(GHOST ghost, GhostsInfo info) {
		this.ghost = ghost;
		this.info = info;
	}

	@Override
	public MOVE execute(Game game) {
		if (game.doesGhostRequireAction(ghost)) {

			MOVE towardsPacman = game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(ghost),
					game.getPacmanCurrentNodeIndex(), game.getGhostLastMoveMade(ghost), DM.PATH);
			MOVE disperse = game.getApproximateNextMoveAwayFromTarget(game.getGhostCurrentNodeIndex(ghost),
					this.info.getNearestGhost(ghost), game.getGhostLastMoveMade(ghost), DM.PATH);

			return towardsPacman == disperse
					? game.getApproximateNextMoveAwayFromTarget(game.getGhostCurrentNodeIndex(ghost),
							game.getPacmanCurrentNodeIndex(), game.getGhostLastMoveMade(ghost), DM.PATH)
					: disperse;
		}

		return MOVE.NEUTRAL;
	}

	@Override
	public String getActionId() {
		return "disperse";
	}
}
