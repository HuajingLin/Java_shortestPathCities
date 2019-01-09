/* 
 * Author: Huajijg Lin
 * update: 5/20/2017
 */

package cityproject;

import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.io.*;

public class CityProject {


    // main metod for the project
    public static void main(String[] args) {

        City[] cities = new City[200]; //array of cities (Vertices) max = 200
        for (int i = 0; i < cities.length; i++) {
            cities[i] = new City();
        }

        Edge[] links = new Edge[2000];// array of links  (Edges)  max = 2000
        for (int i = 0; i < links.length; i++) {
            links[i] = new Edge();
        }

        int cityCount; //    actual number of cities
        int linkCount; //    actual number of links
                
        // load cities into an array from a datafile
        cityCount = readCities(cities);
        System.out.printf("city count: %d\n", cityCount);
        
        // load links into an array from a datafile
        linkCount = readLinks(links, cities);
        System.out.printf("link count: %d\n", linkCount);
        
        // create the adjacency list for each city based on the link array
        createAdjacencyLists(cityCount, cities, linkCount, links);

        // print adjacency lists for all cities
        PrintAdjacencyLists(cityCount, cities);

        // draw a map of the cities and links
        drawMap(cityCount, cities,linkCount, links);
        
        int dialogResult = 0;
        do{
        // ask the user input two cities names
        int[] city = new int[2];
        city[0] = -1;
        city[1] = -1;
        askUserEnterCities(city, cityCount, cities);
        
        if (city[0] >= 0 && city[1] >= 0) {
            int[] prev = new int[cityCount];
            int[] dist = new int[cityCount];
            //creat a matrix for adjacency nodes
            int[][] matrix = new int[cityCount][cityCount];
            readDataToMatrix(matrix, cityCount, cities);

            dijkstra(city[0], prev, dist, cityCount, cities, matrix);

            String str = String.format("shortest( %s => %s ) = %d\n", cities[city[0]].getName(), cities[city[1]].getName(), dist[city[1]]);
            System.out.print(str);
            JOptionPane.showMessageDialog(null, str, "Result", JOptionPane.CLOSED_OPTION);
        }
        dialogResult = JOptionPane.showConfirmDialog(null, "Would You Like to try again?", "Warning", JOptionPane.YES_NO_OPTION);
    }while (dialogResult == JOptionPane.YES_OPTION);
        
    } // end main
    
    //************************************************************************
    
    //ask user enter two cities' name.
    private static void askUserEnterCities(int[] city, int cityCount, City[] cities){
        String sourceCity = JOptionPane.showInputDialog("Please enter a source city");
        System.out.printf("source city:%s\n", sourceCity);
        
        while (city[0] < 0 && sourceCity != null) {

            for (int i = 0; i < cityCount; i++) {
                if (cities[i].getName().compareToIgnoreCase(sourceCity) == 0) {
                    city[0] = i;
                    break;
                }
            }

            if (city[0] < 0) {
                sourceCity = JOptionPane.showInputDialog(null, "The city you entered does not exist. Please re-enter.",
                        "Source city", JOptionPane.ERROR_MESSAGE);
                System.out.printf("source city:%s\n", sourceCity);
            }
        }
        if (city[0] >= 0) {
            String destinationCity = JOptionPane.showInputDialog("Please enter a destination city");
            System.out.printf("destination city:%s\n", destinationCity);
            
            while (city[1] < 0 && destinationCity != null) {
                for (int i = 0; i < cityCount; i++) {
                    if (cities[i].getName().compareToIgnoreCase(destinationCity) == 0) {
                        city[1] = i;
                        break;
                    }
                }
                if (city[1] < 0) {
                    destinationCity = JOptionPane.showInputDialog(null, "The city you entered does not exist. Please re-enter.",
                            "Destination city", JOptionPane.ERROR_MESSAGE);
                    System.out.printf("destination city:%s\n", destinationCity);
                }
            }
        }
    }
    
    //read data to matrix
    private static void readDataToMatrix(int[][] matrix, int cityCount, City[] cities){
        
        //初始化矩阵        
        for (int i = 0; i < cityCount; i++) {
            for (int j = 0; j < cityCount; j++) {
                if (i==j)
                    matrix[i][j] = 0;
                else
                    matrix[i][j] = Integer.MAX_VALUE;
            }
        }
        //读取邻点路径长到矩阵
        for (int c = 0; c < cityCount; c++) {
            for (int i = 0; i < cityCount; i++) {
                AdjacencyNode node = cities[c].getAdjacencyListHead();
                while (node != null) {
                    if (node.getCity().getName().compareTo(cities[i].getName()) == 0) {
                        matrix[c][i] = node.getcDistance();
                        //System.out.println("\t lll " + node.toString());
                    }
                    node = node.getNext();
                }
            }
        }
    }
    /*
     * Dijkstra shortest path
     */
    public static void dijkstra(int sv, int[] prev, int[] dist, int cityCount, City[] cities,int[][] matrix) {
        
        boolean[] flag = new boolean[cityCount];

        //initialization
        for (int i = 0; i < cityCount; i++) {
            flag[i] = false;          
            prev[i] = 0;
            dist[i] = matrix[sv][i];
        }

        flag[sv] = true;
        dist[sv] = 0;

        // Find the shortest path of a vertex every time
        int k = 0;
        for (int i = 1; i < cityCount; i++) {
            //Find the current smallest path
            int min = Integer.MAX_VALUE;
            for (int j = 0; j < cityCount; j++) {
                if (flag[j] == false && dist[j] < min) {
                    min = dist[j];
                    k = j;
                }
            }
            // Mark, got the shortest path.
            flag[k] = true;

            // Fix current shortest path
            for (int j = 0; j < cityCount; j++) {//mVexs.length
                int tmp = (matrix[k][j] == Integer.MAX_VALUE ? Integer.MAX_VALUE : (min + matrix[k][j]));
                if (flag[j] == false && (tmp < dist[j])) {
                    dist[j] = tmp;
                    prev[j] = k;
                }
            }
        }

        // print result:
        System.out.printf("dijkstra(%s): \n", cities[sv].getName());
        for (int i=0; i < cityCount; i++)
            System.out.printf("  shortest(%s, => %s) = %d\n", cities[sv].getName(), cities[i].getName(), dist[i]);
    }
    
    // method to read city data into an array from a data file
    public static int readCities(City[] cities) {

        int count = 0; // number of cities[] elements with data

        String[][] cityData = new String[123][3]; // holds data from the city file
        String delimiter = ",";                   // the delimiter in a csv file
        String line;                              // a String to hold each line from the file
        
        String fileName = "cities.csv";           // the file to be opened  

        try {
            // Create a Scanner to read the input from a file
            Scanner infile = new Scanner(new File(fileName));

            /* This while loop reads lines of text into an array. it uses a Scanner class 
             * boolean function hasNextLine() to see if there is another line in the file.
             */
            while (infile.hasNextLine()) {
                // read the line 
                line = infile.nextLine();

                // split the line into separate objects and store them in a row in the array
                cityData[count] = line.split(delimiter);
                
                // read data from the 2D array into an array of City objects
                cities[count].setName(cityData[count][0]);
                cities[count].setX(Integer.parseInt(cityData[count][1]));
                cities[count].setY(Integer.parseInt(cityData[count][2]));

                count++;
            }// end while

            infile.close();

        } catch (IOException e) {
            // error message dialog box with custom title and the error icon
            JOptionPane.showMessageDialog(null, "File I/O error:" + fileName, "File Error", JOptionPane.ERROR_MESSAGE);
        }
        return count;

    } // end loadCities()
    //*************************************************************************

    // method to read link data into an array from a data file
    public static int readLinks(Edge[] links, City[] cities) {
        int count = 0; // number of links[] elements with data

        String[][] CityLinkArray = new String[695][3]; // holds data from the link file
        String delimiter = ",";                       // the delimiter in a csv file
        String line;				      // a String to hold each line from the file

        String fileName = "links.csv";                // the file to be opened  

        try {
            // Create a Scanner to read the input from a file
            Scanner infile = new Scanner(new File(fileName));

            /* This while loop reads lines of text into an array. it uses a Scanner class 
             * boolean function hasNextLine() to see if there another line in the file.
             */
            while (infile.hasNextLine()) {
                // read the line 
                line = infile.nextLine();
                

                // split the line into separate objects and store them in a row in the array
                CityLinkArray[count] = line.split(delimiter);

                // read link data from the 2D array into an array of Edge objects
                // set source to vertex with city name in source column
                links[count].setSource(findCity(cities, CityLinkArray[count][0]));
                // set destination to vertex with city name in destination column
                links[count].setDestination(findCity(cities, CityLinkArray[count][1]));
                //set length to integer valuein length column
                links[count].setLength(Integer.parseInt(CityLinkArray[count][2]));

                count++;

            }// end while

        } catch (IOException e) {
            // error message dialog box with custom title and the error icon
            JOptionPane.showMessageDialog(null, "File I/O error:" + fileName, "File Error", JOptionPane.ERROR_MESSAGE);
        }
        return count;
    } // end loadLinks()
    //*************************************************************************

    // emthod to find the City onject with the given city name
    public static City findCity(City[] cities, String n) {
        int index = 0;  // loop counter
        // go through the cities array until the name is found
        // the name will be in the list

        while (cities[index].getName().compareTo(n) != 0) {

            index++;
        }// end while()
        return cities[index];

    } // end  findCity()

// method to create an adjacency lists for each city
    public static void createAdjacencyLists(int cityCount, City[] cities, int linkCount, Edge[] links) {

        AdjacencyNode temp = new AdjacencyNode();

        // iterate city array
        for (int i = 0; i < cityCount; i++) {

            //iterate link array
            for (int j = 0; j < linkCount; j++) {
                // if the currentl link's source is the current city
                if (links[j].getSource() == cities[i]) {

                    /* create a node for the link and inseert it into the adjancency list
                     * as the new head of the list. 
                     */
                    // temporarily store the current value of the list's head
                    temp = cities[i].getAdjacencyListHead();
                    //create a new node
                    AdjacencyNode newNode = new AdjacencyNode();
                    // add city and distance data
                    newNode.setCity(links[j].getDestination());
                    newNode.setDistance(links[j].getLength());
                    // point newNode to the previous list head
                    newNode.setNext(temp);

                    // set the new head of the list to newNode
                    cities[i].setAdjacencyListHead(newNode);

                }  // end if
            } // end for j
        } // end for i

    } // end createAdjacencyLists()

    // method to print adjacency lists
    public static void PrintAdjacencyLists(int cityCount, City[] cities) {

        System.out.println("List of Edges in the Graph of Cities by Source City");
        // iterate array of cities
        for (int i = 0; i < cityCount; i++) {

            // set current to adjacency list for this city    
            AdjacencyNode current = cities[i].getAdjacencyListHead();

            // print city name
            System.out.println("\nFrom " + cities[i].getName());

            // iterate adjacency list and print each node's data
            while (current != null) {
                System.out.println("\t"+ current.toString());
                current = current.getNext();
            } // end while (current != null) 

        }   // end for i 

    } // end PrintAdjacencyLists()

    
    // method to draw the graph (map of cities and links)
   static void drawMap(int cCount, City[] c, int lCount, Edge[] l){
       CityMap canvas1 = new CityMap(cCount,  c, lCount, l);
       

        int width = 1500; // width of the Canvas
        int height = 900; // heightof the Canvas 
        
        
        // set up a JFrame to hold the canvas
        JFrame frame = new JFrame();
        frame.setTitle("U.S. Cities");
        frame.setSize(width, height);
        frame.setLocation(10, 10);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // add the canvas to the frame as a content panel
        frame.getContentPane().add(canvas1);
        frame.setVisible(true);

   } // end drawMap() 
    
    
    
} // end class cityProject
