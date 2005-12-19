package com._17od.upm.invest;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class TestSpringLayout {

    private static final String applicationName = "Test Application";
    private static JFrame mainFrame;

    
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    createAndShowGUI();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
    
    private static void createAndShowGUI() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
        //Use the System look and feel
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        //Create and set up the window.
        mainFrame = new JFrame(applicationName);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Set up the content pane.
        addComponentsToPane(mainFrame);

        //Display the window.
        mainFrame.pack();
        mainFrame.setVisible(true);
    } 
 
    
    private static void addComponentsToPane(JFrame mainframe) {

        Container pane = mainFrame.getContentPane();
            
        //Ensure the layout manager is a BorderLayout
        if (!(pane.getLayout() instanceof BorderLayout)) {
            pane.setLayout(new BorderLayout());
        }

        //Create a panel to contain the search dialog and accounts listview
        JPanel centralPanel = new JPanel();
        SpringLayout layout = new SpringLayout();
        centralPanel.setLayout(layout);
        centralPanel.setBackground(Color.BLUE);
        
        //The search field
        JTextField searchField = new JTextField("Search", 20);
        centralPanel.add(searchField);

        //The Accounts listview
        JList accounts = new JList();
        accounts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        accounts.setSelectedIndex(0);
        //accounts.setVisibleRowCount(10);
        populateListView(accounts);
        JScrollPane accountsScrollList = new JScrollPane(accounts, 
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        centralPanel.add(accountsScrollList);
        
        layout.putConstraint(SpringLayout.NORTH, searchField, 5, SpringLayout.NORTH, centralPanel);
        layout.putConstraint(SpringLayout.NORTH, accountsScrollList, 5, SpringLayout.SOUTH, searchField);
        layout.putConstraint(SpringLayout.EAST, accountsScrollList, 0, SpringLayout.EAST, centralPanel);
        layout.putConstraint(SpringLayout.WEST, accountsScrollList, 0, SpringLayout.WEST, centralPanel);
        //layout.putConstraint(SpringLayout.SOUTH, accountsScrollList, 0, SpringLayout.SOUTH, centralPanel);

        pane.add(centralPanel, BorderLayout.CENTER);
       
    }
 
    
    private static void populateListView(JList accounts) {
        DefaultListModel listModel = new DefaultListModel();
        
        for (int i=0; i<100; i++) {
            listModel.addElement("Account " + i);
        }

        accounts.setModel(listModel);
        //TODO Add impl
    }

}
