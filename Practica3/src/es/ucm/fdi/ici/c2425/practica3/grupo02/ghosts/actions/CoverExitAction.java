package es.ucm.fdi.ici.c2425.practica3.grupo02.ghosts.actions;

import es.ucm.fdi.ici.Action;
import es.ucm.fdi.ici.c2425.practica3.grupo02.ghosts.GhostsInput.GhostsInfo;
import es.ucm.fdi.ici.rules.RulesAction;
import jess.Fact;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class CoverExitAction implements RulesAction {

	private GHOST ghost;
	private GhostsInfo info;

	public CoverExitAction(GHOST ghost, GhostsInfo info) {
		this.ghost = ghost;
		this.info = info;
	}

	@Override
	public MOVE execute(Game game) {
		Action action = new GoToAction(this.ghost, this.getNearestExit(game));
		return action.execute(game);
	}

	private int getNearestExit(Game game) {
		int ghostIndex = game.getGhostCurrentNodeIndex(this.ghost);

		int exit = 0;
		int minDistance = Integer.MAX_VALUE;

		for (int e : this.info.exits) {
			int distance = game.getShortestPathDistance(ghostIndex, e);
			if (distance < minDistance) {
				exit = e;
				minDistance = distance;
			}
		}

		return exit;
	}

	@Override
	public String getActionId() {
		return "Cover exit";
	}

	@Override
	public void parseFact(Fact actionFact) {
		// Nothing to parse
	}

}
