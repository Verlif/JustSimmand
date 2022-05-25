package idea.verlif.justsimmand;

import idea.verlif.justsimmand.anno.SimmOption;

/**
 * 指令配置
 *
 * @author Verlif
 */
public class SimmandConfig {

    /**
     * 加载模式，默认 {@link LoadMode#POSITIVE} 积极模式。
     */
    private LoadMode loadMode = LoadMode.POSITIVE;

    public LoadMode getLoadMode() {
        return loadMode;
    }

    public void setLoadMode(LoadMode loadMode) {
        this.loadMode = loadMode;
    }

    /**
     * 加载模式
     */
    public enum LoadMode {
        /**
         * 积极模式，除了被 {@link SimmOption#isCommand()} 指定为 {@code false} 的方法，其他的所有方法都加载。
         */
        POSITIVE,

        /**
         * 消极模式，只加载被 {@link SimmOption#isCommand()} 指定为 {@code true} 的方法。
         */
        NEGATIVE
    }

}
