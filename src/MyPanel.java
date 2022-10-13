import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

public class MyPanel extends JPanel implements ActionListener {

    private int nodeNr = 1;
    private final int node_diam = 30;
    private Vector<Node> listaNoduri;
    private Vector<Arc> listaArce;
    JButton schimbareModGraf, editButton, clearButton;
    JLabel nodSelectat;
    Point pointStart = null;
    Point pointEnd = null;
    Node nodeStart = null;
    Node nodeEnd = null;
    Node helpNode = null;
    boolean isDragging = false;
    boolean editMode = false;
    static boolean grafOrientat = false;

    public MyPanel() {

        listaNoduri = new Vector<Node>();
        listaArce = new Vector<Arc>();
        schimbareModGraf = new JButton("Graf neorientat");
        schimbareModGraf.addActionListener(this);

        editButton = new JButton("Edit");
        editButton.addActionListener(this);

        nodSelectat = new JLabel("Nod selectat: null");
        nodSelectat.setForeground(Color.GREEN);
        nodSelectat.setVisible(false);

        clearButton = new JButton("Clear");
        clearButton.addActionListener(this);

        add(clearButton);
        add(nodSelectat);
        add(editButton);
        add(schimbareModGraf);
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        setBackground(Color.BLACK);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {

                if (!checkNode(e.getX(), e.getY(), 1) && !editMode)
                    pointStart = e.getPoint();

            }
            public void mouseReleased(MouseEvent e) {
                if (editMode) {
                    Node tempNode = helpNode;
                    if (checkNode(e.getX(), e.getY(), node_diam) || helpNode == tempNode) {
                        helpNode.setCoordX(e.getX());
                        helpNode.setCoordY(e.getY());
                        updateArcs();
                        repaint();
                        return;
                    }
                }
                if (!isDragging) {
                    if (checkNode(e.getX(), e.getY(), node_diam)) {
                        addNode(e.getX(), e.getY());
                        saveMatrixToFile();
                    }
                } else if (nodeStart != nodeEnd && nodeStart != null && !editMode) {
                    Arc arc = new Arc(pointStart, pointEnd, nodeStart, nodeEnd);
                    nodeStart = null;
                    nodeEnd = null;
                    listaArce.add(arc);
                    saveMatrixToFile();
                    repaint();
                }
                pointStart = null;
                isDragging = false;
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (!checkNode(e.getX(), e.getY(), 1)) {
                    pointEnd = e.getPoint();
                    isDragging = true;
                    repaint();
                }
            }
        });
    }

    public static boolean isGrafOrientat() {
        return grafOrientat;
    }

    private void addNode(int x, int y) {
        Node node = new Node(x, y, nodeNr);
        listaNoduri.add(node);
        nodeNr++;
        repaint();
    }

    public boolean checkNode(int x, int y, int diam) {

        Rectangle rect1 = new Rectangle(x,y, diam,diam);
        return searchNodes(x,y, rect1);

    }

    public boolean searchNodes(int x, int y, Rectangle r) {

        Rectangle rectangle = new Rectangle(x, y, node_diam, node_diam);

        if (listaNoduri.size() == 0)
            return true;

        for (int i = 0; i<listaNoduri.size(); i++) {
            rectangle.setBounds(listaNoduri.elementAt(i).getCoordX(), listaNoduri.elementAt(i).getCoordY(), 30, 30);
            if (!verifyIntersection(rectangle, r)) {
                helpNode = listaNoduri.elementAt(i);
                nodSelectat.setText("Nod selectat: " + helpNode.getNumber());
                if (nodeStart == null)
                    nodeStart = listaNoduri.elementAt(i);
                else nodeEnd = listaNoduri.elementAt(i);
                return false;
            }
        }
        r = null;
        return true;

    }
    public boolean verifyIntersection(Rectangle r, Rectangle r1) {

        Rectangle intersection = r.intersection(r1);
        return intersection.isEmpty();

    }

    private void saveMatrixToFile() {

        int[][] matriceAdiacenta = new int[nodeNr][nodeNr];

        for (Arc a : listaArce) {
            if (grafOrientat)
                matriceAdiacenta[a.getNodeStart().getNumber()][a.getNodeEnd().getNumber()] = 1;
            else {
                matriceAdiacenta[a.getNodeStart().getNumber()][a.getNodeEnd().getNumber()] = 1;
                matriceAdiacenta[a.getNodeEnd().getNumber()][a.getNodeStart().getNumber()] = 1;
            }
        }

        try {

            FileWriter fileWriter = new FileWriter("matriceAdiacenta");
            fileWriter.append(nodeNr-1 + "\n");
            for (int i = 0; i<nodeNr; i++) {
                for (int j = 0; j<nodeNr; j++) {
                    if (matriceAdiacenta[i][j] != 1)
                        matriceAdiacenta[i][j] = 0;
                    fileWriter.append(matriceAdiacenta[i][j] + " ");
                }
                fileWriter.append("\n");
            }
            fileWriter.close();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void clearGraph() {
        listaNoduri.removeAllElements();
        listaArce.removeAllElements();
        nodeNr = 1;
        repaint();
    }

    public void updateArcs() {

        for (int i = 0; i<listaArce.size(); i++)
            listaArce.elementAt(i).editArc(new Point(listaArce.elementAt(i).getNodeStart().getCoordX()+15, listaArce.elementAt(i).getNodeStart().getCoordY()+15), new Point(listaArce.elementAt(i).getNodeEnd().getCoordX()+15, listaArce.elementAt(i).getNodeEnd().getCoordY()+15));

    }

    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == schimbareModGraf) {
            grafOrientat = !grafOrientat;
            if (!grafOrientat)
                schimbareModGraf.setText("Graf neorientat");
            else
                schimbareModGraf.setText("Graf orientat");
            saveMatrixToFile();
            repaint();
        }
        if (e.getSource() == editButton) {
            editMode = !editMode;
            nodSelectat.setVisible(editMode);
            clearButton.setVisible(!editMode);
            nodeStart = null;
            nodeEnd = null;
            pointStart = null;
            pointEnd = null;
            if (editMode)
                setBackground(Color.GRAY);
            else setBackground(Color.BLACK);
            repaint();
        }
        if (e.getSource() == clearButton)
            clearGraph();

    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (Arc a: listaArce) {
            a.setDoDrawing(true);
            a.drawArc(g);
        }
        if (pointStart!=null) {
            g.setColor(Color.RED);
            g.drawLine(pointStart.x, pointStart.y, pointEnd.x, pointEnd.y);
        }
        for (int i = 0; i<listaNoduri.size(); i++) {
            listaNoduri.elementAt(i).drawNode(g, node_diam);
        }
    }

}
