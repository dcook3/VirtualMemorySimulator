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
        fifo_vram = PerformFIFOMemoryPolicy(fifo_vram, pages, frames);

        Integer[][] lfu_vram = new Integer[100][frames];
        //lfu_vram = PerformLFUMemoryPolicy(lfu_vram, pages, frames);

        Integer[][] opra_vram = new Integer[100][frames];
        //opra_vram = PerformOPRAMemoryPolicy(opra_vram, pages, frames);


    }

    public static Integer[][] PerformFIFOMemoryPolicy(Integer[][] memory, int[] pages, int frames){
        int[] timeSinceInserted = new int[frames];
        int faults = 0;

        for(int time = 0; time < 100; time++) {

            // Track page frequency for LFU using a hashmap
            int pageToWrite = pages[time];

            // Populate with -1 to know where empty values are w/o using null. Zero is default, but cannot be used as it is a possible page
            Arrays.fill(memory[time], -1);

            boolean generatesFault = true;

            if(time > frames) {
                for (int i = 0; i < memory[time - 1].length; i++) {
                    if (memory[time - 1][i] == pageToWrite) {
                        generatesFault = false;
                        memory[time] = Arrays.copyOf(memory[time - 1], memory[time - 1].length);
                        timeSinceInserted[i] = 0;
                        break;
                    }
                    else {
                        timeSinceInserted[i]++;
                    }
                }
            }

            if(generatesFault) {
                faults++;
                System.out.println("FIFO Algorithm produced a fault at time " + time + ".");
                if(time == 0) {
                    memory[time][0] = pageToWrite;
                }
                else if(time < frames) {
                    memory[time] = Arrays.copyOf(memory[time - 1], memory[time - 1].length);
                    for(int i = 0; i < frames; i++){
                        if(memory[time][i] == -1){
                            memory[time][i] = pageToWrite;
                            timeSinceInserted[i] = 0;
                            break;
                        }
                        else{
                            timeSinceInserted[i]++;
                        }
                    }
                }
                else {
                    for (int framesIndex = 0; framesIndex < frames; framesIndex++) {
                        // FIFO Paging -------------------------------------------------------------------------------------------------------------

                        //Determine the oldest page
                        int oldestPageIndex = 0;

                        for (int i = 0; i < timeSinceInserted.length; i++) {
                            if (timeSinceInserted[i] > timeSinceInserted[oldestPageIndex]) {
                                oldestPageIndex = i;
                            }
                        }

                        for (int i = 0; i < memory[time].length; i++) {
                            if (i == oldestPageIndex)
                                memory[time][oldestPageIndex] = pageToWrite;
                            else
                                memory[time][i] = memory[time - 1][i];
                        }

                        //--------------------------------------------------------------------------------------------------------------------------
                    }
                }
            }
        }
        System.out.println("FIFO Algorithm has completed and produced a total of " + faults + " page faults./n/n/n");
        return memory;
    }

    public static Integer[][] PerformLFUMemoryPolicy(Integer[][] memory, int[] pages, int frames) {
        int faults = 0;
        HashMap<Integer, Integer> pageFrequencies = new HashMap<Integer, Integer>();

        for(int time = 0; time < 100; time++) {
            // Track page frequency for LFU using a hashmap
            int pageToWrite = pages[time];
            if(pageFrequencies.containsKey(pageToWrite))
                pageFrequencies.put(pageToWrite, (pageFrequencies.get(pageToWrite) + 1));
            else
                pageFrequencies.put(pageToWrite, 0);

            // Populate with -1 to know where empty values are w/o using null. Zero is default, but cannot be used as it is a possible page
            Arrays.fill(memory[time], -1);

            boolean generatesFault = true;

            if (time > frames) {
                for (int i = 0; i < memory[time - 1].length; i++) {
                    if (memory[time - 1][i] == pageToWrite) {
                        generatesFault = false;
                        memory[time] = Arrays.copyOf(memory[time - 1], memory[time - 1].length);
                        break;
                    }
                }
            }

            if (generatesFault) {
                faults++;
                System.out.println("FIFO Algorithm produced a fault at time " + time + ".");
                if (time == 0) {
                    memory[time][0] = pageToWrite;
                } else if (time < frames) {
                    memory[time] = Arrays.copyOf(memory[time - 1], memory[time - 1].length);
                    for (int i = 0; i < frames; i++) {
                        if (memory[time][i] == -1) {
                            memory[time][i] = pageToWrite;
                            break;
                        }
                    }
                }
                else {
                    int mostFrequentPage = pages[0];
                    int mostFrequent = pageFrequencies.get(pages[0]);
                    for(Integer freq : pageFrequencies.values()){
                        if(freq > mostFrequent){
                            mostFrequent = freq;
                        }
                    }


                }
            }
        }
        return memory;
    }
    public static Integer[][] PerformOPRAMemoryPolicy(Integer[][] memory, int[] pages, int frames) {
        return memory;
    }
}