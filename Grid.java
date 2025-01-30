package parallelAbelianSandpile;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

// This class represents the grid for the Abelian Sandpile cellular automaton simulation.
public class Grid {
    private int rows, columns;   // The number of rows and columns in the grid, including the "sink" border.
    public int[][] grid;         // The grid representing the current state of the simulation.
    public int[][] updateGrid;   // The grid representing the next state of the simulation.

    // Constructor to initialize the grid with given dimensions (width and height).
    // An additional border (the "sink") is added around the grid.
    public Grid(int w, int h) {
        rows = w + 2;            // Adding 2 to the width for the "sink" border.
        columns = h + 2;         // Adding 2 to the height for the "sink" border.
        grid = new int[this.rows][this.columns];        // Initializing the grid.
        updateGrid = new int[this.rows][this.columns];  // Initializing the update grid.

        // Initializing both the grid and updateGrid with zeroes.
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.columns; j++) {
                grid[i][j] = 0;
                updateGrid[i][j] = 0;
            }
        }
    }

    // Constructor to initialize the grid from an existing 2D array.
    public Grid(int[][] newGrid) {
        this(newGrid.length, newGrid[0].length); // Calls the main constructor above with the array dimensions.
        
        // Copies the input array values into the grid, excluding the "sink" border.
        for (int i = 1; i < rows - 1; i++) {
            for (int j = 1; j < columns - 1; j++) {
                this.grid[i][j] = newGrid[i - 1][j - 1];
            }
        }
    }

    // Copy constructor to create a new Grid object from an existing one.
    public Grid(Grid copyGrid) {
        this(copyGrid.rows, copyGrid.columns);  // Calls the main constructor above with the same dimensions.
        
        // Copies the values from the input grid into this grid.
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                this.grid[i][j] = copyGrid.get(i, j);
            }
        }
    }

    // Method to get the number of rows excluding the "sink" border.
    public int getRows() {
        return rows - 2;
    }

    // Method to get the number of columns excluding the "sink" border.
    public int getColumns() {
        return columns - 2;
    }

    // Method to get the value at a specific cell in the grid.
    public int get(int i, int j) {
        return this.grid[i][j];
    }

    // Method to set all grid cells (excluding the borders) to a specified value.
    public void setAll(int value) {
        // Borders are not modified and remain as zero.
        for (int i = 1; i < rows - 1; i++) {
            for (int j = 1; j < columns - 1; j++)
                grid[i][j] = value;
        }
    }

    // Method to copy the values from updateGrid to grid for the next time step in the simulation.
    public void nextTimeStep() {
        for (int i = 1; i < rows - 1; i++) {
            for (int j = 1; j < columns - 1; j++) {
                this.grid[i][j] = updateGrid[i][j];
            }
        }
    }

    // Method to update the grid to the next state based on the rules of the Abelian Sandpile model.
    public boolean update() {
        boolean change = false;
        // Update grid cells, excluding the borders.
        for (int i = 1; i < rows - 1; i++) {
            for (int j = 1; j < columns - 1; j++) {
                // Compute the next state for each cell based on its current state and the states of its neighbors.
                updateGrid[i][j] = (grid[i][j] % 4) +
                        (grid[i - 1][j] / 4) +
                        grid[i + 1][j] / 4 +
                        grid[i][j - 1] / 4 +
                        grid[i][j + 1] / 4;
                
                // Check if there is any change in the grid.
                if (grid[i][j] != updateGrid[i][j]) {
                    change = true;
                }
            }
        }
        // If there was any change, update the grid to the new state.
        if (change) {
            nextTimeStep();
        }
        return change;
    }

    // Method to print the grid to the console in a formatted manner.
    public void printGrid() {
        int i, j;
        // Prints a header row of dashes.
        System.out.printf("Grid:\n");
        System.out.printf("+");
        for (j = 1; j < columns - 1; j++) System.out.printf("  --");
        System.out.printf("+\n");

        // Prints each row of the grid.
        for (i = 1; i < rows - 1; i++) {
            System.out.printf("|");
            for (j = 1; j < columns - 1; j++) {
                if (grid[i][j] > 0)
                    System.out.printf("%4d", grid[i][j]); // Prints the cell value if greater than 0.
                else
                    System.out.printf("    "); // Prints spaces for empty cells.
            }
            System.out.printf("|\n");
        }

        // Prints a footer row of dashes.
        System.out.printf("+");
        for (j = 1; j < columns - 1; j++) System.out.printf("  --");
        System.out.printf("+\n\n");
    }

    // Method to write the grid to an image file.
    public void gridToImage(String fileName) throws IOException {
        BufferedImage dstImage = new BufferedImage(rows, columns, BufferedImage.TYPE_INT_ARGB);

        // Variables to hold color values.
        int a = 0;
        int g = 0; // Green component
        int b = 0; // Blue component
        int r = 0; // Red component

        // Convert the grid cells to pixel colors and store them in the image.
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                g = 0; // Reset green
                b = 0; // Reset blue
                r = 0; // Reset red

                switch (grid[i][j]) {
                    case 0:
                        break;        // Black
                    case 1:
                        g = 255;      // Green for value 1
                        break;
                    case 2:
                        b = 255;      // Blue for value 2
                        break;
                    case 3:
                        r = 255;      // Red for value 3
                        break;
                    default:
                        break;
                }
                // Assemble the ARGB pixel value.
                int dpixel = (0xff000000) | (a << 24) | (r << 16) | (g << 8) | b;
                dstImage.setRGB(i, j, dpixel); // Set the pixel color in the image.
            }
        }

        // Write the image to a file with the specified file name.
        File dstFile = new File(fileName);
        ImageIO.write(dstImage, "png", dstFile);
    }
}
