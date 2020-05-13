/*
Running with Bunnies
====================

You and your rescued bunny prisoners need to get out of this collapsing death trap of a space station - and fast! Unfortunately, some of the bunnies have been weakened by their long imprisonment and can't run very fast. Their friends are trying to help them, but this escape would go a lot faster if you also pitched in. The defensive bulkhead doors have begun to close, and if you don't make it through in time, you'll be trapped! You need to grab as many bunnies as you can and get through the bulkheads before they close. 

The time it takes to move from your starting point to all of the bunnies and to the bulkhead will be given to you in a square matrix of integers. Each row will tell you the time it takes to get to the start, first bunny, second bunny, ..., last bunny, and the bulkhead in that order. The order of the rows follows the same pattern (start, each bunny, bulkhead). The bunnies can jump into your arms, so picking them up is instantaneous, and arriving at the bulkhead at the same time as it seals still allows for a successful, if dramatic, escape. (Don't worry, any bunnies you don't pick up will be able to escape with you since they no longer have to carry the ones you did pick up.) You can revisit different spots if you wish, and moving to the bulkhead doesn't mean you have to immediately leave - you can move to and from the bulkhead to pick up additional bunnies if time permits.

In addition to spending time traveling between bunnies, some paths interact with the space station's security checkpoints and add time back to the clock. Adding time to the clock will delay the closing of the bulkhead doors, and if the time goes back up to 0 or a positive number after the doors have already closed, it triggers the bulkhead to reopen. Therefore, it might be possible to walk in a circle and keep gaining time: that is, each time a path is traversed, the same amount of time is used or added.

Write a function of the form answer(times, time_limit) to calculate the most bunnies you can pick up and which bunnies they are, while still escaping through the bulkhead before the doors close for good. If there are multiple sets of bunnies of the same size, return the set of bunnies with the lowest prisoner IDs (as indexes) in sorted order. The bunnies are represented as a sorted list by prisoner ID, with the first bunny being 0. There are at most 5 bunnies, and time_limit is a non-negative integer that is at most 999.

For instance, in the case of
[
  [0, 2, 2, 2, -1],  # 0 = Start
  [9, 0, 2, 2, -1],  # 1 = Bunny 0
  [9, 3, 0, 2, -1],  # 2 = Bunny 1
  [9, 3, 2, 0, -1],  # 3 = Bunny 2
  [9, 3, 2, 2,  0],  # 4 = Bulkhead
]
and a time limit of 1, the five inner array rows designate the starting point, bunny 0, bunny 1, bunny 2, and the bulkhead door exit respectively. You could take the path:

Start End Delta Time Status
    -   0     -    1 Bulkhead initially open
    0   4    -1    2
    4   2     2    0
    2   4    -1    1
    4   3     2   -1 Bulkhead closes
    3   4    -1    0 Bulkhead reopens; you and the bunnies exit

With this solution, you would pick up bunnies 1 and 2. This is the best combination for this space station hallway, so the answer is [1, 2].
*/

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;

public class RunningWithBunnies {

	class Path {
		ArrayList<Integer> nl = new ArrayList<Integer>();
		HashSet<Integer> ns = new HashSet<Integer>();
		int t; // timeSpent

		@Override
		public boolean equals(Object o) {
			if (o == this) {
				return true;
			}

			if (!(o instanceof Path)) {
				return false;
			}

			Path p = (Path) o;

			return this.nl.equals(p.nl) && this.t == p.t;
		}

		public boolean isPathBetterThan(Path p) {
			if (this.t - times_limit <= 0 && p.t - times_limit > 0) {
				return true;
			} else if (p.t - times_limit <= 0 && this.t - times_limit > 0) {
				return false;
			}
			if (p.ns.size() > this.ns.size()) {
				return false;
			} else if (p.ns.size() < this.ns.size()) {
				return true;
			} else {
				for (int i = 0; i < hw; i++) {
					if (p.ns.contains(i) && !this.ns.contains(i)) {
						return false;
					} else if (this.ns.contains(i) && !p.ns.contains(i)) {
						return true;
					}
				}
			}
			return true;
		}
	}

	int[][] times;
	int times_limit;
	ArrayList<Path> pathList = new ArrayList<Path>(); // all paths
	ArrayList<Path> loopList = new ArrayList<Path>(); // loops
	int hw;

	public RunningWithBunnies(int[][] times, int times_limit) {
		this.times = times;
		this.times_limit = times_limit;
		this.hw = times.length;
	}

	public boolean loopAlreadyTraversed(Path p) {
		for (int i = 0; i < this.loopList.size(); i++) {
			if (this.loopList.get(i).equals(p)) {
				return true;
			}
		}
		return false;

	}

	// find all paths starting at row 0 to row hw-1, and all loops..
	public void buildLoopsAndPaths(LinkedHashSet<Integer> pathTillNow, int row) {
		for (int i = 0; i < hw; i++) {
			if (i != row) {
				if (pathTillNow.contains(i)) // add into loop
				{
					Path lp = new Path();
					boolean startAdd = false;
					for (Integer j : pathTillNow) {
						if (j == i) {
							startAdd = true;
						}

						if (startAdd) {
							lp.nl.add(j);
							lp.ns.add(j);
							if (lp.nl.size() > 1) {
								lp.t += times[lp.nl.get(lp.nl.size() - 2)][lp.nl.get(lp.nl.size() - 1)];
							}
						}
					}
					lp.nl.add(i);
					lp.t += times[lp.nl.get(lp.nl.size() - 2)][lp.nl.get(lp.nl.size() - 1)];

					if (!loopAlreadyTraversed(lp)) {
						loopList.add(lp);
					}
				} else {
					if (i == hw - 1) // add into path
					{
						pathTillNow.add(i);
						Path lp = new Path();
						lp.nl.addAll(pathTillNow);
						lp.ns.addAll(pathTillNow);
						for (int j = 1; j < lp.nl.size(); j++) {
							lp.t += times[lp.nl.get(j - 1)][lp.nl.get(j)];
						}
						pathList.add(lp);
					}
					@SuppressWarnings("unchecked")
					LinkedHashSet<Integer> subPath = (LinkedHashSet<Integer>) pathTillNow.clone();
					subPath.add(i);
					buildLoopsAndPaths(subPath, i);
				}
			}
		}
	}

	public void addLoopsToPath() {
		for (int i = 0; i < pathList.size(); i++) {
			for (int j = 0; j < loopList.size(); j++) {
				if (pathList.get(i).ns.contains(loopList.get(j).nl.get(0))
						&& (pathList.get(i).t + loopList.get(j).t - times_limit <= 0
								|| pathList.get(i).t + loopList.get(j).t <= pathList.get(i).t)) {
					@SuppressWarnings("unchecked")
					HashSet<Integer> pathPlusLoop = (HashSet<Integer>) pathList.get(i).ns.clone();
					pathPlusLoop.addAll(loopList.get(j).ns);

					if (pathPlusLoop.size() > pathList.get(i).ns.size()
							|| pathList.get(i).t + loopList.get(j).t < pathList.get(i).t) {
						Path pNew = new Path();
						pNew.ns = pathPlusLoop;
						pNew.t = pathList.get(i).t + loopList.get(j).t;
						pNew.nl.addAll(pathList.get(i).nl);
						int loopStartIndex = pNew.nl.indexOf(loopList.get(j).nl.get(0));
						pNew.nl.remove(loopStartIndex);
						pNew.nl.addAll(loopStartIndex, loopList.get(j).nl);
						pathList.add(pNew);
						if(pNew.ns.size() == hw && pNew.t - times_limit<=0) 
						{
							return;
						}
					}
				}
			}
		}
	}

	public int[] getBestPathPossible() {
		Path currBestPath = pathList.get(0);
		for (int i = pathList.size() - 1; i > 0; i--) {
			if (!currBestPath.isPathBetterThan(pathList.get(i))) {
				currBestPath = pathList.get(i);
			}
		}

		@SuppressWarnings("unchecked")
		HashSet<Integer> bestPathSet = (HashSet<Integer>) currBestPath.ns.clone();
		bestPathSet.remove(0);
		bestPathSet.remove(hw - 1);

		int[] ans = new int[bestPathSet.size()];
		int j = 0;
		for (Integer i : bestPathSet) {
			ans[j] = i - 1;
			j++;
		}
		return ans;
	}

	public int[] bestPathInCaseOfNegativeLoops() {
		int[] ans = new int[hw - 2];
		for (int i = 0; i < ans.length; i++) {
			ans[i] = i;
		}
		return ans;
	}

	public int[] findSolution() {
		LinkedHashSet<Integer> pathTillNow = new LinkedHashSet<Integer>();
		pathTillNow.add(0);
		buildLoopsAndPaths(pathTillNow, 0);

		for (int i = 0; i < loopList.size(); i++) {
			if (loopList.get(i).t < 0) {
				return bestPathInCaseOfNegativeLoops();
			}
		}

		addLoopsToPath();

		return getBestPathPossible();
	}

	public static int[] solution(int[][] times, int times_limit) {
		if (times.length < 3) {
			return new int[0];
		}
		RunningWithBunnies s = new RunningWithBunnies(times, times_limit);
		return s.findSolution();
	}

	public static void main(String[] args) {		
		int[] ans = RunningWithBunnies.solution(new int[][] { { 1, 1, 1, 1 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 } },
				1);
		for (int i = 0; i < ans.length; i++) {
			System.out.print(ans[i] + " ");
		}
	}
}
