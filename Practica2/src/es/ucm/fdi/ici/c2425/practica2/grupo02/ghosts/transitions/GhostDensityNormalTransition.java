package es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.transitions;

import es.ucm.fdi.ici.Input;
import es.ucm.fdi.ici.fsm.Transition;
import pacman.game.Constants.GHOST;

public class GhostDensityNormalTransition implements Transition {

	private GHOST ghost;

	public GhostDensityNormalTransition(GHOST ghost) {
		super();
		this.ghost = ghost;
	}

	@Override
	public boolean evaluate(Input in) {
		Transition requireAction = new GhostRequiresActionTransition(this.ghost);
		Transition transition = new GhostDensityHighTransition(this.ghost);
		return requireAction.evaluate(in) && !transition.evaluate(in);
	}

	@Override
	public String toString() {
		return "Density normal";
	}

}
