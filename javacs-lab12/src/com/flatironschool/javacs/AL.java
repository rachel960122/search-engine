package com.flatironschool.javacs;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AL extends Frame implements WindowListener,ActionListener {
    TextField inputTextbox = new TextField(20);
    TextArea outputTextbox =new TextArea(80,80);
    Button b;
    QueryParser parser;


    public static void main(String[] args) {
        AL myWindow = new AL("Search");
        myWindow.setSize(900,900);
        myWindow.setVisible(true);
    }

    public AL(String title) {

        super(title);
        setLayout(new FlowLayout());
        addWindowListener(this);
        b = new Button("Search...");
        add(inputTextbox);
        add(b);
        add(outputTextbox);

        b.addActionListener(this);
        parser = new QueryParser();
    }

    public String processQuery(List<String> listOfString) {
        String outputString = "";
        for(String a :listOfString) {
            outputString = outputString + "\n" + a;
        }
        return outputString;
    }


    public void actionPerformed(ActionEvent e) {
        String userInput = inputTextbox.getText();
        Map<String, List<String>> parsedInput = parser.processArgument(userInput);
        outputTextbox.setText(parsedInput.toString());
    }

    public void textSeter(String textInput){
       outputTextbox.setText(textInput);
       outputTextbox.setText(textInput+"done");
    }

    public void windowClosing(WindowEvent e) {
        dispose();
        System.exit(0);
    }

    public void windowOpened(WindowEvent e) {}
    public void windowActivated(WindowEvent e) {}
    public void windowIconified(WindowEvent e) {}
    public void windowDeiconified(WindowEvent e) {}
    public void windowDeactivated(WindowEvent e) {}
    public void windowClosed(WindowEvent e) {}

}
