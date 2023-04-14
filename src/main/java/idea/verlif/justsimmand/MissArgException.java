package idea.verlif.justsimmand;

/**
 * 缺失参数错误。当指令中有必须输入且没有默认值的参数，但此参数没有进行输入时抛出此异常。
 */
public class MissArgException extends RuntimeException {

    public MissArgException(String key) {
        super("Missing arg - " + key);
    }
}
