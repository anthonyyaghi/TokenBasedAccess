package app;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import simulation.Simulation;
import simulation.memory.Memory;
import simulation.util.CsvWriter;
import simulation.util.SimpleTimeProvider;
import simulation.util.StochasticTimeProvider;
import simulation.util.TimeProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SimController {
    private static final double CANVAS_W = 800;
    private static final double CANVAS_H = 600;
    private static final double STARTING_X = 50;
    private static final double STARTING_Y = 100;
    private static final double QUEUE_W = 600;
    private static final double QUEUE_H = 50;
    private static final double PACKET_W = 30;
    private static final double TOKEN_RADIUS = 50;

    SimulationSettings settings;

    private Timer eventTimer;

    @FXML
    private Canvas canvas;


    void startSim() {
        TimeProvider timeProvider;
        if (settings.isSimpleTime()) {
            timeProvider = new SimpleTimeProvider(settings.getInterArrTime(), settings.getTxTime(),
                    settings.getTimeOutTime(), settings.getSwitchOverTime());
        } else {
            timeProvider = new StochasticTimeProvider(settings.getInterArrTime(), settings.getShortTxTime(),
                    settings.getLongTxTime(), settings.getTimeOutTime(), settings.getSwitchOverTime());
        }

        Simulation sim;
        if (settings.isRandomInit()) {
            sim = new Simulation(settings.getNbQ(), settings.getTokenHolder(), timeProvider, settings.isWithPriority());
        } else {
            sim = new Simulation(settings.getNbQ(), settings.getTokenHolder(), settings.getInitArr(), timeProvider, settings.isWithPriority());
        }

        List<Memory> memories = new ArrayList<>();
        Memory latest;
        do {
            latest = sim.simulate();
            memories.add(latest);
        } while (latest.getTime() <= settings.getReplayDur());

        // Output to csv file
        if (settings.isGenCsv()) {
            CsvWriter writer = new CsvWriter("output.csv");
            writer.printMemoryToCsvFile(memories, settings.isWithPriority());
        }

        GraphicsContext gc = canvas.getGraphicsContext2D();

        eventTimer = new Timer();
        for (Memory memory : memories) {
            eventTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    drawMemory(gc, memory);
                }
            }, (long) (memory.getTime() * 1000));
        }
    }

    private void drawMemory(GraphicsContext gc, Memory memory) {
        gc.clearRect(0, 0, CANVAS_W, CANVAS_H);

        gc.setLineWidth(5);
        double[][] queueState = memory.getQueueState();
        for (int i = 0; i < queueState.length; i++) {
            drawQueue(gc, STARTING_X, STARTING_Y + (i * (QUEUE_H + gc.getLineWidth() * 2)),
                    (int) queueState[i][Memory.HIGH_PRIORITY_INDEX],
                    (int) queueState[i][Memory.LOW_PRIORITY_INDEX]);

        }

        // Draw the token
        gc.setFill(Color.DARKGREY);
        if (memory.isTimeOut()) {
            gc.setFill(Color.RED);
        }
        gc.fillOval(STARTING_X + QUEUE_W + gc.getLineWidth(),
                STARTING_Y + memory.getTokenHolder() * ((QUEUE_H + gc.getLineWidth() * 2)),
                TOKEN_RADIUS, TOKEN_RADIUS);

        drawTime(gc, memory.getTime());
    }

    private void drawQueue(GraphicsContext gc, double x, double y, int hpPackets, int lpPackets) {
        // Draw the queue
        gc.setStroke(Color.BLUE);
        gc.strokeRect(x, y, QUEUE_W, QUEUE_H);

        // Draw HP elements
        int packetSpacing = 2;
        gc.setFill(Color.GREEN);
        double nextPacketX = (x + QUEUE_W - PACKET_W);
        for (int i = 0; i < hpPackets; i++) {
            gc.fillRect(nextPacketX, y, PACKET_W, QUEUE_H);
            nextPacketX -= (PACKET_W + packetSpacing);
        }

        // Draw LP elements
        gc.setFill(Color.YELLOW);
        for (int i = 0; i < lpPackets; i++) {
            gc.fillRect(nextPacketX, y, PACKET_W, QUEUE_H);
            nextPacketX -= (PACKET_W + packetSpacing);
        }
    }

    private void drawTime(GraphicsContext gc, double time) {
        gc.setFill(Color.BLACK);
        gc.clearRect(0, 0, CANVAS_W / 2, 50);
        gc.fillText("Time: " + time + " s", 0, 50);
    }

    public void load(SimulationSettings settings) {
        this.settings = settings;
        startSim();
    }

    public void shutdown() {
        if (eventTimer != null) {
            eventTimer.cancel();
        }
    }
}
