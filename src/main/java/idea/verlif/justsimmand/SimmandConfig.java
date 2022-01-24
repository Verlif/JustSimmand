package idea.verlif.justsimmand;

/**
 * 指令配置
 *
 * @author Verlif
 */
public class SimmandConfig {

    /**
     * 加载模式
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
         * 积极模式，除了显式不加载的方法，都加载。
         */
        POSITIVE,

        /**
         * 消极模式，只加载显式确定的
         */
        NEGATIVE
    }

}
