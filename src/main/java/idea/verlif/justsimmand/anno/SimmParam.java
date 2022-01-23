package idea.verlif.justsimmand.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Verlif
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface SimmParam {

    /**
     * 参数名。
     */
    String key() default "";

    /**
     * 是否必填参数
     */
    boolean force() default true;

    /**
     * 默认值。
     */
    String defaultVal() default "";
}
