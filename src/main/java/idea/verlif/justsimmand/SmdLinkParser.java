package idea.verlif.justsimmand;

/**
 * 指令链接解析器，用来分离链接调用区域。
 */
public interface SmdLinkParser {

    /**
     * 解析指令链接字符串，将其转换成多条指令。
     *
     * @param smdLink 指令链接字符串
     * @return 转换后的多条指令
     */
    String[] parse(String smdLink);
}
