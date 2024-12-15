package es.ucm.fdi.ici.c2425.practica5.grupo02.mspacman;

import es.ucm.fdi.gaia.jcolibri.exception.NoApplicableSimilarityFunctionException;
import es.ucm.fdi.gaia.jcolibri.method.retrieve.NNretrieval.similarity.LocalSimilarityFunction;

/**
 * This function returns the similarity of two index inside the maze based on
 * distance.
 */
public class Distance implements LocalSimilarityFunction {

	/**
	 * Applies the similarity function.
	 * 
	 * @param o1 Number
	 * @param o2 Number
	 * @return result of apply the similarity function.
	 */
	public double compute(Object o1, Object o2) throws NoApplicableSimilarityFunctionException {
		if ((o1 == null) || (o2 == null))
			return 0;
		if (!(o1 instanceof Number))
			throw new NoApplicableSimilarityFunctionException(this.getClass(), o1.getClass());
		if (!(o2 instanceof Number))
			throw new NoApplicableSimilarityFunctionException(this.getClass(), o2.getClass());

		Number i1 = (Number) o1;
		Number i2 = (Number) o2;

		int v1 = i1.intValue();
		int v2 = i2.intValue();

		int distance = getShortestDistance(v1, v2);

		if (distance == 0)
			return 1.0;
		if (distance >= 100)
			return 0.0;

		return (100 - distance) / 100.0;
	}

	/** Applicable to Integer */
	public boolean isApplicable(Object o1, Object o2) {
		if ((o1 == null) && (o2 == null))
			return true;
		else if (o1 == null)
			return o2 instanceof Number;
		else if (o2 == null)
			return o1 instanceof Number;
		else
			return (o1 instanceof Number) && (o2 instanceof Number);
	}

	private int getShortestDistance(int fromNodeIndex, int toNodeIndex) {
		return 0; // TODO el game no tiene formula para calcular la distancia entre dos nodos, lo tiene cacheado,
		// por lo que no se puede calcular la distancia entre dos nodos. No Usamos esta clase
	}
}