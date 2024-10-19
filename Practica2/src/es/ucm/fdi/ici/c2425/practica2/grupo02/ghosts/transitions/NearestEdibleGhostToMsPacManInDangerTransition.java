package es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.transitions;

import es.ucm.fdi.ici.Input;
import es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.GhostsInput;
import es.ucm.fdi.ici.fsm.Transition;
import pacman.game.Constants.GHOST;

public class NearestEdibleGhostToMsPacManInDangerTransition implements Transition {

	private static final int THRESHOLD = 100;

	private GHOST ghost;
	private static int num;
	private int id;
	private int hacer;

	public NearestEdibleGhostToMsPacManInDangerTransition(GHOST ghost) {
		super();
		this.ghost = ghost;
		this.id = ++num;
	}

	@Override
	public boolean evaluate(Input in) {
		GhostsInput input = (GhostsInput) in;
		int distance = input.getPacManShortestPathDistance(ghost);
		return input.anyGhostEdible() && distance < THRESHOLD
				&& distance > input.getNearestGhostShortestPathDistance(ghost);
	}

	@Override
	public String toString() {
		return "Edible ghost in danger" + this.id;
	}

}
