package idea.verlif.justsimmand.anno;

import java.lang.annotation.*;

/**
 * 指令参数标识，用于额外描述参数信息。
 *
 * @author Verlif
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SmdParam {

    /**
     * 参数名。<br/>
     * 例如在方法 {@code sayHi(@SimmParam(key = "n") String name)} 中，参数 {@code name} 会被 {@code n} 所接收。
     * 例如指令行 {@code sayHi --n Verlif}
     *
     * @return 用于在指令行识别的参数。
     */
    String value() default "";

    /**
     * 参数描述，用于说明此参数的作用与数值填写规则。
     */
    String description() default "";

    /**
     * 是否必填参数，默认true。
     *
     * @return {@code true} - 必要参数，当参数未在指令行中指明时，则从默认值取值。若默认值未添加则抛出异常。<br>
     * {@code false} - 非必要参数，不对此参数进行空值校验。
     */
    boolean force() default true;

    /**
     * 默认值。
     *
     * @return 返回当指令行中为知名此参数有效值时，基于方法的默认值。
     */
    String defaultVal() default "";
}
