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


import java.awt.Graphics2D;
import javax.swing.event.ChangeListener;

public interface Scene {
  void draw(Graphics2D g);
  int getWidth();
  int getHeight();
  void addChangeListener(ChangeListener listener);
}
