package es.ucm.fdi.ici.c2425.practica3.grupo02.pacman;

import java.util.Collection;


import java.util.Vector;
import es.ucm.fdi.ici.rules.RulesInput;
import pacman.game.Constants.GHOST;
import pacman.game.Game;

public class MsPacManInput extends RulesInput {

    private double distanceToBlinky;
    private double distanceToInky;
    private double distanceToPinky;
    private double distanceToSue;

    public MsPacManInput(Game game) {
        super(game);
    }

    @Override
    public Collection<String> getFacts() {
        Vector<String> facts = new Vector<String>();

		facts.add(String.format("(BLINKY (distace %d))", this.distanceToBlinky));
		facts.add(String.format("(INKY (distace %d))", this.distanceToInky));
		facts.add(String.format("(PINKY (distace %d))", this.distanceToPinky));
		facts.add(String.format("(SUE (distace %d))", this.distanceToSue));
        
		return facts;
    }

    @Override
    public void parseInput() {
        this.distanceToBlinky = game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(), game.getGhostCurrentNodeIndex(GHOST.BLINKY));
        this.distanceToInky = game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(), game.getGhostCurrentNodeIndex(GHOST.INKY));
        this.distanceToPinky = game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(), game.getGhostCurrentNodeIndex(GHOST.PINKY));
        this.distanceToSue = game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(), game.getGhostCurrentNodeIndex(GHOST.SUE));
    }
    
}
