import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {

    static final int LIST_CAPACITY = 100;
    static final int COUNT = 10_000;

    static BlockingQueue<String> list1 = new ArrayBlockingQueue<>(LIST_CAPACITY);
    static BlockingQueue<String> list2 = new ArrayBlockingQueue<>(LIST_CAPACITY);
    static BlockingQueue<String> list3 = new ArrayBlockingQueue<>(LIST_CAPACITY);
    static Thread generateText;

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static int showOccurrenceLetter(BlockingQueue<String> queue, char letter) {
        String text;
        int max = 0;
        int count;
        try {
            while (generateText.isAlive()) {
                text = queue.take();
                count = (int) text.chars().filter(c -> c == letter).count();
                if (count > max) {
                    max = count;
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return max;
    }

    static Thread getThread(BlockingQueue<String> queue, char letter) {
        return new Thread(() -> {
            int max = showOccurrenceLetter(queue, letter);
            System.out.println("Длина строки содержащая больше всего " + letter + ": " + max);
        });
    }

    public static void main(String[] args) throws InterruptedException {
        generateText = new Thread(() -> {
            for (int i = 0; i < COUNT; i++) {
                String text = generateText("abc", 100_000);
                try {
                    list1.put(text);
                    list2.put(text);
                    list3.put(text);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Thread thread1 = getThread(list1, 'a');
        Thread thread2 = getThread(list2, 'b');
        Thread thread3 = getThread(list3, 'c');


        generateText.start();
        thread1.start();
        thread2.start();
        thread3.start();

        generateText.join();
        thread1.join();
        thread2.join();
        thread3.join();
    }
}