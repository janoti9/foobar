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
