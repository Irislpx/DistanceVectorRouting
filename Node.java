import java.io.*;

/**
 * This is the class that students need to implement. The code skeleton is provided.
 * Students need to implement rtinit(), rtupdate() and linkhandler().
 * printdt() is provided to pretty print a table of the current costs for reaching
 * other nodes in the network.
 */ 
public class Node { 
    
    public static final int INFINITY = 9999;
    
    int[] lkcost;		/*The link cost between this node and other nodes*/
    int[][] costs;  		/*Define distance table*/
    int nodename;               /*Name of this node*/
    int[][] minCost;
    
    /* Class constructor */
    public Node() { 
    		this.costs = new int[4][4];
    		//first row keep min cost, second row is to keep next hop
    		this.minCost = new int[2][4];
    }
    
    public int[] minValueofRow(int[] row) {
    		int min = INFINITY;
    		int hop = -1;
    		for(int i = 0; i < row.length; i++) {
    			if(row[i] < min) {
    				min = row[i];
    				hop = i;
    			}
    		}
    		return new int[] {min, hop};
    }
    /* students to write the following two routines, and maybe some others */
    //entry [i,j] in the distance table specifies the node’s currently computed cost to node i via direct neighbor j. 
    void rtinit(int nodename, int[] initial_lkcost) {
    		this.lkcost = initial_lkcost;
    		this.nodename = nodename;
    		for(int i = 0; i < 4; i++) {
    			for(int j = 0; j < 4; j++) {
    				if(i == j) {
    					costs[i][j] = lkcost[i];
    				}
    				else {
    					costs[i][j] = INFINITY;
    				}
    			}
    		}
    		//pick min cost for each row
    		for(int i = 0; i < costs.length; i++) {
    			int[] rst = minValueofRow(costs[i]);
    			 minCost[0][i] = rst[0];
    			 minCost[1][i] = rst[1];
    		}
    		System.out.println("Inital table=======");
    		printdt();
    		System.out.println("=========");
    		//send packet to all neighbor
    		for(int i = 0; i < lkcost.length; i++) {
    			if(i != this.nodename && lkcost[i] != INFINITY) {
    				NetworkSimulator.tolayer2(new Packet(this.nodename, i, minCost[0]));
    			}
    		}
    }    
    
    void rtupdate(Packet rcvdpkt) {
    		boolean updated = false;
    		int src = rcvdpkt.sourceid;
    		int[] minCostRcv = rcvdpkt.mincost;
    		for(int i = 0; i < minCostRcv.length; i++) {
    			int dis = minCostRcv[i] + lkcost[src];
    			 if(dis >= INFINITY){
                 this.costs[i][src] = INFINITY;
              }
    			 else if(costs[i][src] > dis && dis < INFINITY ) {
    				updated = true;
    				costs[i][src] = dis;
    			}
    		}
    		for(int i = 0; i < minCostRcv.length; i++) {
    			int[] rst = minValueofRow(costs[i]);
    			minCost[0][i] = rst[0];
    			minCost[1][i] = rst[1];
    		}
    		
    		System.out.println("Updated table=======");
    		printdt();
    		System.out.println("=========");
    		if(updated) {
    		for(int i = 0; i < lkcost.length; i++) {
    			if(i != nodename && lkcost[i] != INFINITY) {
    				int[] PosionReverse = new int[lkcost.length];
    				for(int j = 0; j < lkcost.length; j++) {
    					if(minCost[1][j] == i) {
    						PosionReverse[j] = INFINITY;
    					}
    					else {
    						PosionReverse[j] = minCost[0][j];
    					}
    				}
    				NetworkSimulator.tolayer2(new Packet(nodename, i, PosionReverse));
    				}
    			}
    		}
    }
    
    
    /* called when cost from the node to linkid changes from current value to newcost*/
    void linkhandler(int linkid, int newcost) { 
    	System.out.println("*********************************************************");
        System.out.println("Change made to "+ this.nodename + ": " + lkcost[linkid] + " --> "+ newcost);
        System.out.println("*********************************************************");
        for(int i = 0; i < costs[0].length; i++){
            for(int j =0; j < costs[0].length; j ++){
                // check to make sure it is reachable
                if(j == linkid && costs[i][j] != INFINITY && lkcost[j] != INFINITY){
                    // change in link cost
                    this.costs[i][j] -= lkcost[j];
                    // update with new cost
                    this.costs[i][j] += newcost;
                }
            }
        }
        // update
        lkcost[linkid]= newcost;
        //System.out.println("New cost updated")
        // Broadcast position to network
        for(int i = 0; i < lkcost.length; i++) {
			int[] rst = minValueofRow(costs[i]);
			minCost[0][i] = rst[0];
			minCost[1][i] = rst[1];
		}
		System.out.println("Updated table=======");
		printdt();
		System.out.println("=========");
		for(int i = 0; i < lkcost.length; i++) {
			if(i != nodename && lkcost[i] != INFINITY) {
				int[] PosionReverse = new int[lkcost.length];
				for(int j = 0; j < lkcost.length; j++) {
					if(minCost[1][j] == i) {
						PosionReverse[j] = INFINITY;
					}
					else {
						PosionReverse[j] = minCost[0][j];
					}
				}
				NetworkSimulator.tolayer2(new Packet(nodename, i, PosionReverse));
				}
			}
    }    


    /* Prints the current costs to reaching other nodes in the network */
    void printdt() {
        switch(nodename) {
	
	case 0:
	    System.out.printf("                via     \n");
	    System.out.printf("   D0 |    1     2 \n");
	    System.out.printf("  ----|-----------------\n");
	    System.out.printf("     1|  %3d   %3d \n",costs[1][1], costs[1][2]);
	    System.out.printf("dest 2|  %3d   %3d \n",costs[2][1], costs[2][2]);
	    System.out.printf("     3|  %3d   %3d \n",costs[3][1], costs[3][2]);
	    break;
	case 1:
	    System.out.printf("                via     \n");
	    System.out.printf("   D1 |    0     2    3 \n");
	    System.out.printf("  ----|-----------------\n");
	    System.out.printf("     0|  %3d   %3d   %3d\n",costs[0][0], costs[0][2],costs[0][3]);
	    System.out.printf("dest 2|  %3d   %3d   %3d\n",costs[2][0], costs[2][2],costs[2][3]);
	    System.out.printf("     3|  %3d   %3d   %3d\n",costs[3][0], costs[3][2],costs[3][3]);
	    break;    
	case 2:
	    System.out.printf("                via     \n");
	    System.out.printf("   D2 |    0     1    3 \n");
	    System.out.printf("  ----|-----------------\n");
	    System.out.printf("     0|  %3d   %3d   %3d\n",costs[0][0], costs[0][1],costs[0][3]);
	    System.out.printf("dest 1|  %3d   %3d   %3d\n",costs[1][0], costs[1][1],costs[1][3]);
	    System.out.printf("     3|  %3d   %3d   %3d\n",costs[3][0], costs[3][1],costs[3][3]);
	    break;
	case 3:
	    System.out.printf("                via     \n");
	    System.out.printf("   D3 |    1     2 \n");
	    System.out.printf("  ----|-----------------\n");
	    System.out.printf("     0|  %3d   %3d\n",costs[0][1],costs[0][2]);
	    System.out.printf("dest 1|  %3d   %3d\n",costs[1][1],costs[1][2]);
	    System.out.printf("     2|  %3d   %3d\n",costs[2][1],costs[2][2]);
	    break;
        }
    }
    
}
