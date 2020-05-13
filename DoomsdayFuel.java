
/*
Making fuel for the LAMBCHOP's reactor core is a tricky process because of the exotic matter involved. It starts as raw ore, then during processing, begins randomly changing between forms, eventually reaching a stable form. There may be multiple stable forms that a sample could ultimately reach, not all of which are useful as fuel.

Commander Lambda has tasked you to help the scientists increase fuel creation efficiency by predicting the end state of a given ore sample. You have carefully studied the different structures that the ore can take and which transitions it undergoes. It appears that, while random, the probability of each structure transforming is fixed. That is, each time the ore is in 1 state, it has the same probabilities of entering the next state (which might be the same state). You have recorded the observed transitions in a matrix. The others in the lab have hypothesized more exotic forms that the ore can become, but you haven't seen all of them.

Write a function answer(m) that takes an array of array of nonnegative ints representing how many times that state has gone to the next state and return an array of ints for each terminal state giving the exact probabilities of each terminal state, represented as the numerator for each state, then the denominator for all of them at the end and in simplest form. The matrix is at most 10 by 10. It is guaranteed that no matter which state the ore is in, there is a path from that state to a terminal state. That is, the processing will always eventually end in a stable state. The ore starts in state 0. The denominator will fit within a signed 32-bit integer during the calculation, as long as the fraction is simplified regularly.

For example, consider the matrix m:

[
  [0,1,0,0,0,1],  # s0, the initial state, goes to s1 and s5 with equal probability
  [4,0,0,3,2,0],  # s1 can become s0, s3, or s4, but with different probabilities
  [0,0,0,0,0,0],  # s2 is terminal, and unreachable (never observed in practice)
  [0,0,0,0,0,0],  # s3 is terminal
  [0,0,0,0,0,0],  # s4 is terminal
  [0,0,0,0,0,0],  # s5 is terminal
]
So, we can consider different paths to terminal states, such as:

s0 -> s1 -> s3
s0 -> s1 -> s0 -> s1 -> s0 -> s1 -> s4
s0 -> s1 -> s0 -> s5
Tracing the probabilities of each, we find that:

s2 has probability 0
s3 has probability 3/14
s4 has probability 1/7
s5 has probability 9/14
So, putting that together, and making a common denominator, gives an answer in the form of [s2.numerator, s3.numerator, s4.numerator, s5.numerator, denominator] which is: [0, 3, 2, 9, 14].

Test cases
Inputs: (int) m = [[0, 2, 1, 0, 0], [0, 0, 0, 3, 4], [0, 0, 0, 0, 0], [0, 0, 0, 0, 0], [0, 0, 0, 0, 0]]

Output: (int list) [7, 6, 8, 21]

Inputs: (int) m = [[0, 1, 0, 0, 0, 1], [4, 0, 0, 3, 2, 0], [0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0]]

Output: (int list) [0, 3, 2, 9, 14]
*/
import java.util.ArrayList;
import java.util.HashMap;

public class DoomsdayFuel {

	class Fraction {
		long n;
		long d;

		public Fraction(long n, long d) {
			this.n = n;
			this.d = d;
		}

		public Fraction(Fraction f) {
			this.n = f.n;
			this.d = f.d;

		}

		public void addFraction(Fraction f) {
			if (f.isZero()) {
				return;
			} else if (this.isZero()) {
				this.n = f.n;
				this.d = f.d;
				return;
			}

			n = f.d * n + d * f.n;
			d = d * f.d;
			simplify();
		}

		public Fraction getFractionSum(Fraction f) {
			Fraction fo = new Fraction(f.d * n + d * f.n, d * f.d);
			fo.simplify();
			return fo;
		}

		public Fraction multiplyFraction(Fraction f1, Fraction f2) {
			Fraction fo = new Fraction(f1.n * f2.n, f1.d * f2.d);
			fo.simplify();
			return fo;
		}

		public Fraction multiplyFractionGetNew(Fraction f) {
			Fraction fo = new Fraction(this.n * f.n, this.d * f.d);
			fo.simplify();
			return fo;
		}

		public Fraction multiplyFractionIntoThis(Fraction f) {
			this.n = this.n * f.n;
			this.d = this.d * f.d;
			this.simplify();
			return this;
		}

		public void simplify() {
			if (!isZero() && this.n > 1 && this.d > 1) {
				long hcfND = hcf(this.n, this.d);
				this.n = n / hcfND;
				this.d = d / hcfND;
			}
		}

		public boolean isZero() {
			return n == 0;
		}
	}

	class Node {
		int rowNumber;
		Fraction mFactor;
		ArrayList<Integer> childNodesIndex;
		int parentIndex;
		boolean isLoopStartNode;

		public Node(int rowNumber, Fraction mFactor) {
			this.rowNumber = rowNumber;
			this.mFactor = mFactor;
			this.childNodesIndex = new ArrayList<Integer>();
			this.isLoopStartNode = false;
		}

	}

	int[][] pData;
	boolean[] isTerminated;
	int[] rowTotal;
	int numberOfTerminatedRows = 0;
	int hw;
	ArrayList<Node> nodes;

	public DoomsdayFuel(int[][] pData) {
		this.pData = pData;
		hw = pData.length;
		this.isTerminated = new boolean[hw];
		this.rowTotal = new int[hw];
		this.nodes = new ArrayList<Node>();
	}

	public long hcf(long x, long y) // considering both are non negative integers > 0
	{
		if (x % y == 0) {
			return y;
		} else if (y % x == 0) {
			return x;
		} else {
			long r, n, d;
			if (x > y) {
				n = x;
				d = y;
			} else {
				n = y;
				d = x;
			}
			r = n % d;
			while (r != 0) {
				n = d;
				d = r;
				r = n % d;
			}

			return d;
		}
	}

	public long lcm(long x, long y) {
		return x * y / hcf(x, y);
	}

	public void setOriginals() {
		for (int i = 0; i < hw; i++) {
			int sum = 0;
			for (int j = 0; j < hw; j++) {
				sum += pData[i][j];
			}

			isTerminated[i] = (sum == 0);

			if (sum == 0) {
				numberOfTerminatedRows++;
			}
			rowTotal[i] = sum;
		}
	}

	public boolean isLooping(int indx) {

		int currIndex = nodes.get(indx).parentIndex;

		while (currIndex >= 0) // go till root
		{
			if (nodes.get(currIndex).rowNumber == nodes.get(indx).rowNumber) {
				nodes.get(currIndex).isLoopStartNode = true;
				return true;
			}
			currIndex = nodes.get(currIndex).parentIndex;
		}
		return false;
	}

	public void recursiveBuildTree(int indx) {
		for (int i = 0; i < hw; i++) {
			if (pData[nodes.get(indx).rowNumber][i] != 0) {
				Node c = new Node(i,
						new Fraction(pData[nodes.get(indx).rowNumber][i], rowTotal[nodes.get(indx).rowNumber]));
				c.parentIndex = indx;
				nodes.add(c);
				nodes.get(indx).childNodesIndex.add(nodes.size() - 1);
				if (!isTerminated[i] && !isLooping(nodes.size() - 1)) {
					recursiveBuildTree(nodes.size() - 1);
				}
			}
		}
	}

	public void getLoopNodeMultiplier(int loopNodeRowNumber, int indx, Fraction factorTillNow,
			HashMap<Integer, Fraction> totalFactors) {
		Node currNode = nodes.get(indx);
		if (currNode.childNodesIndex.size() == 0) // leaf node
		{
			if (totalFactors.containsKey(currNode.rowNumber)) {
				totalFactors.get(currNode.rowNumber).addFraction(factorTillNow);
			} else {
				totalFactors.put(currNode.rowNumber, factorTillNow);
			}
			if (currNode.rowNumber == loopNodeRowNumber) {
				currNode.mFactor.n = 0; // set zero..
			}
		} else {
			for (int i = 0; i < currNode.childNodesIndex.size(); i++) {
				Node childNode = nodes.get(currNode.childNodesIndex.get(i));
				Fraction childFraction = factorTillNow.multiplyFractionGetNew(childNode.mFactor);
				getLoopNodeMultiplier(loopNodeRowNumber, currNode.childNodesIndex.get(i), childFraction, totalFactors);
			}
		}
	}

	public Fraction getNodeAbsoluteMultiplier(int indx) {
		Node n = nodes.get(indx);
		Fraction f = new Fraction(n.mFactor);
		while (n.parentIndex >= 0) {
			n = nodes.get(n.parentIndex);
			f = f.multiplyFractionIntoThis(n.mFactor);
		}
		return f;

	}

	public void removeLoops() {
		for (int i = nodes.size() - 1; i >= 0; i--) {
			if (nodes.get(i).isLoopStartNode) {
				Node currLoopingNode = nodes.get(i);
				HashMap<Integer, Fraction> totalFactors = new HashMap<Integer, Fraction>();
				getLoopNodeMultiplier(currLoopingNode.rowNumber, i, new Fraction(1, 1), totalFactors);

				Fraction infFactorMultiplier = new Fraction(totalFactors.get(currLoopingNode.rowNumber).d,
						totalFactors.get(currLoopingNode.rowNumber).d - totalFactors.get(currLoopingNode.rowNumber).n);
				currLoopingNode.mFactor.multiplyFractionIntoThis(infFactorMultiplier);
			}
		}
	}

	public int[] getSimplifiedResult(Fraction[] f) // make all fractions come to same denominator
	{
		int[] result = new int[numberOfTerminatedRows + 1];
		long lcmAll = 1;
		for (int i = 0; i < f.length; i++) {
			if (f[i] != null && !f[i].isZero()) {
				lcmAll = lcm(lcmAll, f[i].d);
			}
		}

		for (int i = 0, j = 0; i < f.length; i++) {
			if (isTerminated[i]) {
				if (f[i] == null || f[i].isZero()) {
					result[j] = 0;
				} else {
					result[j] = (int) (f[i].n * lcmAll / f[i].d);
				}
				j++;
			}
		}
		result[result.length - 1] = (int) lcmAll;
		return result;

	}

	public int[] findProbabilities() {
		this.setOriginals();
		nodes.add(new Node(0, new Fraction(1, 1)));
		nodes.get(0).parentIndex = -1; // set parent index as -1
		recursiveBuildTree(0);
		removeLoops();

		Fraction[] finalFraction = new Fraction[hw];
		for (int i = 0; i < nodes.size(); i++) {
			if (nodes.get(i).childNodesIndex.size() == 0 && isTerminated[nodes.get(i).rowNumber]) // leaf node
			{
				Fraction leafFraction = getNodeAbsoluteMultiplier(i);
				if (!leafFraction.isZero()) {
					if (finalFraction[nodes.get(i).rowNumber] == null) {
						finalFraction[nodes.get(i).rowNumber] = leafFraction;
					} else {
						finalFraction[nodes.get(i).rowNumber].addFraction(leafFraction);
					}
				}
			}

		}
		return getSimplifiedResult(finalFraction);
	}

	public static int[] solution(int[][] m) {
		DoomsdayFuel s = new DoomsdayFuel(m);
		return s.findProbabilities();
	}

	public static void main(String[] args) {

		int[] result = DoomsdayFuel.solution(new int[][] { { 0, 1, 0, 0, 0, 1 }, { 4, 0, 0, 3, 2, 0 },
				{ 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0 } });

		for (int i = 0; i < result.length; i++) {
			System.out.print(result[i] + ",");
		}
	}
}
