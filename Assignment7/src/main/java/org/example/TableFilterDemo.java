package org.example;

import net.coderazzi.filters.gui.AutoChoices;
import net.coderazzi.filters.gui.TableFilterHeader;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.Dimension;
import java.awt.Component;
import java.awt.event.*;
import java.io.*;
import java.lang.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class TableFilterDemo extends JPanel {
    private boolean DEBUG = false;
    private JTable table;
    private JTextField filterText;
    private JTextField statusText;
    private TableRowSorter<DefaultTableModel> sorter;

    private String[] columnNames = {"First Name",
            "Last Name",
            "Sport",
            "# of Years",
            "Vegetarian"};
    private Object[][] data = {
            {"Kathy", "Smith",
                    "Snowboarding", new Integer(5), new Boolean(false)},
            {"John", "Doe",
                    "Rowing", new Integer(3), new Boolean(true)},
            {"Sue", "Black",
                    "Knitting", new Integer(2), new Boolean(false)},
            {"Jane", "White",
                    "Speed reading", new Integer(20), new Boolean(true)},
            {"Joe", "Brown",
                    "Pool", new Integer(10), new Boolean(false)}
    };

    public TableFilterDemo() {
        super();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        //Create a table with a sorter.
        //DefaultTableModel model = new DefaultTableModel(data, columnNames);
        //task 6
        final Class<?>[] columnClass = new Class[]{
                String.class, String.class, String.class, Integer.class, Boolean.class
        };
        DefaultTableModel model = new DefaultTableModel(data, columnNames){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
            @Override
            public Class<?>getColumnClass(int columnIndex){
                return columnClass[columnIndex];
            }
        };

        Box b1 = Box.createHorizontalBox();
        b1.add(initMenu());
        b1.add(Box.createHorizontalGlue());
        add(b1);

        sorter = new TableRowSorter<DefaultTableModel>(model);
        table = new JTable(model);
        table.setRowSorter(sorter);
        table.setPreferredScrollableViewportSize(new Dimension(500, 70));
        table.setFillsViewportHeight(true);

        //For the purposes of this example, better to have a single
        //selection.
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        //When selection changes, provide user with row numbers for
        //both view and model.
        table.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
                    public void valueChanged(ListSelectionEvent event) {
                        int viewRow = table.getSelectedRow();
                        if (viewRow < 0) {
                            //Selection got filtered away.
                            statusText.setText("");
                        } else {
                            int modelRow =
                                    table.convertRowIndexToModel(viewRow);
                            statusText.setText(
                                    String.format("Selected Row in view: %d. " +
                                                    "Selected Row in model: %d.",
                                            viewRow, modelRow));
                        }
                    }
                }
        );


        //Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(table);

        //Add the scroll pane to this panel.
        add(scrollPane);

        //Create a separate form for filterText and statusText
        JPanel form = new JPanel(new SpringLayout());

        JLabel l1 = new JLabel("Filter Text:", SwingConstants.TRAILING);
        form.add(l1);
        filterText = new JTextField();
        //Whenever filterText changes, invoke newFilter.
        filterText.getDocument().addDocumentListener(
                new DocumentListener() {
                    public void changedUpdate(DocumentEvent e) {
                        newFilter();
                    }
                    public void insertUpdate(DocumentEvent e) {
                        newFilter();
                    }
                    public void removeUpdate(DocumentEvent e) {
                        newFilter();
                    }
                });
        l1.setLabelFor(filterText);
        form.add(filterText);

        JLabel l2 = new JLabel("Status:", SwingConstants.TRAILING);
        form.add(l2);
        statusText = new JTextField();
        l2.setLabelFor(statusText);
        form.add(statusText);


        SpringUtilities.makeCompactGrid(form, 2, 2, 6, 6, 6, 6);
        add(form);


        JButton button = new JButton("Remove");
        button.addActionListener(new RemoveLineActionLister());
        add(button);

        //task 4
        JButton dialogButton = new JButton("Dialog");
        dialogButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                javax.swing.SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        MyDialog dialog = new MyDialog(table);
                    }
                });
            }
        });
        add(dialogButton);

        //task 5
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        DeleteRowFromTableAction deleteAction = new DeleteRowFromTableAction(table, (DefaultTableModel) table.getModel());
        JToolBar toolbar = new JToolBar();
        Box floatRightBox = Box.createHorizontalBox();
        floatRightBox.add(Box.createHorizontalGlue());
        toolbar.add(deleteAction);
        floatRightBox.add(toolbar);
        add(floatRightBox);

        TableFilterHeader filterHeader = new TableFilterHeader(table, AutoChoices.ENABLED);
    }

    private final class RemoveLineActionLister implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            int viewRow = table.getSelectedRow();
            if(viewRow < 0){
                JOptionPane.showMessageDialog(null, "No row selected!");
            }
            else{
                int modelRow = table.convertRowIndexToModel(viewRow);
                DefaultTableModel model = (DefaultTableModel) table.getModel();

                int answer = JOptionPane.showConfirmDialog(null,
                            "Do you want to remove " + model.getValueAt(modelRow, 0) + ""
                                    + model.getValueAt(modelRow, 1) + "?",
                            "Warning", JOptionPane.YES_NO_OPTION);
                    if (answer == 0) {
                        model.removeRow(modelRow);
                    }
            }
        }
    }

    /**
     * Update the row filter regular expression from the expression in
     * the text box.
     */
    private void newFilter() {
        RowFilter<DefaultTableModel, Object> rf = null;
        //If current expression doesn't parse, don't update.
        try {
            rf = RowFilter.regexFilter(filterText.getText(), 0,1,2); //allows to filter other col
        } catch (java.util.regex.PatternSyntaxException e) {
            return;
        }
        sorter.setRowFilter(rf);
    }


    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("TableFilterDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        TableFilterDemo newContentPane = new TableFilterDemo();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    private JMenuBar initMenu(){
//        //Where the GUI is created:
//        JMenuBar menuBar;
//        JMenu menu, submenu;
//        JMenuItem menuItem;
//        JRadioButtonMenuItem rbMenuItem;
//        JCheckBoxMenuItem cbMenuItem;
//
//        //Create the menu bar.
//        menuBar = new JMenuBar();
//
//        //Build the first menu.
//        menu = new JMenu("A Menu");
//        menu.setMnemonic(KeyEvent.VK_A);
//        menu.getAccessibleContext().setAccessibleDescription(
//                "The only menu in this program that has menu items");
//        menuBar.add(menu);
//
//        //a group of JMenuItems
//        menuItem = new JMenuItem("A text-only menu item",
//                KeyEvent.VK_T);
//        menuItem.setAccelerator(KeyStroke.getKeyStroke(
//                KeyEvent.VK_1, ActionEvent.ALT_MASK));
//        menuItem.getAccessibleContext().setAccessibleDescription(
//                "This doesn't really do anything");
//        menu.add(menuItem);
//
//        menuItem = new JMenuItem("Both text and icon",
//                new ImageIcon("images/middle.gif"));
//        menuItem.setMnemonic(KeyEvent.VK_B);
//        menu.add(menuItem);
//
//        menuItem = new JMenuItem(new ImageIcon("images/middle.gif"));
//        menuItem.setMnemonic(KeyEvent.VK_D);
//        menu.add(menuItem);
//
//        //a group of radio button menu items
//        menu.addSeparator();
//        ButtonGroup group = new ButtonGroup();
//        rbMenuItem = new JRadioButtonMenuItem("A radio button menu item");
//        rbMenuItem.setSelected(true);
//        rbMenuItem.setMnemonic(KeyEvent.VK_R);
//        group.add(rbMenuItem);
//        menu.add(rbMenuItem);
//
//        rbMenuItem = new JRadioButtonMenuItem("Another one");
//        rbMenuItem.setMnemonic(KeyEvent.VK_O);
//        group.add(rbMenuItem);
//        menu.add(rbMenuItem);
//
//        //a group of check box menu items
//        menu.addSeparator();
//        cbMenuItem = new JCheckBoxMenuItem("A check box menu item");
//        cbMenuItem.setMnemonic(KeyEvent.VK_C);
//        menu.add(cbMenuItem);
//
//        cbMenuItem = new JCheckBoxMenuItem("Another one");
//        cbMenuItem.setMnemonic(KeyEvent.VK_H);
//        menu.add(cbMenuItem);
//
//        //a submenu
//        menu.addSeparator();
//        submenu = new JMenu("A submenu");
//        submenu.setMnemonic(KeyEvent.VK_S);
//
//        menuItem = new JMenuItem("An item in the submenu");
//        menuItem.setAccelerator(KeyStroke.getKeyStroke(
//                KeyEvent.VK_2, ActionEvent.ALT_MASK));
//        submenu.add(menuItem);
//
//        menuItem = new JMenuItem("Another item");
//        submenu.add(menuItem);
//        menu.add(submenu);
//
//        //Build second menu in the menu bar.
//        menu = new JMenu("Another Menu");
//        menu.setMnemonic(KeyEvent.VK_N);
//        menu.getAccessibleContext().setAccessibleDescription(
//                "This menu does nothing");
//        menuBar.add(menu);
        JMenuBar menuBar;
        JMenu menu;
        JMenuItem header, menuItem;

        menuBar = new JMenuBar();

        menu = new JMenu("Menu");
        menuBar.add(menu);

        header = new JMenuItem("COMMANDS:");
        header.setEnabled(false);
        menu.add(header);

        menuItem = new JMenuItem("Remove");
        menuItem.addActionListener(null);
        menu.add(menuItem);
        menu.addSeparator();

        menuItem.addActionListener(new RemoveLineActionLister());
        menu.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                int viewRow = table.getSelectedRow();
                if(viewRow < 0){
                    menuItem.setEnabled(false);
                }
                else{
                    menuItem.setEnabled(true);
                }
                menu.repaint();
            }

            @Override
            public void menuDeselected(MenuEvent e) {}

            @Override
            public void menuCanceled(MenuEvent e) {}
        });

        /////TASK 3/////
        JMenuItem menuCSV = new JMenuItem("Load CSV");
        menu.add(menuCSV);
        menuCSV.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                model.setRowCount(0);

                try(BufferedReader br = new BufferedReader(new FileReader(new File(this.getClass().getResource("/data.csv").getFile())))){
                    String line;
                    while((line = br.readLine())!=null){
                        String[]row = line.split(",");
                        Vector<Object> correction = new Vector<>();
                        for(int i=0; i<3; i++){
                            correction.add(row[i]);
                        }
                        correction.add(Integer.parseInt(row[3]));
                        correction.add(Boolean.parseBoolean(row[4]));
                        model.addRow(correction);
                    }
                }
                catch(FileNotFoundException ex){
                    JOptionPane.showMessageDialog(null, "Issue with loading file: " + ex.getMessage());
                    ex.printStackTrace();
                }
                catch(IOException ex){
                    JOptionPane.showMessageDialog(null, "Issue with loading file: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });

        return menuBar;
    }

    //task 5
    public abstract class AbstractTableAction<T extends JTable, M extends TableModel> extends AbstractAction {

        private T table;
        private M model;

        public AbstractTableAction(T table, M model) {
            this.table = table;
            this.model = model;
        }

        public T getTable() {
            return table;
        }

        public M getModel() {
            return model;
        }

    }

    public class DeleteRowFromTableAction extends AbstractTableAction<JTable, DefaultTableModel> {

        public DeleteRowFromTableAction(JTable table, DefaultTableModel model) {
            super(table, model);
            putValue(NAME, "Delete selected rows");
            putValue(SHORT_DESCRIPTION, "Delete selected rows");
            table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    setEnabled(getTable().getSelectedRowCount() > 0);
                }
            });
            setEnabled(getTable().getSelectedRowCount() > 0);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("...");
            JTable table = getTable();
            if (table.getSelectedRowCount() > 0) {
                List<Vector> selectedRows = new ArrayList<>(25);
                DefaultTableModel model = getModel();
                Vector rowData = model.getDataVector();
                for (int row : table.getSelectedRows()) {
                    int modelRow = table.convertRowIndexToModel(row);
                    Vector rowValue = (Vector) rowData.get(modelRow);
                    selectedRows.add(rowValue);
                }

                for (Vector rowValue : selectedRows) {
                    int rowIndex = rowData.indexOf(rowValue);
                    model.removeRow(rowIndex);
                }
            }
        }

    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
