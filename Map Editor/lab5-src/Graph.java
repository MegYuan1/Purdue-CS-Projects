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

class Graph{
  //image of the graph
  private String imageName="purdue-map.jpg";
  
  // Number of vertices in the graph
  private int current=0;
  private int maximum=200;
  
  // Array with all vertices in the graph
  public Location[] locations;
  
  // Adjacency matrix.
  // _adjacent[i][j]==true if vertices i,j are adjacent
  public boolean [][] adjacent;
  
  // Weight matrix. _weight[i][j] is the weight in the
  // edge between vertex i, j
  public double [][] weight;
  
  //scale-feet-per-pixel
  private double scaleFeetPixel=0.0;
  
  //select index
  private int selectIndex=-1;
  
  //draw path index
  private int dirFrom=-1;
  private int dirTo=-1;
  
  // Initially the estimated distance to all vertices
  // from startVertex is inifinite. We set infinite to be MaxDistance
  double MaxDistance = 1000000000;
  
  public String getImageName(){
    return imageName; 
  }
  public void setImageName(String img){
    imageName=img; 
  }
  
  public double getScale(){
    return  scaleFeetPixel;
  }
  public void setScale(double sfp){
    scaleFeetPixel=sfp;
  }
  
  public void setSelectIndex(int select){
    selectIndex=select; 
  }
  public int getSelectIndex(){
    return selectIndex; 
  }
  
  public void setDir(int x,int y){
    dirFrom=x;
    dirTo=y;
  }
  public int getDirFrom(){
    return dirFrom; 
  }
  public int getDirTo(){
    return dirTo; 
  }
  
  public double min(double x,double y){
    return (x>y)?y:x;
  }
  public double max(double x,double y){
    return (x>y)?x:y;
  }
  
  //resize the table
  public void reSize(){
    maximum=2*maximum;
    //construct new vars
    Location[] locations1 = new Location[maximum];
    for (int i=0; i < maximum; i++) {
      locations1[i]=new Location(null);
    }
    boolean[][] adjacent1 = new boolean[maximum][maximum];
    double[][] weight1 = new double[maximum][maximum]; 
    //copy the values of locations
    for(int i=0;i<current;i++){
      locations1[i].setName(locations[i].getName());
      locations1[i].setPoint(locations[i].getPoint());
      locations1[i].setID(locations[i].getID());
    }
    //copy the values of adjacent and weight
    for(int i=0;i<current;i++){
      for(int j=0;j<current;j++){
        adjacent1[i][j]= adjacent[i][j];
        weight1[i][j]= weight[i][j];
      }
    }
    //set the vars to the new ones
    locations=locations1;
    adjacent=adjacent1;
    weight=weight1;
  }
  
  //Construct an initial graph with maximum vertices
  public Graph() {
    locations = new Location[maximum];
    for (int i=0; i < maximum; i++) {
      locations[i]=new Location(null);
    }
    adjacent = new boolean[maximum][maximum];
    weight = new double[maximum][maximum];    
  }
  
  //construct an graph according to a file name
  public Graph(String filename) throws Exception{
    //construct an initial graph with maximum locations
    locations = new Location[maximum];
    for (int i=0; i < maximum; i++) {
      locations[i]=new Location(null);
    }
    adjacent = new boolean[maximum][maximum];
    weight = new double[maximum][maximum]; 
    //get all the information
    FileInputStream in;
    String s="";
    int innum;
    char c;
    try{
      in=new FileInputStream(filename);
      while((innum=in.read())!=-1){
        c=(char)innum;
        if(c=='\n'){
          //parse s now
          if(!s.equals("")){
            if(s.charAt(1)=='m'){
              String imgname="";
              String scale="";
              int j;
              for(j=17;s.charAt(j)!='\"';j++){
                imgname=imgname+s.charAt(j);
              }
              for(j=j+1;s.charAt(j)!='\"';j++);
              imageName=imgname;
              for(j=j+1;s.charAt(j)!='\"';j++){
                scale=scale+s.charAt(j);
              }
              scaleFeetPixel=Double.parseDouble(scale);
            }else if(s.charAt(1)=='l'){
              String id="";
              String name="";
              String x="";
              String y="";
              int j;
              for(j=14;s.charAt(j)!='\"';j++){
                id=id+s.charAt(j);
              }
              for(j=j+1;s.charAt(j)!='\"';j++);
              for(j=j+1;s.charAt(j)!='\"';j++){
                name=name+s.charAt(j);
              }
              for(j=j+1;s.charAt(j)!='\"';j++);
              for(j=j+1;s.charAt(j)!='\"';j++){
                x=x+s.charAt(j);
              }
              for(j=j+1;s.charAt(j)!='\"';j++);
              for(j=j+1;s.charAt(j)!='\"';j++){
                y=y+s.charAt(j);
              }
              Point newpt=new Point(Integer.parseInt(x),Integer.parseInt(y));
              int index=addLoc(newpt);
              locations[index].setID(Integer.parseInt(id));
              locations[index].setName(name);
            }else if(s.charAt(1)=='p'){
              String idfrom="";
              String idto="";
              int j;
              for(j=14;s.charAt(j)!='\"';j++){
                idfrom=idfrom+s.charAt(j);
              }
              for(j=j+1;s.charAt(j)!='\"';j++);
              for(j=j+1;s.charAt(j)!='\"';j++){
                idto=idto+s.charAt(j);
              }
              int IDfrom=findIndex(Integer.parseInt(idfrom));
              int IDto=findIndex(Integer.parseInt(idto));
              setEdge(IDfrom,IDto);
            }
            //reset s
            s="";
          }
        }else{
          s=s+c;
        }
      }
      if(in!=null){
        in.close();
      }
    }catch(Exception e){
      System.out.println("Error:"+e.getMessage());
      throw new Exception();
    }
  }
  
  //add a point in the graph, return the index of the point
  public int addLoc(Point point){
    int curr=findIndex(point);
    if(curr!=-1)
      return curr;
    if(current==maximum){
      reSize(); 
    }
    locations[current]=new Location(point);
    locations[current].setID(current);
    current++;
    return (current-1);
  }
  
  //delete a point in the graph
  public boolean deleteLoc(Point point){
    int curr=findIndex(point);
    //If not found, return false.
    if(curr==-1){
      return false; 
    }
    //found.
    //if curr,dirFrom,dirTo is equal to selectIndex, then set it to -1;if not, then do not change.
    if(curr==selectIndex){
      selectIndex=-1; 
    }
    if(curr==dirFrom){
      dirFrom=-1; 
    }
    if(curr==dirTo){
      dirTo=-1; 
    }
    //(1)delete it in locations[]
    for(int i=curr;i<current-1;i++){
      locations[i].setName(locations[i+1].getName());
      locations[i].setPoint(locations[i+1].getPoint());
      locations[i].setID(i);
    }
    //(2)delete all the paths
    for(int i=curr;i<current-1;i++){
      for(int j=0;j<current;j++){
        adjacent[i][j]=adjacent[i+1][j];
        adjacent[j][i]=adjacent[i][j];
        weight[i][j]=weight[i+1][j];
        weight[j][i]=weight[i][j];
      }
    }
    for(int j=0;j<current;j++){
      adjacent[current-1][j]=false;
      adjacent[j][current-1]=false;
      weight[current-1][j]=0;
      weight[j][current-1]=0;
    }
    current--;
    return true;
  }
  
  // Set the name of a vertex
  public void setVertex(int i, String name) {
    locations[i].setName(name);
  }
  
  // Set an edge between vertex i, j
  public void setEdge(int i, int j, double weight){
    if(i!=j){
      adjacent[i][j] = true;
      adjacent[j][i] = true;
      this.weight[i][j] = weight;
      this.weight[j][i] = weight;
    }
  }
  
  //set an edge between vertex i,j
  public void setEdge(int i, int j) {
    if(i!=j){
      double myweight=locations[i].getPoint().distance(locations[j].getPoint());
      adjacent[i][j] = true;
      adjacent[j][i] = true;
      weight[i][j] = myweight*scaleFeetPixel;
      weight[j][i] = myweight*scaleFeetPixel;
    }
  } 
  
  //delete edge between i,j
  public void deleteEdge(int i, int j){
    adjacent[i][j] = false;
    adjacent[j][i] = false;
    weight[i][j] = 0.0;
    weight[j][i] = 0.0;
  }
  
  //Get the current number of elements
  public int getCurr(){
    return current; 
  }
  
  //Find the index of a point. If not found, return -1. Otherwise return its index.
  public int findIndex(Point point){
    int curr=-1;
    for(int i=0;i<current;i++){
      if(locations[i].isThisYou(point)){
        curr=i;
        break;
      }
    }
    return curr;
  }
  
  //Find the index of a point according to its ID. If not found, return -1. Otherwise return its index.
  public int findIndex(int id){
    for(int i=0;i<current;i++){
      if(locations[i].getID()==id){
        return i; 
      }
    }
    return -1;
  }
  
  //check if a point is on the path of two points
  public boolean checkPointPath(int x,int y,Point point){
    //first check if point is in the triangle bounded by locations[x] and locations[y]
    double minx=min(locations[x].getPoint().getX(),locations[y].getPoint().getX());
    double maxx=max(locations[x].getPoint().getX(),locations[y].getPoint().getX());
    double miny=min(locations[x].getPoint().getY(),locations[y].getPoint().getY());
    double maxy=max(locations[x].getPoint().getY(),locations[y].getPoint().getY());
    if((point.getX()<=maxx) && (point.getX()>=minx) && (point.getY()<=maxy) && (point.getY()>=miny)){
    }else{
     return false; 
    }
    
    double slope=0;
    double newy;
    //Find the point on the line with same x
    double diff=maxx-minx;
    diff=(diff>0)?diff:(-diff);
    if(diff<=4){
      return true;
    }else{
      slope=(locations[y].getPoint().getY()-locations[x].getPoint().getY())/(locations[y].getPoint().getX()-locations[x].getPoint().getX());
      newy=locations[x].getPoint().getY()+slope*(point.getX()-locations[x].getPoint().getX());
      slope=(slope>0)?slope:(-slope);
      if((point.getY()<=(newy+slope*10))&&(point.getY()>=(newy-slope*10))){
       return true; 
      }else{
       return false; 
      }
    }
  }
  
  //delete path which contains a point p
  public void deletePath(Point p){
    for(int i=0;i<current;i++){
      for(int j=i+1;j<current;j++){
        if(checkPointPath(i,j,p)){
          deleteEdge(i,j);
        }
      }
    }
  }
  
  
  
  // Display the graph as a string
  public String toString() {
    String s;
    s = "Vertices:\n";
    for (int i=0; i < current; i++) {
      s += "  " + i + ": " + locations[i].getName() + "\n";
    }
    s+="\n";
    s += "Edges:\n";
    for (int i=0; i < current; i++) {
      for (int j=0; j < i; j++) {
        if (adjacent[i][j]) {
          s+="  "+i+"->"+j+": "+weight[i][j]+"\n";
        }
      }
    }
    return s;
  }
  
  // Structure that is used to return values from shortestpath algorithm
  public class Path {
    // distance[i] is the distance from startVertex to i
    public double [] distance;
    
    // path[i] contains the previous vertex used to reach i
    // from startVertex
    public int [] path;
  };
  
  Path shortestPath(int startVertex) {    
    // Shortest distance known so far to reach vertex i
    /// from startVertex
    double [] distance = new double[current];
    
    // path[i] is the next vertex to use in the path from i
    // to startVertex (in the notes it may be refered as "parent")
    int [] path = new int[current];
    
    // Initially all vertices are unvisited.
    boolean [] visited = new boolean[current];
    
    for (int i=0; i < current; i++) {
      distance[i]=MaxDistance;
    }
    distance[startVertex]=0;
    
    // The next vertex to use to reach startVertex from StartVertex is
    // startVertex itself.
    path[startVertex]=startVertex;
    
    // Iterate while there are unvisited vertices
    while (true) {     
      // Get a vertex u that has not been visited and
      // that has minimum distance
      int u = -1;
      for (int i=0; i < current; i++) {
        if (!visited[i]) {
          // node has not been visited
          if (u<0 || distance[i] < distance[u]) {
            u=i;
          }
        }
      }
      
      if (u == -1) {
        // All nodes have been visited. We break from while
        break;
      }
      
      /// Set visited to true
      visited[u]=true;
      
      // Now recompute distances
      for (int z = 0; z < current; z++) {
        
        // For all adjacent vertices
        if (adjacent[u][z]) {
          // z is adjacent to u
          if (distance[z] > distance[u]+weight[u][z]) {
            // Update distance if path to z
            // through u is shorter
            distance[z] = distance[u]+weight[u][z];
            path[z]=u;
          }
        }
      }
    }
    
    // Return path and distance in Path class
    Path p = new Path();
    p.distance = distance;
    p.path = path;
    
    return p;
  }
  
  public void findPrintDirection(Graphics2D g){
    if((dirFrom==-1)||(dirTo==-1)){
      return;
    }
    if(dirFrom>=current || dirTo>=current){
      return; 
    }
    Path p = shortestPath(dirFrom);
    if(p.distance[dirTo]==MaxDistance){
      return; 
    }
    
    int u=dirTo;
    int newu;
    do {
      newu = p.path[u];
      g.setColor(Color.BLUE);
      g.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
      g.drawLine((int)locations[u].getPoint().getX(),(int)locations[u].getPoint().getY(),
                 (int)locations[newu].getPoint().getX(),(int)locations[newu].getPoint().getY());
      u=newu;
    } while (u != dirFrom);
  }
  
  public String findPrintDirection(){
    if((dirFrom==-1)||(dirTo==-1)){
      return "Direction Index Out Of Bound!";
    }
    if(dirFrom>=current || dirTo>=current){
      return "Direction Index Out Of Bound!"; 
    }
    if(dirFrom==dirTo){
      return "[Location From: "+locations[dirFrom].getName()+"]\nis the same as\n[Location To: "+locations[dirTo].getName()+"].";
    }
    Path p = shortestPath(dirFrom);
    if(p.distance[dirTo]==MaxDistance){
      return "Cannot Find Path!"; 
    }
    if(scaleFeetPixel==0){
      String s=JOptionPane.showInputDialog("Please set the scale feet per pixel:");
      if(s==null){
        return "You did not set the scale feet per pixel!"; 
      }
      double sfpp=0;
      while(sfpp==0){
        try{
          sfpp=Double.parseDouble(s);
          if(s==null){
            return "You did not set the scale feet per pixel!"; 
          }
          scaleFeetPixel=sfpp;
        }catch(Exception e){
          s=JOptionPane.showInputDialog("Please set the scale feet per pixel:");
        }
      }
    }
    // Print distances
    String distanceString = "Distance:\n";
    distanceString += "[ID: "+locations[dirFrom].getID()+" Name: "+locations[dirFrom].getName()+"] to " +
      "[ID: "+locations[dirTo].getID()+" Name: "+locations[dirTo].getName()
      +"]\nis "+p.distance[dirTo]*scaleFeetPixel+".\n"; 
    
    // Print path
    String pathString="\nPath:\n";
    int counter=0;
    int u=dirTo;
    int newu;
    int[] arr=new int[current];
    arr[counter++]=u;
    do{
      newu = p.path[u];
      u=newu;
      arr[counter++]=u;
    }while (u != dirFrom);
    int i,j;
    for(i=counter-1,j=1;i>0;i--,j++){
      pathString+="[ID: "+locations[arr[i]].getID()+" Name: "+locations[arr[i]].getName()+"]->\n";
    }
    pathString+="[ID: "+locations[arr[0]].getID()+" Name: "+locations[arr[0]].getName()+"]\n";
    return (distanceString+pathString);
  }
  
};

