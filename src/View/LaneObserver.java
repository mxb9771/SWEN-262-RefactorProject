package src.View;/* $Id$
 *
 * Revisions:
 *   $Log: src.View.LaneObserver.java,v $
 *   Revision 1.2  2003/01/30 21:44:25  ???
 *   Fixed speling of received in may places.
 *
 *   Revision 1.1  2003/01/19 22:12:40  ???
 *   created laneevent and laneobserver
 *
 *
 */

import src.Model.LaneEvent;

public interface LaneObserver {
	public void receiveLaneEvent(LaneEvent le);
};

