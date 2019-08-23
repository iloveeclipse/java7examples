/*******************************************************************************
 * Copyright (c) 2019 Andrey Loskutov. All rights reserved.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *     Andrey Loskutov - initial API and implementation
 *******************************************************************************/
package nmt;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Periodically polls native memory use for given JVM pid and prints it to the console
 *
 * Will try to provide some readable output from this:
 *
35545:
Native Memory Tracking:
Total: reserved=53062237KB, committed=22972161KB
-                 Java Heap (reserved=50331648KB, committed=20472320KB)
                            (mmap: reserved=50331648KB, committed=20472320KB)
-                     Class (reserved=207782KB, committed=207014KB)
                            (classes #25901)
                            (malloc=19366KB #77531)
                            (mmap: reserved=188416KB, committed=187648KB)
-                    Thread (reserved=305025KB, committed=305025KB)
                            (thread #80)
                            (stack: reserved=304668KB, committed=304668KB)
                            (malloc=265KB #410)
                            (arena=93KB #146)
-                      Code (reserved=297202KB, committed=174550KB)
                            (malloc=30962KB #34452)
                            (mmap: reserved=266240KB, committed=143588KB)
-                        GC (reserved=1855099KB, committed=1747775KB)
                            (malloc=16247KB #1151)
                            (mmap: reserved=1838852KB, committed=1731528KB)
-                  Compiler (reserved=377KB, committed=377KB)
                            (malloc=246KB #3838)
                            (arena=131KB #15)
-                  Internal (reserved=19513KB, committed=19509KB)
                            (malloc=19477KB #31738)
                            (mmap: reserved=36KB, committed=32KB)
-                    Symbol (reserved=36627KB, committed=36627KB)
                            (malloc=34349KB #408712)
                            (arena=2278KB #1)
-    Native Memory Tracking (reserved=8747KB, committed=8747KB)
                            (malloc=23KB #279)
                            (tracking overhead=8724KB)
-               Arena Chunk (reserved=217KB, committed=217KB)
                            (malloc=217KB)
 *
 */
public class NmtPrinter {

    static class NmtData {
        NmtSubEntry total;
        List<NmtEntry> data;

        NmtData(String input){
            data = new ArrayList<>();
            String[] lines = input.split("\\\n");
            boolean start = false;
            for (int i = 0; i < lines.length; i++) {
                String line = lines[i];
                line = line.trim();
                if(!start) {
                    if (line.startsWith("Total:")) {
                        start = true;
                        total = new NmtSubEntry(line);
                    } else if(line.startsWith("Native memory tracking is not enabled")) {
                        line += ".\n Start target JVM with following arguments:\n";
                        line += "-XX:+UnlockDiagnosticVMOptions\n" +
                                "-XX:+PrintNMTStatistics\n" +
                                "-XX:NativeMemoryTracking=summary\n";
                        total = new NmtSubEntry(line);
                        return;
                    }
                    continue;
                }
                List<String> list = new ArrayList<>();
                list.add(line);
                for (int j = ++i; j < lines.length; j++, i++) {
                    line = lines[j].trim();
                    if(line.startsWith("(")) {
                        list.add(line);
                    } else {
                        data.add(new NmtEntry(list.toArray(new String[0])));
                        break;
                    }
                }
            }

            Collections.sort(data, new Comparator<NmtEntry>() {

                @Override
                public int compare(NmtEntry o1, NmtEntry o2) {
                    int c = o2.committed() - o1.committed();
                    if(c == 0) {
                        return o2.reserved() - o1.reserved();
                    }
                    return c;
                }
            });
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("-      ");
            sb.append(total);
            for (NmtEntry nmtEntry : data) {
                sb.append("\n");
                sb.append(nmtEntry);
            }
            return sb.toString();
        }
    }
    static class NmtEntry {
        String name;
        Map<String, String> map;
        NmtSubEntry sub;
        private final Map<String, Integer> numbers;

        NmtEntry(String ... lines){
            map = new LinkedHashMap<>();
            numbers = new LinkedHashMap<>();
            name = lines[0].split("\\(")[0].trim();
            parseData(lines);
        }

        String name() {
            return name;
        }

        int reserved() {
            Integer r = numbers.get("reserved");
            return r == null ? 0 : r.intValue();
        }
        int committed() {
            Integer r = numbers.get("committed");
            return r == null ? 0 : r.intValue();
        }

        void parseData(String[] lines){
            for (String line : lines) {
                line = line.substring(line.indexOf('(') + 1);
                line = line.substring(0, line.indexOf(')'));
                if(line.indexOf(':') > 0) {
                    if(sub != null) {
                        throw new IllegalStateException(line);
                    }
                    sub = new NmtSubEntry(line);
                    continue;
                }
                String [] values = line.split(",");
                for (String string : values) {
                    string = string.trim();
                    numbers.putAll(parse(string, map));
                }
            }
        }

        @Override
        public String toString() {
            LinkedHashMap<String, String> subset = new LinkedHashMap<>(map);
            subset.remove("reserved");
            String string = name + ": " + subset;
            if(sub != null
                    && !sub.name.startsWith("mmap")
                    && !sub.name.startsWith("stack")
                    ) {
                string += "\n\t" + sub;
            }
            return string;
        }
    }

    static class NmtSubEntry {
        String name;
        Map<String, String> map;
        private final Map<String, Integer> numbers;
        NmtSubEntry(String line){
            map = new LinkedHashMap<>();
            numbers = new LinkedHashMap<>();
            if(line.contains("Native memory tracking is not enabled")) {
                name = line;
                return;
            }
            String[] strings = line.split(":");
            name = strings[0].trim();
            numbers.putAll(parse(strings[1], map));
        }

        @Override
        public String toString() {
            if(map.isEmpty()) {
                return name;
            }
            return "               " + name + ": "+ map;
        }
    }

    static Map<String, Integer> parse(String line, Map<String, String> map){
        Map<String, Integer> numbers = new LinkedHashMap<String, Integer>();
        String[] strings = line.split(",");
        for (String string : strings) {
            String[] pair;
            if(string.indexOf('=') > 0) {
                pair = string.split("=");
            } else {
                pair = string.split(" ");
            }
            String key = pair[0].trim();
            String value = pair[1];
            if(value.indexOf('#') >= 0) {
                value = value.substring(value.indexOf('#') + 1, value.length());
            }
            if(value.indexOf("KB") >= 0) {
                value = value.substring(0, value.indexOf("KB")).trim();
                Integer number = Integer.valueOf(value);
                numbers.put(key, number);
                int mb = number.intValue() / 1024;
                if(mb >= 0 && mb < 1024) {
                    value = mb + " MB";
                } else {
                    value = (mb / 1024) + " GB";
                }
            }
            map.put(key, value.trim());
        }
        return numbers;
    }

    @SuppressWarnings("null")
    public static void main(String[] args) throws Exception {
        if(args == null || args.length < 1) {
            System.err.println("Give a pid for JVM started with NMT enabled and timeout in seconds to redo the measurement");
            System.exit(1);
        }

        int timeout = 5000;
        try {
            if(args.length > 1) {
                timeout = Integer.parseInt(args[1]) * 1000;
                if(timeout < 1000) {
                    timeout = 1000;
                }
            }
        } catch (Exception e) {
            // don't care
        }
        while(true) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            execAndWaitFor(out, "/bin/jcmd", args[0], "VM.native_memory");
            String output = new String(out.toByteArray(), StandardCharsets.UTF_8);
            NmtData data = new NmtData(output);
//            System.out.println(output);
//            System.out.println("----");
            System.out.println(data);
            System.out.println("-----------------------------------------------------------------");
            if(data.data.isEmpty()) {
                break;
            }

            Thread.sleep(timeout);
        }
    }
    /**
     * Executes the given command, wait until execution is done. Write process input and
     * error output to given output stream. Uses ProcessBuilder to maintain process and
     * redirects error stream into input stream.
     *
     * @param command non null
     * @param out
     *            non null output stream used for command output
     * @return the exit value of the process. By convention, <code>0</code> indicates
     *         normal termination.
     */
    public static int execAndWaitFor(OutputStream out, String... command) throws IOException {
        return execAndWaitFor(new ProcessBuilder(command), out);
    }

    public static int execAndWaitFor(ProcessBuilder processBuilder, OutputStream out) throws IOException {
        int retVal = 0;
        processBuilder.redirectErrorStream(true);

        Process p = null;
            p = processBuilder.start();

        ReaderThread readerThread = new ReaderThread(p, out);
        readerThread.start();

        try {
            retVal = p.waitFor();
            readerThread.stop = true;
            // let the process finish output
            // avoid 100% CPU load
            try {
                while (!readerThread.done) {
                    Thread.sleep(10);
                }
            } catch (InterruptedException e) {
                // ignore
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return retVal;
    }

    private static final class ReaderThread extends Thread {
        private final Process process;

        private volatile boolean stop;

        private volatile boolean done;

        private final OutputStream out;

        ReaderThread(Process process, OutputStream out) {
            super();
            this.process = process;
            this.out = out;
        }

        /**
         * System dependent max pipe size. Used to assign buffer size for writing.
         */
        private static final int INITIAL_PIPE_SIZE = 4096;

        @Override
        public void run() {
            byte[] buffer = new byte[INITIAL_PIPE_SIZE];
            try (BufferedInputStream stream = new BufferedInputStream(process.getInputStream())) {
                // loop is running until end of stream is reached.
                while (!stop || stream.available() > 0) {
                    if (stream.available() > 0) {
                        // will block current thread
                        int read = stream.read(buffer);
                        if (read == -1) { // paranoia: available > 0 here
                            break;
                        }
                        out.write(buffer, 0, read);
                    } else {
                        // avoid 100% CPU load
                        synchronized (this) {
                            try {
                                if(!stop) {
                                    wait(10);
                                }
                            } catch (InterruptedException e) {
                                // ignore
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                done = true;
            }
        }
    }
}
