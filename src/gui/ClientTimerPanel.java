package gui;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultFormatter;

public class ClientTimerPanel extends ClientPanel {

    // Variables declaration - do not modify                     
    private JSpinner hoursSpinner;
    private JLabel minutesLabel;
    private JSpinner minutesSpinner;
    private JLabel hoursLabel;

    public ClientTimerPanel(Duration totalTime, String clientName) {
        super(totalTime, clientName);

        hoursSpinner = new JSpinner();
        hoursLabel = new JLabel();
        minutesSpinner = new JSpinner();
        minutesLabel = new JLabel();

        hoursLabel.setText(TimerFrame.CLIENTTIMEHOURSLABEL_TEXT);
        minutesLabel.setText(TimerFrame.CLIENTTIMEMINUTESLABEL_TEXT);

        hoursSpinner.setModel(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
        DefaultFormatter formatterH = (DefaultFormatter) ((JFormattedTextField) hoursSpinner.getEditor().getComponent(0)).getFormatter();
        formatterH.setCommitsOnValidEdit(true);
        hoursSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int h = (Integer) hoursSpinner.getValue();
                if (h <= ClientTimerPanel.this.maxTime.getHours()) {
                    time.setHours(h);
                    maxTime.addHours(time.getHours()-h);
                } else {
                    hoursSpinner.setValue(new Integer(time.getHours()));
                }
            }
        });

        minutesSpinner.setModel(new SpinnerNumberModel(0, 0, 60, 1));
        DefaultFormatter formatterM = (DefaultFormatter) ((JFormattedTextField) minutesSpinner.getEditor().getComponent(0)).getFormatter();
        formatterM.setCommitsOnValidEdit(true);
        minutesSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int m = (Integer) minutesSpinner.getValue();
                int h = (Integer) hoursSpinner.getValue();
                if (h < ClientTimerPanel.this.maxTime.getHours() || m <= ClientTimerPanel.this.maxTime.getMinutes()) {
                    time.setMinutes(m);
                    maxTime.addMinutes(time.getMinutes()-m);
                } else {
                    minutesSpinner.setValue(new Integer(time.getMinutes()));
                }
            }
        });
        timeArea.add(hoursSpinner);
        timeArea.add(hoursLabel);
        timeArea.add(minutesSpinner);
        timeArea.add(minutesLabel);

        setClientName(clientName, false);

        activeCheckBox.setVisible(false);
        timeLabel.setVisible(false);
    }

    public void updateTime() {
        int hours = time.getHours();
        int minutes = time.getMinutes();
        int seconds = time.getSeconds();
        hoursSpinner.setValue(new Integer(hours));
        minutesSpinner.setValue(new Integer(minutes));
        //secondsSpinner.setValue(new Integer(seconds));
        super.updateTime();
    }

}
