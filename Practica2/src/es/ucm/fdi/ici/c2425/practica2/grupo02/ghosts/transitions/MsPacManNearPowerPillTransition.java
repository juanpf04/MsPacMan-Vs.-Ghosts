package es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.transitions;

import es.ucm.fdi.ici.Input;
import es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.GhostsInput;
import es.ucm.fdi.ici.fsm.Transition;

public class MsPacManNearPowerPillTransition implements Transition {

	public static double NEAR_DISTANCE = 30;
	static int num;
	int id;

	public MsPacManNearPowerPillTransition() {
		super();
		this.id = ++num;
	}

	@Override
	public boolean evaluate(Input in) {
		GhostsInput input = (GhostsInput) in;
		return input.getMinPacmanDistancePPill() < NEAR_DISTANCE;
	}

	@Override
	public String toString() {
		return "MsPacman near PPill " + this.id;
	}

}
