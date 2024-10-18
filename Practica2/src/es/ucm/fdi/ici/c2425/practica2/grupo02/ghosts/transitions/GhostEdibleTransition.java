package es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.transitions;

import es.ucm.fdi.ici.Input;
import es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.GhostsInput;
import es.ucm.fdi.ici.fsm.Transition;
import pacman.game.Constants.GHOST;

public class GhostEdibleTransition implements Transition {

	GHOST ghost;
	static int num;
	int id;

	public GhostEdibleTransition(GHOST ghost) {
		super();
		this.ghost = ghost;
		this.id = ++num;
	}

	@Override
	public boolean evaluate(Input in) {
		GhostsInput input = (GhostsInput) in;
		switch (ghost) {
		case BLINKY:
			return input.isBLINKYedible();
		case INKY:
			return input.isINKYedible();
		case PINKY:
			return input.isPINKYedible();
		case SUE:
			return input.isSUEedible();
		default:
			return false;
		}
	}

	@Override
	public String toString() {
		return "Edible " + this.id;
	}

}
