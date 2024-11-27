package es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.transitions;

import es.ucm.fdi.ici.Input;
import es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.GhostsInput;
import es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.GhostsInput.GhostsInfo;
import es.ucm.fdi.ici.fsm.Transition;
import pacman.game.Constants.GHOST;

public class GhostBehindMsPacManTransition implements Transition {

	private GHOST ghost;

	public GhostBehindMsPacManTransition(GHOST ghost) {
		super();
		this.ghost = ghost;
	}

	@Override
	public boolean evaluate(Input in) {
		GhostsInfo info = ((GhostsInput) in).getInfo();
		Transition requireAction = new GhostRequiresActionTransition(this.ghost);
		return requireAction.evaluate(in) && info.isGhostBehindPacman.get(this.ghost);
	}

	@Override
	public String toString() {
		return "Behind MsPacMan";
	}

}
