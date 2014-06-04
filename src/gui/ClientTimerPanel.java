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
    private Duration totalTime;

    public ClientTimerPanel(Duration maxTime, Duration totTime, String clientName) {
        super(maxTime, clientName);
        totalTime = totTime;
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
                checkTime();
            }
        });

        minutesSpinner.setModel(new SpinnerNumberModel(0, 0, 60, 1));
        DefaultFormatter formatterM = (DefaultFormatter) ((JFormattedTextField) minutesSpinner.getEditor().getComponent(0)).getFormatter();
        formatterM.setCommitsOnValidEdit(true);
        minutesSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                checkTime();
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

    private void checkTime() {
        System.out.println("\n"+time+" "+maxTime+" "+totalTime);
        int m = (Integer) minutesSpinner.getValue();
        int diffM = m - time.getMinutes();
        int h = (Integer) hoursSpinner.getValue();
        int diffH = h - time.getHours();
        if (diffM >= 60) {
            diffM = diffM % 60;
            diffH = diffH + ((int) diffM / 60);
        }
        if ((h < maxTime.getHours() && totalTime.getHours() + diffH < maxTime.getHours()) //hour would stay strictly inferior to maximum hour
                || ((h == maxTime.getHours() || totalTime.getHours() + diffH == maxTime.getHours())
                && (m <= maxTime.getMinutes() && totalTime.getMinutes() + diffM <= maxTime.getMinutes()))) {//hour would be equal to maximum hour and minute would stay inferior or equal to maximum minute
            time.setMinutes(m);
            totalTime.addMinutes(diffM);
            totalTime.addHours(diffH);
        } else {//new time is invalid
            hoursSpinner.setValue(time.getHours());
            minutesSpinner.setValue(time.getMinutes());
        }
        System.out.println(time+" "+maxTime+" "+totalTime);
    }
}