import object_data.Vertex;
import raster_data.ColorRaster;
import raster_data.ZBuffer;
import rasterops.Triangler;
import transforms.Col;
import transforms.Point3D;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.Serial;

public class Canvas {

    private final ColorRaster img;

    public Canvas(int width, int height) {
        JFrame frame = new JFrame();
        frame.setLayout(new BorderLayout());
        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        img = new ColorRaster(width, height, new Col(50, 50, 50));
        JPanel panel = new JPanel() {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                img.present(g);
            }
        };
        panel.setPreferredSize(new Dimension(width, height));

        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    System.exit(0);
                }
            }
        });

        frame.add(panel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);

    }

    public void start() {
        Vertex v1 = new Vertex(new Point3D(50, 50, 0.5), new Col(255, 0, 0));
        Vertex v2 = new Vertex(new Point3D(150, 400, 0.5), new Col(0, 255, 0));
        Vertex v3 = new Vertex(new Point3D(250, 150, 0.5), new Col(0, 0, 255));

        Vertex v4 = new Vertex(new Point3D(50, 400, 0.7), new Col(255, 0, 0));
        Vertex v5 = new Vertex(new Point3D(300, 100, 0.2), new Col(255, 0, 0));
        Vertex v6 = new Vertex(new Point3D(400, 300, 0.5), new Col(255, 0, 0));

        Vertex v7 = new Vertex(new Point3D(300, 50, 0.7), new Col(0, 255, 0));
        Vertex v8 = new Vertex(new Point3D(50, 150, 0.3), new Col(0, 255, 0));
        Vertex v9 = new Vertex(new Point3D(400, 200, 0.2), new Col(0, 255, 0));

        Vertex v10 = new Vertex(new Point3D(120, 100, 0.1), new Col(0, 0, 255));
        Vertex v11 = new Vertex(new Point3D(20, 160, 0.9), new Col(0, 0, 255));
        Vertex v12 = new Vertex(new Point3D(250, 450, 0.6), new Col(0, 0, 255));

        Triangler triangler = new Triangler(new ZBuffer(img));

        // TEST TRIANGLER
//        triangler.draw(v1, v2, v3);

        // TEST ZBUFFER
        triangler.draw(v4, v5, v6);
        triangler.draw(v7, v8, v9);
        triangler.draw(v10, v11, v12);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Canvas(500, 500).start());
    }


}
