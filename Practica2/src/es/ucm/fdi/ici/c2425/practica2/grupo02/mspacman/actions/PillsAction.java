package es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.actions;

import es.ucm.fdi.ici.Action;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class PillsAction implements Action{
        
        @Override
        public String getActionId() {
            return "PillsAction";
        }
        
        @Override
        public String toString() {
            return "PillsAction";
        }

        @Override
        public MOVE execute(Game game) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'execute'");
        }
}
