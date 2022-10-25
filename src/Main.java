import javax.swing.*;
import java.awt.*;

public class Main {

    private static void initUI() {
        JFrame f = new JFrame("AG");
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(new MyPanel());
        //f.setSize((int)size.getWidth() ,(int)size.getHeight());
        f.setExtendedState(JFrame.MAXIMIZED_BOTH);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                initUI();
            }
        });
    }
}