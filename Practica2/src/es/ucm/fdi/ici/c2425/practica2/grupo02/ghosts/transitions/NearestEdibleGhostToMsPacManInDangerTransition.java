package es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.transitions;

import es.ucm.fdi.ici.Input;
import es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.GhostsInput;
import es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.GhostsInput.GhostsInfo;
import es.ucm.fdi.ici.fsm.Transition;
import pacman.game.Constants.GHOST;

public class NearestEdibleGhostToMsPacManInDangerTransition implements Transition {

	private static final int THRESHOLD = 70;

	private GHOST ghost;
	private static int num;
	private int id;

	public NearestEdibleGhostToMsPacManInDangerTransition(GHOST ghost) {
		super();
		this.ghost = ghost;
		this.id = ++num;
	}

	@Override
	public boolean evaluate(Input in) {
		GhostsInfo info = ((GhostsInput) in).getInfo();
		Transition requireAction = new GhostRequiresActionTransition(this.ghost);
		int distance = info.distanceFromPacmanToNearestEdibleGhost;
		return requireAction.evaluate(in) && info.edibleGhosts > 0 && distance < THRESHOLD
				&& distance > info.distancesFromGhostToEdibleGhost.get(this.ghost);
	}

	@Override
	public String toString() {
		return "Edible ghost in danger" + this.id;
	}

}
