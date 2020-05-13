/*
Expanding Nebula
    ================
    You've escaped Commander Lambda's exploding space station along with numerous escape pods full of bunnies.
    But - oh no! - one of the escape pods has flown into a nearby nebula, causing you to lose track of it. You start
    monitoring the nebula, but unfortunately, just a moment too late to find where the pod went. However, you do find
    that the gas of the steadily expanding nebula follows a simple pattern, meaning that you should be able to determine
    the previous state of the gas and narrow down where you might find the pod.
    From the scans of the nebula, you have found that it is very flat and distributed in distinct patches, so you can
    model it as a 2D grid. You find that the current existence of gas in a cell of the grid is determined exactly by its
    4 nearby cells, specifically, (1) that cell, (2) the cell below it, (3) the cell to the right of it, and (4) the
    cell below and to the right of it. If, in the current state, exactly 1 of those 4 cells in the 2x2 block has gas,
    then it will also have gas in the next state. Otherwise, the cell will be empty in the next state.
    For example, let's say the previous state of the grid (p) was:
    .O..
    ..O.
    ...O
    O...
    To see how this grid will change to become the current grid (c) over the next time step, consider the 2x2 blocks of
    cells around each cell.  Of the 2x2 block of [p[0][0], p[0][1], p[1][0], p[1][1]], only p[0][1] has gas in it, which
    means this 2x2 block would become cell c[0][0] with gas in the next time step:
    .O -> O
    ..
    Likewise, in the next 2x2 block to the right consisting of [p[0][1], p[0][2], p[1][1], p[1][2]], two of the
    containing cells have gas, so in the next state of the grid, c[0][1] will NOT have gas:
    O. -> .
    .O
    Following this pattern to its conclusion, from the previous state p, the current state of the grid c will be:
    O.O
    .O.
    O.O
    Note that the resulting output will have 1 fewer row and column, since the bottom and rightmost cells do not have a cell below and to the right of them, respectively.
    Write a function answer(g) where g is an array of array of bools saying whether there is gas in each cell (the current scan of the nebula), and return an int with the number of possible previous states that could have resulted in that grid after 1 time step.  For instance, if the function were given the current state c above, it would deduce that the possible previous states were p (given above) as well as its horizontal and vertical reflections, and would return 4. The width of the grid will be between 3 and 50 inclusive, and the height of the grid will be between 3 and 9 inclusive.  The answer will always be less than one billion (10^9).
*/
import java.util.HashMap;

public class ExplodingNebula {
		boolean[][] state;
		HashMap<SmallState, Integer> mapTrue;
		HashMap<SmallState, Integer> mapFalse;

		class SmallState {
			String left;
			String right;
			String up;
			String down;

			public SmallState(String left, String right, String up, String down) {
				this.left = left;
				this.right = right;
				this.up = up;
				this.down = down;
			}

			@Override
			public int hashCode() {
				final int prime = 31;
				int result = 1;
				result = prime * result + ((down == null) ? 0 : down.hashCode());
				result = prime * result + ((left == null) ? 0 : left.hashCode());
				result = prime * result + ((right == null) ? 0 : right.hashCode());
				result = prime * result + ((up == null) ? 0 : up.hashCode());
				return result;
			}

			@Override
			public boolean equals(Object obj) {
				if (this == obj)
					return true;
				if (obj == null)
					return false;
				if (getClass() != obj.getClass())
					return false;
				SmallState other = (SmallState) obj;

				if (down == null) {
					if (other.down != null)
						return false;
				} else if (!down.equals(other.down))
					return false;
				if (left == null) {
					if (other.left != null)
						return false;
				} else if (!left.equals(other.left))
					return false;
				if (right == null) {
					if (other.right != null)
						return false;
				} else if (!right.equals(other.right))
					return false;
				if (up == null) {
					if (other.up != null)
						return false;
				} else if (!up.equals(other.up))
					return false;
				return true;
			}

		}

		public ExplodingNebula(boolean[][] state) {
			this.state = state;

			mapTrue = new HashMap<SmallState, Integer>();
			mapTrue.put(new SmallState("10", "00", "10", "00"), 1);
			mapTrue.put(new SmallState("00", "10", "01", "00"), 1);
			mapTrue.put(new SmallState("01", "00", "00", "10"), 1);
			mapTrue.put(new SmallState("00", "01", "00", "01"), 1);

			mapFalse = new HashMap<SmallState, Integer>();
			mapFalse.put(new SmallState("00", "00", "00", "00"), 1);
			mapFalse.put(new SmallState("11", "00", "10", "10"), 1);
			mapFalse.put(new SmallState("00", "11", "01", "01"), 1);
			mapFalse.put(new SmallState("10", "10", "11", "00"), 1);
			mapFalse.put(new SmallState("01", "01", "00", "11"), 1);
			mapFalse.put(new SmallState("10", "01", "10", "01"), 1);
			mapFalse.put(new SmallState("01", "10", "01", "10"), 1);
			mapFalse.put(new SmallState("01", "11", "01", "11"), 1);
			mapFalse.put(new SmallState("10", "11", "11", "01"), 1);
			mapFalse.put(new SmallState("11", "10", "11", "10"), 1);
			mapFalse.put(new SmallState("11", "01", "10", "11"), 1);
			mapFalse.put(new SmallState("11", "11", "11", "11"), 1);

		}

		public HashMap<SmallState, Integer> mergeMaps(HashMap<SmallState, Integer> map1, HashMap<SmallState, Integer> map2,
				boolean rowMerge, boolean truncLeft, boolean truncRight, boolean truncUp, boolean truncDown) {
			HashMap<SmallState, Integer> mergedMap = new HashMap<SmallState, Integer>();
			for (SmallState map1Key : map1.keySet()) {
				for (SmallState map2Key : map2.keySet()) {
					if (rowMerge && map1Key.down.equals(map2Key.up)) {
						SmallState newMapEntry = new SmallState(truncLeft ? "X" : map1Key.left + map2Key.left.substring(1),
								truncRight ? "X" : map1Key.right + map2Key.right.substring(1), truncUp ? "X" : map1Key.up,
								truncDown ? "X" : map2Key.down);
						mergedMap.put(newMapEntry, map1.get(map1Key) * map2.get(map2Key)
								+ (mergedMap.containsKey(newMapEntry) ? mergedMap.get(newMapEntry) : 0));
					} else if (!rowMerge && map1Key.right.equals(map2Key.left)) {
						SmallState newMapEntry = new SmallState(truncLeft ? "X" : map1Key.left,
								truncRight ? "X" : map2Key.right, truncUp ? "X" : map1Key.up + map2Key.up.substring(1),
								truncDown ? "X" : map1Key.down + map2Key.down.substring(1));
						mergedMap.put(newMapEntry, map1.get(map1Key) * map2.get(map2Key)
								+ (mergedMap.containsKey(newMapEntry) ? mergedMap.get(newMapEntry) : 0));
					}
				}
			}
			return mergedMap;
		}

		// rowStart, rowEnd, colStart, colEnd all are inclusive
		public HashMap<SmallState, Integer> getPossibleStatesCount(int rowStart, int rowEnd, int colStart, int colEnd) {
			// split if you can....
			if (rowEnd > rowStart || colEnd > colStart) {
				boolean rowSplit = (rowEnd-rowStart > colEnd-colStart);
				HashMap<SmallState, Integer> firstMap = getPossibleStatesCount(rowStart,
						rowSplit ? (rowEnd + rowStart) / 2 : rowEnd, colStart, rowSplit ? colEnd : (colStart + colEnd) / 2);
				HashMap<SmallState, Integer> secondMap = getPossibleStatesCount(
						rowSplit ? (rowEnd + rowStart) / 2 + 1 : rowStart, rowEnd,
						rowSplit ? colStart : (colStart + colEnd) / 2 + 1, colEnd);
				
				return mergeMaps(firstMap, secondMap, rowSplit, colStart == 0, colEnd == state[0].length - 1, rowStart == 0,
						rowEnd == state.length - 1);
			} else if (state[rowStart][colStart]) {
				return mapTrue;
			} else {
				return mapFalse;
			}
		}

		public int getAnswer() {
			HashMap<SmallState, Integer> ansMap = getPossibleStatesCount(0, state.length - 1, 0, state[0].length - 1);
			int count = 0;

			for (SmallState i : ansMap.keySet()) {
				count += ansMap.get(i);
			}
			return count;
		}

		public static int solution(boolean[][] g) {
			return (g == null) ? 2 : new ExplodingNebula(g).getAnswer();
		}

		public static void main(String[] args) {

			
			System.out.println(ExplodingNebula.solution(new boolean[][] {
					{ true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true,
							true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true,
							true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true,
							true },
					{ true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true,
							true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true,
							true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true,
							true } }));

			
			/*
			 * System.out.println( ExplodingNebula.solution(new boolean[][] { { true, true, false,
			 * true, false, true, false, true, true, false }, { true, true, false, false,
			 * false, false, true, true, true, false }, { true, true, false, false, false,
			 * false, false, false, false, true }, { false, true, false, false, false,
			 * false, true, true, false, false } }));
			 */
			/*
			 * System.out.println(ExplodingNebula.solution(new boolean[][] { { true, false, true,
			 * false, false, true, true, true }, { true, false, true, false, false, false,
			 * true, false }, { true, true, true, false, false, false, true, false }, {
			 * true, false, true, false, false, false, true, false }, { true, false, true,
			 * false, false, true, true, true } }));
			 */
			// System.out.println(ExplodingNebula.solution(null));
			// System.out.println(ExplodingNebula .solution(new boolean[][] { { false}}));
		}
}
