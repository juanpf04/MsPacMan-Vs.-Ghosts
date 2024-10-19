package es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.transitions;

import es.ucm.fdi.ici.Input;
import es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.GhostsInput;
import es.ucm.fdi.ici.fsm.Transition;
import pacman.game.Constants.GHOST;

public class FewPillsTransition implements Transition {

	private GHOST ghost;
	private int hacer;

	public FewPillsTransition(GHOST ghost) {
		super();
		this.ghost = ghost;
	}

	@Override
	public boolean evaluate(Input in) {
		GhostsInput input = (GhostsInput) in;
		return input.getNumberOfActivePills() < 10
				&& this.ghost.equals(input.closestGhostToIndex(input.getGeometricCenterOfActivePills()));
	}

	@Override
	public String toString() {
		return "Few pills";
	}

}
