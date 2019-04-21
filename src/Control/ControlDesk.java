package Control;/* src.Control.ControlDesk.java
 *
 *  Version:
 *  		$Id$
 * 
 *  Revisions:
 * 		$Log: src.Control.ControlDesk.java,v $
 * 		Revision 1.13  2003/02/02 23:26:32  ???
 * 		src.Control.ControlDesk now runs its own thread and polls for free lanes to assign queue members to
 * 		
 * 		Revision 1.12  2003/02/02 20:46:13  ???
 * 		Added " 's src.Model.Party" to party names.
 * 		
 * 		Revision 1.11  2003/02/02 20:43:25  ???
 * 		misc cleanup
 * 		
 * 		Revision 1.10  2003/02/02 17:49:10  ???
 * 		Fixed problem in getPartyQueue that was returning the first element as every element.
 * 		
 * 		Revision 1.9  2003/02/02 17:39:48  ???
 * 		Added accessor for lanes.
 * 		
 * 		Revision 1.8  2003/02/02 16:53:59  ???
 * 		Updated comments to match javadoc format.
 * 		
 * 		Revision 1.7  2003/02/02 16:29:52  ???
 * 		Added src.Control.ControlDeskEvent and src.View.ControlDeskObserver. Updated src.Model.Queue to allow access to Vector so that contents could be viewed without destroying. Implemented observer model for most of src.Control.ControlDesk.
 * 		
 * 		Revision 1.6  2003/02/02 06:09:39  ???
 * 		Updated many classes to support the src.View.ControlDeskView.
 * 		
 * 		Revision 1.5  2003/01/26 23:16:10  ???
 * 		Improved thread handeling in lane/controldesk
 * 		
 * 
 */

/**
 * Class that represents control desk
 *
 */

import Model.Bowler;
import Model.Lane;
import Model.Party;
import Model.Queue;
import View.ControlDeskObserver;

import java.util.*;
import java.io.*;

public class ControlDesk extends Thread {

	/** The collection of Lanes */
	private HashSet lanes;

	/** The party wait queue */
	private Model.Queue partyQueue;

	/** The number of lanes represented */
	private int numLanes;
	
	/** The collection of subscribers */
	private Vector subscribers;

    /**
     * Constructor for the src.Control.ControlDesk class
     *
     * @param numLanes	the numbler of lanes to be represented
     *
     */

	public ControlDesk(int numLanes) {
		this.numLanes = numLanes;
		lanes = new HashSet(numLanes);
		partyQueue = new Queue();

		subscribers = new Vector();

		for (int i = 0; i < numLanes; i++) {
			lanes.add(new Lane());
		}
		
		this.start();

	}
	
	/**
	 * Main loop for src.Control.ControlDesk's thread
	 * 
	 */
	public void run() {
		while (true) {
			
			assignLane();
			
			try {
				sleep(250);
			} catch (Exception e) {}
		}
	}
		

    /**
     * Retrieves a matching src.Model.Bowler from the bowler database.
     *
     * @param nickName	The NickName of the src.Model.Bowler
     *
     * @return a src.Model.Bowler object.
     *
     */

	private Bowler registerPatron(String nickName) {
		Bowler patron = null;

		try {
			// only one patron / nick.... no dupes, no checks

			patron = BowlerFile.getBowlerInfo(nickName);

		} catch (FileNotFoundException e) {
			System.err.println("Error..." + e);
		} catch (IOException e) {
			System.err.println("Error..." + e);
		}

		return patron;
	}

    /**
     * Iterate through the available lanes and assign the paties in the wait queue if lanes are available.
     *
     */

	public void assignLane() {
		Iterator it = lanes.iterator();

		while (it.hasNext() && partyQueue.hasMoreElements()) {
			Lane curLane = (Lane) it.next();

			if (curLane.isPartyAssigned() == false) {
				System.out.println("ok... assigning this party");
				curLane.assignParty(((Party) partyQueue.next()));
			}
		}
		publish(new ControlDeskEvent(getPartyQueue()));
	}

    /**
     */

	public void viewScores(Lane ln) {
		// TODO: attach a LaneScoreView object to that lane
	}

    /**
     * Creates a party from a Vector of nickNAmes and adds them to the wait queue.
     *
     * @param partyNicks	A Vector of NickNames
     *
     */

	public void addPartyQueue(Vector partyNicks) {
		Vector partyBowlers = new Vector();
		for (int i = 0; i < partyNicks.size(); i++) {
			Bowler newBowler = registerPatron(((String) partyNicks.get(i)));
			partyBowlers.add(newBowler);
		}
		Party newParty = new Party(partyBowlers);
		partyQueue.add(newParty);
		publish(new ControlDeskEvent(getPartyQueue()));
	}

    /**
     * Returns a Vector of party names to be displayed in the GUI representation of the wait queue.
	 *
     * @return a Vecotr of Strings
     *
     */

	public Vector getPartyQueue() {
		Vector displayPartyQueue = new Vector();
		for ( int i=0; i < ( (Vector)partyQueue.asVector()).size(); i++ ) {
			String nextParty =
				((Bowler) ((Vector) ((Party) partyQueue.asVector().get( i ) ).getMembers())
					.get(0))
					.getNickName() + "'s src.Model.Party";
			displayPartyQueue.addElement(nextParty);
		}
		return displayPartyQueue;
	}

    /**
     * Accessor for the number of lanes represented by the src.Control.ControlDesk
     * 
     * @return an int containing the number of lanes represented
     *
     */

	public int getNumLanes() {
		return numLanes;
	}

    /**
     * Allows objects to subscribe as observers
     *
     * @param adding    the src.View.ControlDeskObserver that will be subscribed
     *
     */

	public void subscribe(View.ControlDeskView adding) {
		subscribers.add(adding);
	}

    /**
     * Broadcast an event to subscribing objects.
     * 
     * @param event	the src.Control.ControlDeskEvent to broadcast
     *
     */

	public void publish(ControlDeskEvent event) {
		Iterator eventIterator = subscribers.iterator();
		while (eventIterator.hasNext()) {
			(
				(ControlDeskObserver) eventIterator
					.next())
					.receiveControlDeskEvent(
				event);
		}
	}

    /**
     * Accessor method for lanes
     * 
     * @return a HashSet of Lanes
     *
     */

	public HashSet getLanes() {
		return lanes;
	}
}
