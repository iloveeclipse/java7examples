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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

@SuppressWarnings("all")
public class RethrowingExceptionsImproved {

    void oldWayRethrow(boolean notFound) throws IOException {
        try {
            if (notFound) {
                throw new FileNotFoundException();
            } else {
                throw new MalformedURLException();
            }
        } catch (IOException e) {
            throw e;
        }
    }

    void java7Rethrow(boolean notFound) throws FileNotFoundException, MalformedURLException {
        try {
            if (notFound) {
                throw new FileNotFoundException();
            } else {
                throw new MalformedURLException();
            }
        } catch (IOException e) {
            throw e; // 1.6 compiler error: "unhandled exception type IOException"
        }
    }
}
