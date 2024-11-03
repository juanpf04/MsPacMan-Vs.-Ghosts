package es.ucm.fdi.ici.c2425.practica3.grupo02.pacman.actions;

import es.ucm.fdi.ici.c2425.practica3.grupo02.pacman.busqueda.MetodosBusqueda;
import es.ucm.fdi.ici.rules.RulesAction;

import jess.Fact;
import jess.JessException;
import jess.Value;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class RunAwayAction implements RulesAction {

    private GHOST ghostToRunAwayFrom;

    @Override
    public String getActionId() {
        return "Pacman run away from " + ghostToRunAwayFrom;
    }

    @Override
    public MOVE execute(Game game) {
        MetodosBusqueda mb = new MetodosBusqueda(game);
        int pacmanNode = game.getPacmanCurrentNodeIndex();
        MOVE pacmanLastMove = game.getPacmanLastMoveMade();
        return mb.getBestMove(pacmanNode, pacmanLastMove);
    }

    @Override
    public void parseFact(Fact actionFact) {
        try {
			Value value = actionFact.getSlotValue("runawaystrategypacman");
			if(value == null)
				return;
			String strategyValue = value.stringValue(null);
            ghostToRunAwayFrom = GHOST.valueOf(strategyValue);
		} catch (JessException e) {
			e.printStackTrace();
		}
    }

}
