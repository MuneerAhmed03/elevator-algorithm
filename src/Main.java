import lift.Lift;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;



public class Main {
    public static void main(String[] args) {
        Lift lift = new Lift();
        Random rand = new Random();

        Thread movementThread = new Thread(() -> {
            try {
                while (true) {
                    if (lift.hasPendingRequests()) {
                        lift.serveNext();
                    }
                    Thread.sleep(1000);
                }
            } catch (InterruptedException ignored) {}
        });

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

        scheduler.scheduleAtFixedRate(() -> {
            int floor = rand.nextInt(10);
            lift.carRequest(floor);
            System.out.println("[CAR] Request to floor " + floor);
        }, 1, 1 + rand.nextInt(2), TimeUnit.SECONDS);

        scheduler.scheduleAtFixedRate(() -> {
            int floor = rand.nextInt(10);
            Lift.Direction dir = rand.nextBoolean() ? Lift.Direction.UP : Lift.Direction.DOWN;
            lift.hallRequest(floor, dir);
            System.out.println("[HALL] Request at floor " + floor + " to go " + dir);
        }, 0, 2 + rand.nextInt(2), TimeUnit.SECONDS);

        movementThread.start();
    }
}
