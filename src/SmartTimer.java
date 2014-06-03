
import gui.TimerFrame;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JTextField;
import javax.swing.Painter;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;

/**
 *
 * @author Tiffany
 */
public class SmartTimer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (!System.getProperty("os.name").contains("Windows")) {
            System.err.println("ERROR: Only implemented on Windows");
            System.exit(1);
        }

        try {

            /* If Nimbus (introduced in Java SE 6) is not available, stay with the default os look and feel; and the default java one if neither can be found.
             * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
             */
            try {
                String lookNfeel = UIManager.getSystemLookAndFeelClassName();
                for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        lookNfeel = info.getClassName();
                        break;
                    }
                }
                System.out.println("setting look and feel" + lookNfeel);
                UIManager.setLookAndFeel(lookNfeel);
            } catch (ClassNotFoundException ex) {
                java.util.logging.Logger.getLogger(TimerFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            } catch (InstantiationException ex) {
                java.util.logging.Logger.getLogger(TimerFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                java.util.logging.Logger.getLogger(TimerFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            } catch (javax.swing.UnsupportedLookAndFeelException ex) {
                java.util.logging.Logger.getLogger(TimerFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
        } catch (Exception e) {
            System.out.println("Unable to set LookAndFeel");
        }
        //UIManager.put("TextField.inactiveBackground", new ColorUIResource(new Color(255, 0, 0)));

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                TimerFrame app = new TimerFrame();
                app.setVisible(true);
                app.toFront();
            }
        });
    }

}
