package es.ucm.fdi.ici.c2425.practica3.grupo02.ghosts.actions;

import es.ucm.fdi.ici.Action;
import es.ucm.fdi.ici.c2425.practica3.grupo02.ghosts.GhostsInput.GhostsInfo;
import es.ucm.fdi.ici.rules.RulesAction;
import jess.Fact;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class GoToPowePillAction implements RulesAction {

	private GHOST ghost;
	private GhostsInfo info;

	public GoToPowePillAction(GHOST ghost, GhostsInfo info) {
		this.ghost = ghost;
		this.info = info;
	}

	@Override
	public MOVE execute(Game game) {
		Action action = new GoToAction(this.ghost, this.info.closestPPillToPacman);
		return action.execute(game);
	}

	@Override
	public String getActionId() {
		return "Go to power pill";
	}

	@Override
	public void parseFact(Fact actionFact) {
		// Nothing to parse
	}

}
