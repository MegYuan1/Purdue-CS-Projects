/*List of features in the handout that work:*/
1.The editor will open the picture of the map and with the mouse you will be abe to add/delete/modify the vertices and edges in the map. 
2.The graph will be stored in an XML file that can be modified by the map editor. 
3.The viewer will be able to read this XML file and show the map and the locations. 
*4.The viewer will have an interface to input a starting location and a destination. The map viewer will find the shortest path to reach the destination and it will draw on the map the path to reach the destination as well as the list of directions with the distance. We will be using the map of Purdue that is provided.

/*List of features in the handout that do not work:*/
None.

/*Extra features*/
*1.In the help menu, you can find the meaning of different colors in my program.
*2.At the begining of the program, you are asked to choose the name of map file, the scale feet per pixel and the file name to save at.
*3.There are directed and undirected paths. And if you add directed paths in the program, the program can only go from the start of a path to an end of a path. So if you want to find a path from a to b,the program will show you a directed path from a to b.

/*About my implementation*/ 
1.The location of the maps are in the folder called "lab5-src", 
so in the xml file, the first line should be: 
<mapfile bitmap="purdue-map.jpg" scale-feet-per-pixel="1.5">
instead of
<mapfile bitmap="maps/purdue-map.jpg"> scale-feet-per-pixel="1.5">
That might be a reason to cause a problem if you use different location name in the xml file.
2.I saved all the xml files in the folder called "lab5-src",
so you may want to put the xml file in that folder to test whether my editor can open files correctly.
3.All the files are save in the folder called"lab5-src".
4.My purdue.xml file contains 109 locations with names. You can check it out.

/*About compiling and running the code*/
You can run my project by typing the following:

javac MapEditor.java
java MapEditor

Thanks for reading this file!
