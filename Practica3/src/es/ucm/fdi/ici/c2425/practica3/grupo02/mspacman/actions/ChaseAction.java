package es.ucm.fdi.ici.c2425.practica3.grupo02.mspacman.actions;

import es.ucm.fdi.ici.c2425.practica3.grupo02.mspacman.busqueda.MetodosBusqueda;
import es.ucm.fdi.ici.rules.RulesAction;
import jess.Fact;
import jess.JessException;
import jess.Value;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class ChaseAction implements RulesAction{

    private GHOST ghostToChase;

    @Override
    public String getActionId() {
        return "Pacman chase " + ghostToChase;
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
			Value value = actionFact.getSlotValue("chasestrategypacman");
			if(value == null)
				return;
			String strategyValue = value.stringValue(null);
            ghostToChase = GHOST.valueOf(strategyValue);
		} catch (JessException e) {
			e.printStackTrace();
		}
    }
    
}
