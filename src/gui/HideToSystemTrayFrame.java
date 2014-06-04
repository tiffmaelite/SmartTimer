package gui;

import java.awt.AWTException;
import java.awt.CheckboxMenuItem;
import java.awt.Dialog;
import java.awt.Dimension;
import static java.awt.Frame.ICONIFIED;
import static java.awt.Frame.MAXIMIZED_BOTH;
import static java.awt.Frame.NORMAL;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

/**
 *
 * @author Mohammad Faisal ermohammadfaisal.blogspot.com
 * facebook.com/m.faisal6621
 * @see http://stackoverflow.com/a/8909348
 * @see http://kodejava.org/how-do-i-create-undecorated-frame/
 * @see http://book.javanb.com/swing-hacks/swinghacks-chp-5-sect-9.html
 */
public class HideToSystemTrayFrame extends JFrame {

    protected TrayIcon trayIcon;
    protected SystemTray tray;
    protected JMenuBar menuBar;
    protected boolean minimized = false;
    protected Dimension normal_size = new Dimension();
    protected Point normal_location = new Point();
    protected ActionListener aboutActionListener;
    protected ActionListener topActionListener;
    protected ActionListener exitActionListener;
    protected PopupMenu popup;
    private CheckboxMenuItem alwaysOnTopPopupItem;
    private JCheckBoxMenuItem topItem;

    HideToSystemTrayFrame(String title, String imgPath) {
        super(title);
        setIconImage(Toolkit.getDefaultToolkit().getImage(imgPath));
        setUndecorated(true);
        setResizable(true);
        // The mouse listener and mouse motion listener we add here is to simply
        // make our frame dragable.
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                normal_location.x = e.getX();
                normal_location.y = e.getY();
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                Point p = getLocation();
                setLocation(p.x + e.getX() - normal_location.x,
                        p.y + e.getY() - normal_location.y);
            }
        });

        System.out.println("creating instance");

        aboutActionListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(HideToSystemTrayFrame.this, "Created by Tiffany Grenier in 2014", "About", JOptionPane.INFORMATION_MESSAGE, null);
            }
        };

        topActionListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                /*if (e.getSource() instanceof MenuItem) {
                 ((MenuItem) e.getSource()).setLabel(isAlwaysOnTop() ? "Not always on top" : "Always on top");
                 }*/
                setAlwaysOnTop(!isAlwaysOnTop());
            }

        };

        exitActionListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Exiting....");
                removeFromSysTray();
                System.exit(0);
            }
        };

        if (SystemTray.isSupported()) {
            System.out.println("system tray supported");
            tray = SystemTray.getSystemTray();
            initPopupMenu();
        } else {
            System.out.println("system tray not supported");
        }

        addWindowStateListener(new WindowStateListener() {
            public void windowStateChanged(WindowEvent e) {
                switch (e.getNewState()) {
                    case ICONIFIED:
                        if (isAlwaysOnTop()) {
                            maximize();
                        } else {
                            minimizeToTray();
                        }
                        break;
                    case 7:
                        minimizeToTray();
                        break;
                    case MAXIMIZED_BOTH:
                    case NORMAL:
                        maximizeFromTray();
                    default:
                }
            }
        });

        initMenuBar();

        if (isAlwaysOnTopSupported()) {
            alwaysOnTopPopupItem.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    topItem.setSelected(alwaysOnTopPopupItem.getState());
                }
            });

            topItem.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    alwaysOnTopPopupItem.setState(topItem.isSelected());
                }
            });
        }

        setVisible(true);
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    protected void minimizeToTray() {
        setVisible(false);
        addToSysTray();
    }

    protected void maximizeFromTray() {
        removeFromSysTray();
        setVisible(true);
    }

    protected void maximize() {
        setExtendedState(JFrame.NORMAL);//will not call maximizeFromTray
        maximizeFromTray();
        try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {
            Logger.getLogger(HideToSystemTrayFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        toFront();
    }

    protected void initPopupMenu() {
        popup = new PopupMenu();
        MenuItem defaultItem = new MenuItem("About");
        defaultItem.addActionListener(aboutActionListener);
        popup.add(defaultItem);

        defaultItem = new MenuItem("Exit");
        defaultItem.addActionListener(exitActionListener);
        popup.add(defaultItem);
        if (isAlwaysOnTopSupported()) {
            setAlwaysOnTop(true);
            alwaysOnTopPopupItem = new CheckboxMenuItem("Leave always on top");
            alwaysOnTopPopupItem.addActionListener(topActionListener);
            alwaysOnTopPopupItem.setState(isAlwaysOnTop());
            popup.add(alwaysOnTopPopupItem);
        }
        defaultItem = new MenuItem("Maximize");
        defaultItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                maximize();
            }
        });
        popup.add(defaultItem);
        trayIcon = new TrayIcon(getIconImage(), getTitle(), popup);
        trayIcon.setImageAutoSize(true);
        trayIcon.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.getClickCount() >= 2) {
                    maximize();
                }
            }
        });
    }

    protected void initMenuBar() {
        menuBar = new JMenuBar();
        JMenu options = new JMenu("Options");
        JMenuItem minimizeItem = new JMenuItem("Iconify to system tray");
        minimizeItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //processWindowStateEvent(new WindowEvent(HideToSystemTrayFrame.this,WindowEvent.WINDOW_ICONIFIED));
                minimizeToTray();
            }

        });

        JMenuItem miniItem = new JMenuItem("Minimize");
        miniItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (minimized) {
                    maximizedVersion();
                    ((JMenuItem) e.getSource()).setText("Minimize");
                } else {
                    minimizedVersion();
                    ((JMenuItem) e.getSource()).setText("Maximize");
                }
                minimized = !minimized;
            }
        });

        topItem = new JCheckBoxMenuItem("Leave always on top");
        topItem.addActionListener(topActionListener);
        topItem.setSelected(isAlwaysOnTop());

        JMenuItem aboutItem = new JMenuItem("About...");
        aboutItem.addActionListener(aboutActionListener);

        JMenuItem exitItem = new JMenuItem("Close");
        exitItem.addActionListener(exitActionListener);

        options.add(miniItem);
        options.add(minimizeItem);
        options.add(topItem);
        options.add(aboutItem);
        options.add(exitItem);

        menuBar.add(options);
        setJMenuBar(menuBar);
    }

    private void removeFromSysTray() {
        if (SystemTray.isSupported() && Arrays.asList(tray.getTrayIcons()).contains(trayIcon)) {
            tray.remove(trayIcon);
            System.out.println("Tray icon removed from System Tray");
        }
    }

    private void addToSysTray() {
        if (SystemTray.isSupported() && !Arrays.asList(tray.getTrayIcons()).contains(trayIcon)) {
            try {
                tray.add(trayIcon);
                System.out.println("Tray icon added to System Tray");
            } catch (AWTException ex) {
                System.out.println("Unable to add Tray icon to System Tray");
            }
        }
    }

    /**
     * @see http://book.javanb.com/swing-hacks/swinghacks-chp-5-sect-9.html
     */
    protected void minimizedVersion() {
        normal_location = getLocation();
        normal_size = getSize();
        hideAccessoryStuff(true);
        revalidate();
        pack();
    }

    protected void maximizedVersion() {
        hideAccessoryStuff(false);
        revalidate();
        pack();
        setSize(normal_size);
        setLocation(normal_location);
    }

    protected void hideAccessoryStuff(boolean hide) {
        //DO NOTHING
    }
}
