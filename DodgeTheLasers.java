/*
Dodge the Lasers!
=================

Oh no! You've managed to escape Commander Lambdas collapsing space station in an escape pod with the rescued bunny prisoners - but Commander Lambda isnt about to let you get away that easily. She's sent her elite fighter pilot squadron after you - and they've opened fire!

Fortunately, you know something important about the ships trying to shoot you down. Back when you were still Commander Lambdas assistant, she asked you to help program the aiming mechanisms for the starfighters. They undergo rigorous testing procedures, but you were still able to slip in a subtle bug. The software works as a time step simulation: if it is tracking a target that is accelerating away at 45 degrees, the software will consider the targets acceleration to be equal to the square root of 2, adding the calculated result to the targets end velocity at each timestep. However, thanks to your bug, instead of storing the result with proper precision, it will be truncated to an integer before adding the new velocity to your current position.  This means that instead of having your correct position, the targeting software will erringly report your position as sum(i=1..n, floor(i*sqrt(2))) - not far enough off to fail Commander Lambdas testing, but enough that it might just save your life.

If you can quickly calculate the target of the starfighters' laser beams to know how far off they'll be, you can trick them into shooting an asteroid, releasing dust, and concealing the rest of your escape.  Write a function answer(str_n) which, given the string representation of an integer n, returns the sum of (floor(1*sqrt(2)) + floor(2*sqrt(2)) + ... + floor(n*sqrt(2))) as a string. That is, for every number i in the range 1 to n, it adds up all of the integer portions of i*sqrt(2).

For example, if str_n was "5", the answer would be calculated as
floor(1*sqrt(2)) +
floor(2*sqrt(2)) +
floor(3*sqrt(2)) +
floor(4*sqrt(2)) +
floor(5*sqrt(2))
= 1+2+4+5+7 = 19
so the function would return "19".

str_n will be a positive integer between 1 and 10^100, inclusive. Since n can be very large (up to 101 digits!), using just sqrt(2) and a loop won't work. Sometimes, it's easier to take a step back and concentrate not on what you have in front of you, but on what you don't.

*/
public class DodgeTheLasers {
		
	final String sqrt2 ="1.414213562373095048801688724209698078569671875376948073176679737990732478462107038850387534327641572735013846";
	final String sqrt2Minus1 ="0.414213562373095048801688724209698078569671875376948073176679737990732478462107038850387534327641572735013846";
			
	String sqrt2ForObj;
	String sqrt2Minus1ForObj;
	
	public DodgeTheLasers(int sLength)	
	{
		this.sqrt2ForObj = sqrt2.substring(0,sLength+4);
		this.sqrt2Minus1ForObj = sqrt2Minus1.substring(0,sLength+4);
	}
	
	public String add(String n1, String n2)
	{
		if(n1.startsWith("-") && n2.startsWith("-"))
		{
			return "-"+add(n1.substring(1),n2.substring(1));
		}
		else if(n1.startsWith("-"))
		{
			return subtract(n2,n1.substring(1));
		}
		else if(n2.startsWith("-"))
		{
			return subtract(n1,n2.substring(1));
		}
		else if(n1.indexOf(".") >= 0 && n2.indexOf(".") >=0)
		{
			String intN1 = n1.substring(0,n1.indexOf("."));
			String intN2 = n2.substring(0,n2.indexOf("."));
			String decimal1 = n1.substring(n1.indexOf(".")+1);
			String decimal2 = n2.substring(n2.indexOf(".")+1);
			int minDecLength = Math.min(decimal1.length(), decimal2.length());
			String decSum = add(decimal1.substring(0,minDecLength),decimal2.substring(0,minDecLength)) 
							+ ((decimal1.length() > decimal2.length())?decimal1.substring(minDecLength):decimal2.substring(minDecLength));
			if(decSum.length() > Math.max(decimal1.length(), decimal2.length()))
			{
				return add(add(intN1,intN2),decSum.substring(0,decSum.length()-Math.max(decimal1.length(), decimal2.length()))) + "."+decSum.substring(decSum.length()-Math.max(decimal1.length(), decimal2.length()));
			}
			else
			{
				return add(intN1,intN2) +"."+decSum;
			}
		}
		else if(n1.indexOf(".") >=0)
		{
			return add(n1.substring(0,n1.indexOf(".")),n2) +"."+n1.substring(n1.indexOf(".")+1);
		}
		else if (n2.indexOf(".") >= 0)
		{
			return add(n2.substring(0,n2.indexOf(".")),n1) +"."+n2.substring(n2.indexOf(".")+1);
		}
			
		int n1Length = n1.length();
		int n2Length = n2.length();
		
		int loopCounts = Math.max(n1Length,n2Length);
		int carryLeft = 0;
		String sum = "";
		for(int i=1; i<=loopCounts; i++)
		{
			int nN1 = i>n1Length?0:Integer.parseInt(n1.substring(n1Length-i, n1Length-i+1));
			int nN2 = i>n2Length?0:Integer.parseInt(n2.substring(n2Length-i, n2Length-i+1));
			sum =   (nN1+nN2+carryLeft)%10 + sum;
			carryLeft = (nN1+nN2+carryLeft)/10;
		}		
		if (carryLeft >0)
		{
			sum = carryLeft+sum;
		}
		
		return sum;
	}
	
	public String subtract(String l, String s) //first is bigger than 2nd
	{
		if(l.startsWith("-") && s.startsWith("-"))
		{
			return subtract(s.substring(1),l.substring(1));
		}
		else if(l.startsWith("-"))
		{
			return "-"+add(l.substring(1),s);
		}
		else if(s.startsWith("-"))
		{
			return add(l,s.substring(1));
		}
		else if(l.indexOf(".") >= 0 || s.indexOf(".") >=0)
		{
			//make number of digits after decimal places equal..
			int decimalDigitsL = (l.indexOf(".") >=0) ? l.length() - l.indexOf(".") -1:0;
			int decimalDigitsS = (s.indexOf(".") >=0) ? s.length() - s.indexOf(".") -1:0;
			
			String pad = "";					
			for(int i=1; i<=Math.abs(decimalDigitsL-decimalDigitsS); i++)
			{
				pad += "0";
			}
			
			String result = subtract(((l.indexOf(".") >=0)? l.substring(0,l.length()-decimalDigitsL-1) + l.substring(l.indexOf(".")+1) : l) + (decimalDigitsL<decimalDigitsS?pad:"")
					  ,((s.indexOf(".") >= 0)? s.substring(0,s.length()-decimalDigitsS-1) + s.substring(s.indexOf(".") +1):s) + (decimalDigitsS<decimalDigitsL?pad:""));
			
			result = result.substring(0,result.length()-Math.max(decimalDigitsL, decimalDigitsS)) + "." + result.substring(result.length()-Math.max(decimalDigitsL, decimalDigitsS));									
			return result;
		}
		
		
		if(l.length() < s.length())
		{
			return '-'+subtract(s,l);
		}
		else if(l.length() == s.length())
		{
			for(int i=0;i<l.length();i++)
			{
				if(Integer.parseInt(l.substring(i,i+1)) > Integer.parseInt(s.substring(i,i+1)))
				{
					break;
				}
				else if(Integer.parseInt(l.substring(i,i+1)) < Integer.parseInt(s.substring(i,i+1)))
				{
					return '-'+subtract(s,l);
				}							
			}
		}
		
		int carryLeft = 0;
		String subtraction = "";
		for(int i=1; i<=l.length(); i++)
		{
			int nL = Integer.parseInt(l.substring(l.length()-i, l.length()-i+1));
			int nS = i>s.length()?0:Integer.parseInt(s.substring(s.length()-i, s.length()-i+1));				
			if(nL-nS-carryLeft>=0 || i==l.length())
			{
				subtraction = (nL-nS-carryLeft) + subtraction;
				carryLeft = 0;
			}
			else
			{
				subtraction = (10+nL-nS-carryLeft) + subtraction;
				carryLeft = 1;
			}
		}
		
		if (carryLeft >0)
		{
			subtraction = carryLeft+subtraction;
		}
		
		return subtraction;
	}
	
	private String multiplyOneDigit(String n, String digit)
	{
		if(digit.equals("0"))
			return "0";
		
		if(digit.equals("1"))
			return n;
			
		int carryLeft = 0;
		int nDigit = Integer.parseInt(digit);
		String valueXp="";
		for(int i=n.length()-1; i>=0; i--)
		{
			int nV = Integer.parseInt(n.substring(i, i+1));
			valueXp =   (nV*nDigit+carryLeft)%10 + valueXp;
			carryLeft = (nV*nDigit+carryLeft)/10;
		}	
		if (carryLeft >0)
		{
			valueXp = carryLeft+valueXp;
		}		
		return valueXp;
	}
	
	public String multiply(String n1, String n2)
	{
		if(n1.equals("0") || n2.equals("0"))
		{
			return "0";
		}
		else if(n1.startsWith("-") && n2.startsWith("-"))
		{
			return multiply(n1.substring(1),n2.substring(1));
		}
		else if(n1.startsWith("-") || n2.startsWith("-"))
		{
			return "-"+multiply(n1.startsWith("-")?n1.substring(1):n1, n2.startsWith("-")?n2.substring(1):n2);
		}
		else if (n1.indexOf(".")>=0 || n2.indexOf(".") >=0)
		{
			int decPoints = (n1.indexOf(".")>=0 ? n1.length()-n1.indexOf(".")-1:0) + (n2.indexOf(".")>=0 ? n2.length()-n2.indexOf(".")-1:0);
			String result = multiply((n1.indexOf(".")>=0 ? n1.substring(0,n1.indexOf("."))+n1.substring(n1.indexOf(".")+1):n1)
									,(n2.indexOf(".")>=0 ? n2.substring(0,n2.indexOf("."))+n2.substring(n2.indexOf(".")+1):n2));
			return result.substring(0,result.length()-decPoints)+"."+result.substring(result.length()-decPoints);
		}
		
		if(n1.length()<=1)
		{
			return multiplyOneDigit(n2,n1);
		}
		else if(n2.length()<=1)
		{
			return multiplyOneDigit(n1,n2);
		}
		else
		{
			String sumTillNow="";
			
			for(int i=0;i<n2.length();i++)
			{					
				sumTillNow += "0";
				String numToMultiply = n2.substring(i,i+1);					
				if(!numToMultiply.equals("0"))
				{
					sumTillNow = add(sumTillNow,multiplyOneDigit(n1,numToMultiply));
				}
			}
			return sumTillNow;
		}
	}
	
	public String divideBy2(String n) //only add +1, -1
	{
		String newN="";
		int currentDigit;
		int carryForward = 0;
		for(int i=0; i<n.length(); i++) {
			currentDigit = Integer.parseInt(n.substring(i, i+1)) + carryForward;
			newN = newN + currentDigit/2;
			carryForward = (currentDigit%2 * 10);
		}
		
		if (newN.startsWith("0"))
		{
			return newN.substring(1);
		}
		else
		{
			return newN;
		}
	}
	
	public String floor(String n)
	{
		if(n.indexOf(".") >=0)
		{
			return n.substring(0,n.indexOf("."));
		}
		else
		{
			return n;
		}
	}
	
	public String trimZeros(String n)
	{
		while(n.startsWith("0"))
		{
			n= n.substring(1);
		}
		return n;
	}
	
	public String findSum(String n)
	{
		if(n.equals("0"))
		{
			return "0";
		}
		if(n.equals("1"))
		{
			return "1";
		}		
		
		String n$ = floor(multiply(n,sqrt2Minus1ForObj));
		String nPlus$N = add(n,n$);
		
		return trimZeros(subtract(subtract(divideBy2(multiply(nPlus$N,add(nPlus$N,"1"))),multiply(n$,add(n$,"1"))),findSum(n$)));
	}
	
	public static String solution(String s) 
	{
		return new DodgeTheLasers(s.length()).findSum(s);
    }

	public static void main(String[] args) {					
		System.out.println(DodgeTheLasers.solution("9999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999"));
		
	}
}
