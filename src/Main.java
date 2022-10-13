import javax.swing.*;

public class Main {

    private static void initUI() {
        JFrame f = new JFrame("AG");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(new MyPanel());
        f.setSize(500,500);
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