package io.pusteblume;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DesignPatternsUsingFunctionalApiTest {

    @Test
    public void iterator() {
        List<Integer> val = List.of(1, 2, 3, 4);

        //procedural
        Iterator<Integer> iter = val.iterator();
        while (iter.hasNext()) {
            System.out.println(iter.next());
        }

        //functional
        val.forEach(System.out::println);
    }

    @Test
    public void strategy() {
        List<ImmutablePair<String, Integer>> stocks = List.of(
                new ImmutablePair<>("GOOG", 4),
                new ImmutablePair<>("AMZ", 5)
        );

        Predicate<ImmutablePair<String, Integer>> byNameStrategy = d -> d.left.contains("G");
        Predicate<ImmutablePair<String, Integer>> byPriceStrategy = d ->  d.right > 4;

        System.out.println(stocks.stream().filter(byNameStrategy).collect(Collectors.toList()));
        System.out.println(stocks.stream().filter(byPriceStrategy).collect(Collectors.toList()));
    }
}


