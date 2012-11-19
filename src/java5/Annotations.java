/*******************************************************************************
 * Copyright (c) 2012 Andrey Loskutov. All rights reserved.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *     Andrey Loskutov - initial API and implementation
 *******************************************************************************/
package java5;

import static java.lang.annotation.ElementType.*;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

/**
 * <ul>
 * <li>
 * Annotations can't be extended!</li>
 * <li>
 * Each method declaration in annotation type defines an element of the annotation type.</li>
 * <li>
 * Method declarations must not have any parameters or a throws clause.</li>
 * <li>
 * Return types are restricted to primitive types, String, Class, enumerations,
 * annotations(!), and 1-dimensional arrays of the preceding types.</li>
 * <li>
 * Methods can have default values.</li>
 * <li>
 * Method "value" can be assigned directly if annotation using single element</li>
 * <li>
 * One cannot annotate same element twice with same annotation type.</li>
 * </ul>
 */
public class Annotations {

    @Runtime (count = 13, value = "Günter", type = Void.class, target = {METHOD, PACKAGE})
    @ClassFileOnly ("Franz")
    @SourceOnly
    public static void main(@ClassFileOnly("Fritz") String[] args) {

        @Runtime(value = "olala", co = @ClassFileOnly("Kunz"))
        String someString = "";
        System.err.print(someString);
        showAnnotations();
    }

    private static void showAnnotations() {
        try {
            Method method = Annotations.class.getMethod("main", String[].class);
            Annotation[] annotations = method.getDeclaredAnnotations();
            for (Annotation ann : annotations) {
                System.out.println("Found: " + ann.getClass() + ", " + ann.annotationType());
                if(ann instanceof Runtime){
                    Runtime runtime = (Runtime) ann;
                    System.out.println(runtime.count());
                    System.out.println(runtime.value());
                    System.out.println(Arrays.toString(runtime.target()));
                    System.out.println(runtime.type());
                }
            }
        } catch (NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        }
    }

    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target(value={CONSTRUCTOR, FIELD, LOCAL_VARIABLE, METHOD, PACKAGE, PARAMETER, TYPE})
    public @interface Runtime {
        int count() default 42;
        String value() default "[unnamed]";
        Class<?> type() default Collection.class ;
        ElementType [] target () default { METHOD };
        ClassFileOnly co() default @ClassFileOnly;
    }

    @Retention(RetentionPolicy.CLASS)
    public @interface ClassFileOnly {
        int count() default 42;
        String value() default "[unnamed]";
        Class<?> type() default Collection.class ;
        ElementType [] target () default { METHOD };
    }

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    public @interface SourceOnly {
        int count() default 42;
        String value() default "[unnamed]";
        Class<?> type() default Collection.class ;
        ElementType [] target () default { METHOD };
    }

}
