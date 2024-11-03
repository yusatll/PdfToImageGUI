package org.example;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PdfConverterGUI extends JFrame {
    private JTextField inputPathField;
    private JTextField outputPathField;
    private JComboBox<String> formatComboBox;
    private JButton convertButton;
    private JProgressBar progressBar;
    private JTextArea logArea;
    private JLabel statusLabel;

    public PdfConverterGUI() {
        // Window settings  // Pencere ayarları
        setTitle("PDF Image Converter");  // PDF Görüntü Dönüştürücü
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        // Main panel  // Ana panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top panel (file selection and settings)  // Üst panel (dosya seçimi ve ayarlar)
        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // PDF File selection  // PDF Dosyası seçimi
        gbc.gridx = 0;
        gbc.gridy = 0;
        topPanel.add(new JLabel("PDF File:"), gbc);  // PDF Dosyası:

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        inputPathField = new JTextField(20);
        inputPathField.setEditable(false);
        topPanel.add(inputPathField, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.0;
        JButton browseInputButton = new JButton("Browse");  // Gözat
        browseInputButton.addActionListener(e -> selectInputFile());
        topPanel.add(browseInputButton, gbc);

        // Output directory selection  // Çıktı klasörü seçimi
        gbc.gridx = 0;
        gbc.gridy = 1;
        topPanel.add(new JLabel("Output Directory:"), gbc);  // Çıktı Klasörü:

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        outputPathField = new JTextField(20);
        outputPathField.setEditable(false);
        topPanel.add(outputPathField, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.0;
        JButton browseOutputButton = new JButton("Browse");  // Gözat
        browseOutputButton.addActionListener(e -> selectOutputDirectory());
        topPanel.add(browseOutputButton, gbc);

        // Format selection  // Format seçimi
        gbc.gridx = 0;
        gbc.gridy = 2;
        topPanel.add(new JLabel("Image Format:"), gbc);  // Görüntü Formatı:

        gbc.gridx = 1;
        formatComboBox = new JComboBox<>(new String[]{"PNG", "JPEG"});
        topPanel.add(formatComboBox, gbc);

        // Convert button  // Dönüştür butonu
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        convertButton = new JButton("Convert");  // Dönüştür
        convertButton.addActionListener(this::startConversion);
        convertButton.setEnabled(false);
        topPanel.add(convertButton, gbc);

        // Progress bar  // İlerleme çubuğu
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        topPanel.add(progressBar, gbc);

        // Status label  // Durum etiketi
        gbc.gridy = 5;
        statusLabel = new JLabel("Ready");  // Hazır
        topPanel.add(statusLabel, gbc);

        // Log area  // Log alanı
        logArea = new JTextArea(10, 40);
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);

        // Add panels to main window  // Panelleri ana pencereye ekle
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);
    }

    private void selectInputFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".pdf");
            }
            public String getDescription() {
                return "PDF Files (*.pdf)";  // PDF Dosyaları (*.pdf)
            }
        });

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            inputPathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
            updateConvertButtonState();
        }
    }

    private void selectOutputDirectory() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            outputPathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
            updateConvertButtonState();
        }
    }

    private void updateConvertButtonState() {
        convertButton.setEnabled(!inputPathField.getText().isEmpty() &&
                !outputPathField.getText().isEmpty());
    }

    private void startConversion(ActionEvent e) {
        String inputPath = inputPathField.getText();
        String outputPath = outputPathField.getText();
        String format = (String) formatComboBox.getSelectedItem();

        // Get PDF file name (without extension)  // PDF dosya adını al (uzantısız)
        File inputFile = new File(inputPath);
        final String pdfName = inputFile.getName().toLowerCase().endsWith(".pdf")
                ? inputFile.getName().substring(0, inputFile.getName().length() - 4)
                : inputFile.getName();

        // Create subdirectory  // Alt klasör oluştur
        File outputDir = new File(outputPath, pdfName);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        // Start conversion process in background  // Dönüştürme işlemini arka planda başlat
        convertButton.setEnabled(false);
        statusLabel.setText("Converting...");  // Dönüştürülüyor...

        final String finalOutputPath = outputDir.getAbsolutePath();

        SwingWorker<Void, Integer> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                try (PDDocument document = Loader.loadPDF(new File(inputPath))) {
                    PDFRenderer pdfRenderer = new PDFRenderer(document);
                    int pageCount = document.getNumberOfPages();

                    log("PDF Name: " + pdfName);  // PDF Adı:
                    log("Total Pages: " + pageCount);  // Toplam Sayfa Sayısı:
                    log("Output Directory: " + finalOutputPath);  // Çıktı Klasörü:

                    for (int pageNumber = 0; pageNumber < pageCount; pageNumber++) {
                        BufferedImage image = pdfRenderer.renderImageWithDPI(pageNumber, 300);
                        String fileName = String.format("%s/page_%03d.%s",  // sayfa_ yerine page_ kullanıldı
                                finalOutputPath, pageNumber + 1, format.toLowerCase());

                        ImageIO.write(image, format, new File(fileName));

                        int progress = (pageNumber + 1) * 100 / pageCount;
                        publish(progress);

                        log(String.format("Page %d/%d saved: page_%03d.%s",  // Sayfa %d/%d kaydedildi: page_%03d.%s
                                pageNumber + 1, pageCount, pageNumber + 1, format.toLowerCase()));
                    }
                }
                return null;
            }

            @Override
            protected void process(java.util.List<Integer> chunks) {
                int latestProgress = chunks.get(chunks.size() - 1);
                progressBar.setValue(latestProgress);
            }

            @Override
            protected void done() {
                try {
                    get();
                    statusLabel.setText("Conversion completed!");  // Dönüştürme tamamlandı!
                    log("Process completed successfully.");  // İşlem başarıyla tamamlandı.
                } catch (Exception ex) {
                    statusLabel.setText("Error occurred!");  // Hata oluştu!
                    log("Error: " + ex.getMessage());  // Hata:
                    ex.printStackTrace();
                } finally {
                    convertButton.setEnabled(true);
                    progressBar.setValue(0);
                }
            }
        };

        worker.execute();
    }

    private void log(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new PdfConverterGUI().setVisible(true);
        });
    }
}