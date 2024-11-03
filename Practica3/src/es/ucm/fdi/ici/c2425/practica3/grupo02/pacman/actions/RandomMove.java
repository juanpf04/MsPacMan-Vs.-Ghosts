package es.ucm.fdi.ici.c2425.practica3.grupo02.pacman.actions;

import java.util.Random;

import es.ucm.fdi.ici.rules.RulesAction;
import jess.Fact;
import jess.JessException;
import jess.Value;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class RandomMove implements RulesAction {

    @Override
    public String getActionId() {
        return "Pacman random move";
    }

    @Override
    public MOVE execute(Game game) {
        Random r = new Random();
        return MOVE.values()[r.nextInt(MOVE.values().length - 1)];
    }

    @Override
    public void parseFact(Fact actionFact) {
        try {
			Value value = actionFact.getSlotValue("moverandom");
			if(value == null)
				return;
		} catch (JessException e) {
			e.printStackTrace();
		}
    }

}
