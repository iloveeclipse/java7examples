/*******************************************************************************
 * Copyright (c) 2012 Andrey Loskutov. All rights reserved.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *     Andrey Loskutov - initial API and implementation
 *******************************************************************************/

import java.net.URI;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.spi.FileSystemProvider;
import java.util.List;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

public class FileSystemProviderAPI {
    public static void main(String[] args) throws Exception {
        List<FileSystemProvider> providers = FileSystemProvider.installedProviders();
        for (FileSystemProvider fsProvider : providers) {
            System.out.println(fsProvider.getScheme() + ": " + fsProvider.getClass());
        }
        Path tmpFile = Files.createTempFile("", ".tmp");
        Files.write(tmpFile, "Hello".getBytes());
        Path jarFile = Files.createTempFile("", ".jar");
        JarOutputStream outputStream = new JarOutputStream(Files.newOutputStream(jarFile));
        outputStream.putNextEntry(new ZipEntry("Hello"));
        Files.copy(tmpFile, outputStream);
        outputStream.close();
        URI uri = new URI("file:"+ jarFile.toString());
        System.out.println(uri);
        Path path = Paths.get(uri);
        FileSystem fileSystem = FileSystems.newFileSystem(path, FileSystemProviderAPI.class.getClassLoader());
        System.out.println(fileSystem);
        Iterable<Path> directories = fileSystem.getRootDirectories();
        for (Path path2 : directories) {
            System.out.println(path2.toUri());
            DirectoryStream<Path> stream = Files.newDirectoryStream(path2);
            for (Path path3 : stream) {
                System.out.println(path3);
            }
        }

    }
}
