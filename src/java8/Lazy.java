// Taken from https://nofluffjuststuff.com/magazine/2016/11/the_duality_of_pure_functions
package java8;

import static java.util.stream.Collectors.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@SuppressWarnings("boxing")
public class Lazy {
    public static final int factor = 2;

    public static Stream<Integer> transform(List<Integer> numbers) {
        return numbers.stream().map(e -> e * factor);
    }

    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3);

        // The stream have not calculated anything so far!
        Stream<Integer> stream = transform(numbers);

        // Calculation starts here!
        System.out.println(stream.collect(toList())); // [2, 4, 6]
    }
}
