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

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.util.List;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

public class FileSystemProviderAPI {
    public static void main(String[] args) throws Exception {
        List<FileSystemProvider> providers = FileSystemProvider.installedProviders();
        for (FileSystemProvider fsProvider : providers) {
            System.out.println("sheme: '" + fsProvider.getScheme() + "', provider: " + fsProvider.getClass());
        }

        Path tmpFile = Files.createTempFile("", ".tmp");
        Files.write(tmpFile, "Hello".getBytes());
        Path jarFile = Files.createTempFile("", ".jar");

        try(JarOutputStream outputStream = new JarOutputStream(Files.newOutputStream(jarFile))){
            outputStream.putNextEntry(new ZipEntry(tmpFile.getFileName().toString()));
            Files.copy(tmpFile, outputStream);
        }

        try(FileSystem fileSystem = createVirtualFS(jarFile)){
            Iterable<Path> directories = fileSystem.getRootDirectories();
            for (Path dir : directories) {
                System.out.println("Reading dir: " + dir.toUri());
                DirectoryStream<Path> stream = Files.newDirectoryStream(dir);
                for (Path file : stream) {
                    System.out.println("Reading file: " + file.toUri());
                    System.out.println("\tfrom " + file.getFileSystem().provider().getClass());
                    System.out.print("Content: ");
                    Files.copy(file, System.out);
                }
            }
        }

    }

    static FileSystem createVirtualFS(Path jarFile) throws IOException {
        return FileSystems.newFileSystem(jarFile, FileSystemProviderAPI.class.getClassLoader());
    }
}
