package idea.verlif.justsimmand.anno;

import java.lang.annotation.*;

/**
 * 指令方法参数。
 *
 * @author Verlif
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SimmOption {

    /**
     * 指令别名。
     *
     * @return 指令的多个别名，这些别名都会被识别为此方法。
     */
    String[] value() default {};

    /**
     * 是否是指令，默认true。
     *
     * @return {@code true} - 可被执行的指令方法；{@code false} - 此方法不会被指令所识别。
     */
    boolean isCommand() default true;
}
