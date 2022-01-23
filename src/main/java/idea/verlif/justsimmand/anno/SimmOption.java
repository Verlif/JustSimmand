package idea.verlif.justsimmand.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Verlif
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SimmOption {

    /**
     * 指令别名
     */
    String[] value() default {};

    /**
     * 是否是指令
     */
    boolean isCommand() default true;
}
