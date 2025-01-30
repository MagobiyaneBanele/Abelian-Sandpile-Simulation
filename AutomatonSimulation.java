package parallelAbelianSandpile;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ForkJoinPool;

public class AutomatonSimulation {
    // Debug flag for additional output
    static final boolean DEBUG = false;

    // Variables to track the start and end time of the simulation
    static long startTime = 0;
    static long endTime = 0;

    // Method to start the timer
    private static void tick() {
        startTime = System.currentTimeMillis();
    }

    // Method to stop the timer and calculate elapsed time
    private static void tock() {
        endTime = System.currentTimeMillis();
    }

    /**
     * Reads a 2D array from a CSV file. The first row of the CSV contains the dimensions
     * of the array (width and height). Subsequent rows contain the array data.
     *
     * @param filePath the path to the CSV file
     * @return a 2D array of integers read from the CSV file
     */
    public static int[][] readArrayFromCSV(String filePath) {
        int[][] array = null;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine();
            if (line != null) {
                // The first line contains the width and height of the array
                String[] dimensions = line.split(",");
                int width = Integer.parseInt(dimensions[0]);
                int height = Integer.parseInt(dimensions[1]);
                System.out.printf("Rows: %d, Columns: %d\n", width, height);

                array = new int[height][width];
                int rowIndex = 0;

                // Read the array data from the file
                while ((line = br.readLine()) != null && rowIndex < height) {
                    String[] values = line.split(",");
                    for (int colIndex = 0; colIndex < width; colIndex++) {
                        array[rowIndex][colIndex] = Integer.parseInt(values[colIndex]);
                    }
                    rowIndex++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return array;
    }

    public static void main(String[] args) throws IOException {
        // Create a Grid object for the simulation
        Grid simulationGrid;
        // Create a ForkJoinPool for parallel execution
        ForkJoinPool forkJoinPool = new ForkJoinPool();

        // Ensure that exactly two command-line arguments are provided
        if (args.length != 2) {
            System.out.println("Incorrect number of command line arguments provided.");
            System.exit(0);
        }

        // Get the input and output file names from command-line arguments
        String inputFileName = args[0];
        String outputFileName = args[1];

        // Initialize the grid by reading the input file
        simulationGrid = new Grid(readArrayFromCSV(inputFileName));

        int counter = 0; // Step counter for the simulation
        tick(); // Start timing the simulation
        if (DEBUG) {
            System.out.printf("starting config: %d \n", counter);
            simulationGrid.printGrid();
        }

        boolean change;
        do {
            // Create a GridUpdate task to update the grid in parallel
            GridUpdate task = new GridUpdate(simulationGrid, 1, simulationGrid.getRows() + 1);
            forkJoinPool.invoke(task); // Execute the task

            change = false;
            /*
             * The following code iterates through each cell of the grid, excluding the border, to determine if any changes have occurred 
             * between the current state (simulationGrid.grid) and the next state (simulationGrid.updateGrid). The outer loop traverses 
             * each row, while the inner loop examines each column within that row. For each cell, it compares the value in the 
             * current grid with the value in the update grid. If a discrepancy is found, indicating a change, the change flag is 
             * set to true and the inner loop is terminated to stop further checks for that row. If a change is detected, the outer 
             * loop also breaks to avoid unnecessary comparisons, as we only need to confirm the presence of any changes to decide 
             * whether to continue the simulation.
             */
            for (int i = 1; i < simulationGrid.getRows() + 1; i++) {
                for (int j = 1; j < simulationGrid.getColumns() + 1; j++) {
                    if (simulationGrid.grid[i][j] != simulationGrid.updateGrid[i][j]) {
                        change = true;
                        break;
                    }
                }
                if (change) break;
            }
            if (change) simulationGrid.nextTimeStep(); // if change has happened then Update the grid to the next state
            counter++; // Increment the step counter
        } while (change); // Repeat until no changes occur (steady state)

        tock(); // Stop timing the simulation

        System.out.println("Simulation complete, writing image...");
        // Convert the final grid state to an image and save it to a file
        simulationGrid.gridToImage(outputFileName);

        // Output the simulation results
        System.out.printf("\t Rows: %d, Columns: %d\n", simulationGrid.getRows(), simulationGrid.getColumns());
        System.out.printf("Number of steps to stable state: %d \n", counter);
        System.out.printf("Time: %d ms\n", endTime - startTime);

        // Shut down the ForkJoinPool
        forkJoinPool.shutdown();
    }
}
