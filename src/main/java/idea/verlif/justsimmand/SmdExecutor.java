package idea.verlif.justsimmand;

import idea.verlif.justsimmand.anno.SmdClass;
import idea.verlif.justsimmand.anno.SmdOption;
import idea.verlif.justsimmand.anno.SmdParam;
import idea.verlif.justsimmand.info.SmdGroupInfo;
import idea.verlif.justsimmand.info.SmdMethodInfo;
import idea.verlif.justsimmand.parser.ClassParser;
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
@SmdClass(value = "executor", description = "指令执行器，用于构建对象指令")
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
     * 变量表
     */
    private final Map<String, Object> varsMap;

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
    private SmdItemFactory smdFactory;

    /**
     * 指令链接解析器
     */
    private SmdLinkParser smdLinkParser;

    /**
     * 参数解析器工厂类
     */
    private ArgParserFactory argParserFactory;

    /**
     * 帮助指令
     */
    private HelpSmd helpSmd;

    public SmdExecutor() {
        this(new SmdConfig());
    }

    public SmdExecutor(SmdConfig smdConfig) {
        this.smdItemMap = new HashMap<>();
        this.smdInfoMap = new HashMap<>();
        this.defaultConfig = new LoadConfig();
        this.prefixMap = new HashMap<>();
        this.varsMap = new HashMap<>();

        this.smdConfig = smdConfig;
        this.smdFactory = new SpecialSmdFactory();
        this.smdLinkParser = new BlockSmdLinkParser('(', ')');
        this.argParserFactory = new SpecialArgParser();

        // 添加Help指令
        helpSmd = new HelpSmd();
        add(helpSmd);
        // 添加Help别名
        addPrefixReplace("help help", "help");

        // 添加解析器
        addOrReplace(new ClassParser());
    }

    @SmdOption(value = "setSmdConfig", description = "设置指令配置", example = "setSmdConfig #{config1}")
    public void setSmdConfig(@SmdParam(value = "config", description = "指令配置对象") SmdConfig smdConfig) {
        this.smdConfig = smdConfig;
    }

    @SmdOption(value = {"getSmdConfig", "smdConfig"}, description = "获取当前指令配置", example = "smdConfig")
    public SmdConfig getSmdConfig() {
        return smdConfig;
    }

    @SmdOption(value = "setSmdFactory", description = "设置指令单项信息工厂类", example = "setSmdFactory #{config1}")
    public void setSmdFactory(@SmdParam(value = "factory", description = "指令单项信息工厂类") SmdItemFactory smdFactory) {
        this.smdFactory = smdFactory;
    }

    @SmdOption(value = "setSmdLinkParser", description = "设置指令链解析器", example = "setSmdLinkParser #{linkParser1}")
    public void setSmdLinkParser(@SmdParam(value = "parser", description = "指令链解析器") SmdLinkParser smdLinkParser) {
        this.smdLinkParser = smdLinkParser;
    }

    @SmdOption(value = "setArgParserFactory", description = "设置参数解析器工厂类", example = "setArgParserFactory #{parserFactory1}")
    public void setArgParserFactory(@SmdParam(value = "factory", description = "参数解析器工厂类") ArgParserFactory argParserFactory) {
        this.argParserFactory = argParserFactory;
    }

    /**
     * 手动添加指令组信息
     *
     * @param smdGroupInfo 指令组信息
     */
    @SmdOption(value = "addSmdGroupInfo", description = "手动添加指令组信息", example = "addSmdGroupInfo #{groupInfo1}")
    public void addSmdGroupInfo(@SmdParam(value = "groupInfo", description = "指令组信息对象") SmdGroupInfo smdGroupInfo) {
        if (smdGroupInfo.getKey() != null) {
            smdInfoMap.put(smdGroupInfo.getKey(), smdGroupInfo);
        }
    }

    /**
     * 添加统一变量，可以在指令中使用 {@code #{key}} 来填充变量对象到指令参数
     *
     * @param key      变量名
     * @param variable 变量对象
     */
    @SmdOption(ignored = true)
    public void variable(String key, Object variable) {
        varsMap.put(key, variable);
    }

    /**
     * 添加指令对象。使用默认的参数解析器与配置。
     *
     * @param o 指令对象
     */
    @SmdOption(value = "add", description = "添加指令对象", example = "addWithConfig #{smdObject1}")
    public void add(@SmdParam(value = "o", description = "指令对象") Object o) {
        add(o, defaultConfig);
    }

    /**
     * 添加指令对象。
     *
     * @param o      指令对象
     * @param config 指定加载的方法
     */
    @SmdOption(value = "addWithConfig", description = "添加指令对象", example = "addWithConfig #{smdObject1} #{loadConfig1}")
    public void add(@SmdParam(value = "o", description = "指令对象") Object o, @SmdParam(value = "config", description = "指令对象加载配置") LoadConfig config) {
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
        allMethods.sort(Comparator.comparingInt(method -> method.getName().charAt(0)));
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
    @SmdOption(value = {"execute", "exec"}, description = "通过指令行运行指令", example = "exec \"plus 1 2\"")
    public synchronized Object execute(@SmdParam(value = "line", description = "指令行或指令链") String smdLine) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        if (!smdConfig.isLinkable()) {
            return run(smdLine);
        }
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

    @SmdOption(value = "help", description = "查看当前加载的指令列表",
            example = "输入 help 来显示所有的指令，输入 help --group group --key key 来显示group下key指令的信息，例如 help math plus。" +
                    "实际上也可以使用 help --key key 来查询所有组下key指令的信息。")
    public List<SmdGroupInfo> help(
            @SmdParam(value = "group", force = false, description = "指定指令指令组名") String group,
            @SmdParam(value = "key", force = false, description = "指定指令名") String key) {
        return helpSmd.help(group, key);
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
    @SmdOption(value = {"allKey", "keys"}, description = "获取所有可用的指令key", example = "allKey")
    public Set<String> allKey() {
        return smdItemMap.keySet();
    }

    /**
     * 添加指令前缀替换
     *
     * @param name    指令前缀
     * @param aliases 指令替换字符
     */
    @SmdOption(value = {"addPrefixReplace", "prefix"}, description = "添加指令前缀替换，适合设置指令别名或快捷指令", example = "prefix plus \"plus 1 1\"")
    public void addPrefixReplace(@SmdParam(value = "name", description = "指令前缀") String name, @SmdParam(value = "alias", description = "替换的指令字符串") String... aliases) {
        for (String alias : aliases) {
            prefixMap.put(alias, name);
        }
    }

    @Override
    @SmdOption(value = "parse", description = "解析字符串成对象，优先解析成变量", example = "parse com.demo.person person")
    public <T> T parse(@SmdParam(value = "cl", description = "对象类全名") Class<T> cl, @SmdParam(value = "param", description = "对象字符串") String param) {
        int length = param.length();
        if (length > 3
                && param.charAt(0) == '#'
                && param.charAt(1) == '{' && param.charAt(length - 1) == '}') {
            return (T) varsMap.get(param.substring(2, length - 1));
        }
        return super.parse(cl, param);
    }

    /**
     * 获取指令本名
     *
     * @param alias 指令别名
     * @return 指令本名
     */
    @SmdOption(value = {"getOriginalName", "originalName"}, description = "获取指令本名", example = "originalName plus")
    public String getOriginalName(@SmdParam(value = "alias", description = "指令别名") String alias) {
        return prefixMap.get(alias);
    }

    /**
     * 设置默认指令加载配置对象，用于 {@link #add(Object)}、{@link #add(Object, LoadConfig)} 方法中。
     *
     * @param defaultConfig 默认指令加载配置
     */
    @SmdOption(value = "setDefaultConfig", description = "设置默认指令加载配置对象", example = "setDefaultConfig #{defaultConfig}")
    public void setDefaultConfig(@SmdParam(value = "config", description = "默认指令加载配置对象") LoadConfig defaultConfig) {
        this.defaultConfig = defaultConfig;
    }

    /**
     * 获取默认指令加载配置对象。
     *
     * @return 默认的指令加载配置。
     */
    @SmdOption(value = "defaultConfig", description = "获取默认指令加载配置对象", example = "defaultConfig")
    public LoadConfig getDefaultConfig() {
        return defaultConfig;
    }

    /**
     * 清空指令缓存与指令信息
     */
    @SmdOption(value = "clear", description = "清空指令缓存与指令信息")
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
    private class SpecialSmdFactory implements SmdItemFactory {

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
