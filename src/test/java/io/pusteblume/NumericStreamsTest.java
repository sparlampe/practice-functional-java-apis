package io.pusteblume;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class NumericStreamsTest {

    @Test
    public void numericStreamMethods() {

        //IntStream
        //DoubleStream
        //LongStream
        Stream<Integer> streamBoxed= stream().boxed();
        Stream<String> streamObject = stream().mapToObj(Integer::toString);

        Assertions.assertEquals(stream().max(), OptionalInt.of(3));
        Assertions.assertEquals(stream().min(), OptionalInt.of(1));
        Assertions.assertEquals(stream().sum(), 6);
        Assertions.assertEquals(stream().average(), OptionalDouble.of(2.0));

        IntSummaryStatistics stats = stream().summaryStatistics();
        Assertions.assertEquals(stats.getAverage(), 2.0);
        Assertions.assertEquals(stats.getCount(), 3);
        Assertions.assertEquals(stats.getSum(), 6);
        Assertions.assertEquals(stats.getMin(), 1);
        Assertions.assertEquals(stats.getMax(), 3);

    }

    static IntStream stream(){
        return IntStream.range(1,4);
    }
}


