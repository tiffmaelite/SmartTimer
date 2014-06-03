package gui;

//import com.ha.common.windows.StandByDetector;
//import com.ha.common.windows.StandByRequestListener;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.WindowConstants;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellReference;
//import win32.Win32IdleTime;

/**
 *
 * @author Tiffany
 */
public class TimerFrame extends HideToSystemTrayFrame {
    public enum State {
         UNKNOWN, ONLINE, IDLE, AWAY
     };

    // Variables declaration
    private int currentClientIndex;
    private LinkedList<ClientPanel> clientPanels;
    private ButtonGroup clients;
    private JPanel mainPanel;
    private JButton plusButton;
    private JButton exportButton;
    private JButton pauseButton;
    private JLabel totalNonWorkingTimeLabel;
    private JLabel totalNonWorkingTimeValueLabel;
    private JLabel totalTimeLabel;
    private JLabel totalTimeValueLabel;
    private Duration totalTime;
    private Duration totalNonWorkingTime;
    private Duration totalAwayTime;
    private Timer timer;
    private SequentialGroup mainPanelVerticalContent;
    private ParallelGroup mainPanelHorizontalContent;
    private boolean pause;
    private State state = State.UNKNOWN;
    private GregorianCalendar lastUpdateDate;
    ClientPanel activeClient;
    // End of variables declaration

    // Text strings and formats declaration
    private static final String PLUSBUTTON_TEXT = "+";
    private static final String PAUSEBUTTON_TEXT = "| |";
    private static final String PAUSEBUTTON_ALTTEXT = "Start";
    private static final String EXPORTBUTTON_TEXT = "Export...";
    private static final String EXPORTDIALOG_TEXT = "Export to...";
    private static final String TOTALTIMELABEL_TEXT = "Total time:";
    //private static final SimpleDateFormat TOTALTIMEVALUELABEL_DATEFORMAT = new SimpleDateFormat("HH 'h' mm");
    private static final String NONWORKINGTIMELABEL_TEXT = "Non-working time";
    private static final SimpleDateFormat SHEET_DATEFORMAT = new SimpleDateFormat("dd-MM-yy");
    private static final String SHEET_CLIENT = "Activity";
    private static final String SHEET_TIME = "Time";
    private static final String SHEET_HOURLYRATE = "Hourly rate";
    private static final String SHEET_COST = "Cost";
    public static final SimpleDateFormat CLIENTTIME_MAXIMIZED_DATEFORMAT = new SimpleDateFormat("HH:mm:ss");
    public static final SimpleDateFormat CLIENTTIME_MINIMIZED_DATEFORMAT = new SimpleDateFormat("HH:mm");
    public static final String CLIENTTIMEHOURSLABEL_TEXT = "hours";
    public static final String CLIENTTIMEMINUTESLABEL_TEXT = "minutes";
    public static final String CLIENTTIMESECONDSLABEL_TEXT = "seconds";
    public static final DecimalFormat TIME_DECIMALHOURS_DECIMALFORMAT = new DecimalFormat("0.000");

    public static SimpleDateFormat CLIENTTIME_DATEFORMAT = CLIENTTIME_MAXIMIZED_DATEFORMAT;
    // End of text strings and formats declaration

    /**
     * Creates new form NewJFrame
     */
    public TimerFrame() {
        super("SmartTimer", "images/wheel.png");

        initComponents();
        /*for (Component c : this.getComponents()) {
         if (c instanceof JTextField) {
         ((JTextField) c).setDisabledTextColor(Color.LIGHT_GRAY);
         ((JTextField) c).setBackground(c.isEnabled() ? Color.WHITE : Color.DARK_GRAY);
         }
         }*/
        activeClient = clientPanels.getFirst();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        pack();

        timer = new Timer();
        initTimer();
    }

    private void initComponents() {
        totalTime = new Duration();
        totalNonWorkingTime = new Duration();
        totalAwayTime = new Duration();

        mainPanel = new JPanel();
        plusButton = new JButton();
        exportButton = new JButton();
        pauseButton = new JButton();
        totalTimeLabel = new JLabel();
        totalTimeValueLabel = new JLabel();
        //totalNonWorkingTimeLabel = new JLabel();
        //totalNonWorkingTimeValueLabel = new JLabel();
        clientPanels = new LinkedList<ClientPanel>();
        clients = new ButtonGroup();

        pause = false;

        plusButton.setText(PLUSBUTTON_TEXT);
        plusButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                plusButtonActionPerformed(evt);
            }
        });

        exportButton.setText(EXPORTBUTTON_TEXT);
        exportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportButtonActionPerformed(evt);
            }
        });

        pauseButton.setText(PAUSEBUTTON_TEXT);
        pauseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pauseButtonActionPerformed(evt);
            }
        });

        totalTimeLabel.setText(TOTALTIMELABEL_TEXT);

        totalTimeValueLabel.setText(totalTime.toFullString());//TOTALTIMEVALUELABEL_DATEFORMAT.format(new GregorianCalendar(0, 0, 0, 0, 0, 0).getTime()));

        //totalNonWorkingTimeLabel.setText("Total non working time");
        //totalNonWorkingTimeValueLabel.setText("0 h 0");
        GroupLayout mainPanelLayout = new GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelHorizontalContent = mainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING);
        mainPanelLayout.setHorizontalGroup(
                mainPanelHorizontalContent
                .addGroup(mainPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(totalTimeLabel)
                        .addGap(6, 6, 6)
                        .addComponent(totalTimeValueLabel)
                        //.addGap(246, 246, 246)
                        //.addComponent(totalNonWorkingTimeLabel)
                        //.addGap(26, 26, 26)
                        //.addComponent(totalNonWorkingTimeValueLabel)
                        //.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                )
        );
        mainPanelVerticalContent = mainPanelLayout.createSequentialGroup();
        mainPanelLayout.setVerticalGroup(
                mainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(mainPanelVerticalContent
                        .addContainerGap()
                        .addGroup(mainPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(totalTimeLabel)
                                .addComponent(totalTimeValueLabel)
                        //.addComponent(totalNonWorkingTimeLabel)
                        //.addComponent(totalNonWorkingTimeValueLabel)
                        )
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                )
        );

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(mainPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(plusButton)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(exportButton)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(pauseButton)
                        .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(mainPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(plusButton)
                                .addComponent(exportButton)
                                .addComponent(pauseButton))
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        /*StandByDetector sd = new StandByDetector(new StandByRequestListener() {
         public void standByRequested() {
         System.out.println("standby requested");
         }

         public void fireWakeUpRequested() {
         throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
         }
         });
         sd.setAllowStandby(true);*/
        addClient(NONWORKINGTIMELABEL_TEXT, false);
        addClient("client 1", true);

        currentClientIndex = 0;
        clientPanels.get(0).getActivationCheckBox().setSelected(true);
    }

    public ClientPanel getClient(int i) {
        return clientPanels.get(i);
    }

    public int getNbClients() {
        return clientPanels.size();
    }

    private void addClient(String clientName, boolean editable) {
        ClientPanel newClient = new ClientPanel(totalTime, clientName);
        clientPanels.add(newClient);
        newClient.setClientName(clientName, editable);
        clients.add(newClient.getActivationCheckBox());

        mainPanelVerticalContent.addComponent(newClient, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap();
        mainPanelHorizontalContent.addComponent(newClient, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
        revalidate();
        pack();
    }

    private void initTimer() {
        lastUpdateDate = new GregorianCalendar();
        final int period = 1000;//1000 milliseconds = 1 second
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                GregorianCalendar now = new GregorianCalendar();
                long idleMilliSec = now.getTimeInMillis() - lastUpdateDate.getTimeInMillis();
                lastUpdateDate = now;
                /*int idleMilliSec = Math.min(period,Win32IdleTime.getIdleTimeMillisWin32());
                 System.out.println(idleMilliSec);
                 if (idleMilliSec <= 0 || pause) {
                 idleMilliSec = period;
                 }*/
                totalTime.addTotalMilliseconds(idleMilliSec);
                totalTimeValueLabel.setText(totalTime.toFullString());
                //int quantity = 0;
                State newState = pause
                        ? State.AWAY : idleMilliSec < 30 * 1000
                        ? State.ONLINE : idleMilliSec > 5 * 60 * 1000
                        ? State.AWAY : State.IDLE;
                if (newState != State.AWAY) {
                    for (int i = 0; i < clientPanels.size(); i++) {
                        ClientPanel c = clientPanels.get(i);
                        if (c.isActive()) {
                            c.incrementTime(idleMilliSec);
                        }
                    }
                }
                if (newState != state && newState == State.AWAY) {//new state is "away"
                    totalAwayTime = new Duration();
                }
                if (state == State.AWAY) {//old state was "away"
                    totalAwayTime.addTotalMilliseconds(idleMilliSec);
                    if (newState != state && totalAwayTime.getMinutes() > 0) {//current state is no longer "away" and have been for at least one minute
                        //open dialog and ask how to distribute time between activities and non-working time
                        AwayTimerDialog timeDistributer = new AwayTimerDialog(TimerFrame.this, totalAwayTime);
                        timeDistributer.addWindowListener(new WindowAdapter() {
                            @Override
                            public void windowOpened(WindowEvent e) {
                                for (int i = 0; i < clientPanels.size(); i++) {
                                    ClientPanel c = clientPanels.get(i);
                                    if (c.isActive()) {
                                        activeClient = c;
                                    }
                                }
                                clientPanels.getFirst().getActivationCheckBox().setSelected(true);
                                activeClient.getActivationCheckBox().setSelected(false);
                                super.windowOpened(e);
                            }

                            @Override
                            public void windowClosed(WindowEvent e) {
                                activeClient.getActivationCheckBox().setSelected(true);
                                super.windowClosed(e);
                            }
                        });
                        timeDistributer.setVisible(true);
                    }
                }

                state = newState;
                //System.out.println((new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss")).format(new Date()) + " # " + state + " for the last " + idleMilliSec + " milliseconds");

                //TODO: si pas en pause : nonworkingtime = totaltime - maxworkingtime
            }
        }, 0, period);
    }

    private void pauseButtonActionPerformed(ActionEvent evt) {
        pause = !pause;
        if (pause) {
            pauseButton.setText(PAUSEBUTTON_ALTTEXT);
        } else {
            pauseButton.setText(PAUSEBUTTON_TEXT);
        }
        exportButton.setEnabled(!pause);
    }

    private void plusButtonActionPerformed(java.awt.event.ActionEvent evt) {
        addClient("client " + clientPanels.size(), true);
        pack();
    }

    /**
     * @see http://viralpatel.net/blogs/java-read-write-excel-file-apache-poi/
     * @param evt
     */
    private void exportButtonActionPerformed(ActionEvent evt) {
        if(pause) { pause = false; }
        else {
        HSSFWorkbook workbook = new HSSFWorkbook();
        GregorianCalendar today = new GregorianCalendar();
        HSSFSheet sheet = workbook.createSheet(SHEET_DATEFORMAT.format(today.getTime()));
        int headerRow = 0;
        //Create a new row in current sheet
        Row hRate = sheet.createRow(headerRow++);
        int hourlyRate = 1;
        //Create a new cell in current row
        hRate.createCell(0).setCellValue(SHEET_HOURLYRATE);
        hRate.createCell(1).setCellValue(hourlyRate);

        headerRow++;//on saute une ligne
        
        Row header = sheet.createRow(headerRow++);
        header.createCell(0).setCellValue(SHEET_CLIENT);
        header.createCell(1).setCellValue(SHEET_TIME);
        header.createCell(2).setCellValue(SHEET_COST);
        
        for (int i = 0; i < clientPanels.size(); i++) {
            //Create a new row in current sheet
            Row row = sheet.createRow(i + headerRow);

            ClientPanel p = clientPanels.get(i);
            //Activity
            row.createCell(0).setCellValue(p.getClientName());
            //Hours, in decimal format
            Cell hoursCell = row.createCell(1);
            hoursCell.setCellValue(p.getDecimalHours());
            //Cost
            row.createCell(2).setCellFormula((new CellReference(hRate.getCell(1))).formatAsString() + "*" + (new CellReference(hoursCell)).formatAsString());
        }
        try {
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new java.io.File("."));
            chooser.setDialogTitle(EXPORTDIALOG_TEXT);
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            // disable the "All files" option.
            chooser.setAcceptAllFileFilterUsed(false);
            //    
            if (chooser.showOpenDialog(TimerFrame.this) == JFileChooser.APPROVE_OPTION) {
                //Note getSelectedFile() returns the selected folder, despite the name. getCurrentDirectory() returns the directory of the selected folder
                String path = chooser.getSelectedFile().getAbsolutePath() + "\\" + SHEET_DATEFORMAT.format(today.getTime()) + ".xls";
                FileOutputStream out = new FileOutputStream(new File(path));
                workbook.write(out);
                out.close();
                System.out.println("Excel file written successfully at " + path + ".");
            } else {
                System.out.println("No folder selection.");
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        }
    }

    protected void hideAccessoryStuff(boolean hide) {
        CLIENTTIME_DATEFORMAT = hide ? CLIENTTIME_MINIMIZED_DATEFORMAT : CLIENTTIME_MAXIMIZED_DATEFORMAT;
        pauseButton.setVisible(!hide);
        exportButton.setVisible(!hide);
        plusButton.setVisible(!hide);
        totalTimeLabel.setVisible(!hide || pause);
        totalTimeValueLabel.setVisible(!hide || pause);
        totalTimeValueLabel.setText(totalTime.toFullString());
        for (ClientPanel c : clientPanels) {
            boolean masquer = (pause && hide) || (!pause && hide && !c.isActive());
            c.setVisible(!masquer);
            c.updateTime();
        }
    }
}
