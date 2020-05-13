/*
Bringing a Gun to a Guard Fight Uh-oh - you've been cornered by one of Commander Lambdas elite guards! Fortunately, you grabbed a beam weapon from an abandoned guard post while you were running through the station, so you have a chance to fight your way out. But the beam weapon is potentially dangerous to you as well as to the elite guard: its beams reflect off walls, meaning you'll have to be very careful where you shoot to avoid bouncing a shot toward yourself!

Luckily, the beams can only travel a certain maximum distance before becoming too weak to cause damage. You also know that if a beam hits a corner, it will bounce back in exactly the same direction. And of course, if the beam hits either you or the guard, it will stop immediately (albeit painfully).

Write a function solution(dimensions, your_position, guard_position, distance) that gives an array of 2 integers of the width and height of the room, an array of 2 integers of your x and y coordinates in the room, an array of 2 integers of the guard's x and y coordinates in the room, and returns an integer of the number of distinct directions that you can fire to hit the elite guard, given the maximum distance that the beam can travel.

The room has integer dimensions [1 < x_dim <= 1250, 1 < y_dim <= 1250]. You and the elite guard are both positioned on the integer lattice at different distinct positions (x, y) inside the room such that [0 < x < x_dim, 0 < y < y_dim]. Finally, the maximum distance that the beam can travel before becoming harmless will be given as an integer 1 < distance <= 10000.

For example, if you and the elite guard were positioned in a room with dimensions [3, 2], your_position [1, 1], guard_position [2, 1], and a maximum shot distance of 4, you could shoot in seven different directions to hit the elite guard (given as vector bearings from your location): [1, 0], [1, 2], [1, -2], [3, 2], [3, -2], [-3, 2], and [-3, -2]. As specific examples, the shot at bearing [1, 0] is the straight-line horizontal shot of distance 1, the shot at bearing [-3, -2] bounces off the left wall and then the bottom wall before hitting the elite guard with a total shot distance of sqrt(13), and the shot at bearing [1, 2] bounces off just the top wall before hitting the elite guard with a total shot distance of sqrt(5).

Languages To provide a Java solution, edit Solution.java To provide a Python solution, edit solution.py

Test cases Your code should pass the following test cases. Note that it may also be run against hidden test cases not shown here.

-- Java cases --
Input: Solution.solution([3,2], [1,1], [2,1], 4) Output: 7

Input: Solution.solution([300,275], [150,150], [185,100], 500) Output: 9
*/

import java.util.HashMap;
import java.util.Map;

public class SolutionShooter {

	class Point {
		int x;
		int y;

		public Point(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}

	class Fraction {
		int n;
		int d;

		public Fraction(int n, int d) {
			this.n = n;
			this.d = d;
			this.simplify();
		}

		public int hcf(int x, int y) // considering both are non negative integers > 0
		{
			if (x % y == 0) {
				return y;
			} else if (y % x == 0) {
				return x;
			} else {
				int r, n, d;
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

		public void simplify() {
			if (n == 0 && d != 0) {
				d /= Math.abs(d);
			} else if (d == 0 && n != 0) {
				n /= Math.abs(n);
			} else if (!(d == 0 && n == 0)) {
				int hcfND = hcf(Math.abs(n), Math.abs(d));
				this.n = n / hcfND;
				this.d = d / hcfND;
			}
		}

		@Override
		public int hashCode() {
			return Math.min(n, d) * 673 + Math.max(n, d) * 19;
		}

		@Override
		public boolean equals(Object o) {
			if (o == this) {
				return true;
			}

			if (!(o instanceof Fraction)) {
				return false;
			}

			Fraction f = (Fraction) o;
			return this.n == f.n && this.d == f.d;
		}

	}

	class Line {
		// line's equation y =mx + c
		Point nearestPoint;
		boolean isNearestPointGuard;
		long nearestPointDistance; // this is a squared value..

		public Line(Point nearestPoint, boolean isNearestPointGuard) {
			this.nearestPoint = nearestPoint;
			this.isNearestPointGuard = isNearestPointGuard;
		}

		public boolean isPointNearerToOrigShooter(Point p) {
			return Math.pow(p.y - origShooter.y, 2) + Math.pow(p.x - origShooter.x, 2) < Math
					.pow(nearestPoint.y - origShooter.y, 2) + Math.pow(nearestPoint.x - origShooter.x, 2);
		}

		public void setNearestPointDistance() {
			nearestPointDistance = (nearestPoint.y - origShooter.y) * (nearestPoint.y - origShooter.y)
					+ (nearestPoint.x - origShooter.x) * (nearestPoint.x - origShooter.x);
		}
	}

	int[] rectDimensions;
	Point origShooter; // shooter
	Point oriGuard; // guard
	int maxD;
	HashMap<Fraction, Line> lines;
	int rX; // number of rectangles in x direction
	int rY; // number of rectangles in y direction

	public SolutionShooter(int[] dimensions, int[] your_position, int[] guard_position, int distance) {
		this.rectDimensions = dimensions;
		this.origShooter = new Point(your_position[0], your_position[1]);
		this.oriGuard = new Point(guard_position[0], guard_position[1]);
		this.maxD = distance;
		lines = new HashMap<Fraction, Line>();
		rX = (maxD + origShooter.x) / rectDimensions[0] + 1;
		rY = (maxD + origShooter.y) / rectDimensions[1] + 1;
	}

	public void checkAndAdd(Point p, Fraction lineSlope, boolean isPointGuard) {
		if (lines.containsKey(lineSlope)) {
			if (lines.get(lineSlope).isPointNearerToOrigShooter(p)) {
				lines.get(lineSlope).nearestPoint = p;
				lines.get(lineSlope).isNearestPointGuard = isPointGuard;
				lines.get(lineSlope).setNearestPointDistance();
			}
		} else {
			lines.put(lineSlope, new Line(p, isPointGuard));
			lines.get(lineSlope).setNearestPointDistance();
		}
		;
	}

	public void buildLines() {
		for (int i = 0; i < rX; i++) {
			for (int j = 0; j < rY; j++) {
				for (int k = 0; k < 3; k++) {
					Point p = new Point(origShooter.x, origShooter.y);
					// 0 shooter, 1 guard, 2 top right corner of rectangle
					if (k == 0) {

						p = new Point(
								i * rectDimensions[0]
										+ (i % 2 == 0 ? this.origShooter.x : rectDimensions[0] - this.origShooter.x),
								j * rectDimensions[1]
										+ (j % 2 == 0 ? this.origShooter.y : rectDimensions[1] - this.origShooter.y));

					} else if (k == 1) {
						p = new Point(
								i * rectDimensions[0]
										+ (i % 2 == 0 ? this.oriGuard.x : rectDimensions[0] - this.oriGuard.x),
								j * rectDimensions[1]
										+ (j % 2 == 0 ? this.oriGuard.y : rectDimensions[1] - this.oriGuard.y));
					} else if (k == 2) {
						p = new Point((i + 1) * rectDimensions[0], (j + 1) * rectDimensions[1]);
					}
					// 4 shooters, one in each quadrant
					if (!(i == 0 && j == 0 && k == 0)) {
						checkAndAdd(new Point(p.x, p.y), new Fraction(p.y - origShooter.y, p.x - origShooter.x),
								k == 1);
					}
					checkAndAdd(new Point(-p.x, p.y), new Fraction(p.y - origShooter.y, -p.x - origShooter.x), k == 1);
					checkAndAdd(new Point(-p.x, -p.y), new Fraction(-p.y - origShooter.y, -p.x - origShooter.x),
							k == 1);
					checkAndAdd(new Point(p.x, -p.y), new Fraction(-p.y - origShooter.y, p.x - origShooter.x), k == 1);

				}
			}
		}
	}

	public static int solution(int[] dimensions, int[] your_position, int[] guard_position, int distance) {
		SolutionShooter s = new SolutionShooter(dimensions, your_position, guard_position, distance);
		s.buildLines();

		int count = 0;
		for (Map.Entry<Fraction, Line> me : s.lines.entrySet()) {
			if (((Line) me.getValue()).nearestPointDistance <= s.maxD * s.maxD
					&& ((Line) me.getValue()).isNearestPointGuard) {
				count++;
			}

		}
		return count;
	}

	public static void main(String[] args) {
		System.out.println(SolutionShooter.solution(new int[] { 3, 2 }, new int[] { 1, 1 }, new int[] { 2, 1 }, 4));

	}
}
