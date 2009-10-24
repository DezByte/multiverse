package org.multiverse.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that can be added to a method and constructors to make them atomic. An atomic
 * method supports the following properties:
 * <ol>
 * <li>A: failure Atomicity. All changes get in, or no changes get in</li>
 * <li>C: Consistent. A AtomicMethod can expect to enter memory in a valid state, and is
 * expected when it leaves the space is consistent again.</li>
 * </li>I: Isolated. A transaction will not observe changes made others transactions
 * running in parallel. But it is going to see the changes made by transaction that
 * completed earlier. If a transaction doesn't see this, the system could start to suffer
 * from the lost update problem</li>
 * </ol>
 * <p/>
 * Unfortunately @Inherited doesn't work for methods, only for classes. So if you have an
 * interface containing some methods that need to be atomic, it needs to be added to the
 * implementation and not to the interface. In the future Multiverse may try to this
 * inference itself.
 *
 * @author Peter Veentjer
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface AtomicMethod {

    boolean readonly() default false;

    String familyName() default "";

    int retryCount() default Integer.MAX_VALUE;
}