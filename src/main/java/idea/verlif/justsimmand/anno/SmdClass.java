package idea.verlif.justsimmand.anno;

import idea.verlif.justsimmand.SmdConfig;

import java.lang.annotation.*;

/**
 * 指令对象类，用来标记指令对象类，并赋予文字描述
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SmdClass {

    /**
     * 指令组名称。当 {@link SmdConfig#isClassNameGroup()} 未 true 时，指令会加入指令类前缀。
     * 前缀值由本属性提供。
     */
    String value() default "";

    /**
     * 指令描述，用于说明此对象类的用途及作用。
     */
    String description() default "";
}
