package java8;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Unbelievable "production code" example from https://bugs.eclipse.org/498051
 */
@SuppressWarnings("boxing")
public class CrazyLambdas {

    public static void main(String[] args) throws Exception {
        String workspaces = "citc\nworkspaces/ccaracal\ns\n~/workspaces/ccaracal";
        String[] paths = toArray("\n", workspaces);
        try {
            createUniqueWorkspaceNameMapCrazyError(paths);
        } catch (Exception e) {
            e.printStackTrace();
        }

        workspaces = "/tmp/wsp\n/tmp/wsp/";
        String [] paths2 = toArray("\n", workspaces);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> createUniqueWorkspaceNameMapEndlessLoop(paths2, executor));
        executor.awaitTermination(5, TimeUnit.SECONDS);
        executor.shutdownNow();
    }

    private static String[] toArray(String tokens, String workspaces) {
        String[] paths = new String[10];
        StringTokenizer tokenizer = new StringTokenizer(workspaces, tokens);
        for (int i = 0; i < paths.length && tokenizer.hasMoreTokens(); ++i) {
            paths[i] = tokenizer.nextToken();
        }
        return paths;
    }

    private static Map<String, String> createUniqueWorkspaceNameMapCrazyError(String [] paths) {
        final String fileSeparator = File.separator;
        Map<String, String> uniqueWorkspaceNameMap = new HashMap<>();
        List<String[]> splittedWorkspaceNames = Arrays.asList(paths).stream()
                .filter(s -> s != null && !s.isEmpty()).map(s -> s.split(Pattern.quote(fileSeparator)))
                .collect(Collectors.toList());
        for (int i = 1; !splittedWorkspaceNames.isEmpty(); i++) {
            final int c = i;
            Function<String[], String> stringArraytoName = s -> String.join(fileSeparator,
                    Arrays.copyOfRange(s, s.length - c, s.length));
            List<String> uniqueNames = splittedWorkspaceNames.stream().map(stringArraytoName)
                    .collect(Collectors.groupingBy(s -> s, Collectors.counting())).entrySet().stream()
                    .filter(e -> e.getValue() == 1).map(e -> e.getKey()).collect(Collectors.toList());
            splittedWorkspaceNames.removeIf(a -> {
                String joined = stringArraytoName.apply(a);
                if (uniqueNames.contains(joined)) {
                    uniqueWorkspaceNameMap.put(joined, String.join(fileSeparator, a));
                    return true;
                }
                return false;
            });
        }
        return uniqueWorkspaceNameMap;
    }

    private static Map<String, String> createUniqueWorkspaceNameMapEndlessLoop(String [] paths, ExecutorService executor) {
        final String fileSeparator = File.separator;
        Map<String, String> uniqueWorkspaceNameMap = new HashMap<>();

        // Convert workspace paths to arrays of single path segments
        List<String[]> splittedWorkspaceNames = Arrays.asList(paths).stream()
                .filter(s -> s != null && !s.isEmpty()).map(s -> s.split(Pattern.quote(fileSeparator)))
                .collect(Collectors.toList());

        // create and collect unique workspace keys produced from arrays,
        // try to generate unique keys starting with the last segment of the
        // workspace path, increasing number of segments if no unique names
        // could be generated,
        // loop until all array values are removed from array list
        for (int i = 1; !splittedWorkspaceNames.isEmpty(); i++) {
            final int c = i;

            // Function which flattens arrays to (hopefully unique) keys
            Function<String[], String> stringArraytoName = s -> String.join(fileSeparator,
                    s.length < c ? s : Arrays.copyOfRange(s, s.length - c, s.length));

            // list of found unique keys
            List<String> uniqueNames = splittedWorkspaceNames.stream().map(stringArraytoName)
                    .collect(Collectors.groupingBy(s -> s, Collectors.counting())).entrySet().stream()
                    .filter(e -> e.getValue() == 1).map(e -> e.getKey()).collect(Collectors.toList());

            // remove paths for which we have found unique keys
            splittedWorkspaceNames.removeIf(a -> {
                String joined = stringArraytoName.apply(a);
                if (uniqueNames.contains(joined)) {
                    uniqueWorkspaceNameMap.put(joined, String.join(fileSeparator, a));
                    return true;
                }
                return false;
            });

            // For demonstration purpose only, to stop endless loop
            checkCancellation(executor, i);
        }
        return uniqueWorkspaceNameMap;
    }

    private static void checkCancellation(ExecutorService executor, int i) {
        if(executor.isShutdown()) {
            throw new IllegalStateException("Cancelled with loop count " + i);
        } else if (i % 1000000 == 0) {
            System.err.println("I'm streaming ... " + i);
        }
    }


}
