package io.pusteblume;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FunctionalInterfaceTest {

    @Test
    public void threadWithLambdaAndWithout() {
        Thread i = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Thread creation by runnable interface");
            }
        });
        i.start();

        Thread l = new Thread(() -> System.out.println("Thread creation by lambda"));
        l.start();
    }

    @Test
    public void implementFunctionalInterface() {
        invokeMethodOnFunctionalInterface(() -> System.out.println("functional interface as lambda"));
        invokeMethodOnFunctionalInterface(new MyFunctionalInterface() {
            @Override
            public void myMethod() {
                System.out.println("functional interface as anonymous class");
            }
        });
    }

    @FunctionalInterface
    interface MyFunctionalInterface {
        void myMethod();
    }

    static void invokeMethodOnFunctionalInterface(MyFunctionalInterface lambda) {
        lambda.myMethod();
    }

    @Test
    public void declarativeVsImperative() {
        int sumOfEvens = 0;
        for (int i = 0; i <= 100; i++) {
            if (i % 2 == 0) {
                sumOfEvens = sumOfEvens + i;
            }
        }
        System.out.println("Summed imperative style: " + sumOfEvens);


        System.out.println("Summed declarative style: " + IntStream
                .rangeClosed(1, 100)
                .filter(i -> i % 2 == 0)
                .reduce((x, y) -> x + y)
                .getAsInt()
        );
    }

    @Test
    void differentLambdaTypes() {
        NoInputNoOutput l1 = () -> System.out.println("noInputNoOutput");
        InputsNoOutput l2 = (a, b) -> System.out.println("inputsNoOutput" + (a + b));
        InputsOutput l3 = str -> str.length();
    }

    @FunctionalInterface
    interface NoInputNoOutput {
        void myMethod();
    }

    @FunctionalInterface
    interface InputsNoOutput {
        void myMethod(int a, int b);
    }

    @FunctionalInterface
    interface InputsOutput {
        int myMethod(String str);
    }

    @Test
    void functionalGenerics() {
        GenericFunctionalInterface<String, String> fnStringString = s->s.substring(1,5);
        System.out.println(fnStringString.execute("someString"));

        GenericFunctionalInterface<String, Integer> fnStringInteger = s->s.length();
        System.out.println(fnStringInteger.execute("someString"));
    }

    @FunctionalInterface
    interface GenericFunctionalInterface<K,T> {
        T execute(K input);
    }


    @Test
    void providedFunctionalInterfacesPredicates() {
        List<String> listStr = List.of("first", "second", "");
        Predicate<String> predStr = s -> !s.isEmpty();
        System.out.println(filterList(listStr, predStr));


        List<Integer> listInt = List.of(1,3, 5, 6);
        Predicate<Integer> predInt = s -> s%2==0;
        System.out.println(filterList(listInt, predInt));
    }

    static <T> List<T> filterList(List<T> list, Predicate<T> pred){
        List<T> newList = new ArrayList<>();
        list.stream().forEach( e -> {
            if(pred.test(e)){
                newList.add(e);
            }
        });

        return list.stream().filter(pred).collect(Collectors.toList());
    }

    @Test
    void providedFunctionalInterfacesConsumer() {
        List<Integer> listInt = List.of(1,3, 5, 6);
        Consumer<Integer> consumer = e -> System.out.println(e);
        printElements(listInt, consumer);
    }

    static <T> void printElements(List<T> list, Consumer<T> consumer){
        for(T t : list){
            consumer.accept(t);
        }
    }


    @Test
    void providedFunctionalInterfacesSupplier() {
        Supplier<String> supplierString = () -> "A string";
        System.out.println(supplierString.get());
        Supplier<Double> supplierDouble = () -> 5.4;
        System.out.println(supplierDouble.get());
    }

    @Test
    void providedFunctionalInterfacesFunction() {
        Function<String, Integer> supplier = str -> str.length();
        System.out.println(supplier.apply("someString"));
    }

    @Test
    void providedFunctionalInterfacesUnaryOperator() {
        UnaryOperator<String> unaryOperator = str -> str.toUpperCase();
        System.out.println(unaryOperator.apply("someString"));
    }

    @Test
    void providedFunctionalInterfacesBiFunction() {
        BiFunction<String, String, Integer> biFunction = (a, b) -> (a + b).length();
        System.out.println(biFunction.apply("someString", "anotherString"));
    }

    @Test
    void providedFunctionalInterfacesBinaryOperator() {
        BinaryOperator<String> binaryOperator = (a, b) -> a + b;
        System.out.println(binaryOperator.apply("someString", "anotherString"));
    }
}


