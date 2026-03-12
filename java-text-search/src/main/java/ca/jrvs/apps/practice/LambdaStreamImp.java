package ca.jrvs.apps.practice;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class LambdaStreamImp implements LambdaStreamExc {

    @Override
    public Stream<String> createStrStream(String... strings) {
        // Creates a Stream from an array (varargs becomes array internally)
        return Arrays.stream(strings);
    }

    @Override
    public Stream<String> toUpperCase(String... strings) {
        // map = transform each element
        return createStrStream(strings).map(String::toUpperCase);
    }

    @Override
    public Stream<String> filter(Stream<String> stringStream, String pattern) {
        // filter keeps elements that match the condition
        // Ticket example says: keep strings that DO NOT contain pattern
        return stringStream.filter(s -> s != null && !s.contains(pattern));
    }

    @Override
    public IntStream createIntStream(int[] arr) {
        return Arrays.stream(arr);
    }

    @Override
    public <E> List<E> toList(Stream<E> stream) {
        // terminal operation: collect
        return stream.collect(Collectors.toList());
    }

    @Override
    public List<Integer> toList(IntStream intStream) {
        // IntStream is primitive stream, need boxing to Integer objects
        return intStream.boxed().collect(Collectors.toList());
    }

    @Override
    public IntStream createIntStream(int start, int end) {
        // inclusive range
        return IntStream.rangeClosed(start, end);
    }

    @Override
    public DoubleStream squareRootIntStream(IntStream intStream) {
        // mapToDouble transforms int -> double
        return intStream.mapToDouble(Math::sqrt);
    }

    @Override
    public IntStream getOdd(IntStream intStream) {
        // odd numbers => n % 2 != 0
        return intStream.filter(n -> n % 2 != 0);
    }

    @Override
    public Consumer<String> getLambdaPrinter(String prefix, String suffix) {
        // Return a lambda (Consumer) that prints formatted messages
        return msg -> System.out.println(prefix + msg + suffix);
    }

    @Override
    public void printMessages(String[] messages, Consumer<String> printer) {
        // Use stream + forEach (terminal operation)
        createStrStream(messages).forEach(printer);
    }

    @Override
    public void printOdd(IntStream intStream, Consumer<String> printer) {
        // pipeline: filter odd -> convert to String -> print
        getOdd(intStream)
                .mapToObj(String::valueOf)
                .forEach(printer);
    }

    @Override
    public Stream<Integer> flatNestedInt(Stream<List<Integer>> ints) {
        // flatMap flattens Stream<List<Integer>> into Stream<Integer>
        return ints.flatMap(List::stream).map(n -> n * n);
    }

    // Optional: quick main for manual testing (JUnit preferred)
    public static void main(String[] args) {
        LambdaStreamExc lse = new LambdaStreamImp();

        System.out.println(lse.toUpperCase("a", "b", "jarvis").collect(Collectors.toList()));
        System.out.println(lse.filter(lse.createStrStream("cat", "apple", "dog"), "a")
                .collect(Collectors.toList()));

        System.out.println(lse.toList(lse.createIntStream(new int[]{1, 2, 3, 4})));
        System.out.println(lse.toList(lse.getOdd(lse.createIntStream(0, 10))));

        Consumer<String> printer = lse.getLambdaPrinter("msg:", "!");
        lse.printMessages(new String[]{"a", "b", "c"}, printer);

        lse.printOdd(lse.createIntStream(0, 5), lse.getLambdaPrinter("odd number:", "!"));

        Stream<List<Integer>> nested = Stream.of(Arrays.asList(1, 2), Arrays.asList(3));
        System.out.println(lse.flatNestedInt(nested).collect(Collectors.toList()));
    }
}
