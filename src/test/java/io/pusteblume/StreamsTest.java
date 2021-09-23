package io.pusteblume;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.*;

public class StreamsTest {

    @Test
    public void declarativeApproach() {
        List<String> books = List.of("gone with the wind", "little women");
        Predicate<String> containsOn = b-> b.contains("on");
        List<String> filteredBooks = List.of("gone with the wind");

        //Procedural
        List<String> filteredBooksProcedural =  new ArrayList<>();
        for(String book : books){
            if(containsOn.test(book)){
                filteredBooksProcedural.add(book);
            }
        }
        Assertions.assertEquals(filteredBooks, filteredBooksProcedural);

        //Declarative
        List<String> filteredBooksDeclarative =  books
                .stream()
                .filter(containsOn)
                .collect(Collectors.toList());

        Assertions.assertEquals(filteredBooks, filteredBooksDeclarative);

        //DeclarativeParallel
        List<String> filteredBooksParallel =  books
                .parallelStream()
                .filter(containsOn)
                .collect(Collectors.toList());

        Assertions.assertEquals(filteredBooks, filteredBooksParallel);
    }

    @Test
    public void streamPipeline() {


        //Source
        Stream<String> books = Stream.of("gone with the wind", "little women");

        //Intermediate Operation
        Stream<String> booksFilter = books.filter(b-> b.contains("on"));

        //Terminal Operation
        List<String> bookList = booksFilter.collect(Collectors.toList());

    }

    @Test
    public void streamIsAnIteratorNotContainer() {

        Stream<String> books = Stream.of("gone with the wind", "little women");
        List<String> booksWithOn = books
                .filter(b-> b.contains("on"))
                .collect(Collectors.toList());

        Assertions.assertEquals(List.of("gone with the wind"), booksWithOn);

        Assertions.assertThrows(IllegalStateException.class, () -> {
                 List<String> booksWithS = books
                            .filter(b -> b.contains("s"))
                            .collect(Collectors.toList());
                }
        );

    }

    @Test
    public void streamsAreLazy() {

        List<String> collectedBooks = new ArrayList<>();

        Stream<String> books = Stream.of("gone with the wind", "little women");
        Stream<String> booksWithOn = books
                .filter(b-> b.contains("on"))
                .peek(b -> collectedBooks.add(b));

        Assertions.assertTrue(collectedBooks.isEmpty());
        List<String> filteredBooks = booksWithOn.collect(Collectors.toList());

        Assertions.assertEquals(filteredBooks.size(), 1);
        Assertions.assertEquals(collectedBooks.size(), 1);

    }

    @Test
    public void createBoundedStreams() {

        Stream<String> booksStream = List.of("gone with the wind", "little women").stream();

        Map<Integer, String> map = Map.of(1, "1v", 2, "2v");
        Stream<Map.Entry<Integer, String>> streamOfEntries = map.entrySet().stream();
        Stream<String> streamOfValues = map.values().stream();
        Stream<Integer> streamOfKeys = map.keySet().stream();

        Stream<String> streamOf = Stream.of("gone with the wind", "little women");

        Stream<String> streamFromArray = Arrays.stream(new String[]{"gone with the wind", "little women"});
        IntStream intStream = Arrays.stream(new int[]{1, 2});

        Stream.Builder<String> builder = Stream.builder();
        builder.add("gone with the wind");
        builder.add("little women");
        Stream<String> streamFromBuilder = builder.build();

    }

    @Test
    public void createInfiniteStreams() {
        List<Integer> streamFromIterate = Stream
                .iterate(0, i -> i - 1)
                .limit(2)
                .collect(Collectors.toList());
        Assertions.assertEquals(List.of(0, -1), streamFromIterate);

        List<String> streamFromGenerate = Stream
                .generate(() -> "Hallo")
                .limit(2)
                .collect(Collectors.toList());
        Assertions.assertEquals(List.of("Hallo", "Hallo"), streamFromGenerate);
    }

    @Test
    public void flatMap() {
       Stream<String> a = Stream.of("a", "b");
       Stream<String> b = Stream.of("c", "d");

       Stream<Stream<String>> cofStream = Stream.of(a,b);
       Stream<String> c = cofStream.flatMap(v -> v);
    }

    @Test
    public void dedicatedPool() throws ExecutionException, InterruptedException {
        Stream<String> stream = Stream.of("a", "b", "c");
        ForkJoinPool pool = new ForkJoinPool(5);

        Future<Long> future = pool.submit(() -> stream.parallel().count());

        Assertions.assertEquals(future.get(), 3);

    }
    @Test
    public void spliteratorAndCharacteristics() {
        Stream<Integer> stream = Stream.of(1,2,3);
        Spliterator<Integer> spliterator = stream.spliterator();
        int characteristicsBits = spliterator.characteristics();
        int expectedBits = Spliterator.IMMUTABLE | Spliterator.ORDERED | Spliterator.SIZED | Spliterator.SUBSIZED;
        Assertions.assertEquals(Integer.toBinaryString(characteristicsBits), Integer.toBinaryString(expectedBits));
    }

    @Test
    public void customSpliterator() throws IOException {
        String path = this.getClass().getResource("/Books.txt").getPath();
        Stream<String> lines = Files.lines(Path.of(path));
        Spliterator<String> spliterator = lines.spliterator();
        Stream<Book> stream = StreamSupport.stream(new BookSpliterator(spliterator), false);
        stream.forEach(System.out::println);
    }

    static class BookSpliterator implements Spliterator<Book> {
        private Double score;
        private String author;
        private String genre;
        private String name;
        private final Spliterator<String> baseSpliterator;

        public BookSpliterator(Spliterator<String> baseSpliterator){
            this.baseSpliterator = baseSpliterator;
        }
        @Override
        public boolean tryAdvance(Consumer<? super Book> action) {
            if(this.baseSpliterator.tryAdvance(name -> this.name = name) &&
                    this.baseSpliterator.tryAdvance(author -> this.author = author)&&
                    this.baseSpliterator.tryAdvance(genre -> this.genre = genre)&&
                    this.baseSpliterator.tryAdvance(score -> this.score = Double.parseDouble(score))) {
                    action.accept(new Book(this.author, this.genre, this.name, this.score));
                return true;
            } else
                return false;
        }

        @Override
        public Spliterator<Book> trySplit() {
            return null;
        }

        @Override
        public long estimateSize() {
            return baseSpliterator.estimateSize()/4;
        }

        @Override
        public int characteristics() {
            return baseSpliterator.characteristics();
        }
    }

    static class Book implements Comparable<Book>{
        private final Double score;
        private final String author;
        private final String genre;
        private final String name;

        public Book(String author, String genre, String name, Double score){
            this.author = author;
            this.genre = genre;
            this.name = name;
            this.score = score;
        }

        @Override
        public String toString() {
            return "Book{" +
                    "score=" + score +
                    ", author='" + author + '\'' +
                    ", genre='" + genre + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        }

        @Override
        public int compareTo(Book o) {
           return this.author.compareTo(o.author);
        }
    }

    @Test
    public void builtInCollectors() throws IOException {
        String path = this.getClass().getResource("/Books.txt").getPath();
        Stream<String> lines = Files.lines(Path.of(path));
        Spliterator<String> spliterator = lines.spliterator();
        Stream<Book> bookStream = StreamSupport.stream(new BookSpliterator(spliterator), false);
        List<Book> bookList = bookStream.collect(Collectors.toList());
        List<String> nameList = bookList.stream().map(b->b.name).collect(Collectors.toUnmodifiableList());
        Set<String> authorSet = bookList.stream().map(b->b.author).collect(Collectors.toUnmodifiableSet());

        TreeSet<String> sortedAuthorList = bookList.stream().map(b->b.author).collect(Collectors.toCollection(TreeSet::new));
        Map<String,String> map = bookList.stream().collect(Collectors.toMap(e->e.name, e->e.author));
        Map<Boolean, List<Book>> partitions = bookList.stream().collect(Collectors.partitioningBy(a-> a.author.contains("w")));
        Map<String , List<Book>> groups = bookList.stream().collect(Collectors.groupingBy(a-> a.author.substring(0,1)));
        String csNames = bookList.stream().map(v->v.name).collect(Collectors.joining(","));

        Map<String, Long> groupsCounts = bookList
                .stream()
                .collect(
                        Collectors.groupingBy(
                                a-> a.author.substring(0,1),
                                Collectors.counting()
                        )
                );

        Map<String, Double> groupsSum = bookList
                .stream()
                .collect(
                        Collectors.groupingBy(
                                a-> a.author.substring(0,1),
                                Collectors.summingDouble(e->e.score)
                        )
                );

        Map<String, Optional<Book>> maxSalaryEmployee = bookList
                .stream()
                .collect(
                        Collectors.groupingBy(
                                a-> a.author.substring(0,1),
                                Collectors.maxBy(Comparator.comparing(e-> e.score))
                        )
                );

//        Map<String, Optional<Double>> maxSalaries = bookList
//                .stream()
//                .collect(
//                        Collectors.groupingBy(
//                                a -> a.author.substring(0,1),
//                                Collectors.mapping(
//                                        e-> e.score,
//                                        Collectors.maxBy(
//                                                Comparator.comparing(Function.identity())
//                                        )
//                                )
//                        )
//                );

    }

    @Test
    public void customCollectors(){
        List<Integer> list = List.of(4,2,3);
        Collector<Integer, List<Integer>, List<Integer>> toList = Collector.of(
                ArrayList::new,
                (list1, a) -> list1.add(a),
                (list1,list2) -> {
                    list1.addAll(list2);
                    return list1;
                },
                Collector.Characteristics.IDENTITY_FINISH
        );
        Assertions.assertEquals(List.of(4,2,3), list.stream().collect(toList));

        Collector<Integer, List<Integer>, List<Integer>> toSortedList = Collector.of(
                ArrayList::new,
                (list1, a) -> list1.add(a),
                (list1,list2) -> {
                    list1.addAll(list2);
                    return list1;
                },
                list1 -> {Collections.sort(list1); return list1;},
                Collector.Characteristics.UNORDERED
        );

        Assertions.assertEquals(List.of(2,3, 4), list.stream().collect(toSortedList));
    }
}



