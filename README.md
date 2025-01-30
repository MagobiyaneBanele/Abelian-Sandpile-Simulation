
Parallelization of the Abelian Sandpile Simulation 

This assignment focuses on parallelizing the Abelian Sandpile simulation using Java’s Fork/Join framework, aiming to reduce computation time for larger grids. The hypothesis is that parallelization will significantly improve performance, especially as grid sizes and hardware capabilities increase.

The simulation grid is partitioned into row segments, which are processed concurrently by tasks. Each task uses the GridUpdate class (extending RecursiveAction) to handle grid updates, recursively dividing tasks until the workload is small enough to be processed sequentially. 
This divide-and-conquer approach ensures efficient parallel execution across available CPU cores.
The algorithm’s correctness was validated by comparing outputs from both parallel and sequential implementations, confirming that the parallel version produces the same results but faster. Performance benchmarking showed significant speedup with larger grid sizes, highlighting the benefits of parallelism.
However, for smaller grids, the overhead of task management limited the speedup.
Performance also varied between different hardware environments, with the departmental server performing better for larger grids due to more CPU cores.
The project demonstrates the effectiveness of the Fork/Join framework for computationally intensive simulations, emphasizing the importance of optimizing task splitting and synchronizing parallel tasks.

Acknowledgements
Special thanks to Professor Michelle Kuttel for their guidance and support throughout this project.


