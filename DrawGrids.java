 import java.awt.*;
 import java.awt.event.*;

    class Grids extends Canvas {
      int width, height, rows, columns;

      Grids(int w, int h, int r, int c) {
        setSize(width = w, height = h);
        rows = r;
        columns = c;
      }
      public void paint(Graphics g) {
        int k;
        width = getSize().width;
        height = getSize().height;

        int htOfRow = height / (rows);
        for (k = 0; k < rows; k++)
          g.drawLine(0, k * htOfRow , width, k * htOfRow );

        int wdOfRow = width / (columns);
        for (k = 0; k < columns; k++)
          g.drawLine(k*wdOfRow , 0, k*wdOfRow , height);
      }
    }
    public class DrawGrids extends Frame {
      DrawGrids(String title, int w, int h, int rows, int columns) {
        setTitle(title);
        Grids grid = new Grids(w, h, rows, columns);
        add(grid);
      }
    
      public static void main(String[] args) {
      new DrawGrids("Draw Grids", 200, 200, 5, 100).setVisible(true);
      }
    }