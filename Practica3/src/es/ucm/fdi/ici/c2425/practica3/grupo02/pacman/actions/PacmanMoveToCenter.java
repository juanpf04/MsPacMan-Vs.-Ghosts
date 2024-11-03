package es.ucm.fdi.ici.c2425.practica3.grupo02.pacman.actions;

import es.ucm.fdi.ici.rules.RulesAction;
import jess.Fact;
import jess.JessException;
import jess.Value;
import pacman.game.Constants.DM;
import pacman.game.Constants.MOVE;
import pacman.game.Game;



public class PacmanMoveToCenter implements RulesAction{

    @Override
    public String getActionId() {
        return "Pacman move to center";
    }

    @Override
    public MOVE execute(Game game) {
        int index = game.getCurrentMaze().lairNodeIndex;
        return game.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(), index, game.getPacmanLastMoveMade(), DM.PATH);
    }

    @Override
    public void parseFact(Fact actionFact) {
        try {
			Value value = actionFact.getSlotValue("movetocenter");
			if(value == null)
				return;
		} catch (JessException e) {
			e.printStackTrace();
		}    
    }

    
}
