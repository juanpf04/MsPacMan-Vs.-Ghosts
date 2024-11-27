package es.ucm.fdi.ici.c2425.practica3.grupo02.mspacman;

import java.util.Collection;
import java.util.Vector;
import es.ucm.fdi.ici.rules.RulesInput;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class MsPacManInput extends RulesInput {

	private int pacmanNode;
	private MOVE lastMove;
	private int numberNeightbourNodes;

	private double distanceToBlinky;
	private double distanceToInky;
	private double distanceToPinky;
	private double distanceToSue;

	private boolean isBlinkyEdible;
	private boolean isInkyEdible;
	private boolean isPinkyEdible;
	private boolean isSueEdible;

	public static GHOST nearestGhost = null;
	private int distanceToNearestGhost;

	public static GHOST nearestEdibleGhost = null;
	private int distanceToNearestEdibleGhost;

	private int distanceToNearestPill;
	private int indexPill;

	private int distanceToNearestPowerPill;
	private int indexPowerPill;

	public MsPacManInput(Game game) {
		super(game);
	}

	@Override
	public Collection<String> getFacts() {
		Vector<String> facts = new Vector<String>();

		// Añadir hechos para los fantasmas
		facts.add(String.format("(BLINKY (distance %d) (edible %s) (ghostEdibleTime %d) (ghostLairTime %d))",
				(int) this.distanceToBlinky, this.isBlinkyEdible ? "TRUE" : "FALSE",
				game.getGhostEdibleTime(GHOST.BLINKY), game.getGhostLairTime(GHOST.BLINKY)

		));
		facts.add(String.format("(INKY (distance %d) (edible %s) (ghostEdibleTime %d) (ghostLairTime %d))",
				(int) this.distanceToInky, this.isInkyEdible ? "TRUE" : "FALSE", game.getGhostEdibleTime(GHOST.INKY),
				game.getGhostLairTime(GHOST.INKY)));
		facts.add(String.format("(PINKY (distance %d) (edible %s) (ghostEdibleTime %d) (ghostLairTime %d))",
				(int) this.distanceToPinky, this.isPinkyEdible ? "TRUE" : "FALSE", game.getGhostEdibleTime(GHOST.PINKY),
				game.getGhostLairTime(GHOST.PINKY)));
		facts.add(String.format("(SUE (distance %d) (edible %s) (ghostEdibleTime %d) (ghostLairTime %d))",
				(int) this.distanceToSue, this.isSueEdible ? "TRUE" : "FALSE", game.getGhostEdibleTime(GHOST.SUE),
				game.getGhostLairTime(GHOST.SUE)));

		facts.add(String.format("(PACMAN (position %d) (last-move %s) (neightbour-nodes %d))", this.pacmanNode,
				this.lastMove, this.numberNeightbourNodes));

		// Añadir hecho con la distancia al punto de poder más cercano
		facts.add(String.format("(PILL (distance %d) (index %d))", this.distanceToNearestPill, this.indexPill));

		facts.add(String.format("(POWERPILL (distance %d) (index %d))", this.distanceToNearestPowerPill,
				this.indexPowerPill));

		return facts;
	}

	@Override
	public void parseInput() {

		this.pacmanNode = game.getPacmanCurrentNodeIndex();
		this.lastMove = game.getPacmanLastMoveMade();
		this.numberNeightbourNodes = game.getNeighbouringNodes(pacmanNode, lastMove).length;

		this.distanceToBlinky = game.getDistance(game.getPacmanCurrentNodeIndex(),
				game.getGhostCurrentNodeIndex(GHOST.BLINKY), DM.MANHATTAN);
		this.distanceToInky = game.getDistance(game.getPacmanCurrentNodeIndex(),
				game.getGhostCurrentNodeIndex(GHOST.INKY), DM.MANHATTAN);
		this.distanceToPinky = game.getDistance(game.getPacmanCurrentNodeIndex(),
				game.getGhostCurrentNodeIndex(GHOST.PINKY), DM.MANHATTAN);
		this.distanceToSue = game.getDistance(game.getPacmanCurrentNodeIndex(),
				game.getGhostCurrentNodeIndex(GHOST.SUE), DM.MANHATTAN);

		this.isBlinkyEdible = game.isGhostEdible(GHOST.BLINKY);
		this.isInkyEdible = game.isGhostEdible(GHOST.INKY);
		this.isPinkyEdible = game.isGhostEdible(GHOST.PINKY);
		this.isSueEdible = game.isGhostEdible(GHOST.SUE);

		this.distanceToNearestGhost = distanceToNearestGhost(game);
		this.distanceToNearestEdibleGhost = distanceToNearestEdibleGhost(game);

		distanceToNearestPill(game);
		distanceToNearestPowerPill(game);
	}

	private void distanceToNearestPill(Game game) {
		int pacman = game.getPacmanCurrentNodeIndex();
		int[] pills = game.getActivePillsIndices();
		int minDistance = Integer.MAX_VALUE;
		for (int pill : pills) {
			int distance = game.getShortestPathDistance(pacman, pill);
			if (distance < minDistance && distance != 0) {
				this.distanceToNearestPill = distance;
				this.indexPill = pill;
			}
		}
	}

	private void distanceToNearestPowerPill(Game game) {
		int pacman = game.getPacmanCurrentNodeIndex();
		int[] powerPills = game.getActivePowerPillsIndices();
		int minDistance = Integer.MAX_VALUE;
		for (int powerPill : powerPills) {
			int distance = game.getShortestPathDistance(pacman, powerPill);
			if (distance < minDistance && distance != 0) {
				this.distanceToNearestPowerPill = distance;
				this.indexPowerPill = powerPill;
			}
		}

		if (distanceToNearestPowerPill == 0 && this.indexPowerPill == 0) {
			this.distanceToNearestPowerPill = -1;
			this.indexPowerPill = -1;
		}
	}

	private int distanceToNearestGhost(Game game) {
		int pacman = game.getPacmanCurrentNodeIndex();

		int minDistance = Integer.MAX_VALUE;
		for (GHOST ghost : GHOST.values()) {
			int distance = (int) game.getDistance(pacman, game.getGhostCurrentNodeIndex(ghost), DM.MANHATTAN);
			if (distance < minDistance && distance != 0) {
				minDistance = distance;
				nearestGhost = ghost;
			}
		}
		return minDistance;
	}

	private int distanceToNearestEdibleGhost(Game game) {
		int pacman = game.getPacmanCurrentNodeIndex();

		int minDistance = Integer.MAX_VALUE;
		for (GHOST ghost : GHOST.values()) {
			if (game.isGhostEdible(ghost)) {
				int distance = (int) game.getDistance(pacman, game.getGhostCurrentNodeIndex(ghost), DM.MANHATTAN);
				if (distance < minDistance && distance != 0) {
					minDistance = distance;
					nearestEdibleGhost = ghost;
				}
			}
		}
		return minDistance;
	}

}
