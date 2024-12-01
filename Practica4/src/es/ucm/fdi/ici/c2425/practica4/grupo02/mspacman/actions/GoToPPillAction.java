package es.ucm.fdi.ici.c2425.practica4.grupo02.mspacman.actions;

import java.util.HashSet;
import java.util.Set;

import es.ucm.fdi.ici.Action;
import pacman.game.Constants.DM;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class GoToPPillAction implements Action {

	private Set<Integer> powerPills;

	public GoToPPillAction() {
		this.powerPills = new HashSet<Integer>();
		this.powerPills.add(97);
		this.powerPills.add(102);
		this.powerPills.add(1143);
		this.powerPills.add(1148);
	}

	@Override
	public MOVE execute(Game game) {
		try {
			update(game);
			return moveOfPowerPillNearestToPacmanValidate(game);
		} catch (Exception e) {
			return MOVE.NEUTRAL;
		}
	}

	private void update(Game game) {
		for (int node : powerPills) {
			if (game.getShortestPathDistance(node, game.getPacmanCurrentNodeIndex()) < 5)
				powerPills.remove(node);
		}
	}

	private MOVE moveOfPowerPillNearestToPacmanValidate(Game game) {
		int pacmanNode = game.getPacmanCurrentNodeIndex();
		int ppNearest = Integer.MAX_VALUE;
		for (int node : powerPills) {

			if (node != -1 && game.getShortestPathDistance(pacmanNode, node) < ppNearest) {
				ppNearest = node;
			}
		}

		if (ppNearest == Integer.MAX_VALUE) {
			return MOVE.NEUTRAL;
		}
		return game.getApproximateNextMoveTowardsTarget(pacmanNode, ppNearest, game.getPacmanLastMoveMade(), DM.PATH);
	}

	@Override
	public String getActionId() {
		return "GoToPPill";
	}

}
