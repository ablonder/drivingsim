import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;


public class drawGrid {

//    public static void main(String[] args) {
//        new drawGrid();
//        
//    }
	public static CellPane[][] cellMatrix;
	
    public drawGrid(int numLanes, int numCols) {
    	// create a new cellPane matrix for cellMatrix
    	cellMatrix = new CellPane[numLanes][numCols];
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                }

                JFrame frame = new JFrame("Route 364");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setLayout(new BorderLayout());
                frame.add(new TestPane());
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }

    public static class TestPane extends JPanel {

        public TestPane() {
            Color defaultBackground;

            setLayout(new GridBagLayout());
            
            
            GridBagConstraints gbc = new GridBagConstraints();
            for (int row = 0; row < 5; row++) {
                for (int col = 0; col < 100; col++) {
                    gbc.gridx = col;
                    gbc.gridy = row;

                    CellPane cellPane = new CellPane();
                    Border border = null;
                    if (row < 4) {
                        if (col < 99) {
                            border = new MatteBorder(1, 1, 0, 0, Color.GRAY);
                        } else {
                            border = new MatteBorder(1, 1, 0, 1, Color.GRAY);
                        }
                    } else {
                        if (col < 99) {
                            border = new MatteBorder(1, 1, 1, 0, Color.GRAY);
                        } else {
                            border = new MatteBorder(1, 1, 1, 1, Color.GRAY);
                        }
                    }
                    cellPane.setBorder(border);
                    add(cellPane, gbc);
                    defaultBackground = cellPane.getBackground();
                    cellMatrix[row][col] = cellPane;
//                    cellMatrix[row][col].setBackground(Color.BLUE);
                    if(World.road.get(row).get(col).car != null){
                    	System.out.println("Doop");
                    	
                    	cellPane.setBackground(Color.BLUE);                 
                    }
                    else{
                    	cellPane.setBackground(defaultBackground);
                    }
//                    World.road.get(row).get(col).cell = cellPane;
                }
            }
        }
    }

    public static class CellPane extends JPanel {


        public CellPane() {

//            addMouseListener(new MouseAdapter() {
//                @Override
//                public void mouseEntered(MouseEvent e) {
//                    defaultBackground = getBackground();
//                    setBackground(Color.BLUE);
//                }

//                @Override
//                public void mouseExited(MouseEvent e) {
//                    setBackground(defaultBackground);
//                }
//            });
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(50, 50);
        }
    }
}