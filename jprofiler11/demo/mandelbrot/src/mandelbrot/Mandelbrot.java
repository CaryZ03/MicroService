package mandelbrot;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Mandelbrot {

    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        JFrame frame = new JFrame("Mandelbrot Fractal");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(700, 700);

        JPanel contentPane = new JPanel(new BorderLayout());
        ImagePanel imagePanel = new ImagePanel();
        contentPane.add(imagePanel, BorderLayout.CENTER);
        contentPane.add(createControls(imagePanel), BorderLayout.SOUTH);
        frame.setContentPane(contentPane);
        frame.setVisible(true);
    }

    @NotNull
    private static JPanel createControls(final ImagePanel imagePanel) {
        JPanel controls = new JPanel(new FlowLayout());
        controls.add(new JLabel("Max. iterations:"));
        final JSlider slider = new JSlider(4, 128, imagePanel.getMaxIterations());
        final JLabel label = new JLabel(String.valueOf(slider.getValue()));
        Dimension preferredSize = label.getPreferredSize();
        preferredSize.width *= 2;
        label.setPreferredSize(preferredSize);
        slider.setFocusable(false);
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int value = slider.getValue();
                imagePanel.setMaxIterations(value);
                label.setText(String.valueOf(value));
            }
        });
        controls.add(slider);
        controls.add(label);
        return controls;
    }

    public static class ImagePanel extends JComponent {

        private BufferedImage image;
        private BufferedImage oldImage;
        private SwingWorker<BufferedImage, Void> worker;
        private int maxIterations = 32;

        public int getMaxIterations() {
            return maxIterations;
        }

        public void setMaxIterations(int iterations) {
            this.maxIterations = iterations;
            oldImage = image;
            image = null;
            updateImage();
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D)graphics;
            if (image != null) {
                g.drawImage(image, null, 0, 0);
            }
            updateImage();
        }

        private void updateImage() {
            if (worker != null || (image != null && image.getWidth() == getWidth() && image.getHeight() == getHeight())) {
                return;
            }
            worker = new SwingWorker<BufferedImage, Void>() {
                @Override
                protected BufferedImage doInBackground() throws Exception {
                    return createBufferedImage();
                }

                @Override
                protected void done() {
                    try {
                        image = get();
                        worker = null;
                        repaint();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            worker.execute();
        }

        private BufferedImage createBufferedImage() {
            int width = getWidth();
            int height = getHeight();
            int maxIterations = this.getMaxIterations();

            BufferedImage image;
            if (oldImage != null && oldImage.getWidth() == width && oldImage.getHeight() == height) {
                image = oldImage;
            } else {
                image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            }
            int black = 0;
            int[] colors = new int[maxIterations];
            for (int i = 0; i < maxIterations; i++) {
                colors[i] = Color.HSBtoRGB(i / 256f, 1, i / (i + 8f));
            }

            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    double cRe = (col - width / 1.5d) * 3.0 / width;
                    double cIm = (row - height / 2) * 3.0 / width;
                    double x = 0, y = 0;
                    int iteration = 0;
                    while (x * x + y * y < 4 && iteration < maxIterations) {
                        double xNew = x * x - y * y + cRe;
                        y = 2 * x * y + cIm;
                        x = xNew;
                        iteration++;
                    }
                    if (iteration < maxIterations) {
                        image.setRGB(col, row, colors[iteration]);
                    } else {
                        image.setRGB(col, row, black);
                    }
                }
            }

            return image;
        }
    }
}
