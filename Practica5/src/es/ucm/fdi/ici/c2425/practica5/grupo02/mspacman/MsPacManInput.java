package es.ucm.fdi.ici.c2425.practica5.grupo02.mspacman;

import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRQuery;
import es.ucm.fdi.ici.cbr.CBRInput;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Game;

public class MsPacManInput extends CBRInput {

	public MsPacManInput(Game game) {
		super(game);
		
	}

	private Integer score;
	private Integer timeEdibleGhost;
	private Integer nearestPPillDistance;
	private Integer nearestPillDistance;
	private Integer nearestGhostDistance;
	private Integer numberEdibleGhosts;
	private Integer numberJailGhosts;
	@SuppressWarnings("rawtypes")
	private Enum relativePosGhost;
	@SuppressWarnings("rawtypes")
	private Enum relativePosEdibleGhost;
	
	@Override
	public void parseInput() {
		computeNearestGhost(game);
		computeNearestPPill(game);
		timeEdibleGhost = game.getTotalTime();
		score = game.getScore();
	}

	@Override
	public CBRQuery getQuery() {
		MsPacManDescription description = new MsPacManDescription();
		description.setTime(timeEdibleGhost);
		description.setNearestPPillDistance(nearestPPillDistance);
		description.setNearestPillDistance(nearestPillDistance);
		description.setNearestGhostDistance(nearestGhostDistance);
		description.setNumberEdibleGhosts(numberEdibleGhosts);
		description.setNumberJailGhosts(numberJailGhosts);
		description.setRelativePosGhost(relativePosGhost);
		description.setRelativePosEdibleGhost(relativePosEdibleGhost);
		
		CBRQuery query = new CBRQuery();
		query.setDescription(description);
		return query;
	}
	
	private void computeNearestGhost(Game game) {
		nearestGhostDistance = Integer.MAX_VALUE;
		GHOST nearest = null;
		for(GHOST g: GHOST.values()) {
			int pos = game.getGhostCurrentNodeIndex(g);
			int distance; 
			if(pos != -1) 
				distance = (int)game.getDistance(game.getPacmanCurrentNodeIndex(), pos, DM.PATH);
			else
				distance = Integer.MAX_VALUE;
			if(distance < nearestGhostDistance)
			{
				nearestGhostDistance = distance;
				nearest = g;
			}
		}
	}
	
	private void computeNearestPPill(Game game) {
		nearestPPillDistance = Integer.MAX_VALUE;
		for(int pos: game.getPowerPillIndices()) {
			int distance = (int)game.getDistance(game.getPacmanCurrentNodeIndex(), pos, DM.PATH);
			if(distance < nearestGhostDistance)
				nearestPPillDistance = distance;
		}
	}
}
