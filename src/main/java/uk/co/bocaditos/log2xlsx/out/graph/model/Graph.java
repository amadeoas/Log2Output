package uk.co.bocaditos.log2xlsx.out.graph.model;

import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import uk.co.bocaditos.utils.Utils;
import uk.co.bocaditos.utils.model.BaseModel;


/**
 * Representation of vertices and edges.
 * 
 * @param <T> .
 */
public abstract class Graph<T extends TimeOrder, V extends Vertice<T, V, E>, E extends Edge<T, V, E>> 
		extends BaseModel<Graph<T, V, E>> {

	private Set<V> vertices;


	public Graph() {
		this.vertices = new TreeSet<>();
	}

	protected abstract V buildVertice(String label);

	public final V get(final String label) {
		if (Utils.isEmpty(label)) {
			return null;
		}

		return this.vertices
			.stream()
			.filter(v -> label.equals(v.getID()))
			.findFirst()
			.orElse(null);
	}

	public final Set<V> getVertices() {
		return this.vertices;
	}

	public final V add(final String label) {
		V vertice = get(label);

		if (vertice == null) {
			if (Utils.isEmpty(label)) {
				return null;
			}
			vertice = buildVertice(label);
			this.vertices.add(vertice);
		}

		return vertice;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.vertices);
	}

	@Override
	public boolean equalsIt(final Graph<T, V, E> graph) {
		return Objects.equals(this.vertices, graph.vertices);
	}

	@Override
	public StringBuilder toString(final StringBuilder buf) {
		appendIDs(buf, "vertices", this.vertices);

		return buf;
	}

} // end class Graph
