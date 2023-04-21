package idea.verlif.simmand.domain;

import idea.verlif.justsimmand.anno.SmdClass;
import idea.verlif.justsimmand.anno.SmdOption;
import idea.verlif.justsimmand.anno.SmdParam;

@SmdClass(value = "math", description = "简单的测试指令")
public class Math {

    @SmdOption(value = {"plus", "+"}, description = "两数之和")
    public int plus(
            @SmdParam(value = "a", force = false, description = "相加的第一个数字，可以为空") int a,
            @SmdParam(value = "b", description = "相加的第二个数字，默认值是3", defaultVal = "3") int b) {
        return a + b;
    }

    @SmdOption(value = {"square", "^"}, description = "做平方")
    public int square(int a) {
        return a * a;
    }

}
