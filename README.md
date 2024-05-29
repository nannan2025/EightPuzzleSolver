**EightPuzzleSolver**

EightPuzzleSolver is a Java program that solves the 8-puzzle (3x3 grid with 8 numbered tiles and an empty spot) problem using two search algorithms: Breadth-First Search (BFS) and A* Search.

- Supports solving the 8-puzzle using BFS and A* search algorithms.
- Reads the initial puzzle configuration from a file.
- Prints the sequence of moves to solve the puzzle.
- Displays the number of moves required and the number of nodes expanded during the search.

**Prerequisites**

- Java Development Kit (JDK) 8 or higher

**Installation**

1. Clone the repository:
   
   git clone https://github.com/yourusername/EightPuzzleSolver.git
   cd EightPuzzleSolver

2. Compile the Java program:

   javac EightPuzzleSolver.java

**Usage**

1. Prepare a text file with the initial puzzle configuration. Each number represents a tile, and 0 represents the empty spot. Example file content:

  1 2 3
  8 0 4
  7 6 5

2. Run the program

   java EightPuzzleSolver

