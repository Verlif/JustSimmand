package idea.verlif.justsimmand;

import idea.verlif.parser.cmdline.ArgParser;

/**
 * 参数解析器工厂类
 */
public interface ArgParserFactory {

    /**
     * 创建一个参数解析器
     *
     * @param group      指令组名称
     * @param methodName 方法名称
     */
    ArgParser create(String group, String methodName);
}
