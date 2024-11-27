package es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import es.ucm.fdi.ici.Action;
import pacman.game.Constants.DM;
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

			MOVE move = game.getApproximateNextMoveAwayFromTarget(ghostIndex, game.getPacmanCurrentNodeIndex(),
					lastMove, DM.PATH);

			List<MOVE> possibleMoves = new ArrayList<MOVE>();

			for (MOVE m : game.getPossibleMoves(ghostIndex, lastMove))
				if (m != move)
					possibleMoves.add(m);

			return possibleMoves.get(this.rnd.nextInt(possibleMoves.size()));
		}

		return MOVE.NEUTRAL;
	}

	@Override
	public String getActionId() {
		return "Disperse";
	}
}
