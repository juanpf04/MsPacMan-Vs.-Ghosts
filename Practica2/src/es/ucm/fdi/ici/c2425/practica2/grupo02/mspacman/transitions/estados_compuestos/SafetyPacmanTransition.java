package es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.transitions.estados_compuestos;

import es.ucm.fdi.ici.Input;
import es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.MsPacManInput;
import es.ucm.fdi.ici.fsm.Transition;

public class SafetyPacmanTransition implements Transition {

    @Override
    public boolean evaluate(Input in) {
         MsPacManInput input = (MsPacManInput) in;
         return !input.getClosestNotEdibleGhost(40);
    }
    @Override
    public String toString() {
        return "Safety Pacman";
    }
    

}
