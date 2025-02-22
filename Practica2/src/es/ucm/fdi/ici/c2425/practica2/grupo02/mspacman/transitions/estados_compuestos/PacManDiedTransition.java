package es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.transitions.estados_compuestos;

import es.ucm.fdi.ici.Input;
import es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.MsPacManInput;
import es.ucm.fdi.ici.fsm.Transition;

public class PacManDiedTransition implements Transition {

    private String state;
    public PacManDiedTransition(String state) {
         this.state = state;
    }
    @Override
    public boolean evaluate(Input in) {
        MsPacManInput input = (MsPacManInput)in;
        return input.getGame().wasPacManEaten();
    }

    @Override
    public String toString() {
        return "Die " + this.state;
    }

}
