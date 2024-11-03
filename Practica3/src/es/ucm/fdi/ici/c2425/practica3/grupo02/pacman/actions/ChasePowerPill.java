package es.ucm.fdi.ici.c2425.practica3.grupo02.pacman.actions;

import es.ucm.fdi.ici.rules.RulesAction;
import jess.Fact;
import jess.JessException;
import jess.Value;
import pacman.game.Constants.DM;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class ChasePowerPill implements RulesAction{

    private int chasePowerPills;

    @Override
    public String getActionId() {
        return "Pacman chase power pill";
    }

    @Override
    public MOVE execute(Game game) {
        return game.getApproximateNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(), this.chasePowerPills, game.getPacmanLastMoveMade(), DM.PATH);
    }

    @Override
    public void parseFact(Fact actionFact) {
         try {
			Value value = actionFact.getSlotValue("chasepowerpills");
			if(value == null)
				return;
			int powerPillIndex = value.intValue(null);
			chasePowerPills = powerPillIndex;
		} catch (JessException e) {
			e.printStackTrace();
		}
    }
    
}
