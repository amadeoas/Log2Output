package uk.co.bocaditos.log2xlsx.out.graph.model;

import java.time.LocalDateTime;

import uk.co.bocaditos.utils.model.WithID;


/**
 * .
 */
public interface TimeOrder extends WithID, Comparable<TimeOrder> {

	public LocalDateTime getDateTime();

	public default int compareTo(final TimeOrder datetime) {
		return getDateTime().compareTo(datetime.getDateTime());
	}

} // end class TimeOrder
