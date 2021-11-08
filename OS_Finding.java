// Programmer: Nathan Vanos
// Programming Assignment 2, CSCD 501
// Date: 10/07/2021

// imports
import java.io.File;
import java.io.FileNotFoundException;
import java.time.Clock;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.MemoryHandler;


class OS_Finding
{
    // main program
    public static void main(String[] args)
    {
        // only proceed if the user specifies arguments
        if(argsExist(args))
        {
            // first, retrieve the command line arguments
            // note: need a second copy of the array since the random
            // algorithm rearranges order of elements (could effect DS's performance)
            int orderStatistic = Integer.parseInt(args[1]);
            int[] inputArray = readInput(args[0]);
            int inputLength = inputArray.length;
            int[] deterministicArray = inputArray.clone();
            

            // stop the program when bad input occurs
            // also initialize memory usage object for memory tracking
            checkForBadInput(inputArray, inputLength, orderStatistic);
            
            // test the randomized algorithm
            long currentTimeMillis = System.currentTimeMillis();
            /*System.out.print("The result from the randomized algorithm is: ");
            System.out.println(randomizedSelect(inputArray, 0, inputLength - 1, orderStatistic));
            calculateTimeCost(currentTimeMillis, "randomized algorithm");*/
            
            // test the deterministic algorithm
            //currentTimeMillis = System.currentTimeMillis();
            System.out.print("The result from the deterministic algorithm is: ");
            System.out.println(
                deterministicSelect(deterministicArray, orderStatistic - 1, 1, deterministicArray.length - 1)
            );
            calculateTimeCost(currentTimeMillis, "deterministic algorithm");

        } else 
            endProgramWithError("Must enter a file name and an ith order statistic.");
    }

    // adjustHigherIndex(): retrieves the proper value of the highest index in a subarray
    // params (inputs): 
        // currentLowIndex, the first index of the sub array
        // highestIndex, the highest index of the entire array
    // returns (output): currentHighIndex, the proper value of the highest index in the sub array
    public static int adjustHigherIndex(int currentLowIndex, int highestIndex)
    {
        // if the currentLowIndex + 4 is greater than the highest index, 
        // it must be reduced until it is equal to the highest index
        // otherwise, just add 4 to the current low index

        int currentHighIndex = currentLowIndex + 4;

        while(currentHighIndex > highestIndex) currentHighIndex--;

        return currentHighIndex;
    }

    // argsExist checks to see if the command line args were given by the user
    // params (input): args, the command line arguments
    // returns (output): true if the length of the args is 2
    public static boolean argsExist(String[] args)
    {
        return args.length == 2;
    }

    // calculateTimeCost(): prints the time it took for the algorithm to run
    // params (inputs): 
        // startTime, the time in milliseconds at which the algorithm started
        // algorithmName, the name of the algorithm that ran
    // returns (output): no return, just prints a result
    public static void calculateTimeCost(long startTime, String algorithmName)
    {
        // retrieve the endtime from system clock then calculate the difference and print it
        long endTime = System.currentTimeMillis();
        System.out.println("The time it took for the " + algorithmName + " to run was: " + 
            (endTime - startTime) + " milliseconds."
        );
    }

    // checkForBadInput(): ensures nothing went wrong with user input
    // params (inputs):
        // array, an array of values
        // length, the length of array
        // orderStatistic, the order statistic the user specified
    // returns (output): stops program if there was bad input
    public static void checkForBadInput(int[] array, int length, int orderStatistic)
    {
        //  2 checks: 
        //  1. check for the error array (bad file)
        //  2. and check for a bad order statistic
        //      note: before this second check just make sure that if the length is 1
        //      an error does not occur
        if(length == 1 && array[0] == -1)
            endProgramWithError("The file that the user specified was not found.");

        if(orderStatistic == 1 && length == 1) return;
        
        if(orderStatistic > length || orderStatistic < 1)
        {
            System.out.println("The result from the randomized algorithm is: null");
            System.out.println("The result from the deterministic algorithm is: null");
            System.exit(0);
        }
            
    }

    // convertToArray converts an ArrayList of Integers to an array of int
    // params (input): an arraylist of integers
    // returns (output): an array of ints
    public static int[] convertToArray(List<Integer> list)
    {
        // declare a new array of equal length to the arraylist
        // then store every value from the arraylist in here
        // casted as an int
        int length = list.size();
        int[] newArray = new int[length];
        Iterator<Integer> iterator = list.iterator();
        
        for(int i = 0; i < length; i++) 
            newArray[i] = iterator.next().intValue();
        
        return newArray;
    }

    // deterministicSelect(): finds the ith order statistic using the deterministic algorithm
    // params (inputs): 
        // inputArray, the array in which the search is taking place
        // i_statistic, the order statistic that is being searched for
        // lowestIndex: the index of the first element in the current partition
        // highestIndex: the index of the last element in the current partition
    public static int deterministicSelect
    (
        int[] inputArray, 
        int i_statistic, 
        int lowestIndex,
        int highestIndex
    )
    {
        // Steps:
        //  1. get the median of all the n/5 medians through recursion
        //  2. find the index of the median
        //  3. swap the median with the last element in the array
        //  4. partition the array around the median
        //  5. if the i_statistic is the currentStatistic, return 
        //  6. if i_statistic is not the currentStatistic, recurse

        int median = getMedianByRecursion(inputArray, highestIndex, lowestIndex - 1);
        int medianIndex = searchForIndex(inputArray, median, lowestIndex - 1, highestIndex);

        swap(inputArray, highestIndex, medianIndex);
        int currentStatistic = partition(inputArray, lowestIndex - 1, highestIndex);
        //int currentStatistic = partitionIndex - lowestIndex + 1;

        if(i_statistic == currentStatistic) 
            return inputArray[currentStatistic];

        else if(i_statistic < currentStatistic)
            return deterministicSelect(inputArray, i_statistic, lowestIndex, currentStatistic - 1);

        else
            return deterministicSelect(inputArray, i_statistic, currentStatistic + 1, highestIndex);
    }

    // endProgramWithError() terminates the process running this program and notifies the user of an error
    // params (inputs): message, the error message that will be shown to the user
    // returns (outputs): status code to system
    public static void endProgramWithError(String message)
    {
        System.out.println(message);
        System.exit(0);
    }

    // getArrayOfMedians(): generates an array containing the medians of n/5 sub arrays
    // params (inputs): 
        // medians, the array of medians that is being edited
        // mainArray, the array containing all sub arrays
        // lowerIndex, the lower index of the current group
        // highestIndex, the highest index of the current group
    // returns (output): just modifies medians array
    public static void getArrayOfMedians
    (
        int[] medians,
        int[] mainArray, 
        int lowerIndex,
        int highestIndex
    )
    {
        // Steps: 
        //  1. if the array only has one element just return that element
        //  2. initialize an index for the array of medians
        //  3. loop through the main array and find the median for each group of 5
        
        if(mainArray.length == 1)
            medians[0] = mainArray[0];
        
        int currentMedianArrayIndex = 0;
        
        for(int i = lowerIndex; i < highestIndex; i += 5)
        {
            int higherIndex = adjustHigherIndex(i, highestIndex);
            medians[currentMedianArrayIndex] = getGroupMedian(mainArray, i, higherIndex);
            currentMedianArrayIndex++;
        }
    }

    // getGroupMedian(): retrieves the median of a group of 5 numbers
    // params (inputs):
        // mainArray, the array that contains the group of 5 numbers
        // lowIndex, the low range of the group
        // highIndex, the high range of the group
    // returns (output): median, the median of the current group
    public static int getGroupMedian(int[] mainArray, int lowIndex, int highIndex)
    {
        // if there is only one element in this group just return that element
        if(lowIndex >= highIndex) return mainArray[highIndex];
        // otherwise, put the group into an arraylist, sort it, and return the middle element

        List<Integer> subGroupList = new ArrayList<Integer>(highIndex - lowIndex + 1);
        
        for(int i = lowIndex; i <= highIndex; i++)
        {
            subGroupList.add(mainArray[i]);
        }

        Collections.sort(subGroupList);
        return subGroupList.get(((subGroupList.size())/2)).intValue();
    }

    // recursively finds the median of an array
    // params (inputs): 
        // inputArray, the array that is being searched
        // highestIndex, the highest index in the current partition
        // lowestIndex, the starting index of the partition
    // returns (output): the median of the array
    public static int getMedianByRecursion(int[] inputArray, int highestIndex, int lowestIndex)
    {
        // Steps:
        //  1. Base case: if the number of groups can no longer be divided by five,
        //      return the median of this group
        //  2. recursively divide the array of medians by 5 and 
        //      continuously find the medians of each group
        int range = highestIndex - lowestIndex + 1;
        int numberOfGroups = (int)Math.ceil(((double)range)/ 5);

        if(numberOfGroups <= 1)
        {
            return getGroupMedian(
                inputArray, lowestIndex, adjustHigherIndex(lowestIndex, highestIndex)
            );
        } else{
            int[] medians = new int[numberOfGroups]; 
            getArrayOfMedians(medians, inputArray, lowestIndex, highestIndex);
            return getMedianByRecursion(medians, numberOfGroups - 1, 0);
        }
    }

    // getRandomInt(): generates a random integer within a range
    // params (inputs): 
        // min, the low end of the range
        // max, the high end of the range
    // returns (output): a random integer between min and max
    public static int getRandomInt(int min, int max)
    {
        // if the array is size 1 then return the min
        if(min >= max) return min; 
        // use the current time as a seed
        Random randGenerator = new Random(System.currentTimeMillis());
        return randGenerator.nextInt(max - min) + min;
    }

    // openFile(): opens a file
    // params(inputs): filename, the name of the file to be opened
    // returns(output): a new file object
    public static File openFile(String fileName)
    {
        return new File(fileName);
    }

    // partition(): partitions an array of values
    // params (inputs): 
        // inputArray, the array that is being partitioned
        // p, the min index for partition
        // r, the max index for partition
    // returns (output): the index of the pivot
    public static int partition(int[] inputArray, int p, int r)
    {
        // steps: 
        //  1. Choose a pivot
        //  2. set the starting index for partition left
        //  3. find the index where the pivot belongs 
        //  4. swap the element at that index with the pivot
        int pivot = inputArray[r];
        int partitionIndex = p - 1;

        for(int i = p; i < r; i++)
        {
            if(inputArray[i] <= pivot)
            {
                partitionIndex++;
                swap(inputArray, partitionIndex, i);
            }
        }

        swap(inputArray, partitionIndex + 1, r);
        return partitionIndex + 1;
    }

    // randomizedPartition(): randomly partitions an array of values
    // params (inputs):
        // inputArray, the array that is being partitioned
        // p, the min index for partition
        // r, the max index for partition
    // returns (output): a random partition of inputArray
    public static int randomizedPartition(int[] inputArray, int p, int r)
    {
        // steps:
        //  1. Pick a random integer between p and r
        //  2. Swap element r in inputArray with the last element in inputArray
        //  3. invoke a traditional partition
        int randomIndex = getRandomInt(p, r);
        swap(inputArray, randomIndex, r);
        return partition(inputArray, p, r);
    }

    // randomizedSelect(): finds the ith order statistic by choosing a random partition
    // params (inputs): 
        // inputArray, an array which is being searched for a statistic
        // p, the min index for partition
        // r, the max index for partition
        // i_statistic, the order statistic that is being searched for
    // returns (outputs): the ith order statistic
    public static int randomizedSelect(int[] inputArray, int p, int r, int i_statistic)
    {
        // Steps:
        //  1. if p is r then just return the element at r 
        //  2. do a randomized partition
        //  3. choose a statistic
        //  4. if the statistic is the i_statistic, return it
        //  5. if the statistic is not the i_statistic, recurse

        if(p == r) return inputArray[r];

        int partitionIndex = randomizedPartition(inputArray, p, r);
        int currentStatistic = partitionIndex - p + 1;

        if(i_statistic == currentStatistic)
            return inputArray[partitionIndex];

        else if(i_statistic < currentStatistic)
            return randomizedSelect(inputArray, p, partitionIndex - 1, i_statistic);    // recurse left

        else
            return randomizedSelect(inputArray, partitionIndex + 1, r, i_statistic - currentStatistic);    // recurse right
    }

    // swap(): swaps two elements inside an array
    // params (inputs): 
        // array, the array in which the swap is taking place
        // index_1, the index of the first element to be swapped
        // index_2, the index of the second element to be swapped
    // returns (outputs): no return but the contents of array is modified
    public static void swap(int[] array, int index_1, int index_2)
    {
        // if the array only has one element do not swap; saves time
        if(array.length == 1) return;
        int temp = array[index_1];
        array[index_1] = array[index_2];
        array[index_2] = temp;
    }   

    // readInput() reads numbers from a file and stores them in an array
    // params (inputs): 
    // returns (outputs): an array of numbers
    public static int[] readInput(String fileName)
    {
        // grab the file object, then declare a scanner
        // and read it
        File inputFile = openFile(fileName);

        try{
            Scanner inputScanner = new Scanner(inputFile);
            List<Integer> input = new ArrayList<Integer>();
            // use arraylist to build actual array
            while(inputScanner.hasNextLine())
            {
                input.add(Integer.parseInt(inputScanner.nextLine()));
            }
            inputScanner.close();
            int[] convertedArray = convertToArray(input);
            return convertedArray;
        }catch(FileNotFoundException exception){
            System.out.println(exception);
            int[] errorArray = {-1};
            return errorArray;
        }
    } 

    // searchForIndex(): finds the index of a given element in an array
    // params (inputs): 
        // array, the array that is being searched
        // target, the element whose index is needed
        // startIndex, the index of the first element to be searched
        // endIndex, the end of the search range
    // returns (output): the index of the target element
    public static int searchForIndex(int[] array, int target, int startIndex, int endIndex)
    {
        for(int i = startIndex; i <= endIndex; i++)
        {
            if(array[i] == target)
                return i;
        }
        return endIndex;
    }
}