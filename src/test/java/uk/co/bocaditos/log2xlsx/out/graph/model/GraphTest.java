package uk.co.bocaditos.log2xlsx.out.graph.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.Test;


/**
 * JUnit tests for class Graph.
 */
public class GraphTest {

	@Test
	public void test() {
		final GraphImp graph = new GraphImp();
		final DataImp data = new DataImp();
		final LocalDateTime datetime;
		final LocalDateTime datetime1;
		VerticeImp vertice;
		EdgeImp edge;

		assertEquals(0, graph.getVertices().size());
		assertNull(graph.add(null));
		vertice = graph.add("label");
		assertEquals(1, graph.getVertices().size());
		assertEquals(vertice, graph.add("label"));
		vertice.hashCode();
		assertNull(vertice.add(null, data));
		assertEquals(vertice, vertice);
		assertEquals(1, graph.getVertices().size());
		datetime = vertice.getDateTime();
		assertEquals(0, vertice.getEdges().size());
		assertEquals("label", vertice.getID());
		edge = vertice.add("first", data);
		edge.hashCode();
		datetime1 = vertice.getDateTime();
		assertEquals(edge, edge);
		assertEquals(vertice, edge.getFrom());
		assertEquals(vertice, edge.getTo());
		assertEquals("first", edge.getID());
		assertEquals(datetime1, edge.getDateTime());
		assertEquals(data, edge.getData());
		assertEquals(1, vertice.getEdges().size());
		assertNotEquals(datetime, vertice.getDateTime());
		assertEquals(edge, vertice.add("first", data));
		assertEquals(1, vertice.getEdges().size());
		assertNotNull(data.getID());
		assertEquals(graph, graph);
		graph.hashCode();

		assertEquals("{vertices: [\"label\"]}", graph.toString());
		assertEquals("{label: \"label\", edges: [\"first\"]}", vertice.toString());
		assertEquals("{label: \"first\", from: \"label\", to: \"label\", data: \"" + data.getID() + "\"}", 
				edge.toString());

		vertice.setLabel("");
		assertEquals("", vertice.getLabel());
		vertice.setEdges(null);
		assertNull(vertice.getEdges());

		edge.setData(null);
		assertNull(edge.getData());
		edge.setFrom(null);
		assertNull(edge.getFrom());
		edge.setTo(null);
		assertNull(edge.getTo());
		assertEquals("{label: \"first\"}", edge.toString());
		edge.setLabel(null);
		assertNull(edge.getLabel());

		new VerticeImp();
		new Edge<>();
	}

} // end class GraphTest


/**
 * .
 */
class DataImp implements TimeOrder {

	private LocalDateTime datetime = LocalDateTime.now();


	@Override
	public LocalDateTime getDateTime() {
		return this.datetime;
	}


	@Override
	public String getID() {
		return DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss.SSS").format(this.datetime);
	}
	
} // end class DataImp


/**
 * .
 */
class GraphImp extends Graph<DataImp, VerticeImp, EdgeImp> {

	@Override
	protected VerticeImp buildVertice(final String label) {
		return new VerticeImp(label);
	}

} // end class GraphImp


/**
 * .
 */
class VerticeImp extends Vertice<DataImp, VerticeImp, EdgeImp> {

	public VerticeImp() {	
	}

	public VerticeImp(final String label) {
		super(label);
	}

	@Override
	public EdgeImp buildEdge(final String label, final DataImp data) {
		return new EdgeImp(label, this, this, data);
	}
	
} // end class VerticeImp


/**
 * .
 */
class EdgeImp extends Edge<DataImp, VerticeImp, EdgeImp> {

	public EdgeImp(final String label, final VerticeImp from, final VerticeImp to, 
			final DataImp data) {
		super(label, from, to, data);
	}

} // end class EdgeImp
