package uk.co.bocaditos.log2xlsx.out.graph.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import uk.co.bocaditos.utils.Utils;
import uk.co.bocaditos.utils.model.BaseModel;
import uk.co.bocaditos.utils.model.WithID;


/**
 * Representation of an entity.
 * 
 * @param <T> the base data.
 * @param <V> the Vertice the type.
 * @param <E> the edge type.
 */
public abstract class Vertice<T extends TimeOrder, V extends Vertice<T, V, E>, E extends Edge<T, V, E>> 
		extends BaseModel<V> implements WithID, TimeOrder {

	private static final LocalDateTime DATETIME = LocalDateTime.of(1990, 1, 1, 0, 0);

	private String label;
	private List<E> edges;


	protected Vertice() {}

	protected Vertice(final String label) {
		this.label = label;
		this.edges = new ArrayList<>();
	}

	@JsonIgnore
	@Override
	public final String getID() {
		return getLabel();
	}

	@JsonIgnore
	public abstract E buildEdge(final String lable, final T data);

	public final E add(final String label, final T data) {
		if (label == null) {
			return null;
		}

		E edge = this.edges
			.stream()
			.filter(e -> label.equals(e.getID()))
			.findFirst()
			.orElse(null);

		if (edge == null) {
			edge = buildEdge(label, data);
			if (this.edges.add(edge)) {
				return edge;
			}
		}

		return edge;
	}

	@JsonIgnore
	@Override
	public final LocalDateTime getDateTime() {
		if (Utils.isEmpty(this.edges)) {
			return DATETIME;
		}

		return this.edges.get(0).getDateTime();
	}

	public final String getLabel() {
		return this.label;
	}

	public final List<E> getEdges() {
		return this.edges;
	}

	protected void setLabel(final String label) {
		this.label = label;
	}

	protected void setEdges(final List<E> edges) {
		this.edges = edges;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.label);
	}

	@Override
	public boolean equalsIt(final V vertice) {
		return Objects.equals(this.label, vertice.getID())
				&& Objects.equals(this.edges, vertice.getEdges());
	}

	@Override
	public StringBuilder toString(final StringBuilder buf) {
		append(buf, "label", this.label);
		appendIDs(buf, "edges", this.edges);

		return buf;
	}

} // end class Vertice
