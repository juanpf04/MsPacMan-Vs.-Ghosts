package es.ucm.fdi.ici.c2425.practica0.grupoIndividual;

import java.awt.Color;
import java.util.Random;

import pacman.controllers.PacmanController;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.GameView;

public class MsPacMan extends PacmanController{
	
	private static final int LIMIT = 200;
	private Color[] colours = {Color.RED, Color.PINK, Color.CYAN, Color.ORANGE};

	
	@Override
	public MOVE getMove(Game game, long timeDue) {
		GHOST nearestGhost = getNearestChasingGhost(game, LIMIT);
		
		int pacmanNode = game.getPacmanCurrentNodeIndex();
		
		//Huimos del fantasma no comestible mas cercano a nosotros
		if(nearestGhost != null) {
			if(game.getGhostLairTime(nearestGhost)<=0)
				GameView.addPoints(game,colours[nearestGhost.ordinal()],game.getShortestPath(
					game.getGhostCurrentNodeIndex(nearestGhost),
					game.getPacmanCurrentNodeIndex()));
			
			
			return game.getNextMoveAwayFromTarget(pacmanNode,
					game.getGhostCurrentNodeIndex(nearestGhost), 
					DM.EUCLID);
			
		}
		
		
		//Si no hay fantasma del que huir porque todos son comestibles, buscamos el mas cercxano y lo perseguimos
		nearestGhost = getNearestEdibleGhost(game,LIMIT);
		if(nearestGhost != null) {
			
			if(game.getGhostLairTime(nearestGhost)<=0)
				GameView.addPoints(game,colours[nearestGhost.ordinal()],game.getShortestPath(
					game.getGhostCurrentNodeIndex(nearestGhost),
					game.getPacmanCurrentNodeIndex()));
			
			return game.getNextMoveTowardsTarget(pacmanNode,
					game.getGhostCurrentNodeIndex(nearestGhost), DM.EUCLID);
		}

		//Si no hay peligro por ningun fantasma ni tenemos ningunu fantasma comestible cerca, nuestro objetivo son las pills(si aún quedan en el mapa)

		int nearestPill = getNearestPill(game);
		return game.getNextMoveTowardsTarget(pacmanNode, nearestPill, DM.EUCLID);
	}
	
	
	private GHOST getNearestChasingGhost(Game game,int limit) {
		GHOST fantasma = null;
		double distancia_menor = 100000; //Lo he peusto aleatoriamente, solo se que la distancia nunca va a ser mayor que eso en la simulacion
		for(GHOST g: GHOST.values()) {
			double distance = Math.abs(game.getPacmanCurrentNodeIndex() - game.getGhostCurrentNodeIndex(g));
			
			if(!game.isGhostEdible(g) && distance < distancia_menor && distance  <= limit) {
				distancia_menor = distance;
				fantasma = g;
			}
			
		}
		
		return fantasma;
	}
	
	private GHOST getNearestEdibleGhost(Game game, int limit) {
		double distance = 100000;
		GHOST fantasma = null;
		
		for(GHOST g: GHOST.values()) {
			double real_distance = Math.abs(game.getPacmanCurrentNodeIndex() - game.getGhostCurrentNodeIndex(g));
			
			if(game.isGhostEdible(g) && real_distance  <= limit && distance > real_distance) {
				distance = real_distance;
				fantasma = g;
			}
		}
		
		return fantasma;
	}
	
	private int getNearestPill(Game game) {
		int nearestPill = 100000;
		
		for(int p: game.getActivePowerPillsIndices()) {
			int distance = Math.abs((int)game.getPacmanCurrentNodeIndex() - p);
			
			if(nearestPill > distance) {
				nearestPill = distance;
			}
			
		}
		
		return nearestPill;
	}
}
