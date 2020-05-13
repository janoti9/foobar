/*
Prepare the Bunnies' Escape
===========================
You're awfully close to destroying the LAMBCHOP doomsday device and freeing Commander Lambda's bunny prisoners, 
but once they're free of the prison blocks, the bunnies are going to need to escape Lambda's space station via the escape pods as quickly as possible. 
Unfortunately, the halls of the space station are a maze of corridors and dead ends that will be a deathtrap for the escaping bunnies. 
Fortunately, Commander Lambda has put you in charge of a remodeling project that will give you the opportunity to make things a little easier for the bunnies. 
Unfortunately (again), you can't just remove all obstacles between the bunnies and the escape pods - 
	at most you can remove one wall per escape pod path, both to maintain structural integrity of the station and to avoid arousing Commander Lambda's suspicions. 

You have maps of parts of the space station, each starting at a prison exit and ending at the door to an escape pod. 
The map is represented as a matrix of 0s and 1s, where 0s are passable space and 1s are impassable walls. 
The door out of the prison is at the top left (0,0) and the door into an escape pod is at the bottom right (w-1,h-1). 
Write a function answer(map) that generates the length of the shortest path from the prison door to the escape pod, 
	where you are allowed to remove one wall as part of your remodeling plans. 
The path length is the total number of nodes you pass through, counting both the entrance and exit nodes. 
The starting and ending positions are always passable (0). 
The map will always be solvable, though you may or may not need to remove a wall. 
The height and width of the map can be from 2 to 20. Moves can only be made in cardinal directions; no diagonal moves are allowed.
*/

import java.util.ArrayList;

public class PrepareBunniesEscape {

	class Node {
		int x;
		int y;
		int distance;

		public Node(int x, int y, int distance) {
			this.x = x;
			this.y = y;
			this.distance = distance;
		}
	}

	ArrayList<Node> currentNodes;
	int[][] map;
	int[][] nodeDistances;
	static int height;
	static int width;

	public PrepareBunniesEscape(int[][] map) {
		// initialize
		this.map = map;
		nodeDistances = new int[height][width];
		currentNodes = new ArrayList<Node>();

	}

	int[][] cloneArray(int[][] masterArray) {
		int[][] newArray = new int[height][width];
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				newArray[i][j] = masterArray[i][j];
			}
		}
		return newArray;
	}

	void checkNextLevel() {
		for (int i = currentNodes.size() - 1; i >= 0; i--) {
			int row = currentNodes.get(i).x;
			int column = currentNodes.get(i).y;

			if (nodeDistances[row][column] == 0 || nodeDistances[row][column] > currentNodes.get(i).distance) {
				nodeDistances[row][column] = currentNodes.get(i).distance;

				// check and add up
				if (row > 0 && map[row - 1][column] == 0 // up node is traversable..
						&& (nodeDistances[row - 1][column] == 0
								|| nodeDistances[row - 1][column] > currentNodes.get(i).distance + 1)) {
					currentNodes.add(new Node(row - 1, column, currentNodes.get(i).distance + 1));
				}

				// check and add down
				if (row < height - 1 && map[row + 1][column] == 0 // down node is traversable..
						&& (nodeDistances[row + 1][column] == 0
								|| nodeDistances[row + 1][column] > currentNodes.get(i).distance + 1)) {
					currentNodes.add(new Node(row + 1, column, currentNodes.get(i).distance + 1));
				}

				// check and add left
				if (column > 0 && map[row][column - 1] == 0 // left node is traversable..
						&& (nodeDistances[row][column - 1] == 0
								|| nodeDistances[row][column - 1] > currentNodes.get(i).distance + 1)) {
					currentNodes.add(new Node(row, column - 1, currentNodes.get(i).distance + 1));
				}

				// check and add right
				if (column < width - 1 && map[row][column + 1] == 0 // right node is traversable..
						&& (nodeDistances[row][column + 1] == 0
								|| nodeDistances[row][column + 1] > currentNodes.get(i).distance + 1)) {
					currentNodes.add(new Node(row, column + 1, currentNodes.get(i).distance + 1));
				}
			}

			// we are done with current one.. remove it..
			currentNodes.remove(i);
		}
	}

	public void findNodeDistances() {
		while (currentNodes.size() > 0 && nodeDistances[height - 1][width - 1] == height * width) {
			checkNextLevel();
		}
		currentNodes.clear();
	}

	public static int solution(int[][] map) {
		height = map.length;
		width = map[0].length;

		// find shortest path.. and build distance matrix..
		PrepareBunniesEscape solution = new PrepareBunniesEscape(map);
		solution.currentNodes.add(solution.new Node(0, 0, 1));
		solution.nodeDistances[height - 1][width - 1] = height * width;
		solution.findNodeDistances();

		// now for each untraversable node check if making it traversable makes any
		// difference..
		int minPathLength = solution.nodeDistances[solution.height - 1][solution.width - 1];

		if (minPathLength > height + width - 1) // not if its already smallest path..
		{
			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++) {
					// do if it was untraversable..
					if (map[i][j] == 1) {
						int editedNodeDistance = 0;
						// if left is present.. use left
						if (j > 0 // not the leftmost
								&& solution.nodeDistances[i][j - 1] != 0 // left to it was originally traverseable
						// && minPathLength > solution.nodeDistances[i][j-1] + 1 + i + j //what you are
						// assigning and what actual distance is should be less than already found
						// smaller
						// path..
						) {
							editedNodeDistance = solution.nodeDistances[i][j - 1] + 1;
						}
						// if right is present and smaller.. use right..
						if (j < width - 1 && solution.nodeDistances[i][j + 1] != 0 && (editedNodeDistance == 0
								|| solution.nodeDistances[i][j + 1] + 1 < editedNodeDistance)
						// && minPathLength > solution.nodeDistances[i][j+1] + 1 + i + j
						) {
							editedNodeDistance = solution.nodeDistances[i][j + 1] + 1;
						}
						// if up is present and smaller.. use up..
						if (i > 0 && solution.nodeDistances[i - 1][j] != 0 && (editedNodeDistance == 0
								|| solution.nodeDistances[i - 1][j] + 1 < editedNodeDistance)
						// && minPathLength > solution.nodeDistances[i-1][j] + 1 + i + j
						) {
							editedNodeDistance = solution.nodeDistances[i - 1][j] + 1;
						}
						// if down is present and smaller.. use down..
						if (i < height - 1 && solution.nodeDistances[i + 1][j] != 0 && (editedNodeDistance == 0
								|| solution.nodeDistances[i + 1][j] + 1 < editedNodeDistance)
						// && minPathLength > solution.nodeDistances[i+1][j] + 1 + i + j
						) {
							editedNodeDistance = solution.nodeDistances[i + 1][j] + 1;
						}

						if (editedNodeDistance != 0) {
							PrepareBunniesEscape solutionEdited = new PrepareBunniesEscape(map);
							solutionEdited.nodeDistances = solutionEdited.cloneArray(solution.nodeDistances);
							solutionEdited.currentNodes.add(solutionEdited.new Node(i, j, editedNodeDistance));
							solutionEdited.nodeDistances[height - 1][width - 1] = height * width;
							solutionEdited.findNodeDistances();

							if (solutionEdited.nodeDistances[height - 1][width - 1] != 0
									&& solutionEdited.nodeDistances[height - 1][width - 1] < minPathLength) {
								minPathLength = solutionEdited.nodeDistances[height - 1][width - 1];
								if (minPathLength == height + width - 1)
									break;
							}
						}
					}
				}
				if (minPathLength == height + width - 1)
					break;
			}
		}
		return minPathLength;
	}

	public static void main(String[] args) {
		int[][] map;

		map = new int[][] { { 0, 1, 1, 0 }, { 0, 0, 0, 1 }, { 1, 1, 0, 0 }, { 1, 1, 1, 0 } };
		System.out.println(PrepareBunniesEscape.solution(map));

		map = new int[][] { { 0, 0, 0, 0, 0, 0 }, { 1, 1, 1, 1, 1, 0 }, { 0, 0, 0, 0, 0, 0 }, { 0, 1, 1, 1, 1, 1 },
				{ 0, 1, 1, 1, 1, 1 }, { 0, 0, 0, 0, 0, 0 } };
		System.out.println(PrepareBunniesEscape.solution(map));

	}
}
