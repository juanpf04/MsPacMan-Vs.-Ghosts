package es.ucm.fdi.ici.c2425.practica4.grupo02;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import es.ucm.fdi.ici.Action;
import es.ucm.fdi.ici.fuzzy.ActionSelector;

/*
 * Executes the action with the highest score, in case of draw, highest priority
 */
public class MaxActionSelector implements ActionSelector {

	private Map<String, Action> actions;
	private Map<String, Integer> priorities;

	public MaxActionSelector() {
		this.actions = new HashMap<>();
		this.priorities = new HashMap<>();
	}

	public void addAction(Action action, int priority) {
		String id = action.getActionId();
		
		this.actions.put(id, action);
		this.priorities.put(id, priority);
	}

	@Override
	public Action selectAction(HashMap<String, Double> fuzzyOutput) {
		double max = Double.NEGATIVE_INFINITY;
		String maxActionName = null;
		
		for (Entry<String, Double> entry : fuzzyOutput.entrySet()) {
			String actionName = entry.getKey();
			double value = entry.getValue();
			
			if (value > max || (value == max && this.priorities.get(maxActionName) < this.priorities.get(actionName))) {
				max = value;
				maxActionName = actionName;
			}
		}

		if (maxActionName == null)
			return null;

		return this.actions.get(maxActionName);
	}

}
