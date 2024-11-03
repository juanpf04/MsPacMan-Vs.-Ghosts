package es.ucm.fdi.ici.c2425.practica3.grupo02.mspacman.actions;

import es.ucm.fdi.ici.rules.RulesAction;
import jess.Fact;
import jess.JessException;
import jess.Value;
import pacman.game.Constants.DM;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class ChasePills implements RulesAction {

    
	private int chasePills;

    @Override
    public String getActionId() {
        return "Pacman chase pills";
    }

    @Override
    public MOVE execute(Game game) {
        return game.getApproximateNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(), chasePills, game.getPacmanLastMoveMade(), DM.PATH);
    }
    
    @Override
    public void parseFact(Fact actionFact) {
         try {
			Value value = actionFact.getSlotValue("chasepills");
			if(value == null)
				return;
			int indexPill = value.intValue(null);
			chasePills = indexPill;
		} catch (JessException e) {
			e.printStackTrace();
		}
    }
    
}
