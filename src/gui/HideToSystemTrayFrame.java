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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
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

    HideToSystemTrayFrame(String title, String imgPath) {
        super(title);
        
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

        ActionListener aboutActionListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(HideToSystemTrayFrame.this, "Created by Tiffany Grenier in 2014", "About", JOptionPane.INFORMATION_MESSAGE, null);
            }
        };

        ActionListener topActionListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                /*if (e.getSource() instanceof MenuItem) {
                 ((MenuItem) e.getSource()).setLabel(isAlwaysOnTop() ? "Not always on top" : "Always on top");
                 }*/
                setAlwaysOnTop(!isAlwaysOnTop());
            }

        };
        
        ActionListener exitActionListener = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Exiting....");
                    System.exit(0);
                }
            };

        if (SystemTray.isSupported()) {
            System.out.println("system tray supported");
            tray = SystemTray.getSystemTray();

            Image image = Toolkit.getDefaultToolkit().getImage(imgPath);

            PopupMenu popup = new PopupMenu();
            MenuItem defaultItem = new MenuItem("About");
            defaultItem.addActionListener(aboutActionListener);
            popup.add(defaultItem);

            defaultItem = new MenuItem("Exit");
            defaultItem.addActionListener(exitActionListener);
            popup.add(defaultItem);
            if (isAlwaysOnTopSupported()) {
                setAlwaysOnTop(true);
                defaultItem = new CheckboxMenuItem("Leave always on top");
                defaultItem.addActionListener(topActionListener);
                ((CheckboxMenuItem) defaultItem).setState(isAlwaysOnTop());
            }
            popup.add(defaultItem);
            defaultItem = new MenuItem("Maximize");
            defaultItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    setVisible(true);
                    setExtendedState(JFrame.NORMAL);
                }
            });
            popup.add(defaultItem);
            trayIcon = new TrayIcon(image, title, popup);
            trayIcon.setImageAutoSize(true);
            trayIcon.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    if (e.getClickCount() >= 2) {
                        setVisible(true);
                        setExtendedState(JFrame.NORMAL);
                    }
                }
            });
        } else {
            System.out.println("system tray not supported");
        }

        addWindowStateListener(new WindowStateListener() {
            public void windowStateChanged(WindowEvent e) {
                switch (e.getNewState()) {
                    case ICONIFIED:
                    case 7:
                        try {
                            tray.add(trayIcon);
                            setVisible(false);
                            System.out.println("added to SystemTray");
                        } catch (AWTException ex) {
                            System.out.println("unable to add to tray");
                        }
                        break;
                    case MAXIMIZED_BOTH:
                    case NORMAL:
                        tray.remove(trayIcon);
                        setVisible(true);
                        System.out.println("Tray icon removed");
                    default:
                }
            }
        });

        setIconImage(Toolkit.getDefaultToolkit().getImage(imgPath));

        menuBar = new JMenuBar();
        JMenu options = new JMenu("Options");
        JMenuItem minimizeItem = new JMenuItem("Iconify to system tray");
        minimizeItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //processWindowStateEvent(new WindowEvent(HideToSystemTrayFrame.this,WindowEvent.WINDOW_ICONIFIED));
                try {
                    tray.add(trayIcon);
                    setVisible(false);
                    System.out.println("added to SystemTray");
                } catch (AWTException ex) {
                    System.out.println("unable to add to tray");
                }
            }

        });
        
        JMenuItem miniItem = new JMenuItem("Minimize");
        miniItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(minimized) {
                    maximizedVersion();
                    ((JMenuItem)e.getSource()).setText("Minimize");
                } else {
                    minimizedVersion();
                    ((JMenuItem)e.getSource()).setText("Maximize");
                }
                minimized= !minimized;
            }
        });

        
        JCheckBoxMenuItem topItem = new JCheckBoxMenuItem("Leave always on top");
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

        setVisible(true);
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
    
    protected void hideAccessoryStuff(boolean hide)
    {
        //DO NOTHING
    }
}
