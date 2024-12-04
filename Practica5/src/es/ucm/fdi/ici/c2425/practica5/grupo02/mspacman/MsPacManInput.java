package es.ucm.fdi.ici.c2425.practica5.grupo02.mspacman;

import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRQuery;
import es.ucm.fdi.ici.c2425.practica5.grupo02.similitud.RelativePosition;
import es.ucm.fdi.ici.cbr.CBRInput;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class MsPacManInput extends CBRInput {

	private static final int RANGO_DISTANCIA = 40;

	private Integer score;
	private Integer time;
	private Integer nearestGhost;
	private Integer nearestPPill;
	private Integer nearestPill;
	private Integer edible;
	private Integer jailGhosts;
	private Enum relativePosGhost;

	public MsPacManInput(Game game) {
		super(game);
		
	}

	
	@Override
	public void parseInput() {
		computeNearestGhost(game);
		computeNearestPPill(game);
		computeNearestPill(game);
		computeJailGhosts(game);
		computeRelativePosGhost(game);
		time = game.getTotalTime();
		score = game.getScore();
	}

	@Override
	public CBRQuery getQuery() {
		MsPacManDescription description = new MsPacManDescription();
		description.setEdibleGhosts(edible);
		description.setNearestGhostDistance(nearestGhost);
		description.setNearestPPillDistance(nearestPPill);
		description.setNearestPillDistance(nearestPill);
		description.setScore(score);
		description.setTimeEdibleGhost(time);
		description.setNumberJailGhosts(jailGhosts);
		description.setRelativePosGhost(relativePosGhost);
		
		CBRQuery query = new CBRQuery();
		query.setDescription(description);
		return query;
	}
	
	private void computeNearestGhost(Game game) {
		nearestGhost = Integer.MAX_VALUE;
		edible = 0;
		GHOST nearest = null;
		for(GHOST g: GHOST.values()) {
			int pos = game.getGhostCurrentNodeIndex(g);
			int distance; 
			if(pos != -1) 
				distance = (int)game.getDistance(game.getPacmanCurrentNodeIndex(), pos, DM.PATH);
			else
				distance = Integer.MAX_VALUE;
			if(distance < nearestGhost)
			{
				nearestGhost = distance;
				nearest = g;
			}
		}
		if(nearest!=null)
			edible += game.isGhostEdible(nearest) ? 1 : 0;
	}
	
	private void computeNearestPPill(Game game) {
		nearestPPill = Integer.MAX_VALUE;
		for(int pos: game.getPowerPillIndices()) {
			int distance = (int)game.getDistance(game.getPacmanCurrentNodeIndex(), pos, DM.PATH);
			if(distance < nearestGhost)
				nearestPPill = distance;
		}
	}

	private void computeNearestPill(Game game) {
		nearestPill = Integer.MAX_VALUE;
		for(int pos: game.getPillIndices()) {
			int distance = (int)game.getDistance(game.getPacmanCurrentNodeIndex(), pos, DM.PATH);
			if(distance < nearestGhost)
				nearestPill = distance;
		}
	}

	private void computeJailGhosts(Game game){
		jailGhosts = 0;
		for(GHOST g: GHOST.values()) {
			if(game.getGhostLairTime(g) > 0)
				jailGhosts++;
		}
	}

	private void computeRelativePosGhost(Game game){
		for(GHOST g: GHOST.values() ) {
			if(fantasmaDelante(game, g,RANGO_DISTANCIA)&& fantasmaDetras(game, g,RANGO_DISTANCIA)){
				relativePosGhost = RelativePosition.AMBOS;
			}
			else if(fantasmaDelante(game, g,RANGO_DISTANCIA)){
				relativePosGhost = RelativePosition.DELANTE;
			}
			else if(fantasmaDetras(game, g, RANGO_DISTANCIA)){
				relativePosGhost = RelativePosition.DETRAS;
			}
			else{
				relativePosGhost = RelativePosition.NINGUNO;
			}
		}
	}



	public boolean fantasmaDelante(Game game, GHOST ghost, int maxDistance) {
		if(game.getGhostLairTime(ghost) > 0) return false;

        int pacmanIndex = game.getPacmanCurrentNodeIndex();
        int ghostIndex = game.getGhostCurrentNodeIndex(ghost);
        MOVE pacmanMove = game.getPacmanLastMoveMade();

        int[] pathToGhost = game.getShortestPath(pacmanIndex, ghostIndex, pacmanMove);
        if (pathToGhost.length > 0 && pathToGhost.length <= maxDistance) {
            return true;
        }
        return false;
    }

    public boolean fantasmaDetras(Game game, GHOST ghost, int maxDistance) {
		if(game.getGhostLairTime(ghost) > 0) return false;
		
        int pacmanIndex = game.getPacmanCurrentNodeIndex();
        int ghostIndex = game.getGhostCurrentNodeIndex(ghost);
        MOVE pacmanMove = game.getPacmanLastMoveMade();

        int[] pathToGhost = game.getShortestPath(ghostIndex, pacmanIndex, pacmanMove.opposite());
        if (pathToGhost.length > 0 && pathToGhost.length <= maxDistance) {
            return true;
        }
        return false;
    }
}
