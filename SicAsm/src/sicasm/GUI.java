/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sicasm;

/**
 *
 * @author user
 */
//import com.sun.javafx.css.Rule;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;
import javax.swing.Box;
import javax.swing.ImageIcon;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class GUI extends JFrame {

	JButton LisFile = new JButton("List File");
	JButton ObjFile = new JButton("Object File");  
	JButton Run = new JButton("Run Assembler");
	JButton Clear = new JButton("Clear");
	JButton Save = new JButton("Save");
	JButton Load= new JButton("Load");
        //private Rule columnView;
        //private Rule rowView;
	JTextArea input = new JTextArea(30,70);
	JScrollPane scroll ;//= new JScrollPane(input);//,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	JFileChooser chooser = new JFileChooser();
	JPanel p= new JPanel();
	JPanel west = new JPanel();
	JPanel south = new JPanel();
	JLabel status = new JLabel();
        JFrame frame = new JFrame();
	
		
	// constructor
	GUI()
	{
		super("Assember Application");
		setSize(900,900);
                scroll = new JScrollPane(input);
                scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		setResizable(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
                setLayout(new BorderLayout());
                setContentPane(new JLabel(new ImageIcon("Boston-City.jpg")));
                //p.setLayout(new FlowLayout());
                setLayout (new FlowLayout());
                p.setSize(200,200);
		input.setEditable(true);
		input.setLineWrap(true);
		input.setWrapStyleWord(true);
                input.setFont(new Font("monospaced", Font.PLAIN, 12));
		LisFile.addActionListener(new ActionListener() {
			 
            public void actionPerformed(ActionEvent e)
            {
                System.out.println("You clicked the button");
            }
        }); 
		ObjFile.addActionListener(new ActionListener() {
			 
            public void actionPerformed(ActionEvent e)
            {
                System.out.println("You clicked the button");
            }
        });   
		Run.addActionListener(new ActionListener() {
			 
            public void actionPerformed(ActionEvent e)
            {
                try {
                    new ObjectFile(Saving(), true);
                } catch (Exception ex) {
                    System.out.println("Something wrong happened!\n" + ex.getMessage());
                }
            }
        });  
		Clear.addActionListener(new ActionListener() {
			 
            public void actionPerformed(ActionEvent e)
            {
               input.setText(null);
            }
        });  
		Save.addActionListener(new ActionListener() {
			 
            public void actionPerformed(ActionEvent e)
            {
               Saving();
            }
        });  
		Load.addActionListener(new ActionListener() {
			 
            public void actionPerformed(ActionEvent e)
            {
               Loading();
            }
        });  
		//p.add(input);
		p.add(scroll);
               // p.add( Box.createVerticalStrut(400) );
		west.setLayout(new GridLayout(6,1));
		west.add(Clear);
		west.add(Save);
		west.add(Load);
		west.add(LisFile);
		west.add(ObjFile);
		west.add(Run);
		status.setText("Waiting");
		south.add(status);
		add(west,BorderLayout.WEST);
		add(south,BorderLayout.SOUTH);
		add(p,BorderLayout.CENTER);
		setVisible(true);
                //frame.setVisible(true);
		
	}
	public void Loading(){
		int chooserValue = chooser.showOpenDialog(this);
		if(chooserValue == chooser.APPROVE_OPTION){
			try{
				Scanner in = new Scanner(chooser.getSelectedFile());
				String Buffer ="";
				while(in.hasNextLine()){
					Buffer += in.nextLine() + "\n" ;
				}
				input.setText(Buffer);
				status.setText("Loaded Successful");
			}
			catch (FileNotFoundException ex)
			{
				JOptionPane.showMessageDialog(this,"File");
			}
		}
	}
	public String Saving(){
		int chooserValue = chooser.showSaveDialog(this);
		if(chooserValue == chooser.APPROVE_OPTION){
			try{
                            File selectedfile =chooser.getSelectedFile();
			PrintWriter writer = new PrintWriter(chooser.getSelectedFile());
			String s = input.getText();
			String[] lines = s.split("\\n");
			for(int i=0;i<lines.length;i++)
			writer.println(lines[i]);
			writer.close();
                            status.setText("File saved in " + selectedfile.getAbsolutePath());
                        return selectedfile.getAbsolutePath();
			}
			catch(FileNotFoundException ex){
                            status.setText("Error saving File");
			}
			
		}
		return null ;
	}
}
