package io.pusteblume;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class FunctionalApiTest {

    @Test
    public void functionComposition() {
        Function<String, String> prependHallo = d -> "Hallo, " + d;
        Function<String, String> appendDearFriend = d -> d + ", dear friend!";

        Assertions.assertEquals("Hallo, Roman, dear friend!", prependHallo.andThen(appendDearFriend).apply("Roman"));
        Assertions.assertEquals("Hallo, Roman, dear friend!", appendDearFriend.compose(prependHallo).apply("Roman"));
    }

    @Test
    public void curryingPartialApplication() {
        Function<Integer, Function<Integer, Integer>> add =  a -> b -> a + b;

        Function<Integer, Integer> addOne = add.apply(1);
        Assertions.assertEquals(2, addOne.apply(1));
    }

    @Test
    public void lazyEvaluation() {
        Supplier<String> strThunk = ()-> "someStr";
        Assertions.assertEquals("someStr", strThunk.get());
    }

    @Test
    public void tco(){
        Assertions.assertThrows(StackOverflowError.class,() -> {
            System.out.println(this.factorialStackUnsafe.apply(BigInteger.valueOf(5000), BigInteger.ONE));
        });

        Assertions.assertEquals(BigInteger.valueOf(6), factorialStackSafe.apply(BigInteger.valueOf(3), BigInteger.ONE).result());

        System.out.println(factorialStackSafe.apply(BigInteger.valueOf(5000), BigInteger.ONE).result());

    }

    final BiFunction<BigInteger, BigInteger, BigInteger> factorialStackUnsafe = (n, acc) ->
            n.equals(BigInteger.ONE)
            ? BigInteger.ONE
            : this.factorialStackUnsafe.apply(n.subtract(BigInteger.ONE), acc.multiply(n));


    final BiFunction<BigInteger, BigInteger, Trampoline<BigInteger>> factorialStackSafe =  (n, acc) ->
        n.equals(BigInteger.ONE)
                ? Trampoline.done(acc)
                : Trampoline.more(() -> this.factorialStackSafe.apply(n.subtract(BigInteger.ONE), acc.multiply(n)));

    //https://gist.github.com/pkukielka/2842475
    //https://java-design-patterns.com/patterns/trampoline/
    public interface Trampoline<T> {

        T get();

        default Trampoline<T> jump() {
            return this;
        }

        default T result() {
            return get();
        }

        default boolean complete() {
            return true;
        }

        static <T> Trampoline<T> done(final T result) {
            return () -> result;
        }

        static <T> Trampoline<T> more(final Trampoline<Trampoline<T>> trampoline) {
            return new Trampoline<T>() {
                @Override
                public boolean complete() {
                    return false;
                }

                @Override
                public Trampoline<T> jump() {
                    return trampoline.result();
                }

                @Override
                public T get() {
                    return trampoline(this);
                }

                T trampoline(final Trampoline<T> trampoline) {
                    return Stream.iterate(trampoline, Trampoline::jump)
                            .filter(Trampoline::complete)
                            .findFirst()
                            .map(Trampoline::result)
                            .orElseThrow();
                }
            };
        }
    }
}


