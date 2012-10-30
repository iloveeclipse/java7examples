/*******************************************************************************
 * Copyright (c) 2012 Andrey Loskutov. All rights reserved.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *     Andrey Loskutov - initial API and implementation
 *******************************************************************************/

import static java.lang.System.*;
import static java.nio.file.attribute.PosixFilePermission.*;
import static java.nio.file.attribute.PosixFilePermissions.*;
import static java.util.Arrays.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

public class IOandNewIO {
    public static void main(String[] args) throws IOException {

        Path path = Files.createTempFile(null, ".txt");

        basicStuff(path);

        fileAttributes(path);

        directoryStream(path.getParent());
    }

    static void basicStuff(Path path) throws IOException {
        Files.write(path, "Hello\n".getBytes());

        Path link = path.getParent().resolve("link");
        Files.deleteIfExists(link);

        Path symlink = Files.createSymbolicLink(link, path);

        out.println("Real file: " + path);
        out.println("Link file: " + symlink);

        out.println("Is link? " + Files.isSymbolicLink(symlink));
        out.println("Link target: " + Files.readSymbolicLink(symlink));
        out.println("Content: " + Files.readAllLines(path, Charset.defaultCharset()));
        out.println("Content type: " + Files.probeContentType(path));
    }

    static void fileAttributes(Path path) throws IOException {
        Path onlyForMe = path.getParent().resolve("onlyForMe");
        Files.deleteIfExists(onlyForMe);

        // classic command line
        Set<PosixFilePermission> permissions = fromString("rwxrwxrwx");

        // object oriented way
        permissions = sort(asList(PosixFilePermission.values()));

        Files.createFile(onlyForMe, PosixFilePermissions.asFileAttribute(permissions));

        Set<PosixFilePermission> freeAccess = sort(Files.getPosixFilePermissions(onlyForMe));
        System.out.println(freeAccess);

        freeAccess.removeAll(asList(GROUP_WRITE, OTHERS_READ, OTHERS_EXECUTE));

        PosixFileAttributeView attributeView = Files.getFileAttributeView(onlyForMe, PosixFileAttributeView.class);
        attributeView.setPermissions(freeAccess);

        Files.setOwner(path, FileSystems.getDefault().getUserPrincipalLookupService()
                .lookupPrincipalByName("aloskuto"));

        PosixFileAttributes fileAttributes = attributeView.readAttributes();
        System.out.println(sort(fileAttributes.permissions()));
        System.out.println("Current owner: " + attributeView.getOwner());

        BasicFileAttributeView basic = Files.getFileAttributeView(onlyForMe, BasicFileAttributeView.class);
        // prints device id and inode on Linux
        System.out.println(basic.readAttributes().fileKey());
    }

    static void directoryStream(Path dir) throws IOException {
        // simple name based filter
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.{tmp,test}")) {
            for (Path path : stream) {
                System.out.println(path);
            }
        }

        // filter based on file matchers and file attributes
        FileSystem fs = FileSystems.getDefault();
        final PathMatcher regexMatcher = fs.getPathMatcher("regex:.*7\\.\\d+.*");
        final PathMatcher globMatcher = fs.getPathMatcher("glob:/tmp/*.*");

        Filter<? super Path> filter = new Filter<Path>() {
            @Override
            public boolean accept(Path path) throws IOException {
                return globMatcher.matches(path) &&
                        regexMatcher.matches(path) &&
                        !Files.isSymbolicLink(path);
            }
        };
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, filter)) {
            for (Path path : stream) {
                System.out.println(path);
            }
        }
    }

    private static <V> Set<V> sort(Collection<V> set){
        return new TreeSet<>(set);
    }
}
