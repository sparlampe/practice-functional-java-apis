package io.pusteblume;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.Optional;

public class OptionalTest {

    @Test
    public void createOptional() {
        String val = "a string";
        Optional<String> optionalString = Optional.of(val);
        Optional<String> optionalOfNullableStr = Optional.ofNullable(val);

        Optional<Integer> empty = Optional.empty();
        Optional<String> emptyOptional = Optional.ofNullable(null);
    }

    @Test
    public void retrievingValues() {
        Integer a = 10;
        Optional<Integer> optionalInteger = Optional.of(a);
        System.out.println(optionalInteger.get());
        optionalInteger.stream().forEach(System.out::println);
        Optional.empty().stream().forEach(System.out::println);

        System.out.println(Optional.empty().orElse(5));
        System.out.println(Optional.empty().orElseGet(Math::random));

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Optional.empty().orElseThrow(() -> new IllegalArgumentException());
        });

        Assertions.assertThrows(NoSuchElementException.class, () -> {
            Optional.empty().orElseThrow();
        });
    }

    @Test
    public void operators() {

        //map
        System.out.println(Optional.of("someString").map(s -> "mappedToSomethingElse").get());
        System.out.println(Optional.<String>empty().map(s -> "mappedToSomethingElse").orElse("emptyOptionNotMapped"));
        System.out.println(Optional.of("someString").map(s -> null).orElse("presentOptionMappedToEmpty"));


        //filter
        System.out.println(Optional.of("someString").filter(d-> d.contains("x")).orElse("filteredToEmpty"));

        //flatMap
        System.out.println(Optional.of("someString").flatMap(d -> Optional.of("flatMapped")).get());
    }

    @Test
    public void miscOptionalMethods() {
        //ifPresent
        Optional.of("Value").ifPresent(System.out::println);
        Optional.of("Value").stream().forEach(System.out::println);

        //ifPresentOrElse
        Optional.of("Value").ifPresentOrElse(System.out::println, () -> System.out.println("emptyOption"));
        Optional.empty().ifPresentOrElse(System.out::println, () -> System.out.println("emptyOption"));

        //or
        Optional.of("Value").or(() -> Optional.of("New Value")).ifPresent(System.out::println);
        Assertions.assertThrows(NullPointerException.class, () -> {
              Optional.empty().or(() -> null).ifPresent(System.out::println);
        });

        //equals
        Assertions.assertTrue(Optional.of("Value").equals(Optional.of("Value")));

        //hashCode
        Assertions.assertEquals(0, Optional.empty().hashCode());
    }

}


