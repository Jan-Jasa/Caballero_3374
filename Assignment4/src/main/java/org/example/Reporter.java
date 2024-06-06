//Author: Jan Jasa
//CSI 3471
//Assignment 4: Collections, more with CSV file, and Maven

package org.example;
import java.util.*;
import java.io.FileNotFoundException;
import java.io.File;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static java.util.stream.Collectors.*;

public class Reporter {
    enum sortCity implements Comparator<String[]> {
        SORT_CITY{
            @Override
            public int compare(String[] a, String[] b) {
                //ALPHABETICALLY BY CITY NAME, USE INDEX 1
                return a[1].compareToIgnoreCase(b[1]);
            }
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        if (args == null || args.length != 2) {
            System.out.println("syntaxt is Reported <file path> <1-3>");
            System.exit(0);
        }
        Scanner scanner = new Scanner(new File(args[0]));

        //Initialize lists and other variables, as input actually worked
        ArrayList<String[]> inputList = new ArrayList<>();
        int lambda = Integer.parseInt(args[1]);

        scanner.nextLine(); //skip first line
        while (scanner.hasNext())
        {
            String line = scanner.nextLine();
            String[] inputs = line.split(",");
            String[] temp = new String[5]; //Holds (INSTNM,CITY,STABBR,INSTURL,NPCURL)

            //copies inputs array, and takes specific data to be put into temp
            System.arraycopy(inputs, 3, temp, 0, 5);
            inputList.add(temp);
        }
        inputList.sort(sortCity.SORT_CITY);

        //COMPLETE
        if (lambda == 1) {
            System.out.println("List of Institution names, cities, state abbreviations, and URLs ordered by the city.");
            System.out.println("Note: all institutions containing \"tech\" are removed.");
            for(String[] holder : inputList){
                System.out.println("");
                if(!holder[0].toLowerCase().contains("tech")){
                    //inputList.remove(holder);
                    for(int i=0; i<holder.length-1; i++){
                        System.out.print(holder[i] + ", ");
                    }
                    System.out.print(holder[holder.length-1]);
                }
            }
        }
        //COMPLETE
        else if (lambda == 2) { //Alphabetically post cities, along w/ institutions within it
            System.out.println("List of cities (alphabetically), along with amount of institutions:");

            //Map it, by collecting data and grouping it by city. Use counting() to number of elements passed
            Map<String, Integer> cityMap = inputList.stream()
                    .collect(groupingBy(p -> p[1], summingInt(e -> 1)));
            ArrayList<String> cityList = new ArrayList<>(cityMap.keySet());
            Collections.sort(cityList, String.CASE_INSENSITIVE_ORDER);
            
            for(String s : cityList){
                System.out.println(s + ": " + cityMap.get(s) + " institutions/universities.");
            }
        }
        //INCOMPLETE
        else if (lambda == 3) { //Order of states with higher amount of institutions
            System.out.println("List of states (in order of amount of institutions):");
            Map<String, Integer> stateMap = inputList.stream()
                    .collect(groupingBy(p -> p[2], summingInt(e -> 1)));

            //Stream<Map.Entry<String, Integer>> sorted =
            stateMap.entrySet().stream()
                    .sorted(Collections.reverseOrder(Map.Entry.comparingByValue())) //Sorts by value
                    .forEach(entry -> System.out.println(entry.getKey() + " has " + entry.getValue())); //outputs
        }
    }
}
