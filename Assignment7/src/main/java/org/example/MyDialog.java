package org.example;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.Dimension;
import java.awt.Component;
import java.awt.event.*;
import java.io.*;
import java.lang.*;

public class MyDialog extends JDialog{
    private JTable table;

    public MyDialog(JTable owner){
        super(javax.swing.SwingUtilities.windowForComponent(owner));
        table = owner;
        createGUI();
    }

    private void createGUI(){
        setPreferredSize(new Dimension(600, 400));
        setTitle(getClass().getSimpleName());
        JPanel listPane = new JPanel();

        listPane.setLayout(new BoxLayout(listPane, BoxLayout.Y_AXIS));
        JLabel label = new JLabel("Hello:");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        listPane.add(label);

        int row = table.getSelectedRow();
        JLabel dataLabel = null;
        if(row < 0){
            dataLabel = new JLabel("no row selected");
        }
        else{
            dataLabel = new JLabel(table.getModel().getValueAt(row,0)+" "+table.getModel().getValueAt(row,1));
        }
        dataLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        listPane.add(dataLabel);

        JButton addButton = new JButton("Add row");
        addButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((DefaultTableModel)table.getModel()).addRow(new Object[]{"Coolio","Noman","Karate",1,true});
                dispose();
                JOptionPane.showMessageDialog(table, "Added new record");
            }
        });
        listPane.add(addButton);

        add(listPane);
        pack();
        setLocationRelativeTo(getParent());
        setVisible(true);
    }

    @Override
    public void dispose(){
        super.dispose();
    }
}
