package Model.LaneState;

import Control.Events.ControlDeskEvent;
import Control.Events.LaneEvent;
import Control.Events.PinsetterEvent;
import Control.ScoreHistoryFile;
import Model.Bowler;
import Model.Party;
import Model.Pinsetter;
import View.Observer;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

public class LaneOccupied extends Thread implements Observer, LaneStatusState {

    private Party party;
    private Pinsetter setter;
    private HashMap scores;
    private Vector subscribers;

    private boolean gameIsHalted;

    private boolean partyAssigned;
    private boolean gameFinished;
    private Iterator bowlerIterator;
    private int ball;
    private int bowlIndex;
    private int frameNumber;
    private boolean tenthFrameStrike;

    private int[] curScores;
    private int[][] cumulScores;
    private boolean canThrowAgain;

    private int[][] finalScores;
    private int gameNumber;

    private Bowler currentThrower;			// = the thrower who just took a throw

    /** Model.Lane()
     *
     * Constructs a new lane and starts its thread
     *
     * @pre none
     * @post a new lane has been created and its thered is executing
     */
    public LaneOccupied() {
        setter = new Pinsetter();
        scores = new HashMap();
        subscribers = new Vector();

        gameIsHalted = false;
        partyAssigned = false;

        gameNumber = 0;

        setter.subscribe( this );

        this.start();
    }

    public void function() {
        run();
    }

    /** run()
     *
     * entry point for execution of this lane
     */
    public void run() {

        while (true) {
            if (partyAssigned) {	// we have a party on this lane,
                // so next bower can take a throw

                while (gameIsHalted) {
                    try {
                        sleep(10);
                    } catch (Exception e) {}
                }
                if (bowlerIterator.hasNext()) {
                    currentThrower = (Bowler)bowlerIterator.next();

                    canThrowAgain = true;
                    tenthFrameStrike = false;
                    ball = 0;
                    while (canThrowAgain) {
                        setter.ballThrown();		// simulate the thrower's ball hiting
                        ball++;
                    }

                    if (frameNumber == 9){
                        finalScores[bowlIndex][gameNumber] = cumulScores[bowlIndex][9];
                        try{
                            Date date = new Date();
                            String dateString = "" + date.getHours() + ":" + date.getMinutes() + " " + date.getMonth() + "/" + date.getDay() + "/" + (date.getYear() + 1900);
                            ScoreHistoryFile.addScore(currentThrower.getNick(), dateString, new Integer(cumulScores[bowlIndex][9]).toString());
                        } catch (Exception e) {System.err.println("Exception in addScore. "+ e );}
                    }


                    setter.reset();
                    bowlIndex++;

                } else {
                    frameNumber++;
                    resetBowlerIterator();
                    bowlIndex = 0;
                    if (frameNumber > 9) {

                        gameNumber++;
                    }
                }
            }

            try {
                sleep(10);
            } catch (Exception e) {}
        }
    }

    /** recievePinsetterEvent()
     *
     * recieves the thrown event from the pinsetter
     *
     * @pre none
     * @post the event has been acted upon if desiered
     *
     * @param pe 		The pinsetter event that has been received.
     */
    public void receiveEvent(PinsetterEvent pe) {

        if (pe.pinsDownOnThisThrow() >=  0) {			// this is a real throw
            markScore(currentThrower, frameNumber + 1, pe.getThrowNumber(), pe.pinsDownOnThisThrow());

            // next logic handles the ?: what conditions dont allow them another throw?
            // handle the case of 10th frame first
            if (frameNumber == 9) {
                if (pe.totalPinsDown() == 10) {
                    setter.resetPins();
                    if(pe.getThrowNumber() == 1) {
                        tenthFrameStrike = true;
                    }
                }

                if ((pe.totalPinsDown() != 10) && (pe.getThrowNumber() == 2 && tenthFrameStrike == false)) {
                    canThrowAgain = false;
                    //publish( lanePublish() );
                }

                if (pe.getThrowNumber() == 3) {
                    canThrowAgain = false;
                    //publish( lanePublish() );
                }
            } else { // its not the 10th frame

                if (pe.pinsDownOnThisThrow() == 10) {		// threw a strike
                    canThrowAgain = false;
                    //publish( lanePublish() );
                } else if (pe.getThrowNumber() == 2) {
                    canThrowAgain = false;
                    //publish( lanePublish() );
                } else if (pe.getThrowNumber() == 3)
                    System.out.println("I'm here...");
            }
        } else {								//  this is not a real throw, probably a reset
        }
    }

    @Override
    public void receiveEvent(ControlDeskEvent ce) {

    }

    @Override
    public void receiveEvent(LaneEvent le) {

    }

    /** resetBowlerIterator()
     *
     * sets the current bower iterator back to the first bowler
     *
     * @pre the party as been assigned
     * @post the iterator points to the first bowler in the party
     */
    private void resetBowlerIterator() {
        bowlerIterator = (party.getMembers()).iterator();
    }

    /** resetScores()
     *
     * resets the scoring mechanism, must be called before scoring starts
     *
     * @pre the party has been assigned
     * @post scoring system is initialized
     */
    private void resetScores() {
        Iterator bowlIt = (party.getMembers()).iterator();

        while ( bowlIt.hasNext() ) {
            int[] toPut = new int[25];
            for ( int i = 0; i != 25; i++){
                toPut[i] = -1;
            }
            scores.put( bowlIt.next(), toPut );
        }



        gameFinished = false;
        frameNumber = 0;
    }

    /** assignParty()
     *
     * assigns a party to this lane
     *
     * @pre none
     * @post the party has been assigned to the lane
     *
     * @param theParty		Model.Party to be assigned
     */
    public void assignParty( Party theParty ) {
        party = theParty;
        resetBowlerIterator();
        partyAssigned = true;

        curScores = new int[party.getMembers().size()];
        cumulScores = new int[party.getMembers().size()][10];
        finalScores = new int[party.getMembers().size()][128]; //Hardcoding a max of 128 games, bite me.
        gameNumber = 0;

        resetScores();
    }

    /** markScore()
     *
     * Method that marks a bowlers score on the board.
     *
     * @param Cur		The current bowler
     * @param frame	The frame that bowler is on
     * @param ball		The ball the bowler is on
     * @param score	The bowler's score
     */
    private void markScore( Bowler Cur, int frame, int ball, int score ){
        int[] curScore;
        int index =  ( (frame - 1) * 2 + ball);

        curScore = (int[]) scores.get(Cur);


        curScore[ index - 1] = score;
        scores.put(Cur, curScore);
        getScore( Cur, frame );
        publish( lanePublish() );
    }

    /** lanePublish()
     *
     * Method that creates and returns a newly created laneEvent
     *
     * @return		The new lane event
     */
    private LaneEvent lanePublish(  ) {
        LaneEvent laneEvent = new LaneEvent(party, bowlIndex, currentThrower, cumulScores, scores, frameNumber+1, curScores, ball, gameIsHalted);
        return laneEvent;
    }

    /** getScore()
     *
     * Method that calculates a bowlers score
     *
     * @param Cur		The bowler that is currently up
     * @param frame	The frame the current bowler is on
     *
     * @return			The bowlers total score
     */
    private int getScore( Bowler Cur, int frame) {
        int[] curScore;
        int strikeballs = 0;
        int totalScore = 0;
        curScore = (int[]) scores.get(Cur);
        for (int i = 0; i != 10; i++){
            cumulScores[bowlIndex][i] = 0;
        }
        int current = 2*(frame - 1)+ball-1;
        //Iterate through each ball until the current one.
        for (int i = 0; i != current+2; i++){
            //Spare:
            if( i%2 == 1 && curScore[i - 1] + curScore[i] == 10 && i < current - 1 && i < 19){
                //This ball was a the second of a spare.
                //Also, we're not on the current ball.
                //Add the next ball to the ith one in cumul.
                cumulScores[bowlIndex][(i/2)] += curScore[i+1] + curScore[i];
                if (i > 1) {
                    //cumulScores[bowlIndex][i/2] += cumulScores[bowlIndex][i/2 -1];
                }
            } else if( i < current && i%2 == 0 && curScore[i] == 10  && i < 18){
                strikeballs = 0;
                //This ball is the first ball, and was a strike.
                //If we can get 2 balls after it, good add them to cumul.
                if (curScore[i+2] != -1) {
                    strikeballs = 1;
                    if(curScore[i+3] != -1) {
                        //Still got em.
                        strikeballs = 2;
                    } else if(curScore[i+4] != -1) {
                        //Ok, got it.
                        strikeballs = 2;
                    }
                }
                if (strikeballs == 2){
                    //Add up the strike.
                    //Add the next two balls to the current cumulscore.
                    cumulScores[bowlIndex][i/2] += 10;
                    if(curScore[i+1] != -1) {
                        cumulScores[bowlIndex][i/2] += curScore[i+1] + cumulScores[bowlIndex][(i/2)-1];
                        if (curScore[i+2] != -1){
                            if( curScore[i+2] != -2){
                                cumulScores[bowlIndex][(i/2)] += curScore[i+2];
                            }
                        } else {
                            if( curScore[i+3] != -2){
                                cumulScores[bowlIndex][(i/2)] += curScore[i+3];
                            }
                        }
                    } else {
                        if ( i/2 > 0 ){
                            cumulScores[bowlIndex][i/2] += curScore[i+2] + cumulScores[bowlIndex][(i/2)-1];
                        } else {
                            cumulScores[bowlIndex][i/2] += curScore[i+2];
                        }
                        if (curScore[i+3] != -1){
                            if( curScore[i+3] != -2){
                                cumulScores[bowlIndex][(i/2)] += curScore[i+3];
                            }
                        } else {
                            cumulScores[bowlIndex][(i/2)] += curScore[i+4];
                        }
                    }
                } else {
                    break;
                }
            }else {
                //We're dealing with a normal throw, add it and be on our way.
                if( i%2 == 0 && i < 18){
                    if ( i/2 == 0 ) {
                        //First frame, first ball.  Set his cumul score to the first ball
                        if(curScore[i] != -2){
                            cumulScores[bowlIndex][i/2] += curScore[i];
                        }
                    } else if (i/2 != 9){
                        //add his last frame's cumul to this ball, make it this frame's cumul.
                        if(curScore[i] != -2){
                            cumulScores[bowlIndex][i/2] += cumulScores[bowlIndex][i/2 - 1] + curScore[i];
                        } else {
                            cumulScores[bowlIndex][i/2] += cumulScores[bowlIndex][i/2 - 1];
                        }
                    }
                } else if (i < 18){
                    if(curScore[i] != -1 && i > 2){
                        if(curScore[i] != -2){
                            cumulScores[bowlIndex][i/2] += curScore[i];
                        }
                    }
                }
                if (i/2 == 9){
                    if (i == 18){
                        cumulScores[bowlIndex][9] += cumulScores[bowlIndex][8];
                    }
                    if(curScore[i] != -2){
                        cumulScores[bowlIndex][9] += curScore[i];
                    }
                } else if (i/2 == 10) {
                    if(curScore[i] != -2){
                        cumulScores[bowlIndex][9] += curScore[i];
                    }
                }
            }
        }
        return totalScore;
    }

    /** isPartyAssigned()
     *
     * checks if a party is assigned to this lane
     *
     * @return true if party assigned, false otherwise
     */
    public boolean isPartyAssigned() {
        return partyAssigned;
    }

    /** isGameFinished
     *
     * @return true if the game is done, false otherwise
     */
    public boolean isGameFinished() {
        return gameFinished;
    }

    /** subscribe
     *
     * Method that will add a subscriber
     *
     * @param adding	Observer that is to be added
     */

    public void subscribe( Observer adding ) {
        subscribers.add( adding );
    }

    /** unsubscribe
     *
     * Method that unsubscribes an observer from this object
     *
     * @param removing	The observer to be removed
     */

    public void unsubscribe( Observer removing ) {
        subscribers.remove( removing );
    }

    /** publish
     *
     * Method that publishes an event to subscribers
     *
     * @param event	Event that is to be published
     */

    public void publish( LaneEvent event ) {
        if( subscribers.size() > 0 ) {
            Iterator eventIterator = subscribers.iterator();

            while ( eventIterator.hasNext() ) {
                ( (Observer) eventIterator.next()).receiveEvent( event );
            }
        }
    }

    /**
     * Accessor to get this Model.Lane's pinsetter
     *
     * @return		A reference to this lane's pinsetter
     */

    public Pinsetter getPinsetter() {
        return setter;
    }

    /**
     * Pause the execution of this game
     */
    public void pauseGame() {
        gameIsHalted = true;
        publish(lanePublish());
    }

    /**
     * Resume the execution of this game
     */
    public void unPauseGame() {
        gameIsHalted = false;
        publish(lanePublish());
    }
}
