package parallelAbelianSandpile;

import java.util.concurrent.RecursiveAction;

// The GridUpdate class extends RecursiveAction, which is a part of the ForkJoin framework.
// This class is responsible for updating a portion of the grid in parallel.
class GridUpdate extends RecursiveAction {
    
    // Reference to the grid object that holds the current and next states of the simulation grid.
    private final Grid grid;
    
    // The range of rows this task will update.
    private final int startRow, endRow;
    
    // Threshold to decide when to process sequentially or split the task further.
    final static int THRESHOLD = 10;

    // Constructor that initializes the grid and the row range for this task.
    public GridUpdate(Grid grid, int startRow, int endRow) {
        this.grid = grid;
        this.startRow = startRow;
        this.endRow = endRow;
    }

    // The compute method contains the logic to be executed by the ForkJoinPool.
    @Override
    protected void compute() {
        
        // Calculate the number of rows that this task will process.
        int numRows = endRow - startRow;

        // If the number of rows is less than or equal to the threshold, process them sequentially.
        if (numRows <= THRESHOLD) {
            for (int i = startRow; i < endRow; i++) {
                for (int j = 1; j < grid.getColumns() + 1; j++) {
                    // Update each cell in the grid using the sum of grains from neighboring cells.
                    grid.updateGrid[i][j] = (grid.grid[i][j] % 4) +
                                            (grid.grid[i - 1][j] / 4) +
                                            (grid.grid[i + 1][j] / 4) +
                                            (grid.grid[i][j - 1] / 4) +
                                            (grid.grid[i][j + 1] / 4);
                }
            }
        } else {
            // If the number of rows exceeds the threshold, split the task into two subtasks.
            int midRow = (startRow + endRow) / 2;

            // Create two subtasks: one for the top half and one for the bottom half of the row range.
            GridUpdate topHalf = new GridUpdate(grid, startRow, midRow);
            GridUpdate bottomHalf = new GridUpdate(grid, midRow, endRow);
            
            // Fork the top half task to be executed in parallel.
            topHalf.fork();
            
            // Compute the bottom half task sequentially (this thread will continue processing it).
            bottomHalf.compute();
            
            // Wait for the top half task to complete.
            topHalf.join();
        }
    }
}
