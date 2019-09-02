/*
 * Copyright ©2019 Hal Perkins.  All rights reserved.  Permission is
 * hereby granted to students registered for University of Washington
 * CSE 331 for use solely during Spring Quarter 2019 for purposes of
 * the course.  No other use, copying, distribution, or modification
 * is permitted without prior written consent. Copyrights for
 * third-party components of this work must be honored.  Instructors
 * interested in reusing these course materials should contact the
 * author.
 */

package pathfinder.specTest;


import graph.DirectedGraph;
import graph.DirectedLabeledEdge;
import marvel.MarvelPaths;
import pathfinder.datastructures.Path;
import org.junit.Rule;
import org.junit.rules.Timeout;
import pathfinder.Dijkstra;
import pathfinder.parser.CampusBuilding;
import pathfinder.textInterface.Pathfinder;

import java.io.*;
import java.util.*;

/**
 * This class implements a test driver that uses a script file format
 * to test an implementation of Dijkstra's algorithm on a graph.
 */
public class PathfinderTestDriver {

  public static void main(String args[]) {
    try {
      if (args.length > 1) {
        printUsage();
        return;
      }

      PathfinderTestDriver td;

      if (args.length == 0) {
        td = new PathfinderTestDriver(new InputStreamReader(System.in),
                new OutputStreamWriter(System.out));
      } else {

        String fileName = args[0];
        File tests = new File(fileName);

        if (tests.exists() || tests.canRead()) {
          td = new PathfinderTestDriver(new FileReader(tests),
                  new OutputStreamWriter(System.out));
        } else {
          System.err.println("Cannot read from " + tests.toString());
          printUsage();
          return;
        }
      }

      td.runTests();

    } catch (IOException e) {
      System.err.println(e.toString());
      e.printStackTrace(System.err);
    }
  }

  private static void printUsage() {
    System.err.println("Usage:");
    System.err.println("to read from a file: java pathfinder.specTest.PathfinderTestDriver <name of input script>");
    System.err.println("to read from standard in: java pathfinder.specTest.PathfinderTestDriver");
  }

  /**
   * String -> Graph: maps the names of graphs to the actual graph
   **/
  private final Map<String, DirectedGraph<String, Double>> graphs;
  private final PrintWriter output;
  private final BufferedReader input;

  /**
   * @requires r != null && w != null
   * @effects Creates a new MarvelTestDriver which reads command from
   * <tt>r</tt> and writes results to <tt>w</tt>.
   **/
  public PathfinderTestDriver(Reader r, Writer w) {
    graphs = new HashMap<>();
    input = new BufferedReader(r);
    output = new PrintWriter(w);
  }

  public void runTests() throws IOException {
    String inputLine;
    while ((inputLine = input.readLine()) != null) {
      if ((inputLine.trim().length() == 0) ||
              (inputLine.charAt(0) == '#')) {
        // echo blank and comment lines
        output.println(inputLine);
      } else {
        // separate the input line on white space
        StringTokenizer st = new StringTokenizer(inputLine);
        if (st.hasMoreTokens()) {
          String command = st.nextToken();

          List<String> arguments = new ArrayList<String>();
          while (st.hasMoreTokens()) {
            arguments.add(st.nextToken());
          }

          executeCommand(command, arguments);
        }
      }
      output.flush();
    }
  }

  private void executeCommand(String command, List<String> arguments) {
    try {
      if (command.equals("CreateGraph")) {
        createGraph(arguments);
      } else if (command.equals("AddNode")) {
        addNode(arguments);
      } else if (command.equals("AddEdge")) {
        addEdge(arguments);
      } else if (command.equals("ListNodes")) {
        listNodes(arguments);
      } else if (command.equals("ListChildren")) {
        listChildren(arguments);
      } else if (command.equals("FindPath")) {
        findPath(arguments);
      } else {
        output.println("Unrecognized command: " + command);
      }
    } catch (Exception e) {
      output.println("Exception: " + e.toString());
    }
  }

  private void createGraph(List<String> arguments) {
    if (arguments.size() != 1) {
      throw new CommandException("Bad arguments to CreateGraph: " + arguments);
    }

    String graphName = arguments.get(0);
    createGraph(graphName);
  }

  private void createGraph(String graphName) {
    graphs.put(graphName, new DirectedGraph<>());
    output.println("created graph " + graphName);
  }

  private void addNode(List<String> arguments) {
    if (arguments.size() != 2) {
      throw new CommandException("Bad arguments to addNode: " + arguments);
    }

    String graphName = arguments.get(0);
    String nodeName = arguments.get(1).replaceAll("_", " ");
    addNode(graphName, nodeName);
  }

  private void addNode(String graphName, String nodeName) {
    DirectedGraph<String, Double> graph = graphs.get(graphName);
    graph.addNode(nodeName);
    output.println("added node " + nodeName + " to " + graphName);
  }

  private void addEdge(List<String> arguments) {
    if (arguments.size() != 4) {
      throw new CommandException("Bad arguments to addEdge: " + arguments);
    }
    String graphName = arguments.get(0);
    String parentName = arguments.get(1).replaceAll("_", " ");;
    String childName = arguments.get(2).replaceAll("_", " ");;
    Double edgeLabel = Double.parseDouble(arguments.get(3));
    addEdge(graphName, parentName, childName, edgeLabel);

  }

  private void addEdge(String graphName, String parentName, String childName,
                       Double edgeLabel) {
    DirectedGraph<String, Double>  graph = graphs.get(graphName);
    DirectedLabeledEdge<String, Double> edge = new DirectedLabeledEdge<>(parentName, childName, edgeLabel);
    graph.addEdge(edge);
    output.println(String.format("added edge %.3f", edgeLabel) + " from " + parentName + " to " + childName + " in " + graphName);
  }

  private void listNodes(List<String> arguments) {
    if (arguments.size() != 1) {
      throw new CommandException("Bad arguments to listNodes: " + arguments);
    }
    String graphName = arguments.get(0);
    listNodes(graphName);
  }

  private void listNodes(String graphName) {
    DirectedGraph<String, Double> graph = graphs.get(graphName);
    Set<String> nodes = graph.listNodes();
    //converts set to list
    List<String> nodesList = new ArrayList<String>(nodes);
    //sorts the list
    Collections.sort(nodesList);
    output.print(graphName + " contains:");
    for (String node : nodesList) {
      output.print(" " + node.replaceAll("_", " "));
    }
    output.println();
  }

  private void listChildren(List<String> arguments) {
    if (arguments.size() != 2) {
      throw new CommandException("Bad arguments to listChildren: " + arguments);
    }
    String graphName = arguments.get(0);
    String parentName = arguments.get(1).replaceAll("_", " ");
    listChildren(graphName, parentName);
  }


  private void listChildren(String graphName, String parentName) {
    DirectedGraph<String, Double> graph = graphs.get(graphName);
    Set<DirectedLabeledEdge<String, Double>> children = graph.listChildren(parentName);
    List<DirectedLabeledEdge<String, Double>> childrenList = new ArrayList<>();
    //Adds the String representation of each child edge to a list.
    for (DirectedLabeledEdge<String, Double> edge : children) {
      //childrenList.add(edge.getDest() + String.format("(%.3f)", edge.getLabel()));
      childrenList.add(edge);
    }
    Collections.sort(childrenList, new EdgeComparator());

    output.print("the children of " + parentName + " in " + graphName + " are:");
    for (DirectedLabeledEdge<String, Double> edge : childrenList) {
      //NODE 1 to NODE 2 with weight w1
      //output.print(String.format(edge.getSrc() + " to " + edge.getDest() + " with weight %.3f", edge.getLabel()));
      String labelFormatted = String.format("%.3f", edge.getLabel());
      String edgeString = " " + edge.getDest() + "("+ labelFormatted + ")";
      output.print(edgeString);
    }
    output.println();
  }


  private void findPath(List<String> arguments) {
    if (arguments.size() != 3) {
      throw new CommandException("Bad arguments to findPath: " + arguments);
    }
    String graphName = arguments.get(0);
    String node1 = arguments.get(1).replaceAll("_", " ");
    String node2 = arguments.get(2).replaceAll("_", " ");
    findPath(graphName, node1, node2);
  }

  private void findPath(String graphName, String node1, String node2) {
    double total = 0.0;
    DirectedGraph<String, Double> graph = graphs.get(graphName);
    if (!graph.containsNode(node1) || !graph.containsNode(node2)) {
      if (!graph.containsNode(node1)) {
        output.println("unknown node " + node1);
      }
      if (!graph.containsNode(node2)) {
        output.println("unknown node " + node2);
      }
    } else {
      output.println("path from " + node1 + " to " + node2 + ":");
      Path<String> path = Dijkstra.findPath(graph, node1, node2);
      if(path == null) {
        output.println("no path found");
      } else {
        Iterator<Path<String>.Segment> iter = path.iterator();
        //iterates through path list and constructs edges so it can return a list of edges.
        while (iter.hasNext()) {
          Path<String>.Segment segment = iter.next();
          output.println(segment.getStart() + " to " + segment.getEnd() +
                  String.format(" with weight %.3f", segment.getCost()));
          total = segment.getCost() + total;
        }
        output.println(String.format("total cost: %.3f", total));
      }
    }
  }




  private static class EdgeComparator implements Comparator<DirectedLabeledEdge<String, Double>>{
    @Override
    public int compare(DirectedLabeledEdge<String, Double> edge1, DirectedLabeledEdge<String, Double> edge2) {
      if (edge1.getDest().compareTo(edge2.getDest() ) == 0 ) {
        return edge1.getLabel().compareTo(edge2.getLabel());
      } else {
        return edge1.getDest().compareTo(edge2.getDest());
      }
    }
  }




  /**
   * This exception results when the input file cannot be parsed properly
   **/
  static class CommandException extends RuntimeException {
    public CommandException() {
      super();
    }
    public CommandException(String s) {
      super(s);
    }
    public static final long serialVersionUID = 3495;
  }


}
