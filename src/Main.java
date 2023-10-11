import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {

    static final int LIST_CAPACITY = 100;
    static final int COUNT = 10_000;

    static BlockingQueue<String> list1 = new ArrayBlockingQueue<>(LIST_CAPACITY);
    static BlockingQueue<String> list2 = new ArrayBlockingQueue<>(LIST_CAPACITY);
    static BlockingQueue<String> list3 = new ArrayBlockingQueue<>(LIST_CAPACITY);
    static String textMaxA = "";
    static String textMaxB;
    static String textMaxC;

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static long countLetter(String text, char ch) {
        return text.chars().filter(c -> c == ch).count();
    }

    public static void main(String[] args) throws InterruptedException {
        Thread threadGenerate = new Thread(() -> {
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

        Thread thread1 = new Thread(() -> {
            try {
                for (int i = 0; i < COUNT; i++) {
                    String str = list1.take();
                    if (countLetter(str, 'a') > countLetter(textMaxA, 'a')) {
                        textMaxA = str;
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        Thread thread2 = new Thread(() -> {
            try {
                for (int i = 0; i < COUNT; i++) {
                    String str = list2.take();
                    if (countLetter(str, 'b') > countLetter(textMaxA, 'b')) {
                        textMaxA = str;
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        Thread thread3 = new Thread(() -> {
            try {
                for (int i = 0; i < COUNT; i++) {
                    String str = list3.take();
                    if (countLetter(str, 'c') > countLetter(textMaxA, 'c')) {
                        textMaxA = str;
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        threadGenerate.start();
        thread1.start();
        thread2.start();
        thread3.start();

        threadGenerate.join();
        thread1.join();
        thread2.join();
        thread3.join();

        System.out.println("Строка содержащая баольше всего а: " + countLetter(textMaxA, 'a'));
        System.out.println("Строка содержащая баольше всего b: " + countLetter(textMaxA, 'b'));
        System.out.println("Строка содержащая баольше всего c: " + countLetter(textMaxA, 'c'));


    }
}