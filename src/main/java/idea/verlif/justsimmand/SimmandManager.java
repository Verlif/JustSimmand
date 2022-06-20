package idea.verlif.justsimmand;

import idea.verlif.parser.ParamParserService;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 指令管理器，用于执行指令行或指令参数。
 *
 * @author Verlif
 */
public class SimmandManager {

    private static final String SPLIT_PARAM = " ";
    private static final String TIP_UNKNOWN_COMMAND = "Unknown command";
    private static final String TIP_NO_SUCH_COMMAND = "No such command";

    private static final ParamParserService PPS = new ParamParserService();

    /**
     * 指令表。<br/>
     * key - 指令名；<br/>
     * value - 指令参数序号
     */
    private final Map<String, Simmand> simmandMap;

    /**
     * 默认指令配置
     */
    private SimmandConfig defaultConfig;

    public SimmandManager() {
        simmandMap = new HashMap<>();
        defaultConfig = new SimmandConfig();
    }

    /**
     * 添加指令对象。使用默认的参数解析器与配置。
     *
     * @param o 指令对象
     */
    public void add(Object o) {
        add(o, PPS, defaultConfig);
    }

    /**
     * 添加指令对象。使用默认的参数解析器。
     *
     * @param o      指令对象
     * @param config 指定加载的配置
     */
    public void add(Object o, SimmandConfig config) {
        add(o, PPS, config);
    }

    /**
     * 添加指令对象。使用默认的配置。
     *
     * @param o   指令对象
     * @param pps 参数解析器
     */
    public void add(Object o, ParamParserService pps) {
        add(o, pps, defaultConfig);
    }

    /**
     * 添加指令对象。
     *
     * @param o      指令对象
     * @param config 指定加载的方法
     * @param pps    参数解析器
     */
    public void add(Object o, ParamParserService pps, SimmandConfig config) {
        for (Method method : o.getClass().getMethods()) {
            if ((method.getModifiers() & 1) > 0) {
                Simmand simmand = new Simmand(pps, config);
                if (simmand.load(o, method)) {
                    for (String key : simmand.getKey()) {
                        if (config.isAddWithReplace() || !simmandMap.containsKey(key)) {
                            simmandMap.put(key, simmand);
                        }
                    }
                }
            }
        }
    }

    /**
     * 通过指令行运行指令。<br/>
     * 请注意编译时参数名会被屏蔽。
     *
     * @param line 指令行
     * @return 指令结果。执行成功则返回执行方法的返回对象，否则返回错误信息（String）。
     */
    public Object run(String line) throws InvocationTargetException, IllegalAccessException {
        String[] ss = line.trim().split(SPLIT_PARAM, 2);
        // 判定参数
        switch (ss.length) {
            case 0:
                return TIP_UNKNOWN_COMMAND;
            case 1: {
                Simmand simmand = simmandMap.get(ss[0].trim());
                if (simmand == null) {
                    return TIP_NO_SUCH_COMMAND;
                }
                return simmand.run(null);
            }
            default: {
                Simmand simmand = simmandMap.get(ss[0].trim());
                if (simmand == null) {
                    return TIP_NO_SUCH_COMMAND;
                }
                return simmand.run(ss[1].trim());
            }
        }
    }

    /**
     * 获取所有可用的指令Key
     *
     * @return 指令Key集
     */
    public Set<String> allKey() {
        return simmandMap.keySet();
    }

    /**
     * 获取默认的指令参数解析器
     *
     * @return 参数解析器，全局共享
     */
    public ParamParserService getParamParserService() {
        return PPS;
    }

    /**
     * 设置默认的指令参数解析器，用于 {@link #add(Object)}、{@link #add(Object, SimmandConfig)} 方法中。
     *
     * @param defaultConfig 参数解析器
     */
    public void setDefaultConfig(SimmandConfig defaultConfig) {
        this.defaultConfig = defaultConfig;
    }

    /**
     * 获取默认配置。
     *
     * @return 默认的指令加载配置。
     */
    public SimmandConfig getDefaultConfig() {
        return defaultConfig;
    }
}
