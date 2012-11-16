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

import static java.nio.file.StandardWatchEventKinds.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;

public class WatchServiceAPI {
    public static void main(String[] args) throws IOException {
        Path path = Paths.get(System.getProperty("java.io.tmpdir"));
        WatchService watchService = path.getFileSystem().newWatchService();
        WatchKey watchKey = path.register(watchService, OVERFLOW, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        Path tempFile = Files.createTempFile(path, "", ".tmp");
        Path path2 = Paths.get(tempFile + "_moved");
        Files.move(tempFile, path2);
        Files.write(path2, "Hello".getBytes());
        Files.deleteIfExists(path2);
        printEvents(watchKey);
        watchKey.cancel();
    }

    private static void printEvents(WatchKey watchKey) {
        List<WatchEvent<?>> events = watchKey.pollEvents();
        for (WatchEvent<?> event : events) {
            System.out.println("-> " + event.count() + " event(s):");
            Object context = event.context();
            if(context instanceof Path){
                Path path = (Path) context;
                System.out.print("\tPath: " + path);
            }
            System.out.println("\tKind: " + event.kind());
        }
    }
}
