package es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.transitions.estados_pills;

import es.ucm.fdi.ici.Input;
import es.ucm.fdi.ici.fsm.Transition;

public class PathSelected implements Transition{

    @Override
    public boolean evaluate(Input in) {
       return true;
    }

    @Override
    public String toString() {
        return "Path Selected";
    }
    

}
