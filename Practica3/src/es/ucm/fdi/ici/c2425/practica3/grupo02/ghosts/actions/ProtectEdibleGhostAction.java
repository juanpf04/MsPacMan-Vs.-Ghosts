package es.ucm.fdi.ici.c2425.practica3.grupo02.ghosts.actions;

import es.ucm.fdi.ici.rules.RulesAction;
import jess.Fact;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class ProtectEdibleGhostAction implements RulesAction {

    private GHOST ghost;
    
	public ProtectEdibleGhostAction( GHOST ghost) {
		this.ghost = ghost;
	}

	@Override
	public MOVE execute(Game game) {
        if (game.doesGhostRequireAction(this.ghost))        //if it requires an action
        {
                return game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(this.ghost),
                        game.getPacmanCurrentNodeIndex(), game.getGhostLastMoveMade(this.ghost), DM.PATH);
        }
        return MOVE.NEUTRAL;
	}

	@Override
	public void parseFact(Fact actionFact) {
		// Nothing to parse
		
	}

	@Override
	public String getActionId() {
		return this.ghost + "chases";
	}

	

}
