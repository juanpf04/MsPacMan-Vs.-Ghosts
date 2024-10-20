package es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.transitions;

import es.ucm.fdi.ici.Input;
import es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.GhostsInput;
import es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.GhostsInput.GhostsInfo;
import es.ucm.fdi.ici.fsm.Transition;
import pacman.game.Constants.GHOST;

public class EdibleGhostNearGhostThanMsPacManTransition implements Transition {

	private GHOST ghost;

	public EdibleGhostNearGhostThanMsPacManTransition(GHOST ghost) {
		super();
		this.ghost = ghost;
	}

	@Override
	public boolean evaluate(Input in) {
		GhostsInfo info = ((GhostsInput) in).getInfo();
		Transition requireAction = new GhostRequiresActionTransition(this.ghost);
		Transition transition = new GhostEdibleTransition(this.ghost);

		if (info.distancesFromEdibleGhostToGhost.get(this.ghost) == null
				|| info.distancesFromPacmanToGhost.get(this.ghost) == null)
			return false;

		return requireAction.evaluate(in) && transition.evaluate(in) && info.distancesFromEdibleGhostToGhost
				.get(this.ghost) < info.distancesFromPacmanToGhost.get(this.ghost);
	}

	@Override
	public String toString() {
		return "Near to Ghost than MsPacMan";
	}

}
