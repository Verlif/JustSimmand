package idea.verlif.justsimmand;

import idea.verlif.justsimmand.anno.SmdOption;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * 指令加载配置
 *
 * @author Verlif
 */
public class LoadConfig {

    /**
     * 加载模式，默认只加载被 {@link SmdOption} 标记且 {@code ignored = true} 的方法。
     *
     * @see LoadMode
     */
    private int loadMode = 0;

    /**
     * 允许的方法类型
     */
    private int allowedModifier = Modifier.PUBLIC;

    /**
     * 在添加指令对象时，已存在的相同指令是否被替换。true - 被替换；false - 不替换，跳过此指令加载。
     */
    private boolean addWithReplace = true;

    /**
     * 忽略的方法名称
     */
    private List<String> ignoredList;

    /**
     * 允许的方法名称
     */
    private List<String> allowedList;

    public int getLoadMode() {
        return loadMode;
    }

    public void setLoadMode(int loadMode) {
        this.loadMode = loadMode;
    }

    /**
     * 添加加载模式
     *
     * @param loadMode 加载模式
     */
    public void addLoadMode(LoadMode loadMode) {
        this.loadMode |= this.loadMode | loadMode.code;
    }

    /**
     * @see #loadMode
     */
    public LoadConfig loadMode(LoadMode... loadModes) {
        for (LoadMode mode : loadModes) {
            addLoadMode(mode);
        }
        return this;
    }

    public boolean isAddWithReplace() {
        return addWithReplace;
    }

    /**
     * 在添加指令对象时，已存在的相同指令是否被替换。true - 被替换；false - 不替换，跳过此指令加载。
     */
    public void setAddWithReplace(boolean addWithReplace) {
        this.addWithReplace = addWithReplace;
    }

    /**
     * @see #addWithReplace
     */
    public LoadConfig addWithReplace(boolean addWithReplace) {
        setAddWithReplace(addWithReplace);
        return this;
    }

    /**
     * 是否允许的类型
     *
     * @param modifier 类型值
     * @return 是否允许该类型值
     */
    public boolean isAllowedModifier(int modifier) {
        return (allowedModifier & modifier) > 0;
    }

    public void setAllowedModifier(int allowedModifier) {
        this.allowedModifier = allowedModifier;
    }

    /**
     * @see #allowedModifier
     */
    public LoadConfig allowModifier(int allowedModifier) {
        setAllowedModifier(allowedModifier);
        return this;
    }

    /**
     * 添加需要忽略的方法。
     *
     * @param methodName 需要忽略的方法名
     */
    public void addIgnoredMethod(String methodName) {
        if (ignoredList == null) {
            ignoredList = new ArrayList<>();
        }
        ignoredList.add(methodName);
    }

    /**
     * @see #addIgnoredMethod(String)
     */
    public LoadConfig ignoredMethod(String... methodNames) {
        for (String methodName : methodNames) {
            addIgnoredMethod(methodName);
        }
        return this;
    }

    /**
     * 添加需要加载的方法。
     *
     * @param methodName 需要加载的方法名
     */
    public void addAllowedMethod(String methodName) {
        if (allowedList == null) {
            allowedList = new ArrayList<>();
        }
        allowedList.add(methodName);
    }

    /**
     * @see #addAllowedMethod(String)
     */
    public LoadConfig allowedMethod(String... methodNames) {
        for (String methodName : methodNames) {
            addAllowedMethod(methodName);
        }
        return this;
    }

    /**
     * 检测方法是否被允许加载。校验规则如下：<br>
     * <li> 当 ignoredList 与 allowedList 都有值时，优先判定需要忽略的，在判定是否存在许可列表。 </li>
     * <li> 当仅 ignoredList 有值时，不在忽略列表中的方法可以被加载。 </li>
     * <li> 当仅 allowedList 有值时，不在许可列表中的方法不允许加载。 </li>
     * <li> 当 ignoredList 与 allowedList 都没有值时，完全许可。 </li>
     *
     * @param methodName 方法名
     * @return 是否允许该方法加载为指令
     */
    public boolean isAllowedMethod(String methodName) {
        if (ignoredList == null && allowedList == null) {
            return true;
        }
        if (ignoredList != null && ignoredList.contains(methodName)) {
            return false;
        }
        if (allowedList != null) {
            return allowedList.contains(methodName);
        } else {
            return true;
        }
    }

    /**
     * 加载模式。表示本次加载指令的方法。
     *
     * @see #POSITIVE
     * @see #EXTEND
     */
    public enum LoadMode {
        /**
         * 积极模式，除了被 {@link SmdOption#ignored()} 指定为 {@code false} 的方法，其他的所有方法都加载。
         */
        POSITIVE(1),

        /**
         * 拓展模式，会加载父类方法。
         */
        EXTEND(2),
        ;

        private final int code;

        LoadMode(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        /**
         * 此模式是否能匹配到模式值
         *
         * @param code 模式值
         * @return 是否匹配
         */
        public boolean matches(int code) {
            return (this.code & code) == this.code;
        }
    }

}
