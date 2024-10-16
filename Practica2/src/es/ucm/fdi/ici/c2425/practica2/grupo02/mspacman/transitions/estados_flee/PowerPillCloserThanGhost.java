
package es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.transitions.estados_flee;

import es.ucm.fdi.ici.Input;
import es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.MsPacManInput;
import es.ucm.fdi.ici.fsm.Transition;

public class PowerPillCloserThanGhost implements Transition {

    private String state;
    private static final int RANGE = 30;

    public PowerPillCloserThanGhost(String state){
        super();
        this.state = state;
    }

    @Override
    public boolean evaluate(Input in) {
        MsPacManInput input = (MsPacManInput) in;
        return input.powerPillCloserThanGhost(RANGE);
    }

    @Override
    public String toString() {
        return "Power Pill Closer Than Ghost from" + this.state;
    }
}
