import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

// Order model
class Order {
    private final int orderId;
    public Order(int orderId) { this.orderId = orderId; }
    public int getOrderId() { return orderId; }
    @Override
    public String toString() { return "Order#" + orderId; }
}

// Customer Thread (Producer)
class Customer implements Runnable {
    private final BlockingQueue<Order> orderQueue;
    private final int totalOrders;

    public Customer(BlockingQueue<Order> orderQueue, int totalOrders) {
        this.orderQueue = orderQueue;
        this.totalOrders = totalOrders;
    }

    @Override
    public void run() {
        try {
            for (int i = 1; i <= totalOrders; i++) {
                Order order = new Order(i);
                System.out.println("Customer placed " + order);
                orderQueue.put(order); // Blocks if queue full
                Thread.sleep(500); // simulate time between orders
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

// Kitchen Thread (Order Processor)
class Kitchen implements Runnable {
    private final BlockingQueue<Order> orderQueue;
    private final BlockingQueue<Order> deliveryQueue;

    public Kitchen(BlockingQueue<Order> orderQueue, BlockingQueue<Order> deliveryQueue) {
        this.orderQueue = orderQueue;
        this.deliveryQueue = deliveryQueue;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Order order = orderQueue.take(); // waits for order
                System.out.println("Kitchen is preparing " + order);
                Thread.sleep(1000); // simulating preparation time
                System.out.println("Kitchen prepared " + order);
                deliveryQueue.put(order); // pass to delivery
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

// Delivery Thread
class Delivery implements Runnable {
    private final BlockingQueue<Order> deliveryQueue;

    public Delivery(BlockingQueue<Order> deliveryQueue) {
        this.deliveryQueue = deliveryQueue;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Order order = deliveryQueue.take();
                System.out.println("Delivery started for " + order);
                Thread.sleep(500); // simulate delivery time
                System.out.println("Order delivered: " + order);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

public class RestaurantMultithreadingApp {
    public static void main(String[] args) {
        BlockingQueue<Order> orderQueue = new ArrayBlockingQueue<>(5);
        BlockingQueue<Order> deliveryQueue = new ArrayBlockingQueue<>(5);

        Thread customerThread = new Thread(new Customer(orderQueue, 10));
        Thread kitchenThread = new Thread(new Kitchen(orderQueue, deliveryQueue));
        Thread deliveryThread = new Thread(new Delivery(deliveryQueue));

        customerThread.start();
        kitchenThread.start();
        deliveryThread.start();
    }
}
