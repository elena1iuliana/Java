package util;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import org.opencv.core.Mat;
import ui.FaceApp;

public class WindowHelper {
    private JFrame frame;
    private JLabel label;
    private FaceApp app;

    public WindowHelper(String title, FaceApp app) {
        this.app = app;
        frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JMenuBar bar = new JMenuBar();
        JMenu menuDate = new JMenu("Colectare Date");  
      
        JMenuItem itemCapture = new JMenuItem("Captura 200 poze");
        itemCapture.addActionListener(e -> {
            String pseudonim = JOptionPane.showInputDialog(frame, "Introdu Pseudonimul:");
            if (pseudonim != null && !pseudonim.isEmpty()) {
                new Thread(() -> app.captureDataset(pseudonim)).start();
            }
        });

        JMenuItem itemManage = new JMenuItem("Gestionare / Stergere Poze");
        itemManage.addActionListener(e -> showManageDialog());

        menuDate.add(itemCapture);
        menuDate.add(itemManage);

        JMenu menuAI = new JMenu("Inteligenta Artificiala");
        JMenuItem itemTrain = new JMenuItem("Antreneaza Modele SVM");
        itemTrain.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(frame, "Incepem antrenarea?", "Confirmare", JOptionPane.YES_NO_OPTION);
            if(confirm == JOptionPane.YES_OPTION) {
                new Thread(() -> app.train()).start();
            }
        });

        menuAI.add(itemTrain);
        bar.add(menuDate);
        bar.add(menuAI);
        frame.setJMenuBar(bar);
        
        label = new JLabel();
        frame.add(label);
        frame.setSize(800, 640);
        frame.setLocationRelativeTo(null); 
        frame.setVisible(true);
    }

    private void showManageDialog() {
        JDialog dialog = new JDialog(frame, "Gestionare Imagini Invatare", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(600, 450);

        DefaultListModel<String> listModel = new DefaultListModel<>();
        File folder = new File("learning_set/");
        if (!folder.exists()) folder.mkdir();

        
        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".png"));
        if (files != null) {
            for (File f : files) listModel.addElement(f.getName());
        }

        JList<String> fileList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(fileList);
        scrollPane.setPreferredSize(new Dimension(250, 0));

        
        JLabel previewLabel = new JLabel("Selecteaza o poza", SwingConstants.CENTER);
        previewLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

       
        fileList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selected = fileList.getSelectedValue();
                if (selected != null) {
                    ImageIcon icon = new ImageIcon("learning_set/" + selected);
                    Image scaled = icon.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH);
                    previewLabel.setIcon(new ImageIcon(scaled));
                    previewLabel.setText("");
                }
            }
        });

        
        JButton btnDelete = new JButton("Sterge Poza Selectata");
        btnDelete.setBackground(new Color(255, 100, 100));
        btnDelete.addActionListener(e -> {
            String selected = fileList.getSelectedValue();
            if (selected != null) {
                int confirm = JOptionPane.showConfirmDialog(dialog, "Stergi definitiv fisierul " + selected + "?");
                if (confirm == JOptionPane.YES_OPTION) {
                    File toDelete = new File("learning_set/" + selected);
                    if (toDelete.delete()) {
                        listModel.removeElement(selected);
                        previewLabel.setIcon(null);
                        previewLabel.setText("Sters cu succes");
                    }
                }
            }
        });

        
        dialog.add(scrollPane, BorderLayout.WEST);
        dialog.add(previewLabel, BorderLayout.CENTER);
        dialog.add(btnDelete, BorderLayout.SOUTH);

        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
    }

    public void updateImage(Mat mat) {
        if (mat == null || mat.empty()) return;
        try {
            int type = (mat.channels() > 1) ? BufferedImage.TYPE_3BYTE_BGR : BufferedImage.TYPE_BYTE_GRAY;
            BufferedImage img = new BufferedImage(mat.cols(), mat.rows(), type);
            byte[] data = new byte[mat.cols() * mat.rows() * (int)mat.elemSize()];
            mat.get(0, 0, data);
            img.getRaster().setDataElements(0, 0, mat.cols(), mat.rows(), data);
            label.setIcon(new ImageIcon(img));
        } catch (Exception e) {}
    }
}