package es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.transitions.estados_chase;

import es.ucm.fdi.ici.Input;
import es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.MsPacManInput;
import es.ucm.fdi.ici.fsm.Transition;

public class IndifferentNumberGhost implements Transition{

    @Override
    public boolean evaluate(Input in) {
        MsPacManInput input = (MsPacManInput) in;
        return input.sameNumberOfEdibleGhostInEachPath();
    }

    @Override
    public String toString() {
        return "Indifferent Number Ghost";
    }
}
