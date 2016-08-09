package com.flatironschool.javacs;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*; 
import java.awt.event.*;
public class SearchInterface extends Frame implements ActionListener {
	  private Label lblCount;    // Declare a Label component
	  private Label statusLabel;
	  private int width = 1000;
	  private int height = 100;
	 
	   //private TextField tfCount; // Declare a TextField component 
	  // private Button btnCount;   // Declare a Button component
	 //  private int count = 0;     // Counter's value
	   // Constructor to setup the GUI components
	   public SearchInterface() {
		   statusLabel = new Label();
//		   statusLabel.setAlignment(Label.CENTER);
//		   statusLabel.setSize(950,300);
	   }
	   public String Myprogram(){
		   
		   setLayout(new FlowLayout());
		   add(statusLabel);
	      lblCount = new Label("Key words:");  // construct the Label component
	     
	      add(lblCount);                    // "super" Frame adds Label
	      final TextField userText = new TextField(40);
	      setTitle("Search...");  // "super" Frame sets its title
	      setSize(width, height);        // "super" Frame sets its initial window size
	 
	      Button loginButton = new Button("Start Searching!");
	      loginButton.addActionListener(new ActionListener() {
	          public void actionPerformed(ActionEvent e) {     
	          System.out.print(userText.getText());
	          statusLabel.setText(userText.getText()); 
	          }
	          
	       }); 
	     
	      add(userText);
	      add(loginButton);
	      
	      setVisible(true); 
	     
		   return userText.getText();
	   }
	   public void outPut(String s){
		   Frame mainFrame = new Frame("Java AWT Examples");
		   mainFrame.setSize(400,400);
		   mainFrame.setLayout(new GridLayout(3, 1));
		  Label stringLabel2 = new Label("----------------------------------------------------------------------------------------------");
		  TextField output = new TextField(s,40);
		  
		  output.setEditable(false);
		  mainFrame.add(stringLabel2);
		  mainFrame.add(output);
		  setVisible(true); 
	   }
	 
	   public static void main(String[] args) {
		   SearchInterface a = new SearchInterface();
		  String m = a.Myprogram();
		   //System.out.print(m);
		  a.outPut("test");
		   
		   
	      
	   }
	@Override
	public void actionPerformed(ActionEvent e) {
		setTitle("Done...");
		
	}
	}