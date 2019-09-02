# campusPaths

In this project, I wrote a Generic route-finding tool to find the shortest walking route between any two buildings on the UW campus. I implemented Dijkstra's algorithm and built a GUI for the route-finding tool using React and the Spark Java Framework. 

## Important Files to look at in the [bots folder](https://github.com/alexismacaskilll/chessBot/blob/master/src/chess/bots)

- [SparkServer](https://github.com/alexismacaskilll/campusPaths/blob/master/src/main/java/campuspaths/SparkServer.java) implements the ???? 
      -details
- [ParallelSearcher](https://github.com/alexismacaskilll/chessBot/blob/master/src/chess/bots/ParallelSearcher.java) implements the parallel Minimax algorithm.
      -Minimax is a naturally parallelizable algorithm. Each “node” of the game tree can be run on independent threads. 
- [AlphaBetaSearcher](https://github.com/alexismacaskilll/chessBot/blob/master/src/chess/bots/AlphaBetaSearcher.java) implements the sequential Alpha-Beta Pruning algorithm. 
      -Alpha-beta Pruning is a more efficient version of Minimax that avoids considering branches of the game tree that are irrelevant. Before getting too deep into the algorithm, it is very important to note that a correct Alpha-beta Search will return the same answer as Minimax. In other words, it is not an approximation algorithm, it only ignores moves that cannot change the answer.
- [JamboreeSearcher](https://github.com/alexismacaskilll/chessBot/blob/master/src/chess/bots/JamboreeSearcher.java) implements the parallel Alpha-Beta Pruning algorithm.
      -Unfortunately, unlike minimax, alphabeta is not “naturally parallelizable”. In particular, if I attempt to parallelize the loop, it will be unable to propogate the new alpha and beta values to each iteration. This would result in us evaluating unnecessary parts of the tree. In practice, however, it turns out that that this is an acceptable loss, because the parallelism still gives us an overall benefit.

## Provided Code

I did not write the code found in cse332.* and chess.board.
