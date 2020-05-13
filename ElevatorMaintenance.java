/*
Elevator Maintenance
You've been assigned the onerous task of elevator maintenance - ugh! It wouldn't be so bad, except that all the elevator documentation has been lying in a disorganized pile at the bottom of a filing cabinet for years, and you don't even know what elevator version numbers you'll be working on.

Elevator versions are represented by a series of numbers, divided up into major, minor and revision integers. New versions of an elevator increase the major number, e.g. 1, 2, 3, and so on. When new features are added to an elevator without being a complete new version, a second number named "minor" can be used to represent those new additions, e.g. 1.0, 1.1, 1.2, etc. Small fixes or maintenance work can be represented by a third number named "revision", e.g. 1.1.1, 1.1.2, 1.2.0, and so on. The number zero can be used as a major for pre-release versions of elevators, e.g. 0.1, 0.5, 0.9.2, etc (Commander Lambda is careful to always beta test her new technology, with her loyal henchmen as subjects!).

Given a list of elevator versions represented as strings, write a function answer(l) that returns the same list sorted in ascending order by major, minor, and revision number so that you can identify the current elevator version. The versions in list l will always contain major numbers, but minor and revision numbers are optional. If the version contains a revision number, then it will also have a minor number.

For example, given the list l as ["1.1.2", "1.0", "1.3.3", "1.0.12", "1.0.2"], the function answer(l) would return the list ["1.0", "1.0.2", "1.0.12", "1.1.2", "1.3.3"]. If two or more versions are equivalent but one version contains more numbers than the others, then these versions must be sorted ascending based on how many numbers they have, e.g ["1", "1.0", "1.0.0"]. The number of elements in the list l will be at least 1 and will not exceed 100.
*/ 

public class ElevatorMaintenance{
    
    String[] versions;
    int[][] versionNumbers;
    
    public void divideIntoNumbers()
    {
        versionNumbers = new int[versions.length][3];
        for (int i=0;i< versions.length; i++)
        {
            if (versions[i].indexOf(".") < 0)
            {
                versionNumbers[i][0] = Integer.parseInt(versions[i]);
                versionNumbers[i][1] = -1;
                versionNumbers[i][2] = -1;
            }
            else
            {
                 versionNumbers[i][0] = Integer.parseInt(versions[i].substring(0,versions[i].indexOf(".")));
                 String lFrom2 = versions[i].substring(versions[i].indexOf(".")+1);
                 if (lFrom2.indexOf(".") <0)
                 {
                     versionNumbers[i][1] = Integer.parseInt(lFrom2);
                     versionNumbers[i][2] = -1;
                 }
                 else
                 {
                     versionNumbers[i][1] = Integer.parseInt(lFrom2.substring(0,lFrom2.indexOf(".")));
                     versionNumbers[i][2] = Integer.parseInt(lFrom2.substring(lFrom2.indexOf(".")+1));
                 }
            }
        }        
    }
    
    public void concatIntoString()
    {
        for(int i=0;i<versionNumbers.length;i++)
        {
            versions[i] = versionNumbers[i][0] 
                          + (versionNumbers[i][1] == -1 ? "" : "."+ versionNumbers[i][1])
                          + (versionNumbers[i][2] == -1 ? "" : "."+ versionNumbers[i][2]);
        }
    }
    
    public void swapNumbers(int index1, int index2)
    {
        int[] swapNumbers = versionNumbers[index1];
        versionNumbers[index1] = versionNumbers[index2];
        versionNumbers[index2] = swapNumbers;
    }
    
    public void mergeLists(int startIndex, int middleindex, int endIndex)
    {
        int[][] mergedNumbers = new int[endIndex - startIndex + 1][3];
        
        int leftListIndex = startIndex;
        int rightListIndex = middleindex + 1;
        for(int i=0 ; i< mergedNumbers.length; i++)
        {           
            //if any sublist is already done..
            if(leftListIndex > middleindex) //only right has data left
            {
                mergedNumbers[i] = versionNumbers[rightListIndex];
                //mergedStrings[i] = versions[rightListIndex];
                rightListIndex++;
            }
            else if (rightListIndex > endIndex) //only left has data left
            {
                mergedNumbers[i] = versionNumbers[leftListIndex];
                //mergedStrings[i] = versions[leftListIndex];
                leftListIndex++;
            }
            else if ( //if left and right are same
                (versionNumbers[leftListIndex][0] == versionNumbers[rightListIndex][0]
                    && versionNumbers[leftListIndex][1] == versionNumbers[rightListIndex][1]
                    && versionNumbers[leftListIndex][2] == versionNumbers[rightListIndex][2])
                || //if left is smaller    
                (versionNumbers[leftListIndex][0] < versionNumbers[rightListIndex][0]
                     || (versionNumbers[leftListIndex][0] == versionNumbers[rightListIndex][0] 
                         && versionNumbers[leftListIndex][1] < versionNumbers[rightListIndex][1])
                     || (versionNumbers[leftListIndex][0] == versionNumbers[rightListIndex][0] 
                         && versionNumbers[leftListIndex][1] == versionNumbers[rightListIndex][1]
                         && versionNumbers[leftListIndex][2] < versionNumbers[rightListIndex][2])))
            {
                mergedNumbers[i] = versionNumbers[leftListIndex];
                //mergedStrings[i] = versions[leftListIndex];
                leftListIndex++;
            }
            else //right is smaller
            {
                mergedNumbers[i] = versionNumbers[rightListIndex];
                //mergedStrings[i] = versions[rightListIndex];
                rightListIndex++;
            }
            
        }
        
        //assign back to original arrays..
        for(int i=0 ; i< mergedNumbers.length; i++)
        {
            //versions[startIndex + i] = mergedStrings[i];
            versionNumbers[startIndex + i] = mergedNumbers[i];
        }
        
    }
    
    public void sortBucket(int startIndex, int endIndex) //both inclusive
    {
        if (endIndex - startIndex < 1) //should not happen
        {
            return;
        }
        else if (endIndex - startIndex == 1) //sort
        {
            if (versionNumbers[startIndex][0] > versionNumbers[endIndex][0]
                || (versionNumbers[startIndex][0] == versionNumbers[endIndex][0] && versionNumbers[startIndex][1] > versionNumbers[endIndex][1])
                || (versionNumbers[startIndex][0] == versionNumbers[endIndex][0] && versionNumbers[startIndex][1] == versionNumbers[endIndex][1] && versionNumbers[startIndex][2] > versionNumbers[endIndex][2])
                )
                {
                    swapNumbers(startIndex,endIndex);
                }
        }
        else if (endIndex - startIndex > 1) //sort and merge
        {
            int middleIndex = (startIndex + endIndex)/2;
            if(middleIndex != startIndex)
            {
                this.sortBucket(startIndex, middleIndex);
            }
            
            if (middleIndex + 1 < endIndex)
            {
                this.sortBucket(middleIndex + 1, endIndex);
            }
            
            //merge now
            this.mergeLists(startIndex,middleIndex,endIndex);
        }
        
        
    }
    
    public static String[] solution(String[] l) {
        ElevatorMaintenance solution = new ElevatorMaintenance();
        solution.versions = l;
        solution.divideIntoNumbers();
        solution.sortBucket(0,l.length-1);
        solution.concatIntoString();
        return solution.versions;
    }
    
    public static void main(String []args){
        
        String[] versions = new String[]{"1.1.2", "1.0", "1.3.3", "1.0.12", "1.0.2"};
        for (int i =0; i<versions.length; i++)
        {
            System.out.print(versions[i]+"  ,");
        }
        
        System.out.println("\nSorted ");
        
        String[] sortedVersions = ElevatorMaintenance.solution(versions);
        for (int i =0; i<sortedVersions.length; i++)
        {
            System.out.print(sortedVersions[i]+"  ,");
        }
    }
}
