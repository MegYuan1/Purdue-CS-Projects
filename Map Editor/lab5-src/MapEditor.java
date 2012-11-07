/*Project:CS251 Lab5
 * 
 *Objective:The editor will read a bitmap that represents the map. 
 *  The editor will open the picture of the map and with the mouse we will be abe to add/delete/modify 
 *  the vertices and edges in the map. The vertices will represent 
 *  locations and the edges will represent paths from one location to another. 
 *  The graph will be stored in an XML file that can be modified by the map editor.
 * 
 *Name: Lirong Yuan
 *Purdue Account: yuan27
 *Email: yuan27@purdue.edu
 * 
 * */
import javax.swing.*; 
import javax.swing.event.*; 
import javax.swing.text.*; 
import javax.swing.border.*; 
import javax.swing.colorchooser.*; 
import javax.swing.filechooser.*; 
import javax.accessibility.*; 
import javax.imageio.*;

import java.awt.*; 
import java.awt.image.*; 
import java.awt.event.*; 
import java.beans.*; 
import java.util.*; 
import java.io.*; 
import java.applet.*; 
import java.net.*;
import java.lang.Double;

public class MapEditor extends JFrame implements ActionListener{
  // The preferred size of the editor
  private final int PREFERRED_WIDTH = 800;
  private final int PREFERRED_HEIGHT = 600;
  
  //map zone
  private ZoomPane _zoomPane;
  private MapScene _map;
  //info zone
  private JScrollPane _text;
  private JTextArea _textarea;
  
  /*Edit Panel*/
  private JMenuBar editor=new JMenuBar();
  private JMenu file=new JMenu("File");
  private JMenu mode=new JMenu("Mode");
  private JMenu function=new JMenu("Function");
  private JMenu help=new JMenu("Help");
  private JMenuItem fileinfo=new JMenuItem("Current File Infomation");
  private JMenuItem save=new JMenuItem("Save");
  private JMenuItem saveas=new JMenuItem("Save As");
  private JMenuItem openfile=new JMenuItem("Open File");
  private JMenuItem newfile=new JMenuItem("New File");
  private JMenuItem insertloc=new JMenuItem("Insert Location");
  private JMenuItem deleteloc=new JMenuItem("Delete Location");
  private JMenuItem insertpath=new JMenuItem("Insert Path");
  private JMenuItem deletepath=new JMenuItem("Delete Path");
  private JMenuItem location=new JMenuItem("Location Property");
  private JMenuItem find=new JMenuItem("Find");
  private JMenuItem directions=new JMenuItem("Directions");
  private JMenuItem readme=new JMenuItem("Readme");
  
  
  public static void main(String[] args) { 
    MapEditor mapEditor = new MapEditor(); 
    mapEditor.setVisible(true);
  }  
  
  public MapEditor() {
    //set frame properties
    setTitle("Map Editor");
    setSize(PREFERRED_WIDTH, PREFERRED_HEIGHT);
    setBackground(Color.gray);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// Close when closed.For reals.
    
    //set the content of the frame
    try{
      File file=new File("purdue-map.jpg");
      Image image = (Image)ImageIO.read(file);
      _map = new MapScene(image);
    }catch(Exception e){
      System.out.println(e.getMessage());
    }
    
    _zoomPane = new ZoomPane(_map);
    _textarea=new JTextArea("Welcome to Map Editor!", 20, 20);
    _textarea.setEditable(false);
    _text=new JScrollPane();
    _text.getViewport().add(_textarea);
    
    //add components to frame
    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(_text,BorderLayout.WEST);
    getContentPane().add(_zoomPane,BorderLayout.CENTER);
    getContentPane().add(_zoomPane.getJSlider(),BorderLayout.SOUTH);
    
    //add actionlistener to the menuitems
    save.addActionListener(this);
    saveas.addActionListener(this);
    openfile.addActionListener(this);
    newfile.addActionListener(this);
    fileinfo.addActionListener(this);
    insertloc.addActionListener(this);
    deleteloc.addActionListener(this);
    insertpath.addActionListener(this);
    deletepath.addActionListener(this);
    location.addActionListener(this);
    find.addActionListener(this);
    directions.addActionListener(this);
    readme.addActionListener(this);
    
    //add menuitems to menus
    file.add(newfile);
    file.add(openfile);
    file.add(save);
    file.add(saveas);
    file.add(fileinfo);
    mode.add(insertloc);
    mode.add(deleteloc);
    mode.add(insertpath);
    mode.add(deletepath);
    mode.add(location);
    function.add(find);
    function.add(directions);
    help.add(readme);
    editor.add(file);
    editor.add(mode);
    editor.add(function);
    editor.add(help);
    
    //set menu bar of the frame
    setJMenuBar(editor);
    
    //MouseAdapter & MouseMotionAdapter for _zoomPane
    MouseAdapter listener = new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        Point point = _zoomPane.toViewCoordinates(e.getPoint());
        if(point.getX()>_map.getWidth() || point.getY()>_map.getHeight()){
          return;
        }
        _map.mousePressed(point);
      }
      public void mouseClicked(MouseEvent e){
        //System.out.println("mouseclicked");
        int times=e.getClickCount();
        Point point = _zoomPane.toViewCoordinates(e.getPoint());
        if(point.getX()>_map.getWidth() || point.getY()>_map.getHeight()){
          return;
        }
        if(times==1){
          _map.mouseSingleClicked(point);
        }
        if(times==2){
          _map.mouseDoubleClicked(point);
        }
      }     
    };
    
    MouseMotionAdapter motionListener = new MouseMotionAdapter() {
      public void mouseDragged(MouseEvent e) {
        Point point = _zoomPane.toViewCoordinates(e.getPoint());
        if(point.getX()>_map.getWidth() || point.getY()>_map.getHeight()){
          return;
        }
        _map.mouseDragged(point);
      }
    };
    
    //add MouseAdapter & MouseMotionAdapter to _zoomPane
    _zoomPane.getZoomPanel().addMouseListener(listener);
    _zoomPane.getZoomPanel().addMouseMotionListener(motionListener);
  }
  
  public void actionPerformed(ActionEvent e){
    Object obj=e.getSource();  
    if(obj==save){
      if(_map.getName().length()==0){
        String s = JOptionPane.showInputDialog("Please enter the name for the xml file: ");
        if(s==null){
          return;
        }
        while(s.length()==0){
          s = JOptionPane.showInputDialog("Please enter a valid name!\nPlease enter the name for the file: ");
          if(s==null){
            return; 
          }
        }
        _map.setName(s);
      }
      if(_map.mygraph.getScale()==0.0){
        String feet = JOptionPane.showInputDialog("Please enter the feet-per-pixel for the file: ");
        if(feet==null){
          return;
        }             
        while(feet.length()==0){
          try{
            double num=Double.parseDouble(feet);
            break;
          }catch(Exception e1){
            feet = JOptionPane.showInputDialog("Please enter a valid number!\nPlease enter the feet-per-pixel for the file: ");
            if(feet==null){
              return; 
            }
          }
        }
        _map.mygraph.setScale(Double.parseDouble(feet));
      }
      _map.printOut();
      String display="File Saved.";
      _textarea.setText(display);
    }else if(obj==saveas){
      //save as a file DONE
      String s = JOptionPane.showInputDialog("Please enter the name for the xml file: ");
      if(s==null){
        return; 
      }
      while(s.length()==0){
        s = JOptionPane.showInputDialog("Please enter a valid name!\nPlease enter the name for the file: ");
        if(s==null){
          return; 
        }
      }
      if(_map.mygraph.getScale()==0.0){
        String feet = JOptionPane.showInputDialog("Please enter the feet-per-pixel for the file: ");
        if(feet==null){
          return; 
        }
        while(true){
          try{
            double num=Double.parseDouble(feet);
            break;
          }catch(Exception e1){
            feet = JOptionPane.showInputDialog("Please enter a valid number!\nPlease enter the feet-per-pixel for the file: ");
            if(feet==null){
              return; 
            }
          }
        }
        _map.mygraph.setScale(Double.parseDouble(feet)); 
      }
      
      _map.setName(s);
      _map.printOut();
      String display="File Saved As "+s+".";
      _textarea.setText(display);
    }else if(obj==openfile){
      //openfile DONE
      /*String s = JOptionPane.showInputDialog("Open xml File: ");
      if(s==null){
        return; 
      }*/
      
      JFileChooser fc=new JFileChooser(System.getProperty("user.dir"));
      fc.setDialogTitle("Please choose map xml file");
      fc.setDragEnabled(false);
      File file;
      int val;
      String s;
      val=fc.showOpenDialog(this);
      if(val==JFileChooser.CANCEL_OPTION || val==JFileChooser.ERROR_OPTION){
        return;
      }
      file = fc.getSelectedFile();
      s=file.getName();
      while(true){
        try{
          _map.mygraph=new Graph(s);
          _map.setName(s);
          _map.setImage(_map.mygraph.getImageName());
          break;
        }catch(Exception e1){
          val=fc.showOpenDialog(this);
          if(val==JFileChooser.CANCEL_OPTION || val==JFileChooser.ERROR_OPTION){
            return;
          }
          file = fc.getSelectedFile();
          s=file.getName();
        }
      }
      _map.changeNotify();
      String display="File - "+s+" Opened.";
      _textarea.setText(display);
    }else if(obj==newfile){
      //get the name for the image file
      JFileChooser fc=new JFileChooser(System.getProperty("user.dir"));
      fc.setDialogTitle("Please choose Image File");
      fc.setDragEnabled(false);
      int val;
      File file;
      String imgnm;
      BufferedImage img;
      while(true){
        try{
          val=fc.showOpenDialog(this);
          if(val==JFileChooser.CANCEL_OPTION || val==JFileChooser.ERROR_OPTION){
            return;
          }
          file = fc.getSelectedFile();
          imgnm=file.getName();
          img=ImageIO.read(file);
          if(img==null){
            continue;
          }
          break;
        }catch(Exception e1){
        }
      }
      
      //get the name for the new file  
      String s=JOptionPane.showInputDialog("Please enter the name for new xml file: ");
      if(s==null){
        return; 
      }
      while(s.length()==0){
        s = JOptionPane.showInputDialog("Please enter a valid name!\nPlease enter the name for the xml file: ");
        if(s==null){
          return; 
        }
      }
      //get the feet per pixel constant
      String feet= JOptionPane.showInputDialog("Please enter feet per pixel: ");
      if(feet==null){
        return; 
      }
      while(true){
        try{
          double num=Double.parseDouble(feet);
          break;
        }catch(Exception e1){
          feet = JOptionPane.showInputDialog("Please enter a valid number!\nPlease enter feet per pixel: ");
          if(feet==null){
            return; 
          }   
        }
      }
      _map.mygraph=new Graph();
      _map.mygraph.setImageName(imgnm);
      _map.setImage(imgnm);
      _map.setName(s);
      _map.setScaleFeet(Double.parseDouble(feet));
      _map.changeNotify();
      String display="New File - "+s+" Created.";
      _textarea.setText(display); 
    }else if(obj==insertloc){
      _map.setMode(_map.INSERTLOC);
      setTitle("Map Editor(Insert Location Mode)");
      String display="Insert Location Mode:\n\n";
      display+=("Legal Operations:\n"+
        "1.Single Click: add a point\n or select the current location\n"+
        "2.Double Click: open a dialog that\n shows the property of the location\n"+
        "3.Click and Drag: change position\n of a point\n");
      _textarea.setText(display); 
    }else if(obj==deleteloc){
      _map.setMode(_map.DELETELOC);
      setTitle("Map Editor(Delete Location Mode)");
      String display="Delete Location Mode:\n\n";
      display+=("Legal Operations:\n"+
        "1.Single Click: delete a point\n and paths of the point\n"+
        "2.Double Click: open a dialog that\n shows the property of the location\n");
      _textarea.setText(display); 
    }else if(obj==insertpath){
      _map.setMode(_map.INSERTPATH);
      setTitle("Map Editor(Insert Path Mode)");
      String display="Insert Path Mode:\n\n";
      display+=("Legal Operations:\n"+
        "1.Click and Drag: add a path from\n start location to end location\n"+
        "2.Double Click: open a dialog that\n shows the property of the location\n");
      _textarea.setText(display); 
    }else if(obj==deletepath){
      _map.setMode(_map.DELETEPATH);
      setTitle("Map Editor(Delete Path Mode)");
      String display="Delete Path Mode:\n\n";
      display+=("Legal Operations:\n"+
        "1.Click and Drag: delete a path from\n start location to end location\n"+
        "2.Double Click: open a dialog that\n shows the property of the location\n");
      _textarea.setText(display); 
    }else if(obj==location){
      _map.setMode(_map.PROPERTY);
      setTitle("Map Editor(Location Properties Mode)");
      String display="Location Properties Mode:\n\n";
      display+=("Legal Operations:\n"+
        "1.Single Click: open a dialog that\n shows the property of the location\n"+
        "2.Double Click: open a dialog that\n shows the property of the location\n");
      _textarea.setText(display); 
    }else if(obj==find){
      _map.setMode(_map.FIND);
      setTitle("Map Editor(Find Mode)");
      int index=getIndex();
      if(index==-1){
        return;     
      }
      //display it on the textarea
      String display="Find Mode:\n\nLocation Property:\nName:"+_map.mygraph.locations[index].getName()+"\n(x,y)=("
          +_map.mygraph.locations[index].getPoint().getX()+","
          +_map.mygraph.locations[index].getPoint().getY()
          +")\nID="+_map.mygraph.locations[index].getID()+"\n\n";
      display+="If the point is not in the center of\n"+ 
        "the scrollpane,\n"+"that is because it is too close to\n" 
        +"the left or top of the image.\n";
      _textarea.setText(display); 
      //set the point to be the center of the map
      _map.mygraph.setSelectIndex(index);
      _map.setCurrProperty(index);
      _zoomPane.setCenter(_map.mygraph.locations[index].getPoint());
      _map.changeNotify(); 
    }else if(obj==directions){
      _map.setMode(_map.DIRECTIONS);
      setTitle("Map Editor(Directions Mode)");
      int indexFrom=getIndexFrom();
      if(indexFrom==-1){
        return; 
      }
      int indexTo=getIndexTo();
      if(indexTo==-1){
        return; 
      }
      _map.mygraph.setDir(indexFrom,indexTo);
      _map.changeNotify();
      String notification=_map.mygraph.findPrintDirection();
      _textarea.setText("Directions:\n\n"+notification);
    }else if(obj==readme){
      setTitle("Map Editor(Help Mode)");
      String s="Readme:\n\nPeople in my group:\nLirong Yuan: yuan27\nJerry Ma: ma127\n\n"+
        "List of features that work:\n1.Four file operations:\n (1)New File (2)Open File\n (3)Save "+
        "(4)Save As\n2.Five modes:\n (1)Add Loc (2)Delete Loc\n (3)Add Path (4)Delete Path\n"+
        " (5)Location Property\n3.Two functions:\n (1)Find (2)Get Directions\n\n"+
        "List of features that do not work:\nNone.\n\nExtra features:\n"+
        "1.Help menu.\n2.Information panel.(this)\n\n"+
        "About my implementation:\n1.Location: red\n2.Path: yellow\n3.Direction: blue\n"+
        "4.Find point: green\n\nThanks for reading this!\n";
      _textarea.setText(s);
    }else if(obj==fileinfo){
      setTitle("Map Editor(File Info Mode)");
      String filename=_map.getName();
      if(filename.length()==0){
       filename="(Not defined yet)"; 
      }
      String imgnm=_map.mygraph.getImageName();
      String sfpps;
      double sfpp=_map.mygraph.getScale();
      if(sfpp==0.0){
        sfpps="(Not defined yet)";
      }else{
        sfpps=""+sfpp;
      }
      String s="File name:\n"+filename+"\n\nImage name:\n"+imgnm+"\n\nScale-Feet-Per-Pixel:\n"+sfpps;
      _textarea.setText(s);
    }
  }
  
  
  
  //get the index of the location that the user wants to find
  public int getIndex(){
    int numOfOptions=_map.mygraph.getCurr();
    if(numOfOptions==0){
      JOptionPane.showMessageDialog(null,"There isn't any location on the map!","Find",JOptionPane.ERROR_MESSAGE,null);
      return -1; 
    }
    String[] options=new String[numOfOptions];
    for(int i=0;i<numOfOptions;i++){
      options[i]=i+": ID:"+_map.mygraph.locations[i].getID()+" Name:"+_map.mygraph.locations[i].getName(); 
    }
    String s=(String)JOptionPane.showInputDialog(null,"Choose a location:","Find",
                                                 JOptionPane.PLAIN_MESSAGE,null,options,options[0]);
    if(s==null){
      return -1; 
    }
    int index=0;
    for(int i=0;s.charAt(i)!=':';i++){
      index=10*index+(s.charAt(i)-'0');
    }
    return index;
  }
  
  //get the index of the location that the user wants to go from
  public int getIndexFrom(){
    int numOfOptions=_map.mygraph.getCurr();
    if(numOfOptions==0){
      JOptionPane.showMessageDialog(null,"There isn't any location on the map!","Directions",JOptionPane.ERROR_MESSAGE,null);
      return -1; 
    }
    String[] options=new String[numOfOptions];
    for(int i=0;i<numOfOptions;i++){
      options[i]=i+": ID:"+_map.mygraph.locations[i].getID()+" Name:"+_map.mygraph.locations[i].getName(); 
    }
    String s=(String)JOptionPane.showInputDialog(null,"Choose a location to start from:",
                                                 "Choose the start point",
                                                 JOptionPane.PLAIN_MESSAGE,null,options,options[0]);
    if(s==null){
      return -1; 
    }
    int index=0;
    for(int i=0;s.charAt(i)!=':';i++){
      index=10*index+(s.charAt(i)-'0');
    }
    return index;
  }
  
  ////get the index of the location that the user wants to go to
  public int getIndexTo(){
    int numOfOptions=_map.mygraph.getCurr();
    if(numOfOptions==0){
      JOptionPane.showMessageDialog(null,"There isn't any location on the map!",
                                    "Choose the end point",JOptionPane.ERROR_MESSAGE,null);
      return -1; 
    }
    String[] options=new String[numOfOptions];
    for(int i=0;i<numOfOptions;i++){
      options[i]=i+": ID:"+_map.mygraph.locations[i].getID()+" Name:"+_map.mygraph.locations[i].getName(); 
    }
    String s=(String)JOptionPane.showInputDialog(null,"Choose a location to end at:","Find",
                                                 JOptionPane.PLAIN_MESSAGE,null,options,options[0]);
    if(s==null){
      return -1; 
    }
    int index=0;
    for(int i=0;s.charAt(i)!=':';i++){
      index=10*index+(s.charAt(i)-'0');
    }
    return index;
  }
  
}

