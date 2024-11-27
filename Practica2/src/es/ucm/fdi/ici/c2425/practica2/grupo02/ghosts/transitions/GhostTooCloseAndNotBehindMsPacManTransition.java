package es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.transitions;

import es.ucm.fdi.ici.Input;
import es.ucm.fdi.ici.fsm.Transition;
import pacman.game.Constants.GHOST;

public class GhostTooCloseAndNotBehindMsPacManTransition implements Transition {

	private GHOST ghost;

	public GhostTooCloseAndNotBehindMsPacManTransition(GHOST ghost) {
		super();
		this.ghost = ghost;
	}

	@Override
	public boolean evaluate(Input in) {
		Transition transition = new GhostRequiresActionTransition(this.ghost);
		Transition transition1 = new GhostTooCloseMsPacManTransition(this.ghost);
		Transition transition2 = new GhostBehindMsPacManTransition(this.ghost);
		return transition.evaluate(in) && transition1.evaluate(in) && !transition2.evaluate(in);
	}

	@Override
	public String toString() {
		return "Close and not behind MsPacMan";
	}

}
