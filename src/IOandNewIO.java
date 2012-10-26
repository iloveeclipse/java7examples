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

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

public class IOandNewIO {
    public static void main(String[] args) throws IOException {

        Path path = Files.createTempFile(null, ".txt");

        Files.write(path, "Hello\n".getBytes());

        Path link = path.getParent().resolve("link");

        Files.deleteIfExists(link);

        Path symlink = Files.createSymbolicLink(link, path);

        out.println("Real file: " + path);
        out.println("Link path: " + link);
        out.println("Link file: " + symlink);

        out.println("Is link? " + Files.isSymbolicLink(symlink));
        out.println("Link target: " + Files.readSymbolicLink(symlink));
        out.println("Content: " + Files.readAllLines(path, Charset.defaultCharset()));
        out.println("Content type: " + Files.probeContentType(path));
    }
}
