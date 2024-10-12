package es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.transitions;

import es.ucm.fdi.ici.Input;
import es.ucm.fdi.ici.fsm.Transition;

public class RandomTransition implements Transition {

	private double probability;
	public RandomTransition(double probability) {
		this.probability = probability;
	}

	@Override
	public boolean evaluate(Input in) {
		return Math.random() < this.probability;
	}

	@Override
	public String toString() {
		return String.format("Random Transition: %s", this.probability);
	}
}
