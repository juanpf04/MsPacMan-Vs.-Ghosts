package es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.transitions.estados_chase;

import es.ucm.fdi.ici.Input;
import es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.MsPacManInput;
import es.ucm.fdi.ici.fsm.Transition;

public class ShortEdibleTime implements Transition{

    private String state;

    public ShortEdibleTime(String state){
        super();
        this.state = state;
    }
    @Override
    public boolean evaluate(Input in) {
    MsPacManInput input = (MsPacManInput) in;
       return input.withoutEdibleTime();
    }

    @Override
    public String toString() {
        return "Short Edible Time fron " + this.state;
    }
    
}
