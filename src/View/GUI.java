package View;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import Controller.Controller;

/**
 * @author Anna Selstam, 2022-06-30
 */
public class GUI extends JPanel {
    private JPanel mainPanel, inputPanel;
    private JButton upload, check;
    private JFileChooser chooser;
    private JTextField todaysDate; 
    private JLabel dateInfo1, dateInfo2, fileInfo, statusInfo;

    private Controller controller;

    private List<String> files = new ArrayList<>();

    public GUI(Controller controller) {
        this.controller = controller;

        JFrame frame = new JFrame("Daily Report Board Input");
        createComponents();

        frame.setVisible(true);
        frame.pack();
        frame.add(this);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
    }


    private void createComponents() {

        ///////////////////////////////////////////

        inputPanel = new JPanel();
        inputPanel.setPreferredSize(new Dimension(380, 250));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Today's Orders - Daily Reports"));

        dateInfo1 = new JLabel("Data will be extracted from today only.");
        dateInfo1.setFont(new Font("Arial", Font.BOLD, 11));
        dateInfo1.setPreferredSize(new Dimension(220, 20));
        dateInfo1.setHorizontalAlignment((int) CENTER_ALIGNMENT);;
        
        dateInfo2 = new JLabel("Choose all the .xlsl files at once.");
        dateInfo2.setFont(new Font("Arial", Font.BOLD, 10));
        dateInfo2.setPreferredSize(new Dimension(220, 15));
        dateInfo2.setHorizontalAlignment((int) CENTER_ALIGNMENT);;

        upload = new JButton("Upload files");
        upload.setPreferredSize(new Dimension(220, 50));

        upload.addActionListener(e -> {

            chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("EXCEL files", "xlsx");
            chooser.setFileFilter(filter);
            chooser.setMultiSelectionEnabled(true);

            chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
           
            int result = chooser.showOpenDialog(this);

            if (result == JFileChooser.APPROVE_OPTION) {
                File[] file = chooser.getSelectedFiles();

                for (File f : file) {
                    files.add(f.getAbsolutePath());
                }

                setFileInfo("File(s) OK.");
                check.setEnabled(true);

            }
        });

        fileInfo = new JLabel("");
        fileInfo.setPreferredSize(new Dimension(220, 20));
        fileInfo.setHorizontalAlignment((int) CENTER_ALIGNMENT);

        check = new JButton("Transfer");
        check.setPreferredSize(new Dimension(220, 50));
        check.setEnabled(false);

        check.addActionListener(e -> {
            try {
                controller.checkFile(files);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });

        statusInfo = new JLabel("");
        statusInfo.setFont(new Font("Arial", Font.BOLD, 10));
        statusInfo.setPreferredSize(new Dimension(350, 20));
        statusInfo.setHorizontalAlignment(0);

        inputPanel.add(dateInfo1);
        inputPanel.add(dateInfo2);
        inputPanel.add(upload);
        inputPanel.add(fileInfo);
        inputPanel.add(check);
        inputPanel.add(statusInfo);

        //////////////////////////////////////////

        mainPanel = new JPanel();
        mainPanel.setPreferredSize(new Dimension(400, 300));

        mainPanel.add(inputPanel);

        add(mainPanel);

    }

    public String getTodaysDate() {
        return todaysDate.getText();
    }

    public void setTodaysDate(String todaysDate) {
        this.todaysDate.setText(todaysDate);;
    }

    public void setFileInfo(String fileInfo) {
        this.fileInfo.setText(fileInfo);;
    }

    public void setStatusInfo(String text) {
        this.statusInfo.setText(text);
    }


}
