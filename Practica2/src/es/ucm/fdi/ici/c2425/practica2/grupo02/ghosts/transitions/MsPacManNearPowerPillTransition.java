package es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.transitions;

import es.ucm.fdi.ici.Input;
import es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.GhostsInput;
import es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.GhostsInput.GhostsInfo;
import es.ucm.fdi.ici.fsm.Transition;

public class MsPacManNearPowerPillTransition implements Transition {

	private static double THRESHOLD = 40;

	@Override
	public boolean evaluate(Input in) {
		GhostsInfo info = ((GhostsInput) in).getInfo();
		return info.minDistanceFromPacmanToPPill < THRESHOLD;
	}

	@Override
	public String toString() {
		return "MsPacman near PPill";
	}

}
