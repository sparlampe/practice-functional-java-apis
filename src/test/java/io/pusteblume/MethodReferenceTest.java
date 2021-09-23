package io.pusteblume;

import org.junit.jupiter.api.Test;
import java.util.function.*;

public class MethodReferenceTest {

    @Test
    public void methodReference() {
        //object:: instance method
        Consumer<Integer> consumer = System.out::println;
        consumer.accept(56);

        //class:: static method
        //1
        Supplier<Double> supplier = Math::random;
        System.out.println(supplier.get());

        //2
        BiFunction<String, String, Integer> biFunction = Helper::concatCount;
        System.out.println(biFunction.apply("first", "second"));

        //class:: instance method
        Function<String, Integer> fn = String :: length; // str -> str.length()
        System.out.println(fn.apply("someString"));
    }

    static class Helper {
        static Integer concatCount(String a, String b) {
            return (a + b).length();
        }
    }

    @Test
    public void constructorReference() {
        Function<Runnable, Thread> threadGenerator = Thread::new;
        Thread t = threadGenerator.apply(()-> System.out.println("someStr"));
        t.start();
    }
}


