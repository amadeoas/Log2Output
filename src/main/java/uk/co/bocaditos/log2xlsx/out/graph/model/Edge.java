package uk.co.bocaditos.log2xlsx.out.graph.model;

import java.time.LocalDateTime;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import uk.co.bocaditos.utils.model.BaseModel;
import uk.co.bocaditos.utils.model.WithID;


/**
 * Representation of a relationship between Vertices.
 * 
 * @param <T> base data.
 * @param <E> edge.
 */
public class Edge<T extends TimeOrder, V extends Vertice<T, V, E>, E extends Edge<T, V, E>> 
		extends BaseModel<E> implements WithID, TimeOrder {

	private String label;
	private V from;
	private V to;
	private T data;


	public Edge() {}

	protected Edge(final String label, final V from, final V to, final T data) {
		this.label = label;
		this.from = from;
		this.to = to;
		this.data = data;
	}

	@JsonIgnore
	@Override
	public final String getID() {
		return getLabel();
	}

	@JsonIgnore
	@Override
	public final LocalDateTime getDateTime() {
		return this.data.getDateTime();
	}

	public String getLabel() {
		return this.label;
	}

	public V getFrom() {
		return this.from;
	}

	public V getTo() {
		return this.to;
	}

	public T getData() {
		return this.data;
	}

	public void setLabel(final String label) {
		this.label = label;
	}

	public void setFrom(final V from) {
		this.from = from;
	}

	public void setTo(final V to) {
		this.to = to;
	}

	public void setData(final T data) {
		this.data = data;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.label);
	}

	@Override
	public boolean equalsIt(final E edge) {
		return Objects.equals(getID(), edge.getID())
				&& Objects.equals(this.from.getLabel(), edge.getFrom().getID())
				&& Objects.equals(this.to.getLabel(), edge.getTo().getID())
				&& Objects.equals(this.data, edge.getData());
	}

	@Override
	public StringBuilder toString(final StringBuilder buf) {
		append(buf, "label", this.label);
		if (this.from != null) {
			append(buf, "from", this.from.getID());
		}

		if (this.to != null) {
			append(buf, "to", this.to.getID());
		}
		append(buf, "data", this.data);

		return buf;
	}

} // end class Edge
