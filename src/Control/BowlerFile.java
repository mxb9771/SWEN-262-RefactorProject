package Control;/* src.Control.BowlerFile.java
 *
 *  Version:
 *  		$Id$
 * 
 *  Revisions:
 * 		$Log: src.Control.BowlerFile.java,v $
 * 		Revision 1.5  2003/02/02 17:36:45  ???
 * 		Updated comments to match javadoc format.
 * 		
 * 		Revision 1.4  2003/02/02 16:29:52  ???
 * 		Added src.Control.ControlDeskEvent and src.View.ControlDeskObserver. Updated src.Model.Queue to allow access to Vector so that contents could be viewed without destroying. Implemented observer model for most of src.Control.ControlDesk.
 * 		
 * 
 */

/**
 * Class for interfacing with src.Model.Bowler database
 *
 */

import Model.Bowler;

import java.util.*;
import java.io.*;

public class BowlerFile {

	/** The location of the bowelr database */
	private static String BOWLER_DAT = "BOWLERS.DAT";

    /**
     * Retrieves bowler information from the database and returns a src.Model.Bowler objects with populated fields.
     *
     * @param nickName	the nickName of the bolwer to retrieve
     *
     * @return a src.Model.Bowler object
     * 
     */

	public static Bowler getBowlerInfo(String nickName)
		throws IOException, FileNotFoundException {

		BufferedReader in = new BufferedReader(new FileReader(BOWLER_DAT));
		String data;
		while ((data = in.readLine()) != null) {
			// File format is nick\tfname\te-mail
			String[] bowler = data.split("\t");
			if (nickName.equals(bowler[0])) {
				System.out.println(
					"Nick: "
						+ bowler[0]
						+ " Full: "
						+ bowler[1]
						+ " email: "
						+ bowler[2]);
				return (new Bowler(bowler[0], bowler[1], bowler[2]));
			}
		}
		System.out.println("Nick not found...");
		return null;
	}

    /**
     * Stores a src.Model.Bowler in the database
     *
     * @param nickName	the NickName of the src.Model.Bowler
     * @param fullName	the FullName of the src.Model.Bowler
     * @param email	the E-mail Address of the src.Model.Bowler
     *
     */

	public static void putBowlerInfo(
		String nickName,
		String fullName,
		String email)
		throws IOException, FileNotFoundException {

		String data = nickName + "\t" + fullName + "\t" + email + "\n";

		RandomAccessFile out = new RandomAccessFile(BOWLER_DAT, "rw");
		out.skipBytes((int) out.length());
		out.writeBytes(data);
		out.close();
	}

    /**
     * Retrieves a list of nicknames in the bowler database
     *
     * @return a Vector of Strings
     * 
     */

	public static Vector getBowlers()
		throws IOException, FileNotFoundException {

		Vector allBowlers = new Vector();

		BufferedReader in = new BufferedReader(new FileReader(BOWLER_DAT));
		String data;
		while ((data = in.readLine()) != null) {
			// File format is nick\tfname\te-mail
			String[] bowler = data.split("\t");
			//"Nick: bowler[0] Full: bowler[1] email: bowler[2]
			allBowlers.add(bowler[0]);
		}
		return allBowlers;
	}

}