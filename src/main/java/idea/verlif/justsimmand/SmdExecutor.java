package idea.verlif.justsimmand;

import idea.verlif.justsimmand.anno.SmdClass;
import idea.verlif.justsimmand.anno.SmdOption;
import idea.verlif.justsimmand.anno.SmdParam;
import idea.verlif.justsimmand.info.SmdGroupInfo;
import idea.verlif.justsimmand.info.SmdMethodInfo;
import idea.verlif.parser.ParamParserService;
import idea.verlif.parser.cmdline.ArgParser;
import idea.verlif.parser.cmdline.ArgValues;
import idea.verlif.reflection.util.MethodUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 指令执行器，用于执行指令行或指令参数。
 *
 * @author Verlif
 */
public class SmdExecutor extends ParamParserService {

    private static final String SPLIT_PARAM = " ";
    private static final String KEY_PARAM_PREFIX = "--";

    /**
     * 指令表。<br/>
     * key - 分组名称<br/>
     * value - 组内指令表。
     */
    private final Map<String, Map<String, SmdItem>> smdItemMap;

    /**
     * 指令信息表。
     */
    private final Map<String, SmdGroupInfo> smdInfoMap;

    /**
     * 前缀替换表
     */
    private final Map<String, String> prefixMap;

    /**
     * 默认指令配置
     */
    private LoadConfig defaultConfig;

    /**
     * 指令配置
     */
    private SmdConfig smdConfig;

    /**
     * 指令对象工厂类
     */
    private SmdFactory smdFactory;

    /**
     * 指令链接解析器
     */
    private SmdLinkParser smdLinkParser;

    /**
     * 参数解析器工厂类
     */
    private ArgParserFactory argParserFactory;

    public SmdExecutor() {
        this(new SmdConfig());
    }

    public SmdExecutor(SmdConfig smdConfig) {
        this.smdItemMap = new HashMap<>();
        this.smdInfoMap = new HashMap<>();
        this.defaultConfig = new LoadConfig();
        this.prefixMap = new HashMap<>();

        this.smdConfig = smdConfig;
        this.smdFactory = new SpecialSmdFactory();
        this.smdLinkParser = new BlockSmdLinkParser('(', ')');
        this.argParserFactory = new SpecialArgParser();

        add(new HelpSmd());
        addPrefixReplace("help help", "help");
    }

    public void setSmdConfig(SmdConfig smdConfig) {
        this.smdConfig = smdConfig;
    }

    public void setSmdFactory(SmdFactory smdFactory) {
        this.smdFactory = smdFactory;
    }

    public void setSmdLinkParser(SmdLinkParser smdLinkParser) {
        this.smdLinkParser = smdLinkParser;
    }

    public void setArgParserFactory(ArgParserFactory argParserFactory) {
        this.argParserFactory = argParserFactory;
    }

    /**
     * 手动添加指令信息
     *
     * @param smdGroupInfo 指令组信息
     */
    public void addSmdGroupInfo(SmdGroupInfo smdGroupInfo) {
        if (smdGroupInfo.getKey() != null) {
            smdInfoMap.put(smdGroupInfo.getKey(), smdGroupInfo);
        }
    }

    /**
     * 添加指令对象。使用默认的参数解析器与配置。
     *
     * @param o 指令对象
     */
    public void add(Object o) {
        add(o, defaultConfig);
    }

    /**
     * 添加指令对象。
     *
     * @param o      指令对象
     * @param config 指定加载的方法
     */
    public void add(Object o, LoadConfig config) {
        Class<?> cla = o.getClass();
        // 新增指令信息
        SmdGroupInfo smdGroupInfo = new SmdGroupInfo();
        SmdClass smdClass = cla.getAnnotation(SmdClass.class);
        // 获取指令组
        String group = null; // 指令组名称，用于构建指令信息
        String groupName = null; // 指令分组名，用于处理指令的组区分
        if (smdConfig.isClassNameGroup()) {
            if (smdClass == null || smdClass.value().length() == 0) {
                group = cla.getSimpleName();
            } else {
                group = smdClass.value();
            }
            groupName = group;
        }
        // 构建指令信息
        smdGroupInfo.setKey(group);
        if (smdClass != null) {
            smdGroupInfo.setDescription(smdClass.description());
        }
        Map<String, SmdItem> itemMap = smdItemMap.computeIfAbsent(groupName, k -> new HashMap<>());
        List<Method> allMethods = MethodUtil.getAllMethods(cla);
        List<SmdMethodInfo> smdMethodInfoList = new ArrayList<>();
        for (Method method : allMethods) {
            // 过滤方法
            if (config.isAllowedModifier(method.getModifiers()) && config.isAllowedMethod(method.getName())) {
                // 实例化指令
                SmdItem smdItem = smdFactory.create(config);
                if (smdItem.load(o, method)) {
                    smdMethodInfoList.add(smdItem.getMethodInfo());
                    for (String key : smdItem.getKey()) {
                        if (config.isAddWithReplace() || !itemMap.containsKey(key)) {
                            itemMap.put(key, smdItem);
                        }
                    }
                }
            }
        }
        // 添加指令信息到信息表
        if (!smdMethodInfoList.isEmpty()) {
            smdGroupInfo.addSmdMethodInfo(smdMethodInfoList);
            smdInfoMap.put(group, smdGroupInfo);
        }
    }

    /**
     * 通过指令行运行指令。<br/>
     * 请注意编译时参数名会被屏蔽。
     *
     * @param smdLine 指令行
     * @return 指令结果。执行成功则返回执行方法的返回对象，否则抛出异常。
     */
    public synchronized Object execute(String smdLine) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        String[] lines = toLines(smdLine);
        if (lines.length == 1) {
            return run(lines[0]);
        }
        SmdConfig config = new SmdConfig();
        config.setClassNameGroup(false);
        LoadConfig loadConfig = new LoadConfig()
                .loadMode(LoadConfig.LoadMode.POSITIVE);
        SmdExecutor tmp = this;
        Object result = null;
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            result = tmp.run(line);
            // 返回值中断
            if (result == null) {
                if (i == lines.length - 1) { // 运行完毕
                    return result;
                } else {
                    throw new NullPointerException(String.format("line - %s can not find result!", line));
                }
            }
            tmp = new SmdExecutor(config);
            tmp.add(result, loadConfig);
        }
        return result;
    }

    /**
     * 通过指令行运行指令。<br/>
     * 请注意编译时参数名会被屏蔽。
     *
     * @param line 指令行
     * @return 指令结果。执行成功则返回执行方法的返回对象，否则返回错误信息（String）。
     */
    private Object run(String line) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        // 处理指令别名
        String newLine = line;
        for (Map.Entry<String, String> entry : prefixMap.entrySet()) {
            if (line.startsWith(entry.getKey())) {
                newLine = entry.getValue() + line.substring(entry.getKey().length());
                break;
            }
        }
        // 构建指令参数
        String[] ss = newLine.split(SPLIT_PARAM, 3);
        String group = null;
        String methodName;
        String paramLine = "";
        if (ss.length == 1) {
            methodName = ss[0];
        } else {
            if (smdConfig.isClassNameGroup()) {
                group = ss[0];
                methodName = ss[1];
                paramLine = ss.length == 3 ? ss[2] : "";
            } else {
                methodName = ss[0];
                paramLine = ss[1];
            }
        }
        if (!smdInfoMap.containsKey(group)) {
            throw new NoSuchGroupException(group);
        }
        SmdItem smdItem = smdItemMap.get(group).get(methodName);
        if (smdItem == null) {
            throw new NoSuchMethodException();
        }
        ArgParser argParser = argParserFactory.create(group, methodName);
        ArgValues argValues = argParser.parseLine(paramLine);
        return smdItem.run(argValues);
    }

    /**
     * 获取所有可用的指令Key
     *
     * @return 指令Key集
     */
    public Set<String> allKey() {
        return smdItemMap.keySet();
    }

    /**
     * 添加指令前缀替换
     *
     * @param name    指令前缀
     * @param aliases 指令替换字符
     */
    public void addPrefixReplace(String name, String... aliases) {
        for (String alias : aliases) {
            prefixMap.put(alias, name);
        }
    }

    /**
     * 获取指令本名
     *
     * @param alias 指令别名
     * @return 指令本名
     */
    public String getOriginalName(String alias) {
        return prefixMap.get(alias);
    }

    /**
     * 设置默认的指令参数解析器，用于 {@link #add(Object)}、{@link #add(Object, LoadConfig)} 方法中。
     *
     * @param defaultConfig 参数解析器
     */
    public void setDefaultConfig(LoadConfig defaultConfig) {
        this.defaultConfig = defaultConfig;
    }

    /**
     * 获取默认配置。
     *
     * @return 默认的指令加载配置。
     */
    public LoadConfig getDefaultConfig() {
        return defaultConfig;
    }

    /**
     * 清空指令缓存与指令信息
     */
    public void clear() {
        smdItemMap.clear();
        smdInfoMap.clear();
    }

    /**
     * 解析链接指令
     *
     * @param smdLink 指令行字符串
     * @return 链接指令数组
     */
    private String[] toLines(String smdLink) {
        return smdLinkParser.parse(smdLink);
    }

    /**
     * 特殊指令对象
     */
    private class SpecialSmdItem extends SmdItem {

        protected SpecialSmdItem(LoadConfig config) {
            super(config);
        }

        @Override
        protected Object convert(String str, Class<?> cla) {
            return SmdExecutor.this.parse(cla, str);
        }
    }

    /**
     * 特殊指令对象工厂类
     */
    private class SpecialSmdFactory implements SmdFactory {

        @Override
        public SmdItem create(LoadConfig config) {
            return new SpecialSmdItem(config);
        }
    }

    /**
     * 特殊指令参数解析器工厂类
     */
    private static class SpecialArgParser implements ArgParserFactory {

        @Override
        public ArgParser create(String group, String methodName) {
            return new SmdArgParser(KEY_PARAM_PREFIX);
        }
    }

    @SmdClass(value = "help", description = "帮助指令，用于查询当前的指令信息")
    private class HelpSmd {

        @SmdOption(value = "help", description = "查看当前加载的指令列表",
                example = "输入 help 来显示所有的指令，输入 help --group group --key key 来显示group下key指令的信息，例如 help math plus。" +
                        "实际上也可以使用 help --key key 来查询所有组下key指令的信息。")
        public List<SmdGroupInfo> help(
                @SmdParam(value = "group", force = false, description = "指定指令指令组名") String group,
                @SmdParam(value = "key", force = false, description = "指定指令名") String key) {
            List<SmdGroupInfo> list = new ArrayList<>();
            // 获取组列表
            if (group == null) {
                for (SmdGroupInfo value : smdInfoMap.values()) {
                    list.add(value.copy());
                }
            } else {
                SmdGroupInfo groupInfo = smdInfoMap.get(group);
                if (groupInfo == null) {
                    return list;
                } else {
                    list.add(groupInfo.copy());
                }
            }
            // 进行key过滤
            if (key != null) {
                for (SmdGroupInfo smdGroupInfo : list) {
                    smdGroupInfo.getMethodInfoList().removeIf(info -> {
                        for (String k : info.getKey()) {
                            if (k.equals(key)) {
                                return false;
                            }
                        }
                        return true;
                    });
                }
            }
            return list;
        }
    }
}
