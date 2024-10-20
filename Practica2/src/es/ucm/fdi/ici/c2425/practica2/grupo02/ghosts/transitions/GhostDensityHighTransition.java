package es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.transitions;

import es.ucm.fdi.ici.Input;
import es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.GhostsInput;
import es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.GhostsInput.GhostsInfo;
import es.ucm.fdi.ici.fsm.Transition;
import pacman.game.Constants.GHOST;

public class GhostDensityHighTransition implements Transition {

	private static final double THRESHOLD = 1.2;

	private GHOST ghost;

	private static int num;
	private int id;

	public GhostDensityHighTransition(GHOST ghost) {
		super();
		this.ghost = ghost;
		this.id = ++num;
	}

	@Override
	public boolean evaluate(Input in) {
		GhostsInfo info = ((GhostsInput) in).getInfo();
		return info.ghostDensity.get(this.ghost) > THRESHOLD;
	}

	@Override
	public String toString() {
		return "Density high " + this.id;
	}

}
