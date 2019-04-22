package View; /**
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class EndGamePrompt implements ActionListener, View{

	private JFrame win;
	private JButton yesButton, noButton;
	private String partyName;
	private int result;

    public EndGamePrompt( String partyName ) {
	    this.partyName = partyName;
		result =0;

		initialize();
	}


	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(yesButton)) {		
			result=1;
		}
		if (e.getSource().equals(noButton)) {		
			result=2;
		}

	}

	public int getResult() {
		while ( result == 0 ) {
			try {
				Thread.sleep(10);
			} catch ( InterruptedException e ) {
				System.err.println( "Interrupted" );
			}
		}
		return result;	
	}
	
	public void distroy() {
		win.hide();
	}

    @Override
    public JPanel setupButtons() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 2));

        new Insets(4, 4, 4, 4);

        yesButton = new JButton("Yes");
        JPanel yesButtonPanel = new JPanel();
        yesButtonPanel.setLayout(new FlowLayout());
        yesButton.addActionListener(this);
        yesButtonPanel.add(yesButton);

        noButton = new JButton("No");
        JPanel noButtonPanel = new JPanel();
        noButtonPanel.setLayout(new FlowLayout());
        noButton.addActionListener(this);
        noButtonPanel.add(noButton);

        buttonPanel.add(yesButton);
        buttonPanel.add(noButton);
        return buttonPanel;
    }

    @Override
    public void initialize() {
        win = new JFrame("Another Game for " + partyName + "?" );
        win.getContentPane().setLayout(new BorderLayout());
        ((JPanel) win.getContentPane()).setOpaque(false);

        JPanel colPanel = new JPanel();
        colPanel.setLayout(new GridLayout( 2, 1 ));

        // Label Panel
        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new FlowLayout());

        JLabel message = new JLabel( "Party " + partyName
                + " has finished bowling.\nWould they like to bowl another game?" );

        labelPanel.add( message );

        // Button Panel
        JPanel buttonPanel = setupButtons();
        // Clean up main panel
        colPanel.add(labelPanel);
        colPanel.add(buttonPanel);

        win.getContentPane().add("Center", colPanel);

        win.pack();

        // Center Window on Screen
        Dimension screenSize = (Toolkit.getDefaultToolkit()).getScreenSize();
        win.setLocation(
                ((screenSize.width) / 2) - ((win.getSize().width) / 2),
                ((screenSize.height) / 2) - ((win.getSize().height) / 2));
        win.show();
    }
}

