package es.ucm.fdi.ici.c2425.practica3.grupo02.ghosts.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import es.ucm.fdi.ici.rules.RulesAction;
import jess.Fact;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import es.ucm.fdi.ici.Action;
import es.ucm.fdi.ici.c2425.practica3.grupo02.ghosts.GhostsInput.GhostsInfo;

public class DisperseAction implements RulesAction {

	private GHOST ghost;
	private GhostsInfo info;
	private Random rnd = new Random();

	public DisperseAction(GHOST ghost, GhostsInfo info) {
		this.ghost = ghost;
		this.info = info;
	}

	@Override
	public MOVE execute(Game game) {
		if (game.doesGhostRequireAction(this.ghost)) {
			int ghostIndex = game.getGhostCurrentNodeIndex(this.ghost);
			MOVE lastMove = game.getGhostLastMoveMade(this.ghost);

			MOVE move = game.getApproximateNextMoveAwayFromTarget(ghostIndex, info.closestGhostIndex.get(ghost),
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

	@Override
	public void parseFact(Fact actionFact) {
		// Nothing to parse
	}

}
