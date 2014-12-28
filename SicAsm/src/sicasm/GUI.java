
package sicasm;

/**
 *
 * @author HackerGhost
 */
//import com.sun.javafx.css.Rule;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.Stack;
import javax.swing.ImageIcon;

import javax.swing.JButton;
import javax.swing.text.DefaultHighlighter;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;

public class GUI extends JFrame {
        private String RunningAbsolutePath;
        Stack <String> undoStack = new Stack();
        Stack <String> redoStack = new Stack();
        private String Finder;
        private String LastAction;
        private String absolutePath ;
        private boolean SpaceChecker = false ;
        private boolean helperBoolean;
        private boolean findControl = false;
        private boolean delay = false;;
        Highlighter.HighlightPainter mySelector = new MyPainter(Color.BLUE);
        Highlighter.HighlightPainter myUnSelector = new MyPainter(Color.WHITE);
	JButton LisFile = new JButton("List File");
	JButton ObjFile = new JButton("Object File");  
	JButton Run = new JButton("Run Assembler");
	JButton Clear = new JButton("Clear");
	JButton Save = new JButton("Save");
	JButton Load= new JButton("Load");
        JButton SaveAs = new JButton("Save As");
        JButton Help = new JButton("Help");
        String helping  = "Ctrl S -> Save\nCtrl O ->Open\nCtrl Shift S -> Save as\n"
                + "Ctrl L -> Listfile\nCtrl J -> ObjFile\nCtrl R -> Run\n"
                + "Ctrl F -> Find";
	JTextArea input = new JTextArea(30,70);
	JScrollPane scroll ;
	JFileChooser chooser = new JFileChooser();
        JTextArea helper = new JTextArea(helping);
	JPanel p= new JPanel();
	JPanel west = new JPanel();
	JPanel south = new JPanel();
	JLabel status = new JLabel();
        JPanel components = new JPanel();
        JFrame frame = new JFrame();
	
		
	// constructor
	GUI()
	{
		super("Assember Application");
                setSize(900,600);
		//frame.setSize(900,600);
                scroll = new JScrollPane(input);
                scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		setResizable(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
                // la3abt hena 
                
                //p.setLayout(new FlowLayout());
               // p.setLayout(new BorderLayout());
                setContentPane(new JLabel(new ImageIcon("Boston-City.jpg")));
                //p.setLayout(new FlowLayout());
                setLayout (new FlowLayout());
                //p.setSize(200,200);
                //components.setSize(200,200);
		input.setEditable(true);
		input.setLineWrap(true);
		input.setWrapStyleWord(true);
                input.setFont(new Font("monospaced", Font.PLAIN, 12));
                this.requestFocusInWindow();
                input.addKeyListener(new KeyListener() {

                    @Override
                    public void keyTyped(KeyEvent e) {
                        
                    }
                    @Override
                    public void keyPressed(KeyEvent e) {
                        
                        input.requestFocus();
                        // lesa msh sha3'ala garab el control shift
                         if((e.getKeyCode() == KeyEvent.VK_S)  &&  
                                 e.isControlDown() && e.isShiftDown() ){
                             Saving();
                             
                         }
                         else if ((e.getKeyCode() == KeyEvent.VK_S) && 
                                 ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)){
                             Save();
                         }
                         else if((e.getKeyCode() == KeyEvent.VK_O) && 
                                 ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)){
                             Loading();
                         }
                         else if((e.getKeyCode() == KeyEvent.VK_L) && 
                                 ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)){
                             lis();
                         }
                         else if((e.getKeyCode() == KeyEvent.VK_J) && 
                                 ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)){
                             obj();
                         }
                         else if((e.getKeyCode() == KeyEvent.VK_H) && 
                                 ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)){
                             help();
                         }
                         else if((e.getKeyCode() == KeyEvent.VK_C) && 
                                 ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)){
                             clearing();
                         }
                         else if((e.getKeyCode() == KeyEvent.VK_R) && 
                                 ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)){
                             Running();
                         }
                         else if((e.getKeyCode() == KeyEvent.VK_F) && 
                                 ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)){
                             Find();
                         }
                         else if((e.getKeyCode() == KeyEvent.VK_DELETE) || 
                                 (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) ||
                                 (e.getKeyCode() == KeyEvent.VK_ENTER) ||
                                 e.getKeyCode() == KeyEvent.VK_SPACE){
                             if(SpaceChecker == false)makeAction();
                             SpaceChecker= true ;
                         }
                         else if((e.getKeyCode() == KeyEvent.VK_Z) && e.isControlDown()){
                             undoAction();
                         }
                         else if((e.getKeyCode() == KeyEvent.VK_Z) && e.isControlDown() && e.isShiftDown()){
                             redoAction();
                         }
                         else {
                             LastAction = input.getText();
                             SpaceChecker = false ;
                             status.setText("Think before you code .. Coders don't need luck ");
                         }
                        if(findControl == true || delay == true)
                         unFind();
                    } 
                    @Override
                    public void keyReleased(KeyEvent e) {
                       
                    }
                } 
                );              
                this.addMouseListener(MouseList);
                input.requestFocus();
                input.addMouseListener(MouseList);
		LisFile.addActionListener(new ActionListener() {
			 
            public void actionPerformed(ActionEvent e)
            {
                  list();
            }
        }); 
		ObjFile.addActionListener(new ActionListener() {
			 
            public void actionPerformed(ActionEvent e)
            {
                obj();
            }
        });  
		Run.addActionListener(new ActionListener() {
			 
            public void actionPerformed(ActionEvent e)
            {
                Running();
            }
        });  
		Clear.addActionListener(new ActionListener() {
			 
            public void actionPerformed(ActionEvent e)
            {
                clearing();
            }
        });  
		Save.addActionListener(new ActionListener() {
			 
            public void actionPerformed(ActionEvent e)
            {
               absolutePath=Saving();
            }
        });
                SaveAs.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Saving();
                    }
                });
		Load.addActionListener(new ActionListener() {
			 
            public void actionPerformed(ActionEvent e)
            {
               Loading();
            }
        }); 
                Help.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        help();
                    }
                });
		//p.add(input);
                this.helperBoolean = true ;
                //p.add( Box.createVerticalStrut(400) );
		//p.add(scroll);
		west.setLayout(new GridLayout(8,1));
		west.add(Clear);
		west.add(Load);
		west.add(Save);
                west.add(SaveAs);
		west.add(Run);
		west.add(LisFile);
		west.add(ObjFile);
                west.add(Help);
		south.add(status);
		status.setText("Waiting");
                components.setBackground(new Color(0,0,0,50));
                //components.set
                components.setLayout(new BorderLayout());
                //components.setPreferredSize(new Dimension(this.getWidth()-200,this.getHeight()-200));
		components.add(west,BorderLayout.WEST);
                components.add(south,BorderLayout.SOUTH);
                components.add(scroll,BorderLayout.CENTER);
		//add(south,BorderLayout.SOUTH);
              //  p.add(components);
               // add(p);
                add(helper);
                add(components);
		setVisible(true);
                //frame.setVisible(true);
	}
	private void makeAction(){
            undoStack.push(LastAction);
        }
        private void undoAction(){
            redoStack.push(input.getText());
            input.setText(undoStack.pop());
        }
        private void redoAction(){
            input.setText(redoStack.pop());
        }
        private void Find(){
            Finder = JOptionPane.showInputDialog("Enter the Text you w"
                    + "ant to find");
            HighLight(input, Finder , false);
        }
        private void unFind(){
            HighLight(input, Finder ,true);
        }
        private String Save(){
            if(absolutePath == null)
                return Saving();
            else
            {
                try{
                            File selectedfile =new File(absolutePath);
                            if(selectedfile.getAbsolutePath() != null){
			PrintWriter writer = new PrintWriter(chooser.getSelectedFile());
			String s = input.getText();
			String[] lines = s.split("\\n");
			for(int i=0;i<lines.length;i++)
			writer.println(lines[i]);
			writer.close();
                            status.setText("File saved in " + selectedfile.getAbsolutePath());
                        return selectedfile.getAbsolutePath();
                            }
                            else status.setText("Nothing was saved");
			}
			catch(FileNotFoundException ex){
                            status.setText("Error saving File");
			}
                return null ;
            }
        }
        
        
        private void help(){
            if(helperBoolean){
                this.helperBoolean = false;
                helper.setVisible(helperBoolean);
            }
            else{
                this.helperBoolean= true;
                helper.setVisible(helperBoolean);
            }
            
        }
        private void Loading(){
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
                            status.setText("Couldn't be loaded");
			}
		}
	}
        
        private void Running(){
            try {
                   
                    new ObjectFile(absolutePath=Saving(), true);
                    RunningAbsolutePath = absolutePath;
                    status.setText("Assembled in " + absolutePath);
                } catch (Exception ex) {
                   status.setText("Something went wrong");
                }
        }
	private String Saving(){
		int chooserValue = chooser.showSaveDialog(this);
		if(chooserValue == chooser.APPROVE_OPTION){
			try{
                            File selectedfile =chooser.getSelectedFile();
                            if(selectedfile.getAbsolutePath() != null){
                        absolutePath = selectedfile.getAbsolutePath();
			PrintWriter writer = new PrintWriter(chooser.getSelectedFile());
			String s = input.getText();
			String[] lines = s.split("\\n");
			for(int i=0;i<lines.length;i++)
			writer.println(lines[i]);
			writer.close();
                            status.setText("File saved in " + selectedfile.getAbsolutePath());
                        return selectedfile.getAbsolutePath();
                            }
                            else status.setText("Nothing was saved");
			}
			catch(FileNotFoundException ex){
                            status.setText("Error saving File");
			}
			
		}
		return null ;
	}
        
        private void lis(){
            if(RunningAbsolutePath != null){
             Runtime rt=Runtime.getRuntime();

        //String file="E:\\Studies\\ProgII\\Lab\\Notepad\\LISFILE.txt";

                try {
                    Process p=rt.exec("notepad "+ absolutePath); 
                    // a space is required after notepad
                } catch (IOException ex) {
                   status.setText("Can't be opened ");
                }
            }
            else  status.setText("Nothing to be opened");
        }
         
        private void clearing(){
            int dialogResult = JOptionPane.showConfirmDialog (frame, "Would You "
                    + "Like to Save your Previous Code First?",
                         "Warning",JOptionPane.YES_NO_OPTION);
                if(dialogResult == JOptionPane.YES_OPTION){
                    absolutePath=Saving();
                if(absolutePath != null) status.setText("Cleared & saved in " 
                        + absolutePath);
                else status.setText("Cleared but not saved");
                input.setText(null);
                }
                else {
                    status.setText(" Command aboarded ");
                }
        }
        
        private void obj(){
            if(RunningAbsolutePath != null){
             Runtime rt=Runtime.getRuntime();
                try {
                    Process p=rt.exec("notepad "+ absolutePath); 
                    status.setText("Opened");
                } catch (IOException ex) {
                   status.setText("Can't be opened");
                }
            }
            else status.setText("Nothing Has been runned");
        }
        
        
        public String checker(){
            StringBuilder str = new StringBuilder();
            String temp ;
            temp = input.getText();
            for( int i=temp.length()-1;  i>=0 &&  (temp.charAt(i)) != 32 ; i-- )
            {
                str.append(temp.charAt(i));
            }
            str.reverse();
            System.out.println(str.toString());
            
            if(Constants.OpTable.containsKey(str.toString())){
                
            }
            
            return str.toString();
        }
        private class MyPainter extends DefaultHighlighter.DefaultHighlightPainter{
            public MyPainter(Color SelectorColor){ 
            super(SelectorColor);
            }
        }
        
        private void HighLight(JTextComponent MyArea,String Finder,boolean unfinder){
            if(findControl == false && unfinder == false){
                try{
                    Highlighter Lite =  MyArea.getHighlighter();
                    Document doc = MyArea.getDocument();
                    String Text = doc.getText(0,doc.getLength());
                    int position= 0;
                    while((position=Text.toUpperCase().indexOf(Finder.toUpperCase(),position))>=0){
                        Lite.addHighlight(position, position+Finder.length(), mySelector);
                        position += Finder.length();
                    }
                    findControl = true ;
                    delay = true;
                }
                catch(Exception e){
                    status.setText("Something went wrong with the finder");
                }
            }
            else if(findControl == true && delay == true)
            {
               // Finder = null ;
                delay=false;
            }
            else{
                try{
                    Highlighter Lite =  MyArea.getHighlighter();
                    Document doc = MyArea.getDocument();
                    String Text = doc.getText(0,doc.getLength());
                    int position= 0;
                    System.out.println("removed");
                    //while((position=Text.toUpperCase().indexOf(Finder.toUpperCase(),position))>=0){
                        Lite.removeAllHighlights();//Highlight(position, position+Finder.length(), myUnSelector);
                        //position += Finder.length();
                    //}
                findControl = false ;
                }
                catch(Exception e){
                    status.setText("Something went wrong with the finder");
                }
            }
            
        }
        
        MouseListener MouseList = new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(findControl == true || Finder != null)
                   unFind();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if(findControl == true || Finder != null)
                    unFind();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
               if(findControl == true || Finder!=null)
                    unFind();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
               if(findControl == true || Finder!=null)
                    unFind();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if(findControl == true || Finder!=null)
                    unFind();
            }
        };
}
