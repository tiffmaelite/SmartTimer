package gui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 *
 * @author Tiffany
 */
public class AwayTimerDialog extends JDialog implements ActionListener {

    private TimerFrame mainFrame;
    private LinkedList<ClientTimerPanel> clients;

    AwayTimerDialog(TimerFrame frame, Duration totalAwayTime) {
        super((JFrame) frame);
        mainFrame = frame;
        setTitle("You have been away for " + totalAwayTime.toDecimalHourString()+ " hour(s).");
        
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        
        add(new JLabel("You have been away for " + totalAwayTime.getHours() + " hour(s) and " + totalAwayTime.getMinutes() + " minute(s). How did you spend this time?"));
        
        clients = new LinkedList<ClientTimerPanel>();
        Duration remainingAwayTime = new Duration();
        for (int i = 1; i < mainFrame.getNbClients(); i++) {
            ClientPanel clientPanel = mainFrame.getClient(i);
            ClientTimerPanel cp = new ClientTimerPanel(totalAwayTime, remainingAwayTime, clientPanel.getClientName());
            clients.add(cp);
            add(cp);
        }

        JLabel warningLabel = new JLabel("Remaining time will be considered as non-working time.\n");
        add(warningLabel);
        JButton button = new JButton("Close");
        button.addActionListener(this);
        add(button);
        
     pack();
     setVisible(true);
     setLocationRelativeTo(frame);
  }

  public void actionPerformed(ActionEvent e) {
      dispose();
  }

    @Override
    public void dispose() {
        for (int i = 1; i < mainFrame.getNbClients(); i++) {
            mainFrame.getClient(i).incrementTime(1000*(clients.get(i-1).getHours() * 3600 + clients.get(i-1).getMinutes() *60 + clients.get(i-1).getSeconds()));
        }
        super.dispose();
    }
}
