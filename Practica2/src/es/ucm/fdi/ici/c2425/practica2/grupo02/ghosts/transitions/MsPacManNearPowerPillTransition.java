package es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.transitions;

import es.ucm.fdi.ici.Input;
import es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.GhostsInput;
import es.ucm.fdi.ici.fsm.Transition;

public class MsPacManNearPowerPillTransition implements Transition {

	public static double THRESHOLD = 50;

	@Override
	public boolean evaluate(Input in) {
		GhostsInput input = (GhostsInput) in;
		return input.getMsPacManMinDistancePPill() < THRESHOLD;
	}

	@Override
	public String toString() {
		return "MsPacman near PPill";
	}

}
