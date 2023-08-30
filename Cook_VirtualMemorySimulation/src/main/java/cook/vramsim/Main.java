package cook.vramsim;

import java.util.*;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // The first input loop for getting number of frames
        int frames = 3;
        boolean isValid = false;
        while(isValid == false)
        {
            System.out.println("Please input the number of page frames available for this application from 2-7:");
            try {
                frames = scanner.nextInt();
                if (frames >= 2 && frames <= 7){
                    isValid = true;
                }
                else {
                    System.out.println("Incorrect input. Please input a numerical value from 2-7");
                    isValid = false;
                    frames = -1;
                }
            }
            catch (InputMismatchException e) {
                System.out.println("Invalid input. Please input a numerical value from 2-7");
                isValid = false;
            }
        }

        // I would use an unsigned type if I was not limited to Java's types
        int[] addrs = new int[100];
        int[] pages = new int[100];
        Random rand = new Random();

        // Populate arrays and output memory addressing information to the user.
        for(int i = 0; i < addrs.length; i++){
            int addr = rand.nextInt(Short.MAX_VALUE);
            int pageNum = addr / 4096;
            int offset = addr % 4096;
            addrs[i] = addr;
            pages[i] = pageNum;

            System.out.println("The address %d contains: page number = %d offset = %d".formatted(addr, pageNum, offset));
        }

        Integer[][] fifo_vram = new Integer[100][frames];
        int fifo_faults = 0;
        ArrayList<Integer> timeSinceInserted = new ArrayList<>(frames);

        Integer[][] lfu_vram = new Integer[100][frames];
        int lfu_faults = 0;
        Integer[][] opra_vram = new Integer[100][frames];
        int opra_faults = 0;

        Map<Integer, Integer> pageFrequencies = new HashMap<Integer, Integer>();
        for(int time = 0; time < 100; time++) {

            // Track page frequency for LFU using a hashmap
            int pageToWrite = pages[time];

            // Populate with -1 to know where empty values are w/o using null. Zero is default, but cannot be used as it is a possible page
            Arrays.fill(fifo_vram[time], -1);
            Arrays.fill(lfu_vram[time], -1);
            Arrays.fill(opra_vram[time], -1);

            if(pageFrequencies.containsKey(pageToWrite))
                pageFrequencies.put(pageToWrite, (pageFrequencies.get(pageToWrite) + 1));
            else
                pageFrequencies.put(pageToWrite, 0);

            boolean generatesFault = true;

            if(time > frames) {
                for (int i = 0; i < fifo_vram[time - 1].length; i++) {
                    if (fifo_vram[time - 1][i] == pageToWrite) {
                        generatesFault = false;
                        fifo_vram[time] = Arrays.copyOf(fifo_vram[time - 1], fifo_vram[time - 1].length);
                        timeSinceInserted.set(i, 0);
                        break;
                    }
                    else {
                        timeSinceInserted.set(i, (timeSinceInserted.get(i) + 1));
                    }
                }
            }

            if(generatesFault) {
                if(time == 0) {
                    fifo_vram[time][0] = pageToWrite;
                }
                else if(time < frames) {
                    fifo_vram[time] = Arrays.copyOf(fifo_vram[time - 1], fifo_vram[time - 1].length);
                    for(int i = 0; i < frames; i++){
                        if(fifo_vram[time][i] == -1){
                            fifo_vram[time][i] = pageToWrite;
                            timeSinceInserted.set(i, 0);
                            break;
                        }
                        else{
                            timeSinceInserted.set(i, (timeSinceInserted.get(i) + 1));
                        }
                    }
                }
                else {
                    for (int framesIndex = 0; framesIndex < frames; framesIndex++) {
                        // FIFO Paging -------------------------------------------------------------------------------------------------------------
                        fifo_faults++;
                        //Determine oldest page
                        int highestElement = Stream.of(timeSinceInserted.toArray()).max(Comparator.comparing(Integer::valueOf)).get();
                        int oldestPageIndex =timeSinceInserted.indexOf(highestElement);

                        for (int i = 0; i < fifo_vram[time].length; i++) {
                            if (i == oldestPageIndex)
                                fifo_vram[time][oldestPageIndex] = pageToWrite;
                            else
                                fifo_vram[time][i] = fifo_vram[time - 1][i];
                        }

                        //--------------------------------------------------------------------------------------------------------------------------
                    }
                }





                // LFU Paging --------------------------------------------------------------------------------------------------------------

                //--------------------------------------------------------------------------------------------------------------------------



                // Optimal Page Replacement Paging -----------------------------------------------------------------------------------------

                //--------------------------------------------------------------------------------------------------------------------------
                }
            }

        PrintMemory(fifo_vram);
        //PrintMemory(lfu_vram);
        //PrintMemory(opra_vram);
    }

    public static void PrintMemory(Integer[][] memory) {
        String leftAlignFormat = "| %-15s | %-4d | %-4d | %-4d | %-4d | %-4d | %-4d | %-4d | %-4d | %-4d | %-4d |%n";

        System.out.format("+-----------------+------+------+------+------+------+------+------+------+------+------+%n");
        System.out.format("| Frame           | T1   | T2   | T3   | T4   | T5   | T6   | T7   | T8   | T9   | T10  |%n");
        System.out.format("+-----------------+------+------+------+------+------+------+------+------+------+------+%n");
        for (int i = 0; i < memory.length; i++) {
            if(memory[i].length > 10) {
                System.out.format(leftAlignFormat, "Frame " + i, memory[i][0], memory[i][1], memory[i][2], memory[i][3], memory[i][4], memory[i][5], memory[i][6], memory[i][7], memory[i][8], memory[i][9]);
                System.out.format("+-----------------+------+%n");
            }
            else {
                System.out.println("Something went wrong during memory writing. Debug Info below:");
                System.out.println("Memory length: " + memory.length);
                System.out.println("Frame length: " + memory[0].length);
                break;
            }
        }
    }
}