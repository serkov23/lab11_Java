package lab11;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MyFrame extends JFrame {
    MyPanel panel = new MyPanel();
    JPanel optionPanel1 = new JPanel(new GridLayout(6, 1));

    MyFrame() {
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        var dm = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode();
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panel.setPreferredSize(new Dimension(dm.getWidth(), dm.getHeight()));
        panel.setLayout(null);
        JScrollPane mainPane = new JScrollPane(panel);
        mainPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        mainPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        MyListener mouseListener = new MyListener();
        panel.addMouseMotionListener(mouseListener);
        panel.addMouseListener(mouseListener);

        add(mainPane, BorderLayout.CENTER);
        var choose = new JButton("choose color");

        optionPanel1.add(choose);

        optionPanel1.setPreferredSize(new Dimension(100,1000));
        add(optionPanel1, BorderLayout.EAST);
        var colorChooser = new JColorChooser(Color.BLACK);
        JTextField w = new JTextField(dm.getWidth()), h = new JTextField(dm.getHeight());
        optionPanel1.add(w);
        optionPanel1.add(h);
        JButton saveButton = new JButton("Save");
        optionPanel1.add(saveButton);

        JButton openButton = new JButton("Open");
        optionPanel1.add(openButton);

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                switch (fileChooser.showSaveDialog(MyFrame.this)) {
                    case JFileChooser.APPROVE_OPTION:
                        try {
                            ImageIO.write(panel.getImage(), "PNG", fileChooser.getSelectedFile());
                        } catch (IOException e1) {
                            errorMessage("Something went wrong");
                        }
                        break;
                    case JFileChooser.CANCEL_OPTION:
                        break;
                    case JFileChooser.ERROR_OPTION:
                    default:
                        errorMessage("Something went wrong");
                        break;

                }

            }
        });
        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                switch (fileChooser.showOpenDialog(MyFrame.this)) {
                    case JFileChooser.APPROVE_OPTION:
                        try {
                            panel.setImage(ImageIO.read(fileChooser.getSelectedFile()));
                        } catch (IOException e1) {
                            errorMessage("Something went wrong");
                        }
                        break;
                    case JFileChooser.CANCEL_OPTION:
                        break;
                    case JFileChooser.ERROR_OPTION:
                    default:
                        errorMessage("Something went wrong");
                        break;

                }

            }
        });
        JButton changeSize = new JButton("Change Size");
        optionPanel1.add(changeSize);
        changeSize.addActionListener(e -> {
            try {
                Dimension d = new Dimension(Integer.parseInt(w.getText()), Integer.parseInt(h.getText()));
                if (d.height > dm.getHeight() || d.width > dm.getWidth())
                    throw new NumberFormatException("invalid data");
                panel.setPreferredSize(d);
                setSize(d.width + optionPanel1.getWidth(), d.height);
                panel.setMaximumSize(d);
            } catch (NumberFormatException ex) {
                errorMessage("invalid size");
            }

        });
        choose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.setColor(JColorChooser.showDialog(MyFrame.this, "Choose color", Color.BLACK));
            }
        });
        add(mainPane, BorderLayout.CENTER);
    }

    private void errorMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    class MyPanel extends JPanel {
        BufferedImage image = null;
        Color color = Color.BLACK;
        Dimension maxSize = null;

        public MyPanel() {
            super();
            setPreferredSize(new Dimension(100, 100));
        }

        @Override
        public void setMaximumSize(Dimension maximumSize) {
            super.setMaximumSize(maximumSize);
            maxSize = maximumSize;
        }

        public void setColor(Color color) {
            this.color = color;
        }

        public BufferedImage getImage() {
            return image;
        }

        public void setImage(BufferedImage image) {
            this.image = image;
            var d=new Dimension(image.getWidth(),image.getHeight());
            super.setPreferredSize(d);
            setMaximumSize(d);
            clear();
            MyFrame.this.setSize(d.width + optionPanel1.getWidth(), d.height);
            getGraphics().drawImage(image, 0, 0, MyFrame.this);

        }
        private void clear(){
            var panelGraphics = getGraphics();
            panelGraphics.setColor(Color.WHITE);
            panelGraphics.fillRect(0,0,getWidth(),getHeight());
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            g.drawImage(image, 0, 0, Color.WHITE, MyFrame.this);
        }

        @Override
        public void setPreferredSize(Dimension preferredSize) {
            super.setPreferredSize(preferredSize);
            var tmpImg = new BufferedImage(preferredSize.width, preferredSize.height, BufferedImage.TYPE_3BYTE_BGR);
            tmpImg.createGraphics().setPaint(Color.WHITE);
            tmpImg.getGraphics().fillRect(0, 0, tmpImg.getWidth(), tmpImg.getHeight());
            if (image != null)
                tmpImg.getGraphics().drawImage(image, 0, 0, Color.WHITE, MyFrame.this);

            image = tmpImg;
        }

        public void drawLine(Point from, Point to) {
            if (maxSize != null && (from.x > maxSize.width || to.x > maxSize.width || from.y > maxSize.height || to.y > maxSize.height))
                return;
            var panelGraphics = getGraphics();
            panelGraphics.setColor(color);
            panelGraphics.drawLine(from.x, from.y, to.x, to.y);

            var imageGraphics = image.getGraphics();
            imageGraphics.setColor(color);
            imageGraphics.drawLine(from.x, from.y, to.x, to.y);
        }
    }

    class MyListener extends MouseAdapter {
        Point lastPoint = null;

        @Override
        public void mousePressed(MouseEvent e) {
            lastPoint = new Point(e.getPoint());
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            lastPoint = null;
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (lastPoint == null)
                return;
            panel.drawLine(lastPoint, e.getPoint());
            lastPoint.setLocation(e.getPoint());
        }
    }

}
