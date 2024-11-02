package es.ucm.fdi.ici.c2425.practica3.grupo02.ghosts.actions;

import es.ucm.fdi.ici.Action;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class GoToAction implements Action {

	private GHOST ghost;
	private int node;

	public GoToAction(GHOST ghost, int node) {
		this.ghost = ghost;
		this.node = node;
	}

	@Override
	public MOVE execute(Game game) {
		if (game.doesGhostRequireAction(this.ghost))
			return game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(this.ghost), this.node,
					game.getGhostLastMoveMade(this.ghost), DM.PATH);

		return MOVE.NEUTRAL;
	}

	@Override
	public String getActionId() {
		return "Go to " + this.node;
	}
}
