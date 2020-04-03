package simulation.util;

import simulation.memory.Memory;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CsvWriter {
    String filePath;

    public CsvWriter(String filePath) {
        this.filePath = filePath;
    }

    public void printMemoryToCsvFile(List<Memory> memories, boolean withPriority) {
        if (memories.isEmpty()) {
            return;
        }
        try (PrintWriter pw = new PrintWriter(filePath)) {
            // Print header
            int nbQueues = memories.get(0).getQueueState().length;
            List<String> header = new ArrayList<>();
            header.add("MC");
            for (int i = 0; i < nbQueues; i++) {
                header.add("ArrClk " + i);
                header.add("DepClk " + i);
                if (withPriority) {
                    header.add("HPQ " + i);
                    header.add("LPQ " + i);
                    header.add("Active packet " + i);
                    header.add("Next packet " + i);
                }else {
                    header.add("size " + i);
                }
            }
            header.add("Token holder");
            header.add("TimeOutClk");
            header.add("TimeOutFlag");
            header.add("NextArrival");
            pw.println(convertToCsv(header));

            // Print the output of the simulation
            for (Memory memory : memories) {
                List<Object> row = new ArrayList<>();
                row.add(memory.getTime());
                double[][] queueState = memory.getQueueState();
                for (double[] queue : queueState) {
                    row.add(queue[Memory.ARR_CLK_INDEX]);
                    row.add(queue[Memory.DEP_CLK_INDEX]);
                    if (withPriority) {
                        row.add(queue[Memory.HIGH_PRIORITY_INDEX]);
                        row.add(queue[Memory.LOW_PRIORITY_INDEX]);
                        row.add(queue[Memory.ACTIVE_PACKET]);
                        row.add(queue[Memory.NEXT_PACKET]);
                    } else {
                        row.add(queue[Memory.HIGH_PRIORITY_INDEX] + queue[Memory.LOW_PRIORITY_INDEX]);
                    }
                }
                row.add(memory.getTokenHolder());
                row.add(memory.getTimeOutClk());
                row.add(memory.isTimeOut());
                row.add(memory.getNextArrClk());

                pw.println(convertToCsv(row));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private String convertToCsv(List<?> row) {
        return row.stream().map(Object::toString).collect(Collectors.joining(","));
    }
}
