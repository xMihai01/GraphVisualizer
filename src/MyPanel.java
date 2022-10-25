import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Vector;

public class MyPanel extends JPanel implements ActionListener {

    private int nodeNr = 1;
    public static int node_diam = 30;
    private Vector<Node> listaNoduri;
    private Vector<Arc> listaArce;
    JButton schimbareModGraf, editButton, clearButton, drawNodes, changeDiam;
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

        drawNodes = new JButton("Draw Nodes");
        drawNodes.addActionListener(this);

        changeDiam = new JButton("Change Diameter ("+node_diam+")");
        changeDiam.addActionListener(this);

        add(clearButton);
        add(nodSelectat);
        add(editButton);
        add(schimbareModGraf);
        add(drawNodes);
        add(changeDiam);
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
            listaArce.elementAt(i).editArc(new Point(listaArce.elementAt(i).getNodeStart().getCoordX()+node_diam/2,
                    listaArce.elementAt(i).getNodeStart().getCoordY()+node_diam/2),
                    new Point(listaArce.elementAt(i).getNodeEnd().getCoordX()+node_diam/2,
                            listaArce.elementAt(i).getNodeEnd().getCoordY()+node_diam/2));

    }

    void drawNodesByNumber(int number) {
        clearGraph();
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        Random random = new Random();
        while(nodeNr <= number) {
            int x = 15+random.nextInt((int)size.getWidth()-35);
            int y = 30+random.nextInt((int)size.getHeight()-110);
            if (checkNode(x, y, node_diam))
                addNode(x, y);
            repaint();
        }
        double probability = Double.parseDouble(JOptionPane.showInputDialog("Probability for arcs"));
        drawArcsByProbability(probability);
        //saveMatrixToFile();
    }

    void drawArcsByProbability(double prob) {
        double num;
        for (int i = 0; i < listaNoduri.size(); i++) {
            for (int j = 0; j < listaNoduri.size(); j++) {
                num = Math.random();
                if (num < prob && i != j) {
                    Arc arc = new Arc(new Point(listaNoduri.elementAt(i).getCoordX()+node_diam/2, listaNoduri.elementAt(i).getCoordY()+node_diam/2)
                            , new Point(listaNoduri.elementAt(j).getCoordX()+node_diam/2, listaNoduri.elementAt(j).getCoordY()+node_diam/2)
                            , listaNoduri.elementAt(i), listaNoduri.elementAt(j));
                    listaArce.add(arc);
                }
            }
        }
        repaint();

    }
    public boolean checkNode(int x, int y, int diam) {
        if (listaNoduri.isEmpty())
            return true;
        Rectangle r1 = null;
        Rectangle r2 = null;
        Rectangle intersection = null;
        for (int i = 0; i<listaNoduri.size(); i++) {
            r1 = new Rectangle(listaNoduri.elementAt(i).getCoordX(), listaNoduri.elementAt(i).getCoordY(), node_diam, node_diam);
            r2 = new Rectangle(x, y, diam, diam);
            intersection = r1.intersection(r2);
            if (!intersection.isEmpty()) {
                helpNode = listaNoduri.elementAt(i);
                nodSelectat.setText("Nod selectat: " + helpNode.getNumber());
                if (nodeStart == null)
                    nodeStart = listaNoduri.elementAt(i);
                else nodeEnd = listaNoduri.elementAt(i);
                return false;
            }
        }
        return true;
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

        if (e.getSource() == drawNodes) {
            int numberOfDrawnNodes = Integer.parseInt(JOptionPane.showInputDialog("Number of nodes to draw"));
            drawNodesByNumber(numberOfDrawnNodes);
        }
        if (e.getSource() == changeDiam) {
            node_diam = Integer.parseInt(JOptionPane.showInputDialog("WARNING!\nNode counting is unavailable for a diameter less than 20\nChanging will clear the graph!"));
            changeDiam.setText("Change Diameter ("+node_diam+")");
            clearGraph();
        }
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
