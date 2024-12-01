package es.ucm.fdi.ici.c2425.practica4.grupo02.ghosts.actions;

import java.util.Random;

import es.ucm.fdi.ici.Action;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class DisperseAction implements Action {

	private GHOST ghost;
	private Random rnd;

	public DisperseAction(GHOST ghost) {
		this.ghost = ghost;
		this.rnd = new Random();
	}

	@Override
	public MOVE execute(Game game) {
		if (game.doesGhostRequireAction(this.ghost)) {
			int ghostIndex = game.getGhostCurrentNodeIndex(this.ghost);
			MOVE lastMove = game.getGhostLastMoveMade(this.ghost);

			MOVE[] possibleMoves = game.getPossibleMoves(ghostIndex, lastMove);

			return possibleMoves[this.rnd.nextInt(possibleMoves.length)];
		}

		return MOVE.NEUTRAL;
	}

	@Override
	public String getActionId() {
		return "Disperse";
	}
}
