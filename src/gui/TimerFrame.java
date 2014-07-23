package gui;

//import com.ha.common.windows.StandByDetector;
//import com.ha.common.windows.StandByRequestListener;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalTheme;
import javax.swing.plaf.metal.OceanTheme;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellReference;
import win32.Win32IdleTime;
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
    private LinkedList<ClientPanel> clientPanels;
    private ButtonGroup clients;
    private JPanel mainPanel;
    private JButton plusButton;
    private JButton exportButton;
    private JButton pauseButton;
    private JLabel totalTimeLabel;
    private JLabel totalTimeValueLabel;
    private Duration totalTime;
    private Duration totalAwayTime;
    private Timer timer;
    private SequentialGroup mainPanelVerticalContent;
    private ParallelGroup mainPanelHorizontalContent;
    private boolean pause;
    private boolean alwaysShowFullFormat;
    private State state = State.UNKNOWN;
    private GregorianCalendar lastUpdateDate;
    protected ClientPanel activeClient;
    private HashMap<String, Color> colors;
    private HashMap<String,MetalTheme> themes;
    private int awayDelay = 10 * 60 * 1000;
    private int idleDelay = 2 * 60 * 1000;
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
        super("SmartTimer", "src/images/wheel.png");

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
        alwaysShowFullFormat = false;

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
        final int period = 500;//500 milliseconds = 0.5 second
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                GregorianCalendar now = new GregorianCalendar();
                long delaySinceLastUpdate = now.getTimeInMillis() - lastUpdateDate.getTimeInMillis();
                totalTime.addTotalMilliseconds(delaySinceLastUpdate);
                totalTimeValueLabel.setText(totalTime.toFullString());
                //int quantity = 0;
                int idleMillis = Win32IdleTime.getIdleTimeMillisWin32();
                int delay = (int) Math.max(idleMillis, delaySinceLastUpdate);
                State newState = (pause || delay >= awayDelay)
                        ? State.AWAY : delay >= idleDelay
                        ? State.IDLE : State.ONLINE;
                if (state == State.AWAY) {//old state was "away"
                    if (pause || idleMillis <= delaySinceLastUpdate) {//delay == delaySinceLastUpdate
                        totalAwayTime.addTotalMilliseconds(delaySinceLastUpdate);
                    } else {// if (!pause && delay == idleMillis && delay != delaySinceLastUpdate)
                        totalAwayTime.setTotalMilliseconds(idleMillis);
                    }
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
                                super.windowOpened(e);
                            }

                            @Override
                            public void windowClosed(WindowEvent e) {
                                activeClient.getActivationCheckBox().setSelected(true);
                                super.windowClosed(e);
                                totalAwayTime = new Duration();//reset duration
                            }
                        });
                        timeDistributer.setVisible(true);
                    }
                }
                if (newState != State.AWAY) {
                    long maxWorkingMillis = 0;
                    for (int i = 1; i < clientPanels.size(); i++) {
                        ClientPanel c = clientPanels.get(i);
                        if (c.isActive()) {
                            c.incrementTime(delaySinceLastUpdate);
                        }
                        maxWorkingMillis += c.getDuration().getTotalMilliseconds();
                    }
                    clientPanels.getFirst().getDuration().setTotalMilliseconds(totalTime.getTotalMilliseconds()-maxWorkingMillis);
                    clientPanels.getFirst().updateTime();
                }

                state = newState;
                lastUpdateDate = now;
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
        if (pause) {
            pause = false;
        } else {
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
        CLIENTTIME_DATEFORMAT = (hide && !alwaysShowFullFormat) ? CLIENTTIME_MINIMIZED_DATEFORMAT : CLIENTTIME_MAXIMIZED_DATEFORMAT;
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

    @Override
    protected void initMenuBar() {
        super.initMenuBar();
        JCheckBoxMenuItem alwaysFullFormatItem = new JCheckBoxMenuItem("Always show full time");
        alwaysFullFormatItem.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                alwaysShowFullFormat = ((JCheckBoxMenuItem) e.getSource()).isSelected();
                if (minimized) {
                    minimizedVersion();
                    revalidate();
                    pack();
                }
            }
        });
        menuBar.getMenu(1).add(alwaysFullFormatItem);
        JMenu awayTimeMenu = new JMenu("Idle duration");
        ButtonGroup timeItems = new ButtonGroup();
        ItemListener timeItemListener = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                try {
                    awayDelay = Integer.parseInt(((JMenuItem) e.getItem()).getText().replaceAll("[^0-9]", ""));
                } catch (NumberFormatException nfex) {
                    Logger.getLogger(TimerFrame.class.getName()).log(Level.SEVERE, null, nfex);
                }
                if(idleDelay >= awayDelay) {
                    idleDelay = awayDelay - 1;
                }
            }
        };
        for(int i = 1; i <= 6; i++) {
            JRadioButtonMenuItem timeItem = new JRadioButtonMenuItem((5 * i) + " minutes");
            timeItem.addItemListener(timeItemListener);
            timeItems.add(timeItem);
            awayTimeMenu.add(timeItem);
        }
        awayTimeMenu.getItem(1).setSelected(true);
        menuBar.getMenu(1).add(awayTimeMenu);
        JMenu colorMenu = new JMenu("Color");
        if ("Nimbus".equals(UIManager.getLookAndFeel().getName())) {
            ButtonGroup colorItems = new ButtonGroup();
            colors = new HashMap<String, Color>();
            colors.put("Default", UIManager.getLookAndFeelDefaults().getColor("control"));
            colors.put("Gray", Color.GRAY);
            colors.put("Blue", Color.BLUE);
            colors.put("Red", Color.RED);
            colors.put("Green", Color.GREEN);
            colors.put("Yellow", Color.YELLOW);
            colors.put("Cyan", Color.CYAN);
            colors.put("Magenta", Color.MAGENTA);
            colors.put("Orange", Color.ORANGE);
            colors.put("Pink", Color.PINK);
            colors.put("Light gray", Color.LIGHT_GRAY);
            colors.put("Dark gray", Color.DARK_GRAY);
            ItemListener colorListener = new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    // Set the property nimbusBase (base color) as the chosen one
                    Color c = colors.get(((JRadioButtonMenuItem) e.getSource()).getText());
                    String[] colorProperties = {"control", "scrollbar", "info", "desktop","menu","window","PopupMenu.background","Menu.background","MenuBar.background","MenuItem.background"};
                    for (String propertyName : colorProperties) {
                        UIManager.put(propertyName, computeMeanColor(c, UIManager.getLookAndFeelDefaults().getColor(propertyName)));
                    }
                    SwingUtilities.updateComponentTreeUI(TimerFrame.this);
                }
            };
            for (String c : colors.keySet()) {
                JRadioButtonMenuItem colorItem = new JRadioButtonMenuItem(c);
                colorItem.addItemListener(colorListener);
                colorItems.add(colorItem);
                colorMenu.add(colorItem);
                if (c.contains("Default")) {
                    colorItem.setSelected(true);
                }
            }
        } else if ("Metal".equals(UIManager.getLookAndFeel().getName())) {
            ButtonGroup colorItems = new ButtonGroup();
            themes = new HashMap<String, MetalTheme>();
            String metal = "Metal";
            String ocean = "Ocean";
            if(MetalLookAndFeel.getCurrentTheme() instanceof OceanTheme) {
                ocean += " (Default)";
            } else {
                metal += " (Default)";
            }
            themes.put(metal, new DefaultMetalTheme());
            themes.put(ocean, new OceanTheme());
            ItemListener colorListener = new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    try {
                    MetalLookAndFeel.setCurrentTheme(themes.get(((JRadioButtonMenuItem) e.getSource()).getText()));
                        UIManager.setLookAndFeel(new MetalLookAndFeel());
                        // Update the ComponentUIs for all Components. This needs to be invoked for all windows.
                        SwingUtilities.updateComponentTreeUI(TimerFrame.this);
                    } catch (UnsupportedLookAndFeelException ex) {
                        Logger.getLogger(TimerFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            };
            for (String c : themes.keySet()) {
                JRadioButtonMenuItem colorItem = new JRadioButtonMenuItem(c);
                colorItem.addItemListener(colorListener);
                colorItems.add(colorItem);
                colorMenu.add(colorItem);
                if (c.contains("Default")) {
                    colorItem.setSelected(true);
                }
            }
        }
        menuBar.getMenu(1).add(colorMenu);
    }

    private static Color computeMeanColor(Color c1, Color c2) {
        if (c1 == null) {
            return c2;
        }
        if (c2 == null) {
            return c1;
        }
        int r = Math.min(255, (int) (c1.getRed() + c2.getRed()) / 2);
        int g = Math.min(255, (int) (c1.getGreen() + c2.getGreen()) / 2);
        int b = Math.min(255, (int) (c1.getBlue() + c2.getBlue()) / 2);
        return new Color(r, g, b);
    }
}
