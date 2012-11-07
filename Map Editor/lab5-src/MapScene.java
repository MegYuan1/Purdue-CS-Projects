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
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.util.*;
import java.io.*;
import javax.imageio.*;

/**
 * This class is where you keep track of all your locations and edges
 * and you draw them in the draw() method.
 */
public class MapScene implements Scene {
  private int mode=0;
  public final int INSERTLOC=1;
  public final int DELETELOC=2;
  public final int INSERTPATH=3;
  public final int DELETEPATH=4;
  public final int PROPERTY=5;
  public final int FIND=6;
  public final int DIRECTIONS=7;
  
  private ChangeListener _listener;
  private Image _image;
  
  private int lineStartIndex;
  private int lineEndIndex;
  
  //the graph of points
  public Graph mygraph;
  //current index of point
  private int curr=-1;
  //name to save file at
  private String name="";
  
  
  public MapScene(Image image) {
    mygraph=new Graph();
    _image = image;
  }
  
  public void setImage(String imgname){
    File file=new File(imgname);
    try{
      _image=(Image)ImageIO.read(file);
    }catch(Exception e){
      System.out.println(e.getMessage());
    }
  }
  
  public void setMode(int md){
    mode=md; 
  }
  
  public void setName(String nm){
    name=nm;
  }
  public String getName(){
    return name; 
  }
  
  public void setScaleFeet(double sf){
    mygraph.setScale(sf);
  }
  public double getScaleFeet(){
    return mygraph.getScale(); 
  }
  
  public void setCurrProperty(int x){
    curr=x; 
  }
  public int getCurrProperty(){
    return curr; 
  }
  
  
  /**
   * Call this method whenever something in the map has changed that
   * requires the map to be redrawn.
   */
  public void changeNotify() {
    if (_listener != null) _listener.stateChanged(null);
  }
  
  
  /**
   * This method will draw the entire map.
   */
  public void draw(Graphics2D g) {
    // Draw the map image
    g.drawImage(_image, 0, 0, null);   
    //Draw the locations
    for(int i=0;i<mygraph.getCurr();i++){
      mygraph.locations[i].draw(g); 
    }
    // Draw the line
    for(int i=0;i<mygraph.getCurr();i++){
      for(int j=i+1;j<mygraph.getCurr();j++){
        if(mygraph.adjacent[i][j]){
          g.setColor(Color.YELLOW);
          g.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
          g.drawLine((int)mygraph.locations[i].getPoint().getX(),(int)mygraph.locations[i].getPoint().getY(),
                     (int)mygraph.locations[j].getPoint().getX(),(int)mygraph.locations[j].getPoint().getY());
        }
      }
    }
    //Draw the selected location
    int index=mygraph.getSelectIndex();
    if((index!=-1) && (index<mygraph.getCurr())){
      mygraph.locations[index]. drawArrow(g);
    }
    //print the direction
    if(mode==DIRECTIONS){
      mygraph.findPrintDirection(g);
    }
  }
  
  public void mouseSingleClicked(Point p){
    //update current index
    curr=mygraph.findIndex(p);
    //insert location mode
    if(mode==INSERTLOC){
      int result=mygraph.addLoc(p);
      if(result==(mygraph.getCurr()-1)){
        curr=mygraph.getCurr()-1;
        changeNotify();
      }
    }
    //delete location mode
    if(mode==DELETELOC){
      mygraph.deleteLoc(p);
      changeNotify();
    }
    //delete path mode
    if(mode==DELETEPATH){
      mygraph.deletePath(p);
      changeNotify();
    }
    //Location Property Mode
    if(mode==PROPERTY){
      if(curr!=-1){ 
        JOptionPane mypane=new JOptionPane();
        String display="Name:"+getCurrName()+"\n(x,y)=("
          +getCurrPoint().getX()+","+getCurrPoint().getY()+")\nID="+getCurrID()
          +"\nNew location name:";
        String s = mypane.showInputDialog(display);
        if(s==null){
          return;
        }
        while(s.length()==0){
          display="Invalid new Name!\nName:"+getCurrName()+"\n(x,y)=("
            +getCurrPoint().getX()+","+getCurrPoint().getY()+")\nID="+getCurrID()
            +"\nNew location name:";
          s = mypane.showInputDialog(display);
          if(s==null){
            return;
          }
        }
        setCurrName(s);
      }
    }
  }
  
  public void mouseDoubleClicked(Point p){
    //update current index
    curr=mygraph.findIndex(p);
    //pop up the curr location's property 
    if(curr!=-1){ 
      JOptionPane mypane=new JOptionPane();
      String display="Name:"+getCurrName()+"\n(x,y)=("
        +getCurrPoint().getX()+","+getCurrPoint().getY()+")\nID="+getCurrID()
        +"\nNew location name:";
      String s = mypane.showInputDialog(display);
      if(s==null){
        return;
      }
      while(s.length()==0){
        display="Invalid new Name!\nName:"+getCurrName()+"\n(x,y)=("
          +getCurrPoint().getX()+","+getCurrPoint().getY()+")\nID="+getCurrID()
          +"\nNew location name:";
        s = mypane.showInputDialog(display);
        if(s==null){
          return;
        }
      }
      setCurrName(s);
    } 
  }
  
  public void mousePressed(Point p) {
    //update current index
    curr=mygraph.findIndex(p);
    //insert path mode
    if(mode==INSERTPATH){
      lineStartIndex=mygraph.findIndex(p);
    }
  }
 
  public void mouseDragged(Point p) {
    //System.out.println("mousedragged");
    //insert location mode
    if(mode==INSERTLOC){
      if(curr!=-1){
        mygraph.locations[curr].setPoint(p);
        changeNotify();
      }
    }
    //insert path mode
    if(mode==INSERTPATH){
      lineEndIndex=mygraph.findIndex(p);
      if((lineStartIndex!=-1)&&(lineEndIndex!=-1)&&lineStartIndex!=lineEndIndex){
        mygraph.setEdge(lineStartIndex,lineEndIndex);
        changeNotify();
        lineStartIndex=-1;
        lineEndIndex=-1;
      }
    }
  }
  
  public int getWidth() { return _image.getWidth(null); }
  public int getHeight() { return _image.getHeight(null); }
  
  public void printOut(){
    File file=new File(name);
    PrintWriter myout;
    try{
      myout=new PrintWriter(new FileOutputStream(file));
      myout.println("<mapfile bitmap=\""+mygraph.getImageName()+"\" scale-feet-per-pixel=\""+mygraph.getScale()+"\">");
      for(int i=0;i<mygraph.getCurr();i++){
        myout.println("<location id=\""+mygraph.locations[i].getID()
                        +"\" name=\""+mygraph.locations[i].getName()
                        +"\" x=\""+(int)mygraph.locations[i].getPoint().getX()
                        +"\" y=\""+(int)mygraph.locations[i].getPoint().getY()+"\" />");
      }
      for(int i=0;i<mygraph.getCurr();i++){
        for(int j=i;j<mygraph.getCurr();j++){
          if(mygraph.adjacent[i][j]){
            myout.println("<path idfrom=\""+i+"\" idto=\""+j+"\" type=\"undirected\">"); 
          }
        }
      }
      myout.println("</mapfile>");
      if(myout!=null){
        myout.close();
      }
    }
    catch(FileNotFoundException e){
      System.out.println("File "+file.getName()+" not found!"); 
    }
  }
  
  public int getCurrIndex(){
    return curr; 
  }
  public String getCurrName(){
    return mygraph.locations[curr].getName(); 
  }
  public Point getCurrPoint(){
    return mygraph.locations[curr].getPoint();
  }
  public int getCurrID(){
    return mygraph.locations[curr].getID(); 
  }
  public void setCurrName(String nm){
    mygraph.locations[curr].setName(nm);
  }
  public void addChangeListener(ChangeListener listener) {
    _listener = listener;
  }
}
