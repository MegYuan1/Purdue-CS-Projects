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


import java.awt.*;
import java.awt.geom.*;

public class Location {
  private static final int SIZE = 20;
  private final static float dash1[] = {3.0f};
  private final static BasicStroke dashed = new BasicStroke(1.0f, 
                                                            BasicStroke.CAP_BUTT, 
                                                            BasicStroke.JOIN_MITER, 
                                                            3.0f, dash1, 0.0f);
  private String name;
  private Point _point;
  private int id;
  private boolean _selected;
  
  public String getName(){
    return name; 
  }
  public void setName(String nm){
    name=nm; 
  }
  
  public int getID(){
   return id; 
  }
  public void setID(int id){
   this.id=id; 
  }
  
  public Point getPoint(){
   return _point; 
  }
  public void setPoint(Point pt){
   _point=pt; 
  }
  
  public Location(Point point) {
    name="";
    _point = point;
  }
  
  public void draw(Graphics g) {
    g.setColor(Color.RED);
    g.fillOval((int) _point.getX() - SIZE/2, 
               (int) _point.getY() - SIZE/2, 
               SIZE, SIZE);
  }
  
  public void drawSelect(Graphics g) {
    Graphics2D g2 = (Graphics2D) g;
    g2.setColor(Color.GREEN);
    g2.setStroke(dashed);
    g.drawRect((int) _point.getX() - SIZE/2,
               (int) _point.getY() - SIZE/2,
               SIZE, SIZE);
  }
  
  public void drawArrow(Graphics g) {
    Graphics2D g2 = (Graphics2D) g;
    g2.setColor(Color.GREEN);
    g.fillRect((int) _point.getX() -SIZE/4,
               (int) _point.getY() - SIZE/2,
               SIZE/2, SIZE);
  }
  
  /**
   * Return true if this point is inside of this location.
   */
  public boolean isThisYou(Point p) {
    int x = (int) _point.getX();
    int y = (int) _point.getY();
    int px = (int) p.getX();
    int py = (int) p.getY();
    int radius = SIZE/2;
    return px > x - radius && px < x + radius && 
      py > y - radius && py < y + radius;
  }
}
