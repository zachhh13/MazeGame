import java.util.ArrayDeque;
import java.util.ArrayList;

import java.util.Deque;

import tester.*;
import javalib.funworld.World;
import javalib.funworld.WorldScene;

import java.awt.Color;
import javalib.worldimages.*;
import java.util.Random;
import java.util.HashMap;

/*
 * USER GUIDE!!!!!! 
 * Hello user, in order to play this maze game you must know 3 commands.
 *  First Depth first sort is "d". 2nd Breadth 
 * First sort is "b". And finally to reset the maze is "r", 
 * keep in mind that reset is only available once a 
 * sort has been used. Meaning "r" when the maze is unsorted will do nothing.  
 */

//represents a node 
class Node {
  // fields
  Posn posn;
  Color color;
  ArrayList<Edge> outEdges;

  // constructor
  Node(Posn posn, Color color, ArrayList<Edge> outEdges) {
    this.posn = posn;
    this.color = color;
    this.outEdges = outEdges;
  }

  // produces an image of this node
  WorldImage drawNode(int mazeWidth, int mazeHeight) {
    return new RectangleImage((ExamplesMaze.SCENE_SIZE * mazeWidth) / mazeWidth - 5,
        (ExamplesMaze.SCENE_SIZE * mazeHeight) / mazeHeight - 5, OutlineMode.SOLID, color);

  }
}

class Edge {
  // fields
  Node in;
  Node out;
  int weight;

  // constructor
  Edge(Node in, Node out, int weight) {
    this.in = in;
    this.out = out;
    this.weight = weight;
  }
}

//Represents a mutable collection of items

interface ICollection<T> {

  // Is this collection empty?

  boolean isEmpty();

  // EFFECT: adds the item to the collection

  void add(T item);

  // Returns the first item of the collection

  // EFFECT: removes that first item

  T remove();

}

//last in first out

class Stack<T> implements ICollection<T> {

  Deque<T> contents;

  Stack() {

    this.contents = new ArrayDeque<T>();

  }

  public boolean isEmpty() {

    return this.contents.isEmpty();

  }

  public T remove() {

    return this.contents.removeFirst();

  }

  public void add(T item) {

    this.contents.addFirst(item);

  }

}

//first in first out

class Queue<T> implements ICollection<T> {

  Deque<T> contents;

  Queue() {

    this.contents = new ArrayDeque<T>();

  }

  public boolean isEmpty() {

    return this.contents.isEmpty();

  }

  public T remove() {

    return this.contents.removeFirst();

  }

  public void add(T item) {

    this.contents.addLast(item);

  }

}

//represents a search algorithm 
class SearchAlgorithm extends World {
  // fields
  int mazeWidth;
  int mazeHeight;
  ArrayList<Node> maze = new ArrayList<Node>();
  Node src;
  Node dest;
  ArrayList<Edge> edgesInTree;
  ICollection<Node> worklist;
  ArrayDeque<Node> alreadySeen;
  HashMap<Node, Edge> cameFromEdge;
  Node dest1;

  // constructor
  SearchAlgorithm(int mW, int mH, ArrayList<Node> maze, Node src, Node dest,
      ArrayList<Edge> edgesInTree, ICollection<Node> worklist, ArrayDeque<Node> alreadySeen,
      HashMap<Node, Edge> cameFromEdge) {
    this.mazeWidth = mW;
    this.mazeHeight = mH;
    this.maze = maze;
    this.src = src;
    this.dest = dest;
    this.edgesInTree = edgesInTree;
    this.worklist = worklist;
    this.alreadySeen = alreadySeen;
    this.cameFromEdge = cameFromEdge;
    this.dest1 = dest;
  }

  // search algorithm
  boolean searchInTree(Node current, Node out) {
    for (Edge e : edgesInTree) {
      if ((e.in.equals(current) && e.out.equals(out))
          || (e.out.equals(current) && e.in.equals(out))) {
        return true;
      }
    }
    return false;
  }

  void searchHelp(Node src, Node dest, ICollection<Node> worklist) {
    if (worklist.isEmpty()) {
      worklist.add(src);

    }
    if (!worklist.isEmpty()) {
      Node next = worklist.remove();
      next.color = new Color(173, 216, 230);

      if (next.equals(dest)) {

        next.color = Color.blue;

      }
      else if (!alreadySeen.contains(next)) {
        for (Edge e : next.outEdges) {
          if (this.searchInTree(next, e.out)) {
            worklist.add(e.out);
            cameFromEdge.put(e.out, e);
            alreadySeen.add(next);
          }

        }
      }
    }

    if (maze.get(this.mazeHeight * this.mazeWidth - 1).color.equals(Color.blue)) {

      Node next = dest;

      cameFromEdge.get(next).out.color = Color.blue;
      next = cameFromEdge.get(next).in;
      cameFromEdge.get(next).out.color = Color.blue;
      next = cameFromEdge.get(next).in;
      cameFromEdge.get(next).out.color = Color.blue;
      next = cameFromEdge.get(next).in;
      cameFromEdge.get(next).out.color = Color.blue;

    }
  }

  // visualizes the sorting algorithms
  public World onTick() {

    if (maze.get(this.mazeHeight * this.mazeWidth - 1).color.equals(Color.blue)) {

      return this;

    }
    else {

      searchHelp(this.src, this.dest, this.worklist);
      return new SearchAlgorithm(this.mazeWidth, this.mazeHeight, this.maze, this.src, this.dest,
          this.edgesInTree, this.worklist, this.alreadySeen, this.cameFromEdge);

    }

  }

  // resets the maze and alogrithm
  public World onKeyEvent(String key) {
    if (key.equals("r")) {

      MazeWorld mw = new MazeWorld(this.mazeWidth, this.mazeHeight, this.maze, 1);
      mw.maze = new ArrayList<Node>();
      mw.makeGraph();
      mw.edgesInTree = this.edgesInTree;
      return mw;
    }
    return this;
  }

  // draws the maze and sorting algorithm
  public WorldScene makeScene() {
    // this.maze.get(20).color = Color.green;
    WorldScene ws = new WorldScene((ExamplesMaze.SCENE_SIZE * this.mazeWidth),
        (ExamplesMaze.SCENE_SIZE * this.mazeHeight));
    ws = ws.placeImageXY(
        new RectangleImage(ExamplesMaze.SCENE_SIZE * mazeWidth,
            ExamplesMaze.SCENE_SIZE * mazeHeight, OutlineMode.SOLID, Color.black),
        ExamplesMaze.SCENE_SIZE * mazeWidth / 2, ExamplesMaze.SCENE_SIZE * mazeHeight / 2);

    for (int n = 0; n < this.mazeHeight * this.mazeWidth; n++) {
      // this.maze.get(20).color = Color.green;
      // n.color = Color.green;

      ws = ws.placeImageXY(this.maze.get(n).drawNode(this.mazeWidth, this.mazeHeight),
          ExamplesMaze.SCENE_SIZE * this.maze.get(n).posn.x + (ExamplesMaze.SCENE_SIZE / 2),
          ExamplesMaze.SCENE_SIZE * this.maze.get(n).posn.y + (ExamplesMaze.SCENE_SIZE / 2));

    }
    for (int e = 0; e < edgesInTree.size(); e++) {
      if (edgesInTree.get(e).out.posn.y < edgesInTree.get(e).in.posn.y) { // up
        ws = ws.placeImageXY(
            new RectangleImage(ExamplesMaze.SCENE_SIZE - 5, 5, OutlineMode.SOLID,
                edgesInTree.get(e).out.color),
            ExamplesMaze.SCENE_SIZE * edgesInTree.get(e).in.posn.x + (ExamplesMaze.SCENE_SIZE / 2),
            ExamplesMaze.SCENE_SIZE * edgesInTree.get(e).in.posn.y);
      }
      if (edgesInTree.get(e).out.posn.y > edgesInTree.get(e).in.posn.y) { // down
        ws = ws.placeImageXY(
            new RectangleImage(ExamplesMaze.SCENE_SIZE - 5, 5, OutlineMode.SOLID,
                edgesInTree.get(e).out.color),
            ExamplesMaze.SCENE_SIZE * edgesInTree.get(e).in.posn.x + (ExamplesMaze.SCENE_SIZE / 2),
            ExamplesMaze.SCENE_SIZE * edgesInTree.get(e).in.posn.y + ExamplesMaze.SCENE_SIZE);
      }
      if (edgesInTree.get(e).out.posn.x < edgesInTree.get(e).in.posn.x) { // left
        ws = ws.placeImageXY(
            new RectangleImage(5, ExamplesMaze.SCENE_SIZE - 5, OutlineMode.SOLID,
                edgesInTree.get(e).out.color),
            ExamplesMaze.SCENE_SIZE * edgesInTree.get(e).in.posn.x,
            ExamplesMaze.SCENE_SIZE * edgesInTree.get(e).in.posn.y + (ExamplesMaze.SCENE_SIZE / 2));
      }
      if (edgesInTree.get(e).out.posn.x > edgesInTree.get(e).in.posn.x) { // right
        ws = ws.placeImageXY(
            new RectangleImage(5, ExamplesMaze.SCENE_SIZE - 5, OutlineMode.SOLID,
                edgesInTree.get(e).out.color),
            ExamplesMaze.SCENE_SIZE * edgesInTree.get(e).in.posn.x + ExamplesMaze.SCENE_SIZE,
            ExamplesMaze.SCENE_SIZE * edgesInTree.get(e).in.posn.y + (ExamplesMaze.SCENE_SIZE / 2));
      }

    }
    return ws;
  }

}

class MazeWorld extends World {
  // fields
  int mazeWidth;
  int mazeHeight;
  ArrayList<Node> maze;
  int count;
  Random rand = new Random();
  ArrayList<Edge> edgesInTree;

  // constructor
  MazeWorld(int mazeWidth, int mazeHeight, ArrayList<Node> maze, int count) {
    this.mazeWidth = mazeWidth;
    this.mazeHeight = mazeHeight;
    this.maze = maze;
    this.count = count;
  }

  // handles displaying this world
  public WorldScene makeScene() {
    if (this.count == 0) {
      this.makeGraph();

    }
    if (edgesInTree == null) {
      edgesInTree = this.runKruskal();
    }
    WorldScene ws = new WorldScene((ExamplesMaze.SCENE_SIZE * this.mazeWidth),
        (ExamplesMaze.SCENE_SIZE * this.mazeHeight));
    ws = ws.placeImageXY(
        new RectangleImage(ExamplesMaze.SCENE_SIZE * mazeWidth,
            ExamplesMaze.SCENE_SIZE * mazeHeight, OutlineMode.SOLID, Color.black),
        ExamplesMaze.SCENE_SIZE * mazeWidth / 2, ExamplesMaze.SCENE_SIZE * mazeHeight / 2);

    for (Node n : maze) {
      ws = ws.placeImageXY(n.drawNode(this.mazeWidth, this.mazeHeight),
          ExamplesMaze.SCENE_SIZE * n.posn.x + (ExamplesMaze.SCENE_SIZE / 2),
          ExamplesMaze.SCENE_SIZE * n.posn.y + (ExamplesMaze.SCENE_SIZE / 2));

    }
    for (Edge e : edgesInTree) {
      if (e.out.posn.y < e.in.posn.y) { // up
        ws = ws.placeImageXY(
            new RectangleImage(ExamplesMaze.SCENE_SIZE - 5, 5, OutlineMode.SOLID, Color.LIGHT_GRAY),
            ExamplesMaze.SCENE_SIZE * e.in.posn.x + (ExamplesMaze.SCENE_SIZE / 2),
            ExamplesMaze.SCENE_SIZE * e.in.posn.y);
      }
      if (e.out.posn.y > e.in.posn.y) { // down
        ws = ws.placeImageXY(
            new RectangleImage(ExamplesMaze.SCENE_SIZE - 5, 5, OutlineMode.SOLID, Color.LIGHT_GRAY),
            ExamplesMaze.SCENE_SIZE * e.in.posn.x + (ExamplesMaze.SCENE_SIZE / 2),
            ExamplesMaze.SCENE_SIZE * e.in.posn.y + ExamplesMaze.SCENE_SIZE);
      }
      if (e.out.posn.x < e.in.posn.x) { // left
        ws = ws.placeImageXY(
            new RectangleImage(5, ExamplesMaze.SCENE_SIZE - 5, OutlineMode.SOLID, Color.LIGHT_GRAY),
            ExamplesMaze.SCENE_SIZE * e.in.posn.x,
            ExamplesMaze.SCENE_SIZE * e.in.posn.y + (ExamplesMaze.SCENE_SIZE / 2));
      }
      if (e.out.posn.x > e.in.posn.x) { // right
        ws = ws.placeImageXY(
            new RectangleImage(5, ExamplesMaze.SCENE_SIZE - 5, OutlineMode.SOLID, Color.LIGHT_GRAY),
            ExamplesMaze.SCENE_SIZE * e.in.posn.x + ExamplesMaze.SCENE_SIZE,
            ExamplesMaze.SCENE_SIZE * e.in.posn.y + (ExamplesMaze.SCENE_SIZE / 2));
      }

    }
    return ws;
  }

  // handles ticking of the clock
  public World onTick() {
    return new MazeWorld(this.mazeWidth, this.mazeHeight, this.maze, this.count + 1);
  }

  public World onKeyEvent(String key) {
    if (key.equals("d")) {
      return new SearchAlgorithm(this.mazeWidth, this.mazeHeight, this.maze, this.maze.get(0),
          this.maze.get(this.mazeHeight * this.mazeWidth - 1), this.edgesInTree, new Stack<Node>(),
          new ArrayDeque<Node>(), new HashMap<Node, Edge>());
    }
    if (key.equals("b")) {
      return new SearchAlgorithm(this.mazeWidth, this.mazeHeight, this.maze, this.maze.get(0),
          this.maze.get(this.mazeHeight * this.mazeWidth - 1), this.edgesInTree, new Queue<Node>(),
          new ArrayDeque<Node>(), new HashMap<Node, Edge>());
    }
    return this;
  }

  // generates a new maze
  void makeGraph() {
    for (int i = 0; i < this.mazeHeight; i++) { // generate nodes with no edges
      for (int j = 0; j < this.mazeWidth; j++) {
        maze.add(new Node(new Posn(j, i), Color.LIGHT_GRAY, new ArrayList<Edge>()));
      }
    }
    /*
     * for (Node no : this.maze) {//generates all possible edges for (Node ni :
     * this.maze) { no.outEdges.add(new Edge(no, ni, rand.nextInt())); } }
     */
    for (int i = 0; i < this.mazeHeight; i++) { // generates edges between nodes and their neighbors
      for (int j = 0; j < this.mazeWidth; j++) {
        if (i != 0) { // not top
          maze.get(i * mazeWidth + j).outEdges.add(new Edge(maze.get(i * mazeWidth + j),
              maze.get((i - 1) * mazeWidth + j), rand.nextInt(10000)));
        }
        if (j != 0) { // not left
          maze.get(i * mazeWidth + j).outEdges.add(new Edge(maze.get(i * mazeWidth + j),
              maze.get((i) * mazeWidth + j - 1), rand.nextInt(10000)));
        }
        if (i != mazeHeight - 1) { // not bottom
          maze.get(i * mazeWidth + j).outEdges.add(new Edge(maze.get(i * mazeWidth + j),
              maze.get((i + 1) * mazeWidth + j), rand.nextInt(10000)));
        }
        if (j != mazeWidth - 1) { // not right {
          maze.get(i * mazeWidth + j).outEdges.add(new Edge(maze.get(i * mazeWidth + j),
              maze.get((i) * mazeWidth + j + 1), rand.nextInt(10000)));
        }

      }
    }
  }

  // returns the edges of a graph made by running Kruskal's algorithm
  ArrayList<Edge> runKruskal() {
    HashMap<Posn, Posn> representatives = new HashMap<Posn, Posn>();
    ArrayList<Edge> edgesInTree = new ArrayList<Edge>();
    ArrayList<Edge> worklist = new ArrayList<Edge>();

    for (int n = 0; n < maze.size(); n++) {
      for (int e = 0; e < maze.get(n).outEdges.size(); e++) {
        worklist.add(maze.get(n).outEdges.get(e));
      }
    }

    this.quicksort(worklist, 0, worklist.size() - 1); // worklist is now sorted

    for (int n = 0; n < maze.size(); n++) { // initialize every node's representative to itself
      representatives.put(maze.get(n).posn, maze.get(n).posn);
    }

    for (int i = 0; i < worklist.size(); i++) {
      if (this.find(worklist.get(i).in.posn, representatives)
          .equals(this.find(worklist.get(i).out.posn, representatives))) {
        // they're already connected
      }
      else {
        edgesInTree.add(worklist.get(i));
        representatives = this.union(representatives,
            this.find(worklist.get(i).in.posn, representatives),
            this.find(worklist.get(i).out.posn, representatives));
      }
    }

    return edgesInTree;
  }

  // find operation in union/find
  Posn find(Posn posn, HashMap<Posn, Posn> representatives) {
    if (representatives.get(posn) != posn) {
      posn = this.find(representatives.get(posn), representatives);
    }

    return posn;
  }

  // union operation in union/find
  HashMap<Posn, Posn> union(HashMap<Posn, Posn> representatives, Posn inPosn, Posn outPosn) {
    representatives.replace(inPosn, outPosn);
    return representatives;
  }

  // quicksort edges in ascending order by weight
  void quicksort(ArrayList<Edge> array, int lowIndex, int highIndex) {
    if (lowIndex >= highIndex) {
      return;
    }

    int pivot = array.get(highIndex).weight;
    int leftPointer = lowIndex;
    int rightPointer = highIndex;

    while (leftPointer < rightPointer) {
      while (array.get(leftPointer).weight <= pivot && leftPointer < rightPointer) {
        leftPointer++;
      }
      while (array.get(rightPointer).weight >= pivot && leftPointer < rightPointer) {
        rightPointer--;
      }
      this.swap(array, leftPointer, rightPointer);
    }
    this.swap(array, leftPointer, highIndex);

    quicksort(array, lowIndex, leftPointer - 1);
    quicksort(array, leftPointer + 1, highIndex);
  }

  void swap(ArrayList<Edge> array, int index1, int index2) {
    Edge temp = array.get(index1);
    array.set(index1, array.get(index2));
    array.set(index2, temp);
  }
}

class ExamplesMaze {
  public static final int SCENE_SIZE = 15;
  int sizeX = 80;
  int sizeY = 50;

  boolean testFloodItWorld(Tester t) {
    MazeWorld starterWorld = new MazeWorld(sizeX, sizeY, new ArrayList<Node>(), 0);
    return starterWorld.bigBang(SCENE_SIZE * sizeX, SCENE_SIZE * sizeY, 0.0003);
  }

  void testQuickSort(Tester t) {
    MazeWorld ex = new MazeWorld(4, 4, new ArrayList<Node>(), 0);
    ex.makeGraph();
    ArrayList<Edge> worklist = new ArrayList<Edge>();
    for (Node n : ex.maze) {
      for (Edge e : n.outEdges) {
        worklist.add(e);
      }
    }
    ex.quicksort(worklist, 0, worklist.size() - 1);
    for (int i = 0; i < worklist.size() - 1; i++) {
      t.checkExpect(worklist.get(i).weight <= worklist.get(i + 1).weight, true);
    }

  }

  void testFind(Tester t) {
    MazeWorld ex = new MazeWorld(4, 4, new ArrayList<Node>(), 0);
    HashMap<Posn, Posn> representatives = new HashMap<Posn, Posn>();
    representatives.put(new Posn(0, 3), new Posn(0, 3));
    representatives.put(new Posn(0, 1), new Posn(0, 3));
    representatives.put(new Posn(0, 2), new Posn(0, 3));
    representatives.put(new Posn(0, 4), new Posn(0, 3));
    representatives.put(new Posn(0, 5), new Posn(0, 3));
    representatives.put(new Posn(0, 6), new Posn(0, 5));
    representatives.put(new Posn(0, 7), new Posn(0, 5));

    t.checkExpect(ex.find(new Posn(0, 7), representatives), new Posn(0, 3));
    t.checkExpect(ex.find(new Posn(0, 4), representatives), new Posn(0, 3));
    t.checkExpect(ex.find(new Posn(0, 3), representatives), new Posn(0, 3));
  }

  void testUnion(Tester t) {
    MazeWorld ex = new MazeWorld(4, 4, new ArrayList<Node>(), 0);
    HashMap<Posn, Posn> representatives = new HashMap<Posn, Posn>();
    representatives.put(new Posn(0, 3), new Posn(0, 3));
    representatives.put(new Posn(0, 1), new Posn(0, 3));
    representatives.put(new Posn(0, 2), new Posn(0, 3));
    representatives.put(new Posn(0, 4), new Posn(0, 3));
    representatives.put(new Posn(0, 5), new Posn(0, 5));
    representatives.put(new Posn(0, 6), new Posn(0, 5));
    representatives.put(new Posn(0, 7), new Posn(0, 5));

    ex.union(representatives, new Posn(0, 5), new Posn(0, 3));
    t.checkExpect(ex.find(new Posn(0, 7), representatives), new Posn(0, 3));

  }

  void testMakeGraph(Tester t) {
    MazeWorld ex = new MazeWorld(4, 4, new ArrayList<Node>(), 0);
    ex.makeGraph();
    t.checkExpect(ex.maze.get(5).posn, new Posn(1, 1));
    t.checkExpect(ex.maze.get(15).posn, new Posn(3, 3));
  }

  void testRunKruskal(Tester t) {
    MazeWorld ex = new MazeWorld(2, 1, new ArrayList<Node>(), 0);
    ex.maze = new ArrayList<Node>();
    Node node1 = new Node(new Posn(0, 0), Color.LIGHT_GRAY, new ArrayList<Edge>());
    Node node2 = new Node(new Posn(0, 0), Color.LIGHT_GRAY, new ArrayList<Edge>());
    Edge edge1 = new Edge(node1, node2, 1);
    Edge edge2 = new Edge(node2, node1, 0);
    node1.outEdges.add(edge1);
    node2.outEdges.add(edge2);
    ex.maze.add(node1);
    ex.maze.add(node2);
    ArrayList<Edge> arr = ex.runKruskal();
    ArrayList<Edge> expect = new ArrayList<Edge>();
    // expect.add(edge2);
    t.checkExpect(arr, expect);
  }

  void testSearchInTree(Tester t) {

    MazeWorld ex = new MazeWorld(4, 4, new ArrayList<Node>(), 0);
    ex.makeGraph();
    ex.makeScene();

    SearchAlgorithm sa = new SearchAlgorithm(ex.mazeWidth, ex.mazeHeight, ex.maze, ex.maze.get(0),
        ex.maze.get(ex.mazeHeight * ex.mazeWidth - 1), ex.edgesInTree, new Stack<Node>(),
        new ArrayDeque<Node>(), new HashMap<Node, Edge>());

    t.checkExpect(sa.searchInTree(ex.maze.get(2), ex.maze.get(3)), true);

  }

  void testSearchOnTick(Tester t) {
    MazeWorld ex = new MazeWorld(4, 4, new ArrayList<Node>(), 0);
    ex.makeGraph();
    ex.makeScene();

    SearchAlgorithm sa = new SearchAlgorithm(ex.mazeWidth, ex.mazeHeight, ex.maze, ex.maze.get(0),
        ex.maze.get(ex.mazeHeight * ex.mazeWidth - 1), ex.edgesInTree, new Stack<Node>(),
        new ArrayDeque<Node>(), new HashMap<Node, Edge>());

    t.checkExpect(sa.onTick(), new SearchAlgorithm(sa.mazeWidth, sa.mazeHeight, sa.maze, sa.src,
        sa.dest, sa.edgesInTree, sa.worklist, sa.alreadySeen, sa.cameFromEdge));

  }
}
