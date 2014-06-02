package gui;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedList;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultFormatter;

public class ClientPanel extends JPanel {

    // Variables declaration
    protected JTextField nameField;
    protected JLabel timeLabel;
    protected JRadioButton activeCheckBox;
    protected Duration time;
    protected Duration maxTime;
    protected JPanel timeArea;
    
    /**
     * Creates new form ClientTimerPanel
     */
    public ClientPanel(Duration totalTime, String clientName) {
        maxTime = totalTime;
        time = new Duration(clientName);
        nameField = new JTextField(clientName);
        activeCheckBox = new JRadioButton();
        timeArea = new JPanel();
        timeLabel = new JLabel(time.toString() + " (" + time.toDecimalHourString() + " hour(s))");
        
        timeArea.setLayout(new FlowLayout());
        timeArea.add(timeLabel);
        
        activeCheckBox.setSelected(true);
        activeCheckBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent itemEvent) {
                // the line below is the line that matters, that enables/disables the text field
                boolean en = (itemEvent.getStateChange() == ItemEvent.SELECTED);
                nameField.setEnabled(en);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(activeCheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(nameField, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(timeArea)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(activeCheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(nameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(timeArea))
        );
    }

    public boolean isActive() {
        return activeCheckBox.isSelected() && !nameField.getText().isEmpty();
    }

    public void incrementTime(long period) {
        time.addTotalMilliseconds(period);
        updateTime();
    }

    public void updateTime() {
        timeLabel.setText(time.toFullString());
        validate();
    }

    public int getHours() {
        return time.getHours();
    }

    public double getDecimalHours() {
        return time.getDecimalHours();
    }

    public int getMinutes() {
        return time.getMinutes();
    }

    public void setClientName(String name, boolean editable) {
        nameField.setText(name);
        nameField.setEditable(editable);
        if (!editable) {
            nameField.setBackground(new Color(0, 0, 0, 0));//Color.LIGHT_GRAY);//nameField.getParent().getBackground());
            nameField.setOpaque(false);
            nameField.setBorder(null);
        }
    }

    public String getClientName() {
        return nameField.getText();
    }

    public JRadioButton getActivationCheckBox() {
        return activeCheckBox;
    }

}