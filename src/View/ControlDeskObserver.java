package src.View;/* src.View.ControlDeskObserver.java
 *
 *  Version
 *  $Id$
 * 
 *  Revisions:
 * 		$Log$
 * 
 */

import src.Control.ControlDeskEvent;

/**
 * Interface for classes that observe control desk events
 *
 */

public interface ControlDeskObserver {

	public void receiveControlDeskEvent(ControlDeskEvent ce);

}
