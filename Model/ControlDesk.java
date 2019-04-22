package Model;/* Model.ControlDesk.java
 *
 *  Version:
 *  		$Id$
 * 
 *  Revisions:
 * 		$Log: Model.ControlDesk.java,v $
 * 		Revision 1.13  2003/02/02 23:26:32  ???
 * 		Model.ControlDesk now runs its own thread and polls for free lanes to assign queue members to
 * 		
 * 		Revision 1.12  2003/02/02 20:46:13  ???
 * 		Added " 's Model.Party" to party names.
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
 * 		Added Control.ControlDeskEvent and View.ControlDeskObserver. Updated Model.Queue to allow access to Vector so that contents could be viewed without destroying. Implemented observer model for most of Model.ControlDesk.
 * 		
 * 		Revision 1.6  2003/02/02 06:09:39  ???
 * 		Updated many classes to support the View.ControlDeskView.
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

import Control.BowlerFile;
import Control.ControlDeskEvent;

import java.util.*;
import java.io.*;

public class ControlDesk extends Thread {

	/** The collection of Lanes */
	private HashSet<Lane> lanes;

	/** The party wait queue */
	private Model.Queue partyQueue;

	/** The number of lanes represented */
	private int numLanes;
	
	/** The collection of subscribers */
	private Vector<View.ControlDeskView> subscribers;

    /**
     * Constructor for the Model.ControlDesk class
     *
     * @param numLanes	the numbler of lanes to be represented
     *
     */

	public ControlDesk ( int numLanes ) {

		this.numLanes = numLanes;
		lanes = new HashSet<>( numLanes );
		partyQueue = new Queue();

		subscribers = new Vector<View.ControlDeskView>();

		for (int i = 0; i < numLanes; i++) {
			lanes.add(new Lane());
		}
		
		this.start();

	}
	
	/**
	 * Main loop for Model.ControlDesk's thread
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
     * Retrieves a matching Model.Bowler from the bowler database.
     *
     * @param nickName	The NickName of the Model.Bowler
     *
     * @return a Model.Bowler object.
     *
     */

	private Bowler registerPatron(String nickName) {
		Bowler patron = null;

		try {
			// only one patron / nick.... no dupes, no checks

			patron = BowlerFile.getBowlerInfo(nickName);

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
		Iterator<Lane> it = lanes.iterator();

		while (it.hasNext() && partyQueue.hasMoreElements()) {
			Lane curLane = it.next();

			if (!curLane.isPartyAssigned()) {
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

	public void addPartyQueue(Vector<String> partyNicks) {
		Vector<Bowler> partyBowlers = new Vector<Bowler>();
		for (int i = 0; i < partyNicks.size(); i++) {
			Bowler newBowler = registerPatron((partyNicks.get(i)));
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

	public Vector<String> getPartyQueue() {
		Vector<String> displayPartyQueue = new Vector<>();
		for ( int i=0; i < ( (Vector)partyQueue.asVector()).size(); i++ ) {
			String nextParty =
				((Bowler) ((Vector) ((Party) partyQueue.asVector().get( i ) ).getMembers())
					.get(0))
					.getNickName() + "'s Model.Party";
			displayPartyQueue.addElement(nextParty);
		}
		return displayPartyQueue;
	}

    /**
     * Accessor for the number of lanes represented by the Model.ControlDesk
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
     * @param adding    the View.ControlDeskObserver that will be subscribed
     *
     */

	public void subscribe(View.ControlDeskView adding) {
		subscribers.add(adding);
	}

    /**
     * Broadcast an event to subscribing objects.
     * 
     * @param event	the Control.ControlDeskEvent to broadcast
     *
     */

	public void publish(ControlDeskEvent event) {
		Iterator<View.ControlDeskView> eventIterator = subscribers.iterator();
		while (eventIterator.hasNext()) {
			(
					eventIterator
                        .next())
					.receiveEvent(
				event);
		}
	}

    /**
     * Accessor method for lanes
     * 
     * @return a HashSet of Lanes
     *
     */

	public HashSet<Lane> getLanes() {
		return lanes;
	}
}