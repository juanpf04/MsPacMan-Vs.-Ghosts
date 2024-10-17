package es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.actions;

import es.ucm.fdi.ici.Action;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class CoverPowerPillAction implements Action {

	GHOST ghost;
	int powerPillIndex;
	int id;
	static int count;

	public CoverPowerPillAction(GHOST ghost, int powerPillIndex) {
		this.ghost = ghost;
		this.powerPillIndex = powerPillIndex;
		this.id = ++count;
	}

	@Override
	public MOVE execute(Game game) {
		if (game.doesGhostRequireAction(ghost))
			return game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(ghost), this.powerPillIndex,
					game.getGhostLastMoveMade(ghost), DM.PATH);

		return MOVE.NEUTRAL;
	}

	@Override
	public String getActionId() {
		return "cover power pill " + this.id;
	}
}
