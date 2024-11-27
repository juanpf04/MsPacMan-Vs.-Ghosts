package es.ucm.fdi.ici.c2425.practica4.grupo02.mspacman.actions;


import java.util.HashSet;
import java.util.Random;
import java.util.Set;


import es.ucm.fdi.ici.Action;
import pacman.game.Constants.DM;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class GoToPPillAction implements Action {
    
	private Random rnd = new Random();
    private MOVE[] allMoves = MOVE.values();
	Set<Integer> powerPills = new HashSet<Integer>();
	public GoToPPillAction() {
	}
	
	@Override
	public MOVE execute(Game game) {
		initializePowerPills(game);
		//return allMoves[rnd.nextInt(allMoves.length)];
		return moveOfPowerPillNearestToPacmanValidate(game);
    }

	private void initializePowerPills(Game game){
		powerPills.clear();
		for (int i : game.getActivePowerPillsIndices()){
			powerPills.add(i);
		}
	}
	private MOVE moveOfPowerPillNearestToPacmanValidate(Game game){
		int pacmanNode = game.getPacmanCurrentNodeIndex();
		int ppNearest = Integer.MAX_VALUE;
		for(int node: powerPills){

			if(node != -1 && game.getShortestPathDistance(pacmanNode, node) < ppNearest){
				ppNearest = node;
			}
		}

		if(ppNearest == Integer.MAX_VALUE){
			return allMoves[rnd.nextInt(allMoves.length)];
		}
		return game.getApproximateNextMoveTowardsTarget(pacmanNode, ppNearest, game.getPacmanLastMoveMade(), DM.PATH);
	}

	@Override
	public String getActionId() {
		return "GoToPPill";
	}
            
}
