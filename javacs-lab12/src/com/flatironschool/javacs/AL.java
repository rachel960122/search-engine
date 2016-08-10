package com.flatironschool.javacs;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

import java.io.IOException;

public class AL extends Frame implements WindowListener,ActionListener {
    TextField inputTextbox = new TextField(20);
    TextArea outputTextbox =new TextArea(80,80);
    Button b;
    Button c;
    QueryParser parser;
    WikiSearch searcher;


    public static void main(String[] args) throws IOException {
        AL myWindow = new AL("Search");
        myWindow.setSize(900,900);
        myWindow.setVisible(true);
    }

    public AL(String title) throws IOException {

        super(title);
        setLayout(new FlowLayout());
        addWindowListener(this);
        b = new Button("Search...");
        c = new Button("I Feel Lucky!");
        add(inputTextbox);
        add(c);
        add(b);
        add(outputTextbox);

        b.addActionListener(this);
         c.addActionListener(new ActionListener(){

                    public void actionPerformed(ActionEvent e){

                     try {

                        inputTextbox.setText(RandomWord.randomWords());

            } catch (FileNotFoundException e1) {


        e1.printStackTrace();

            }

                   }

                });
        parser = new QueryParser();
        searcher = new WikiSearch();
    }

    public String processResult(List<String> listOfString) {
        String outputString = "";
        String hr = "-----------------------------------------------";
        int count = 0;
        if (listOfString.size() == 0) {
            outputString += "No results found ╯' - ')╯";
        } else {
            outputString += "About " + listOfString.size() + " results\n";
        }
        for (String a :listOfString) {
            if (count > 200) break;
            String[] fileContents = WikiFetcher.getFileContent(a);
            outputString = outputString + "\n" + fileContents[0] + "\n" + a + "\n" + hr + "\n" + fileContents[1] + "\n\n";
            count++;
        }
        System.out.println("printed out " + count + " results");
        return outputString;
    }


    public void actionPerformed(ActionEvent e) {
        String userInput = inputTextbox.getText();
        Map<String, List<String>> parsedInput = parser.processArgument(userInput);
        List<String> results = searcher.search(parsedInput);
        outputTextbox.setText(processResult(results));
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
