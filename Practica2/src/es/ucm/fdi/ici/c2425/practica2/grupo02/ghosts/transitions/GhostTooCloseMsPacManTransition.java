package es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.transitions;

import es.ucm.fdi.ici.Input;
import es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.GhostsInput;
import es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.GhostsInput.GhostsInfo;
import es.ucm.fdi.ici.fsm.Transition;
import pacman.game.Constants.GHOST;

public class GhostTooCloseMsPacManTransition implements Transition {

	private static int THRESHOLD = 30;

	private GHOST ghost;
	private static int num;
	private int id;

	public GhostTooCloseMsPacManTransition(GHOST ghost) {
		super();
		this.ghost = ghost;
		this.id = ++num;
	}

	@Override
	public boolean evaluate(Input in) {
		GhostsInfo info = ((GhostsInput) in).getInfo();
		Transition requireAction = new GhostRequiresActionTransition(this.ghost);
		return requireAction.evaluate(in) && info.distancesFromGhostToPacman.get(this.ghost) < THRESHOLD;
	}

	@Override
	public String toString() {
		return "Close to MsPacMan " + this.id;
	}

}
