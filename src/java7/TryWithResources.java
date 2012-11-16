/*******************************************************************************
 * Copyright (c) 2012 Andrey Loskutov. All rights reserved.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *     Andrey Loskutov - initial API and implementation
 *******************************************************************************/
package java7;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

public class TryWithResources {

    static final class BadWriter extends FileWriter {
        private BadWriter(File file) throws IOException {
            super(file);
        }

        @Override
        public void write(String s) throws IOException {
            throw new IOException("Failed to write!");
        }

        @Override
        public void close() throws IOException {
            throw new IOException("Failed to close!");
        }
    }

    public static void main(String[] args) throws IOException {
        File file = createTmpFile();

        // 15 lines to write one line into file
        oldWayWrite(file);

        // 19 lines to read file
        oldWayRead(file);

        // 5 lines to write one line into file
        java7Write(file);

        // 8 lines to read file
        java7Read(file);

        // 1 line to read file (use API!)
        System.out.println(Files.readAllLines(file.toPath(), Charset.defaultCharset()));

        // 1 line to dump file to stderr (use API!)
        Files.copy(file.toPath(), System.err);
        System.err.println();

        // example how multiple resources can be used
        multipleClose(file, createTmpFile());
        multipleClose2(file, createTmpFile());

        suppressedExceptions(file);
    }


    static void oldWayWrite(File file) {
        FileWriter fw = null;
        try {
            fw = new FileWriter(file);
            fw.write("Hello Java 6!");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(fw != null){
                try {
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static void oldWayRead(File file) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String line;
            while((line = br.readLine()) != null){
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(br != null){
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static void java7Write(File file) {
        try (FileWriter fw =  new FileWriter(file)) {
            fw.write("Hello Java 7!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void java7Read(File file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))){
            String line;
            while((line = br.readLine()) != null){
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void multipleClose(File from, File to) {
        try (
            BufferedReader br = new BufferedReader(new FileReader(from)){
                @Override
                public void close() throws IOException {
                    System.out.println("Closing 'from' stream!");
                    super.close();
                }
            };
            FileWriter fw = new FileWriter(to){
                @Override
                public void close() throws IOException {
                    System.out.println("Closing 'to' stream!");
                    super.close();
                }
            };) {
            String line;
            while ((line = br.readLine()) != null) {
                fw.write(line);
                fw.write(System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        java7Read(to);
    }

    static void multipleClose2(File f1, File f2) {
        System.out.println();
        try (
            FileReader from = new FileReader(f1);
            FileWriter to = new FileWriter(f2)) {
            int data;
            while ((data = from.read()) != -1) {
                to.write(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        java7Read(f2);
    }

    static void suppressedExceptions(File file) {
        try (FileWriter fw = new BadWriter(file)) {
            fw.write("O-o!");
        } catch (IOException e) {
            System.err.println("Thrown: " + e.getMessage());
            Throwable[] suppressed = e.getSuppressed();
            for (Throwable t : suppressed) {
                System.err.println("Suppressed: " + t.getMessage());
            }
        }
    }

    private static File createTmpFile() {
        try {
            File file = File.createTempFile("TryWithResources", "tmp");
            file.deleteOnExit();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
