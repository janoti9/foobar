/*
As Commander Lambda's personal assistant, you've been assigned the task of configuring the LAMBCHOP doomsday device's axial orientation gears. It should be pretty simple - just add gears to create the appropriate rotation ratio. But the problem is, due to the layout of the LAMBCHOP and the complicated system of beams and pipes supporting it, the pegs that will support the gears are fixed in place.

The LAMBCHOP's engineers have given you lists identifying the placement of groups of pegs along various support beams. You need to place a gear on each peg (otherwise the gears will collide with unoccupied pegs). The engineers have plenty of gears in all different sizes stocked up, so you can choose gears of any size, from a radius of 1 on up. Your goal is to build a system where the last gear rotates at twice the rate (in revolutions per minute, or rpm) of the first gear, no matter the direction. Each gear (except the last) touches and turns the gear on the next peg to the right.

Given a list of distinct positive integers named pegs representing the location of each peg along the support beam, write a function answer(pegs) which, if there is a solution, returns a list of two positive integers a and b representing the numerator and denominator of the first gear's radius in its simplest form in order to achieve the goal above, such that radius = a/b. The ratio a/b should be greater than or equal to 1. Not all support configurations will necessarily be capable of creating the proper rotation ratio, so if the task is impossible, the function answer(pegs) should return the list [-1, -1].

For example, if the pegs are placed at [4, 30, 50], then the first gear could have a radius of 12, the second gear could have a radius of 14, and the last one a radius of 6. Thus, the last gear would rotate twice as fast as the first one. In this case, pegs would be [4, 30, 50] and answer(pegs) should return [12, 1].

The list pegs will be given sorted in ascending order and will contain at least 2 and no more than 20 distinct positive integers, all between 1 and 10000 inclusive.

Inputs:
(int list) pegs = [4, 30, 50]
Output:
(int list) [12, 1]

Inputs:
(int list) pegs = [4, 17, 50]
Output:
(int list) [-1, -1]
*/

import java.lang.Math; 

public class GearingUpForDestruction{
    
    public static int[] getRadiuses(int[] pegs , int firstGearRadius)
    {
        int[] radiuses = new int[pegs.length];
        
        for (int i =0; i<pegs.length; i++)
        {
            radiuses[i] = i==0?firstGearRadius:pegs[i]-pegs[i-1]-radiuses[i-1];
        }
        
        return radiuses;
    }
    
    public static int[] solution(int[] pegs) {      
      boolean bolEvenPegs = pegs.length%2==0;
      
      int firstGearRadiusMin = 1;
      int firstGearRadiusMax = pegs[1]-pegs[0]-1;
      
      int[] allRadiusesAtFirstGearMin = GearingUpForDestruction.getRadiuses(pegs,firstGearRadiusMin);
      
     // get min odd even radiuses at first radius 1
      int minRadiusOdd = allRadiusesAtFirstGearMin[0];
      int minRadiusEven = allRadiusesAtFirstGearMin[1];
      for(int i=0; i< allRadiusesAtFirstGearMin.length; i ++)
      {
          if (i%2 ==0 && allRadiusesAtFirstGearMin[i] < minRadiusOdd) // its odd element.. elements 1,3,5 - indexes 0,2,4 
          {
              minRadiusOdd = allRadiusesAtFirstGearMin[i];
          }
          else if (i%2 != 0 && allRadiusesAtFirstGearMin[i] < minRadiusEven)
          {
              minRadiusEven = allRadiusesAtFirstGearMin[i];
          }
      }
      
      if (minRadiusEven <1) {return new int[]{-1,-1};} //since even will only decrease from here..
      
      int currentFirstGearRadius;
      int currentLastGearRadius;
      //boolean currentRadiusesValid = false;
      int lastGearRadiusAtFirstGearRadiusMin = allRadiusesAtFirstGearMin[allRadiusesAtFirstGearMin.length-1];
      for(int i = (minRadiusOdd<0)?Math.abs(minRadiusOdd) + 1 : 0 ; i < Math.max(firstGearRadiusMax-firstGearRadiusMin,minRadiusEven); i++)
      {
          currentFirstGearRadius = firstGearRadiusMin + i;
          currentLastGearRadius = bolEvenPegs ? lastGearRadiusAtFirstGearRadiusMin -i : lastGearRadiusAtFirstGearRadiusMin + i;
          
          //if you increase first gear radius(which is 1, so odd), all odd radiuses will increase by same amount
          if (minRadiusOdd + i >=1 && minRadiusEven -i >= 1 //these 2 condition check that all radiuses are >= 1
             && currentLastGearRadius > 0
             && currentFirstGearRadius == currentLastGearRadius * 2)
          {
              return new int[]{currentFirstGearRadius,1};
          }
          
          //do for + 1/3
          //these are 3 times the actual value
          currentFirstGearRadius = 3 * (firstGearRadiusMin + i) + 1;
          currentLastGearRadius = bolEvenPegs 
                                  ? 3 * (lastGearRadiusAtFirstGearRadiusMin -i) - 1 
                                  : 3 * (lastGearRadiusAtFirstGearRadiusMin + i) + 1;

          if (minRadiusOdd + i >=1 && minRadiusEven -i > 1 //these 2 condition check that all radiuses are >= 1
             && currentLastGearRadius > 0
             && currentFirstGearRadius == currentLastGearRadius * 2)
          {
              return new int[]{currentFirstGearRadius,3};
          }
          
          //do for + 2/3
          //these are 3 times the actual value
          currentFirstGearRadius = 3 * (firstGearRadiusMin + i) + 2;
          currentLastGearRadius = bolEvenPegs 
                                  ? 3 * (lastGearRadiusAtFirstGearRadiusMin -i) - 2 
                                  : 3 * (lastGearRadiusAtFirstGearRadiusMin + i) + 2;
          
          if (minRadiusOdd + i >=1 && minRadiusEven -i > 1 //these 2 condition check that all radiuses are >= 1
             && currentLastGearRadius > 0
             && currentFirstGearRadius == currentLastGearRadius * 2)
          {
              return new int[]{currentFirstGearRadius,3};
          }
          
      }
      
      return new int[]{-1,-1}; 
    } 
    
    public static void main(String []args){
        
        int[] pegs = new int[]{4,30,50};
        for (int i =0; i<pegs.length; i++)
        {
            System.out.print(pegs[i]+",");
        }
        
        System.out.println("\nGearingUpForDestructions ");
        
        int[] sol = GearingUpForDestruction.solution(pegs);
        for (int i =0; i<sol.length; i++)
        {
            System.out.print(sol[i]+",");
        }
    }
}
