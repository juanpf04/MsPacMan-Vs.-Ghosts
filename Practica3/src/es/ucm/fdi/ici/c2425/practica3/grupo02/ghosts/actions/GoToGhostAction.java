package es.ucm.fdi.ici.c2425.practica3.grupo02.ghosts.actions;

import es.ucm.fdi.ici.Action;
import es.ucm.fdi.ici.c2425.practica3.grupo02.ghosts.GhostsInput.GhostsInfo;
import es.ucm.fdi.ici.rules.RulesAction;
import jess.Fact;
import jess.JessException;
import jess.Value;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class GoToGhostAction implements RulesAction {

	private GHOST ghost;
	private GhostsInfo info;
	private boolean isEdible;

	public GoToGhostAction(GHOST ghost, GhostsInfo info) {
		this.ghost = ghost;
		this.info = info;
	}

	@Override
	public MOVE execute(Game game) {
		Action action = new GoToAction(this.ghost,
				this.isEdible ? this.info.nearestGhost : this.info.nearestEdibleGhostToPacman);
		return action.execute(game);
	}

	@Override
	public String getActionId() {
		return "Go to ghost";
	}

	@Override
	public void parseFact(Fact actionFact) {
		try {
			Value value = actionFact.getSlotValue("edible");
			if (value == null)
				return;
			String edibleValue = value.stringValue(null);
			this.isEdible = Boolean.valueOf(edibleValue);
		} catch (JessException e) {
			e.printStackTrace();
		}
	}

}
