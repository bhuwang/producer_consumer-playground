/**
 * 
 */
package com.bhuwan.producerconsumer.waitnotify;

/**
 * @author bhuwan
 *
 */
public class ProducerConsumer {

    private static int[] buffer;
    private static int count;
    private static Object lock = new Object();

    public ProducerConsumer(int[] buffer, int count) {
        this.buffer = buffer;
        this.count = count;
    }

    public ProducerConsumer() {
    }

    static class Producer {
        void produce() {
            synchronized (lock) {
                if (isFull(buffer)) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                buffer[count++] = 1;
                lock.notifyAll();
            }
        }
    }

    static class Consumer {
        void consume() {
            synchronized (lock) {
                if (isEmpty(buffer)) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                buffer[--count] = 0;
                lock.notifyAll();
            }
        }
    }

    private static boolean isFull(int[] buffer) {
        return count == buffer.length;
    }

    private static boolean isEmpty(int[] buffer) {
        return count == 0;
    }

    /**
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        buffer = new int[10];
        count = 0;

        Producer producer = new Producer();
        Consumer consumer = new Consumer();

        Runnable produceTask = () -> {
            for (int i = 0; i < 50; i++) {
                producer.produce();
            }
            System.out.println("Done producing....");
        };

        Runnable consumeTask = () -> {
            for (int i = 0; i < 50; i++) {
                consumer.consume();
            }
            System.out.println("Done consuming....");
        };

        Thread produceThread = new Thread(produceTask);
        Thread consumeThread = new Thread(consumeTask);

        produceThread.start();
        consumeThread.start();

        produceThread.join();
        consumeThread.join();

        System.out.println("Data in the buffer: " + count);
    }
}
