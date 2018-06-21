# DistanceVectorRouting

### Overall Design 
This implementation is based on Bellman-Ford Algorithm that calculate shortest path in a network. To start with rtinit(), I create a 4*4 arrays for each router that presentes a routing table where each row represents the destination and column is for neighbor going via from starting router.

  When initialing distance vector table, we set (i, i) to the distance of intial_lkcost[i] if it does not equal to INFINITY.  Otherwise, fill the arrays with INIFINITY because it is unreachable. Extract shortest path to all destinations and remember corresponding next hops. They then each broadcast their new routing table to all their neighbors. In rtupdate(), as each of these neighbors receives this information, they now recalculate the shortest path using it. Consider c(x,v)  + dv(y) as the newest distance if node x received min cost of v going to y, if this distance is less than the current value in DV, we update DV and extract min cost. Before we send out packet to all neighbors, Poison Reverse should be implemented to avoid routing loop. Therefore, if a neighbor is the next hop, let node lies to it by claiming infinity distance between itself and the destination. The algorithm would be running iteratively in loop as long as there are updated DV in network. Once none of the routers have any new shortest-paths to broadcast, none of the routers receive any new information that might change their routing tables. The algorithm comes to a stop.

### Compilation Instruction:
javac *.java
java Project3
