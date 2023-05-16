package idea.verlif.justsimmand.pretreatment;

/**
 * 指令预处理接口
 */
public interface SmdLinePretreatment {

    /**
     * 指令行处理
     *
     * @param smdLine 输入或处理过后的指令
     * @return 此处理器处理后的指令。返回 null 则终止执行。
     */
    String handle(String smdLine);
}
