package es.ucm.fdi.ici.c2425.practica5.grupo02;

import java.util.*;

/**
 * A priority queue that allows for updating the priority of elements and increasing the priority of elements.
 * @param <T> The type of elements stored in the queue.
 */
public class VariablePQ<T> {

    private final Map<T, Double> priorityMap; // Map to store priorities
    private final TreeSet<T> priorityQueue;   // Priority queue sorted by priority

    /**
     * Constructor to initialize the priority queue and the priority map.
     */
    public VariablePQ() {
        this.priorityMap = new HashMap<>();
        this.priorityQueue = new TreeSet<>((a, b) -> {
            int priorityComparison = Double.compare(this.priorityMap.get(a), this.priorityMap.get(b));
            if (priorityComparison == 0) {
                return a.hashCode() - b.hashCode(); // Tie-breaking using hashCode
            }
            return priorityComparison;
        });
    }

    /**
     * Adds an element with a given priority to the queue.
     * If the element already exists, updates its priority.
     *
     * @param element  The element to add.
     * @param priority The priority of the element.
     */
    public void add(T element, double priority) {
        if (this.priorityMap.containsKey(element)) {
            update(element, priority); // Update priority if element exists
            return;
        }
        this.priorityMap.put(element, priority);
        this.priorityQueue.add(element);
    }

    /**
     * Removes the element with the highest priority (lowest priority value) from the queue.
     */
    public void pop() {
        if (this.priorityQueue.isEmpty()) {
            throw new NoSuchElementException("The queue is empty");
        }
        T element = this.priorityQueue.pollFirst(); // Remove the element with the highest priority
        this.priorityMap.remove(element); // Remove from the priority map
    }

    /**
     * Retrieves the element with the highest priority (lowest priority value) without removing it.
     *
     * @return The element with the highest priority, or null if the queue is empty.
     */
    public T top() {
        return this.priorityQueue.isEmpty() ? null : this.priorityQueue.first();
    }

    /**
     * Updates the priority of an existing element in the queue.
     *
     * @param element     The element to update.
     * @param newPriority The new priority value.
     */
    public void update(T element, double newPriority) {
        if (!this.priorityMap.containsKey(element)) {
            add(element, newPriority); // Add element if it does not exist
            return;
        }
        this.priorityQueue.remove(element);          // Remove element from the queue
        this.priorityMap.put(element, newPriority);  // Update its priority in the map
        this.priorityQueue.add(element);            // Reinsert element with updated priority
    }

    /**
     * Increases the priority by adding the given value to its current priority.
     *
     * @param element         The element whose priority needs to be increased.
     * @param increaseByValue The value to add to the current priority.
     */
    public void increase(T element, double increaseByValue) {
        if (!this.priorityMap.containsKey(element)) {
            add(element, increaseByValue); // Add element if it does not exist
            return;
        }
        double currentPriority = this.priorityMap.get(element);
        update(element, currentPriority + increaseByValue); // Use the update method to set the new priority
    }

    /**
     * Checks if the queue contains a specific element.
     *
     * @param element The element to check.
     * @return True if the element is in the queue, false otherwise.
     */
    public boolean contains(T element) {
        return this.priorityMap.containsKey(element);
    }

    /**
     * Retrieves the size of the queue.
     *
     * @return The number of elements in the queue.
     */
    public int size() {
        return this.priorityQueue.size();
    }

    /**
     * Checks if the queue is empty.
     *
     * @return True if the queue is empty, false otherwise.
     */
    public boolean isEmpty() {
        return this.priorityQueue.isEmpty();
    }
}
