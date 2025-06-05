package lift;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class Lift {
    public enum Direction { UP, DOWN }

    private final PriorityQueue<Integer> upQueue = new PriorityQueue<>();
    private final Set<Integer> upRequests = new HashSet<>();

    private final PriorityQueue<Integer> downQueue = new PriorityQueue<>(Collections.reverseOrder());
    private final Set<Integer> downRequests = new HashSet<>();

    private final ReentrantLock lock = new ReentrantLock();

    Direction currDirection = null;
    Integer currentFloor = 0;

    private boolean shouldUseUpQueue(Integer floor) {
        return floor > currentFloor;
    }

    private void addToUpQueue(Integer floor) {
        if (upRequests.add(floor)) {
            upQueue.add(floor);
        }
    }

    private void addToDownQueue(Integer floor) {
        if (downRequests.add(floor)) {
            downQueue.add(floor);
        }
    }

    public void carRequest(Integer floor) {
        if (floor.equals(currentFloor)) return;

        lock.lock();
        try {
            if (currDirection == null) {
                currDirection = floor > currentFloor ? Direction.UP : Direction.DOWN;
            }

            if (shouldUseUpQueue(floor)) {
                addToUpQueue(floor);
            } else {
                addToDownQueue(floor);
            }
        } finally {
            lock.unlock();
        }
    }

    public void hallRequest(Integer floor, Direction direction) {
        lock.lock();
        try {
            if (direction == Direction.UP && floor >= currentFloor) {
                addToUpQueue(floor);
            } else if (direction == Direction.DOWN && floor <= currentFloor) {
                addToDownQueue(floor);
            } else {
                if (direction == Direction.UP) addToUpQueue(floor);
                else addToDownQueue(floor);
            }
        } finally {
            lock.unlock();
        }
    }

    public void serveNext() {
        lock.lock();
        try {
            if (currDirection == Direction.UP) {
                if (!upQueue.isEmpty()) {
                    int next = upQueue.poll();
                    upRequests.remove(next);
                    currentFloor = next;
                    System.out.println("Lift moved UP to floor: " + currentFloor);
                } else if (!downQueue.isEmpty()) {
                    currDirection = Direction.DOWN;
                }
            }

            if (currDirection == Direction.DOWN) {
                if (!downQueue.isEmpty()) {
                    int next = downQueue.poll();
                    downRequests.remove(next);
                    currentFloor = next;
                    System.out.println("Lift moved DOWN to floor: " + currentFloor);
                } else if (!upQueue.isEmpty()) {
                    currDirection = Direction.UP;
                }
            }

            if (upQueue.isEmpty() && downQueue.isEmpty()) {
                currDirection = null;
            }

        } finally {
            lock.unlock();
        }
    }

    public boolean hasPendingRequests() {
        lock.lock();
        try {
            return !upQueue.isEmpty() || !downQueue.isEmpty();
        } finally {
            lock.unlock();
        }
    }
}
