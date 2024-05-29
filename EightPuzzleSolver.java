import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class EightPuzzleSolver {

    private static final int[][] targetPuzzle = {{1, 2, 3}, {8, 0, 4}, {7, 6, 5}}; //configuration
    private static final int[][] positions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};   //possible movements

    //Java main function
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the Eight-Puzzle Solver! The program will implement two search algorithms (uninformed and informed) to solve the Puzzle: a 3x3 grid containing 8 tiles and an empty spot. ");

        System.out.print("Enter a file name containing the puzzle that you wish to solve: ");
        String filename = scanner.nextLine();
        int[][] start = new int[3][3];
        try {
            Scanner fileScanner = new Scanner(new File(filename));
            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 3; col++) {
                    if (fileScanner.hasNextInt()) {
                        start[row][col] = fileScanner.nextInt();
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error: File not found.");
            return;
        }

        System.out.println("Initial state of the puzzle:");
        for (int[] row : start) {
            for (int col : row) {
                System.out.print(col + " ");
            }
            System.out.println();
        }

        System.out.print("Enter which algorithm you want to run to solve this puzzle: BFS or A* (pick only one at a time)");
        String algorithm = scanner.nextLine().toUpperCase();
        Result result;
        if ("BFS".equals(algorithm)) {
            result = bfs_search(start);
        } else if ("A*".equals(algorithm)) {
            result = a_star_search(start);
        } else {
            System.out.println("Invalid algorithm selected.");
            return;
        }

        if (result != null) {
            System.out.println("\nPath to goal:");
            for (int[][] state : result.path) {
                for (int[] row : state) {
                    for (int col : row) {
                        System.out.print(col + " ");
                    }
                    System.out.println();
                }
                System.out.println("---");
            }
            System.out.println("Moves required:" + result.movesRequired);
            System.out.println("Expanded nodes: " + result.nodesExpanded);
        } else {
            System.out.println("Solution not found.");
        }
    }

    private static class Node {
        int moves;
        int[][] state;
        List<int[][]> path;

        public Node(int moves, int[][] state, List<int[][]> path) {
            this.moves = moves;
            this.state = state;
            this.path = path;
        }
    }

    private static class Result {
        List<int[][]> path;
        int movesRequired;
        int nodesExpanded;

        public Result(List<int[][]> path, int movesRequired, int nodesExpanded) {
            this.path = path;
            this.movesRequired = movesRequired;
            this.nodesExpanded = nodesExpanded;
        }
    }

    private static class StateComparator implements Comparator<Node> {
        @Override
        public int compare(Node o1, Node o2) {
            return Integer.compare(o1.moves, o2.moves);
        }
    }

    private static int[] findEmptyArea(int[][] grid) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (grid[row][col] == 0) {
                    return new int[]{row, col};
                }
            }
        }
        return null; 
    }

    private static List<int[][]> findNewState(int[][] currentState) {
        int[] emptySpot = findEmptyArea(currentState);
        List<int[][]> newPossibleStates = new ArrayList<>();
        for (int[] position : positions) {
            int newRowPos = emptySpot[0] + position[0];
            int newColPos = emptySpot[1] + position[1];
            if (0 <= newRowPos && newRowPos < 3 && 0 <= newColPos && newColPos < 3) {
                int[][] newState = new int[3][3];
                for (int row = 0; row < 3; row++) {
                    System.arraycopy(currentState[row], 0, newState[row], 0, 3);
                }
                newState[emptySpot[0]][emptySpot[1]] = newState[newRowPos][newColPos];
                newState[newRowPos][newColPos] = 0;
                newPossibleStates.add(newState);
            }
        }
        return newPossibleStates;
    }

    private static int manhattanDistance(int[][] state) {
        int distance = 0;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                int value = state[row][col];
                if (value != 0) {
                    int targetRow = (value - 1) / 3;
                    int targetCol = (value - 1) % 3;
                    distance += Math.abs(row - targetRow) + Math.abs(col - targetCol);
                }
            }
        }
        return distance;
    }

    //BFS algorithm function
    private static Result bfs_search(int[][] initialState) {
        Queue<Node> queue = new LinkedList<>();
        Set<String> seen = new HashSet<>();
        queue.add(new Node(0, initialState, new ArrayList<>()));

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            int[][] current_state = current.state;
            if (Arrays.deepEquals(current_state, targetPuzzle)) {
                return new Result(current.path, current.moves, seen.size());
            }
            seen.add(Arrays.deepToString(current_state));

            for (int[][] next_state : findNewState(current_state)) {
                if (!seen.contains(Arrays.deepToString(next_state))) {
                    List<int[][]> newPath = new ArrayList<>(current.path);
                    newPath.add(next_state);
                    queue.add(new Node(current.moves + 1, next_state, newPath));
                }
            }
        }
        return null;
    }
/* 
    //A* algorithm function
    private static Result a_star_search(int[][] initialState) {
        PriorityQueue<Node> heap = new PriorityQueue<>(11, new StateComparator());
        Set<String> seen = new HashSet<>();
        heap.add(new Node(manhattanDistance(initialState), initialState, new ArrayList<>()));

        while (!heap.isEmpty()) {
            Node current = heap.poll();
            int[][] current_state = current.state;
            if (Arrays.deepEquals(current_state, targetPuzzle)) {
                List<int[][]> fullPath = new ArrayList<>(current.path);
                fullPath.add(current_state);
                return new Result(fullPath, current.moves, seen.size());
            }
            seen.add(Arrays.deepToString(current_state));

            for (int[][] next_state : findNewState(current_state)) {
                if (!seen.contains(Arrays.deepToString(next_state))) {
                    List<int[][]> newPath = new ArrayList<>(current.path);
                    newPath.add(current_state);
                    heap.add(new Node(current.moves + 1 + manhattanDistance(next_state), next_state, newPath));
                }
            }
        }
        return null; 
    }

*/
        //Resubmission
        // A* algorithm function with corrected path length
    private static Result a_star_search(int[][] initialState) {
        // Compares nodes based on the sum of moves and the Manhattan distance (total cost)
        PriorityQueue<Node> heap = new PriorityQueue<>(new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                int cost1 = o1.moves + manhattanDistance(o1.state);
                int cost2 = o2.moves + manhattanDistance(o2.state);
                return Integer.compare(cost1, cost2);
            }
        });

        Set<String> seen = new HashSet<>();
        // Start with the initial state, zero moves, and an empty path
        heap.add(new Node(0, initialState, new ArrayList<>()));

        while (!heap.isEmpty()) {
            Node current = heap.poll();
            int[][] current_state = current.state;
            
            if (Arrays.deepEquals(current_state, targetPuzzle)) {
                // Adding the current state to the path to ensure it includes the goal state
                current.path.add(current_state);
                return new Result(current.path, current.path.size() - 1, seen.size());
            }
            seen.add(Arrays.deepToString(current_state));

            for (int[][] next_state : findNewState(current_state)) {
                if (!seen.contains(Arrays.deepToString(next_state))) {
                    List<int[][]> newPath = new ArrayList<>(current.path);
                    newPath.add(current_state); // Adding the current state to the path
                    // Adding the new node with incremented moves
                    heap.add(new Node(current.moves + 1, next_state, newPath));
                }
            }
        }
        return null; 
    }
}
