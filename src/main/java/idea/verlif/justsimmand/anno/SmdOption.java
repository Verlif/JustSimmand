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
public @interface SmdOption {

    /**
     * 指令别名。
     *
     * @return 指令的多个别名，这些别名都会被识别为此方法。
     */
    String[] value() default {};

    /**
     * 指令描述，用于说明此指令方法的作用与效果。
     */
    String description() default "";

    /**
     * 是否忽略此方法作为指令加载，默认true。
     *
     * @return {@code true} - 忽略此方法；{@code false} - 不忽略此方法。
     */
    boolean ignored() default false;

    /**
     * 方法调用举例
     */
    String example() default "";
}
