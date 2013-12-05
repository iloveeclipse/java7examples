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

public class StringsInSwitch {

    public static void main(String[] args) {
        String os = System.getProperty("os.name");
        // NB: if the string in switch is null, we will see NPE on the next line!
        switch (os) {
        case "Linux":
            System.out.println("Cool!");
            break;
        case "Windows":
            System.out.println("Not so cool!");
            break;
        default:
            System.out.println("Obst?");
            break;
        }
    }
}
