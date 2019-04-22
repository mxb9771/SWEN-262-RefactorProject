package Control;/* Control.BowlerFile.java
 *
 *  Version:
 *  		$Id$
 * 
 *  Revisions:
 * 		$Log: Control.BowlerFile.java,v $
 * 		Revision 1.5  2003/02/02 17:36:45  ???
 * 		Updated comments to match javadoc format.
 * 		
 * 		Revision 1.4  2003/02/02 16:29:52  ???
 * 		Added Control.ControlDeskEvent and View.Observer. Updated Model.Queue to allow access to Vector so that contents could be viewed without destroying. Implemented observer model for most of Control.ControlDesk.
 * 		
 * 
 */

/**
 * Class for interfacing with Model.Bowler database
 *
 */

import Model.Bowler;

import java.util.*;
import java.io.*;

public class BowlerFile {

	/** The location of the bowelr database */
	private static String BOWLER_DAT = "BOWLERS.DAT";

    /**
     * Retrieves bowler information from the database and returns a Model.Bowler objects with populated fields.
     *
     * @param nickName	the nickName of the bolwer to retrieve
     *
     * @return a Model.Bowler object
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
     * Stores a Model.Bowler in the database
     *
     * @param nickName	the NickName of the Model.Bowler
     * @param fullName	the FullName of the Model.Bowler
     * @param email	the E-mail Address of the Model.Bowler
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