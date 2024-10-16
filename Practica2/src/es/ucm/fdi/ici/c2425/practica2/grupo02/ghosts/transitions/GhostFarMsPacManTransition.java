package es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.transitions;

import es.ucm.fdi.ici.Input;
import es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.GhostsInput;
import es.ucm.fdi.ici.fsm.Transition;
import pacman.game.Constants.GHOST;

public class GhostFarMsPacManTransition implements Transition  {

	GHOST ghost;
	public GhostFarMsPacManTransition(GHOST ghost) {
		super();
		this.ghost = ghost;
	}



	@Override
	public boolean evaluate(Input in) {
		GhostsInput input = (GhostsInput)in;
		switch(ghost) {
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
		return this.ghost+" edible";
	}

	
	
}
