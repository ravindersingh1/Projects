
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class MazeDriver {
	public static void main(String[] args) {
		// prompt the user for the maze size and create a maze of that size
	    // We have not done any error checking in this section and assuming that the user
	    // will input only integer from (4, 5, 6, 7, 8, 10);
		Scanner keyboard = new Scanner(System.in); // scanner class to take user input
		System.out.print("Enter maze size(4, 5, 6, 7, 8, 10): ");
		int size = keyboard.nextInt(); 
				
		Maze maze = new Maze(size);  //maze of the size equal to user input
		
		// print the original empty perfect maze
		System.out.println();
		maze.draw(Maze.MAZE);
		
		// run DFS based algorithm to generate the path from starting room to finishing room
		// and draw the mazes showing the resulting visitation order and path
		System.out.println();
		System.out.println("Solved with DFS:");
		System.out.println();
		maze.DFS();
		maze.draw(Maze.VISITATION_ORDER);
		System.out.println();
		maze.draw(Maze.PATH);
		
		// run BFS based algorithm to generate the path from starting room to finishing room
		// and draw the mazes showing the resulting visitation order and path
		System.out.println();
		System.out.println("Solved with BFS:");
		System.out.println();
		maze.BFS();
		maze.draw(Maze.VISITATION_ORDER);
		System.out.println();
		maze.draw(Maze.PATH);
	}

}
//Stack, implemented as a ArrayList
class CellStack {
	ArrayList<Room> elements;

	public CellStack() {
		elements = new ArrayList<Room>();
		
	}

	public void push(Room e) {
		//e.setVisitationOrder(elements.size());
		elements.add(e);
	}
    // throws exception if ArrayList is empty 
	public Room pop() {
		if (elements.isEmpty())
			throw new IndexOutOfBoundsException();
		
	    return elements.remove(elements.size() - 1);
	}
}
// Queue implemented as a ArrayList
class CellQueue {
	ArrayList<Room> elements; // ArrayList for Rooms
	int numEnqueuedBefore; // number of elements enqueued before

	public CellQueue() {
		elements = new ArrayList<Room>();
		numEnqueuedBefore = 0;
	}
	//to see if the ArrayList is empty
	public boolean isEmpty() {
		return elements.isEmpty();
	}
    //takes a Room as a parameter and adds it to queue
	public void enqueue(Room e) {
		e.setVisitationOrder(numEnqueuedBefore);
		elements.add(e);
		numEnqueuedBefore++;
	}
   // dequeue room from queue
	public Room dequeue() {
		if (elements.isEmpty())
			throw new IndexOutOfBoundsException();

		return elements.remove(0);
	}
}
 /**
  * class Maze represents an r-by-r maze
  */
class Maze {
	private Room[][] rooms; // rooms in the maze as a two-dimensional array
	private int dimension; // maze dimension, number of rows and columns in the maze
	private int DFSVisitationOrder;  // DFS visitation order number for each room
	// indicates what type of maze diagram to draw
	static final int MAZE = 0;  // perfect maze
	static final int VISITATION_ORDER = 1; // visitation order for algorithms
	static final int PATH = 2;  // path from starting point to end point
	
   // User input number of columns/rows to draw a maze
	public Maze(int r) {
		dimension = r;
		rooms = new Room[r][r];
		for (int i = 0; i < r; i++)
			for (int j = 0; j < r; j++)
				rooms[i][j] = new Room(i, j);
			
		rooms[0][0].removeWall(Room.NORTH);
		

		CellStack cellStack = new CellStack(); // Instantiating stack
		int totalCells = r * r;                // matrix or r*r
		Room currentCell = rooms[0][0];        // current cell is the first element in the arrayList
		int visitedCells = 1;

		while (visitedCells < totalCells) {
			ArrayList<Room> intactNeighbors = new ArrayList<Room>();
			ArrayList<Integer> wallSharedWithNeighbor = new ArrayList<Integer>(); // to keep track of walls
			// find all neighbors of currentCell with all walls intact
			// visit neighbors in the order: E, S, W, N
			// explore neighbor on EAST
			if (currentCell.col < r-1 && rooms[currentCell.row][currentCell.col+1].allWallsIntact()) {
				intactNeighbors.add(rooms[currentCell.row][currentCell.col+1]);
				wallSharedWithNeighbor.add(Room.EAST);
			}
			// explore neighbor on SOUTH
			if (currentCell.row < r-1 && rooms[currentCell.row+1][currentCell.col].allWallsIntact()) {
				intactNeighbors.add(rooms[currentCell.row+1][currentCell.col]);
				wallSharedWithNeighbor.add(Room.SOUTH);
			}
			// explore neighbor on WEST
			if (currentCell.col > 0 && rooms[currentCell.row][currentCell.col-1].allWallsIntact()) {
				intactNeighbors.add(rooms[currentCell.row][currentCell.col-1]);
				wallSharedWithNeighbor.add(Room.WEST);
			}
			// explore neighbor on NORTH
			if (currentCell.row > 0 && rooms[currentCell.row-1][currentCell.col].allWallsIntact()) {
				intactNeighbors.add(rooms[currentCell.row-1][currentCell.col]);
				wallSharedWithNeighbor.add(Room.NORTH);
			}
			
			// if one or more found, choose one at random
			if (!intactNeighbors.isEmpty()) {
				Random rand = new Random();
				int which = rand.nextInt(intactNeighbors.size());
				
				// knock down the wall between it and current cell
				currentCell.removeWall(wallSharedWithNeighbor.get(which));
				switch (wallSharedWithNeighbor.get(which)) {
				case Room.EAST:
					intactNeighbors.get(which).removeWall(Room.WEST);
					break;
				case Room.SOUTH:
					intactNeighbors.get(which).removeWall(Room.NORTH);
					break;
				case Room.WEST:
					intactNeighbors.get(which).removeWall(Room.EAST);
					break;
				case Room.NORTH:
					intactNeighbors.get(which).removeWall(Room.SOUTH);
					break;
				}
				
				//push current cell location on the cell stack
				cellStack.push(currentCell);
				
				// make the new cell current cell
				currentCell = intactNeighbors.get(which);
				
				// add 1 to visited cells
				visitedCells++;
			} else {
				// pop the most recent cell entry off the cell stack
				// make it current cell
				currentCell = cellStack.pop();
			}

		}
		rooms[r - 1][r - 1].removeWall(Room.SOUTH);
	}
	
	/*
	 * DFS based algorithm to find the path from starting room to finishing room
	 */
	public void DFS() {
		resetRoomAttributes();
		DFSVisitationOrder = 0; // visitation order for the algorithm
		DFSVisit(rooms[0][0]);
		backtrackPath();
	}
	private void DFSVisit(Room currentCell) {
		currentCell.visitationOrder = DFSVisitationOrder; 
		DFSVisitationOrder++;
		currentCell.color = Room.GRAY;
		
		// terminate DFS recursion on visiting the finishing room
		if (currentCell.row == dimension - 1 && 
				currentCell.col == dimension - 1)
			return;
		
		// explore neighbor on EAST
		if (currentCell.col < dimension - 1 && !currentCell.walls[Room.EAST]) {
			Room curNeighbor = rooms[currentCell.row][currentCell.col + 1];
			if (curNeighbor.color == Room.WHITE) {
				curNeighbor.parent = currentCell;
				DFSVisit(curNeighbor);
			}
		}
		// explore neighbor on SOUTH
		if (rooms[dimension-1][dimension-1].color == Room.WHITE && currentCell.row < dimension - 1 && !currentCell.walls[Room.SOUTH]) {
			Room curNeighbor = rooms[currentCell.row + 1][currentCell.col];
			if (curNeighbor.color == Room.WHITE) {
				curNeighbor.parent = currentCell;
				DFSVisit(curNeighbor);
			}
		}

		// explore neighbor on WEST
		if (rooms[dimension-1][dimension-1].color == Room.WHITE && currentCell.col > 0 && !currentCell.walls[Room.WEST]) {
			Room curNeighbor = rooms[currentCell.row][currentCell.col - 1];
			if (curNeighbor.color == Room.WHITE) {
				curNeighbor.parent = currentCell;
				DFSVisit(curNeighbor);
			}
		}

		// explore neighbor on NORTH
		if (rooms[dimension-1][dimension-1].color == Room.WHITE && currentCell.row > 0 && !currentCell.walls[Room.NORTH]) {
			Room curNeighbor = rooms[currentCell.row - 1][currentCell.col];
			if (curNeighbor.color == Room.WHITE) {
				curNeighbor.parent = currentCell;
				DFSVisit(curNeighbor);
			}
		}
		currentCell.color = Room.BLACK; //once all the neighbors are explored
	}
	
	/*
	 * BFS based algorithm to find the path from starting room to finishing room
	 */
	public void BFS() {
		resetRoomAttributes();
		rooms[0][0].color = Room.GRAY;
		
		CellQueue cellQ = new CellQueue();
		cellQ.enqueue(rooms[0][0]);
		
		while (!cellQ.isEmpty()) {
			Room currentCell = cellQ.dequeue();
			

			// explore neighbor on EAST
			if (currentCell.col < dimension - 1
					&& !currentCell.walls[Room.EAST]) {
				Room curNeighbor = rooms[currentCell.row][currentCell.col + 1];
				if (curNeighbor.color == Room.WHITE) {
					curNeighbor.color = Room.GRAY;
					curNeighbor.parent = currentCell;
					cellQ.enqueue(curNeighbor);
					// terminate the loop on visiting the finishing room
					if (curNeighbor.row == dimension - 1 && 
							curNeighbor.col == dimension - 1)
						break;
				}
			}
			// explore neighbor on SOUTH
			if (currentCell.row < dimension - 1
					&& !currentCell.walls[Room.SOUTH]) {
				Room curNeighbor = rooms[currentCell.row+1][currentCell.col];
				if (curNeighbor.color == Room.WHITE) {
					curNeighbor.color = Room.GRAY;
					curNeighbor.parent = currentCell;
					cellQ.enqueue(curNeighbor);
					// terminate the loop on visiting the finishing room
					if (curNeighbor.row == dimension - 1 && 
							curNeighbor.col == dimension - 1)
						break;
				}
			}

			// explore neighbor on WEST
			if (currentCell.col > 0
					&& !currentCell.walls[Room.WEST]) {
				Room curNeighbor = rooms[currentCell.row][currentCell.col - 1];
				if (curNeighbor.color == Room.WHITE) {
					curNeighbor.color = Room.GRAY;
					curNeighbor.parent = currentCell;
					cellQ.enqueue(curNeighbor);
				}
			}

			// explore neighbor on NORTH
			if (currentCell.row > 0
					&& !currentCell.walls[Room.NORTH]) {
				Room curNeighbor = rooms[currentCell.row-1][currentCell.col];
				if (curNeighbor.color == Room.WHITE) {
					curNeighbor.color = Room.GRAY;
					curNeighbor.parent = currentCell;
					cellQ.enqueue(curNeighbor);
				}
			}
			currentCell.color = Room.BLACK;

		}
		backtrackPath();

	}

	/*
	 * draw the maze with 3 drawing modes:
	 * MAZE: draw the empty perfect maze
	 * VISITATION_ORDER: draw the maze with room visitation order numbers;
	 * PATH: draw the maze with the path from starting room to finishing room, showing # for the
	 *       walls and rooms on the path
	 */
	public void draw(int mode) {
		for (int i = 0; i < dimension; i++) {
			// for each cell row, draw upper left corner,
			// North wall for each cell on first line
			
			for (int j = 0; j < dimension; j++) {
				System.out.print('+');
				if (rooms[i][j].walls[Room.NORTH])
					System.out.print('-');
				else if (mode == PATH && i > 0 &&
						(rooms[i-1][j].successor == rooms[i][j] ||
						rooms[i][j].successor == rooms[i-1][j]))
					System.out.print('#');
				else
					System.out.print(' ');	
			}
			// draw upper right corner of last cell in row
			System.out.println('+');
			
			// then draw left wall and room for each cell on second line
			for (int j = 0; j < dimension; j++) {
				//System.out.print(rooms[i][j].walls[Room.WEST]? '|': ' ');
				if (rooms[i][j].walls[Room.WEST])
					System.out.print('|');
				else if (mode == PATH && j > 0 && 
						(rooms[i][j-1].successor == rooms[i][j] ||
								rooms[i][j].successor == rooms[i][j-1]))
					System.out.print('#');
				else
					System.out.print(' ');	
				switch (mode) {
				case MAZE:
					System.out.print(' ');
					break;
				case VISITATION_ORDER:
					if (rooms[i][j].visitationOrder == -1)
						System.out.print(' ');
					else
						System.out.print(rooms[i][j].visitationOrder % 10);
					break;
				case PATH:
					if (rooms[i][j].visitationOrder != -1 &&
					(rooms[i][j].parent == null || rooms[i][j].parent.successor == rooms[i][j]))
						System.out.print('#');
					else
						System.out.print(' ');
					break;
				}
			}
			// draw EAST wall of last cell in row
			System.out.println('|');
		}
		// draw south corners and walls of the entire maze
		for (int j = 0; j < dimension; j++) {
			System.out.print('+');
			System.out.print(rooms[dimension-1][j].walls[Room.SOUTH]? '-': ' ');
		}
		// draw lower left corner of finishing room
		System.out.println('+');

	}
	/*
	 * back track the unique path from starting room to finishing room
	 * starting with the finishing room, following the parent link to
	 * the predecessor in the path and update the path successor of the
	 * predecessor
	 * 
	 * called as the last step in the DFS and BFS based algorithms to find the path
	 * from the starting room to the finishing room
	 */
	private void backtrackPath() {
		for (Room room = rooms[dimension-1][dimension-1]; room != null; 
				room = room.parent) {
			if (room.parent != null)
				room.parent.successor = room;
		}
	}
	
	/*
	 * reset all room attributes for rooms in the maze
	 * Has to be called as the first thing in DFS and BFS based algorithms to find the path from starting
	 * room to finishing room
	 */
	private void resetRoomAttributes() {
		for (int i = 0; i < dimension; i++) {
			for (int j = 0; j < dimension; j++) {
				rooms[i][j].parent = null;
				rooms[i][j].color = Room.WHITE;
				rooms[i][j].successor = null;
				rooms[i][j].visitationOrder = -1;
			}
		}
	}
}
/*
 * class Room represent a room in the maze
 */
class Room {
	// color values
	static final int WHITE = 0; // unvisited room
	static final int GRAY = 1; // visited but have not finished visiting all the neighbors 
	static final int BLACK = 2; // All neighbors have been visited
	
	int row; // row index of this room in the grid of rooms
	int col; // column index of this room in the grid of rooms
	int visitationOrder; // visitation order number
	Room parent; // parent of this room in the path from starting room to finishing room
	Room successor; // successor of this room in the path from starting room to finishing room
	int color; 
	
	// 4 sides of walls of a room
	public static final int EAST = 0;
	public static final int SOUTH = 1;
	public static final int WEST = 2;
	public static final int NORTH = 3;
	
	boolean[] walls; // one entry for each wall, true if the wall is not removed

	public Room(int r, int c) {
		row = r;
		col = c;
		color = WHITE;
		visitationOrder = -1; 
		walls = new boolean[NORTH+1];
		for (int i = EAST; i <= NORTH; i++) //Initially, setting all walls intact
			walls[i] = true;
	}
	/*
	 * returns true if all 4 walls of this room are intact
	 */
	public boolean allWallsIntact() {
		return walls[WEST] && walls[NORTH] && walls[EAST] && walls[SOUTH];
	}

	public void removeWall(int whichSide) {
		if (whichSide < 0 || whichSide > 3)
			throw new IndexOutOfBoundsException();
		walls[whichSide] = false;
	}
	public int getVisitationOrder() {
		return visitationOrder;
	}
	public void setVisitationOrder(int n) {
		visitationOrder = n;
	}
}

