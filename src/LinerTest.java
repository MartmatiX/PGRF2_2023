import object_data.Vertex;
import raster_data.ColorRaster;
import raster_data.ZBuffer;
import rasterops.Liner;
import transforms.Col;
import transforms.Point3D;

import javax.swing.*;
import java.awt.*;

public class LinerTest {

    private final JFrame frame;
    private final JPanel panel;
    private final ColorRaster img;

    public LinerTest(int width, int height) {
        frame = new JFrame();
        frame.setLayout(new BorderLayout());
        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        img = new ColorRaster(width, height, new Col(50, 50, 50));
        panel = new JPanel() {
            private static final long serialVersionUID = 1L;

            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                img.present(g);
            }
        };
        panel.setPreferredSize(new Dimension(width, height));

        frame.add(panel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);

    }

    public void start() {
        ZBuffer zBuffer = new ZBuffer(img);
        Liner liner = new Liner(zBuffer);
        liner.draw(new Vertex(new Point3D(40, 50, 0), new Col(255, 255, 255)), new Vertex(new Point3D(250, 250, 0), new Col(255, 0, 0)));
        liner.draw(new Vertex(new Point3D(250, 250, 0), new Col(255, 0, 0)), new Vertex(new Point3D(500, 50, 0), new Col(255, 255, 255)));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LinerTest(800, 600).start());
    }

}
