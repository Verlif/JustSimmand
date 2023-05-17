package idea.verlif.justsimmand;

import idea.verlif.justsimmand.anno.SmdOption;
import idea.verlif.justsimmand.anno.SmdParam;
import idea.verlif.justsimmand.info.SmdArgInfo;
import idea.verlif.justsimmand.info.SmdMethodInfo;
import idea.verlif.parser.cmdline.ArgValues;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

/**
 * 指令对象，由{@link SmdExecutor}自动生成。<br>
 * 每个Simmand都是一个方法的执行体，只管理一个方法的执行内容。
 *
 * @author Verlif
 */
public abstract class SmdItem {

    private final LoadConfig config;

    /**
     * 指令Key
     */
    private String[] key;

    /**
     * 指令对象
     */
    private Object object;

    /**
     * 指令方法
     */
    private Method method;

    /**
     * 方法参数类型列表
     */
    private Class<?>[] parameterTypes;

    /**
     * 指令说明
     */
    private SmdMethodInfo methodInfo;

    /**
     * 参数名序号表，保存有参数在方法入参的位置序号。<br/>
     * key - 参数名；<br/>
     * value - 指令参数序号
     */
    private Map<String, Integer> paramMap;

    /**
     * 参数名表，表示了方法入参的每个位置的参数名称。<br/>
     * key - 指令参数序号；<br/>
     * value - 参数名称
     */
    private Map<Integer, String> keyMap;

    /**
     * 参数值默认值表，表示了方法入参的每个位置的默认值。<br/>
     * key - 指令参数序号；<br/>
     * value - 参数默认值
     */
    private Map<Integer, Object> valueMap;

    protected SmdItem(LoadConfig config) {
        this.config = config;
    }

    /**
     * 加载方法到此指令。
     *
     * @param o      指令的所属对象
     * @param method 指令方法
     */
    public boolean load(Object o, Method method) {
        methodInfo = new SmdMethodInfo();
        SmdOption smdOption = method.getAnnotation(SmdOption.class);
        // 当有SmdOption注解且当前配置为主动加载且标明此方法不加载时，不加载此方法
        int loadMode = config.getLoadMode();
        if ((smdOption == null && !LoadConfig.LoadMode.POSITIVE.matches(loadMode))
                || (LoadConfig.LoadMode.POSITIVE.matches(loadMode) && smdOption != null && smdOption.ignored())) {
            return false;
        }
        // 判断加载非本身声明的方法
        if (o.getClass() != method.getDeclaringClass() && !LoadConfig.LoadMode.EXTEND.matches(loadMode)) {
            return false;
        }
        this.object = o;
        this.method = method;
        // 设置指令名
        if (smdOption == null || smdOption.value().length == 0) {
            key = new String[]{method.getName()};
        } else {
            key = smdOption.value();
        }
        // 设置指令信息
        methodInfo.setKey(key);
        if (smdOption != null) {
            methodInfo.setDescription(smdOption.description());
            methodInfo.setExample(smdOption.example());
        }
        // 设置指令参数解析表
        parameterTypes = method.getParameterTypes();
        // 设置参数默认值与参数名序号表
        keyMap = new HashMap<>(parameterTypes.length);
        valueMap = new HashMap<>(parameterTypes.length);
        paramMap = new HashMap<>(parameterTypes.length);
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            String paramKey = parameter.getName();
            SmdArgInfo smdArgInfo = new SmdArgInfo();
            SmdParam smdParam = parameter.getAnnotation(SmdParam.class);
            if (smdParam != null) {
                // 设置参数属性
                smdArgInfo.setDescription(smdParam.description());
                smdArgInfo.setForce(smdParam.force());
                // 设置默认值
                if (smdParam.defaultVal().length() > 0) {
                    valueMap.put(i, convert(smdParam.defaultVal(), parameterTypes[i]));
                    smdArgInfo.setDefaultVal(smdParam.defaultVal());
                    if (smdParam.value().length() > 0) {
                        paramKey = smdParam.value();
                    }
                } else if (smdParam.force()) {
                    valueMap.put(i, ForceValue.class);
                }
            } else {
                valueMap.put(i, null);
            }
            keyMap.put(i, paramKey);
            paramMap.put(paramKey, i);
            smdArgInfo.setKey(paramKey);
            methodInfo.addSmdArgInfo(smdArgInfo);
        }
        return true;
    }

    /**
     * 获取此指令对象的所有方法key。
     *
     * @return 此指令对象的所有加载的方法key，这些key都可以被识别。
     */
    public String[] getKey() {
        return key;
    }

    public SmdMethodInfo getMethodInfo() {
        return methodInfo;
    }

    /**
     * 指令执行
     *
     * @return 指令返回值
     * @throws JustSmdException 反射运行出错抛出此异常
     */
    public Object run(ArgValues argValues) throws JustSmdException {
        Object[] params = new Object[keyMap.size()];
        // 构建参数数组
        Deque<String> noKeyValue = new ArrayDeque<>();
        for (int i = 0, size = argValues.size(); i < size; i++) {
            String argKey = argValues.getKey(i);
            if (argKey == null) {
                noKeyValue.add(argValues.getValue(i));
            }
        }
        for (int i = 0, size = keyMap.size(); i < size; i++) {
            String localKey = keyMap.get(i);
            Object value = argValues.getValue(localKey);
            if (value == null && !noKeyValue.isEmpty()) {
                value = noKeyValue.pop();
            }
            // 获取默认值
            if (value == null) {
                value = valueMap.get(i);
            }
            // 排序
            if (value != null) {
                Integer integer = null;
                if (argValues.size() > i) {
                    integer = paramMap.get(localKey);
                }
                if (integer == null) {
                    params[i] = value;
                } else {
                    params[integer] = value;
                }
            }
        }
        // 检测参数是否合规
        for (int i = 0; i < params.length; i++) {
            Object param = params[i];
            if (param == ForceValue.class) {
                // 是否是强制参数
                throw new MissArgException(keyMap.get(i));
            } else if (param == null && parameterTypes[i].isPrimitive()) {
                // 处理基础类型
                params[i] = getPrimitiveValue(parameterTypes[i]);
            }
        }
        for (int i = 0, size = parameterTypes.length; i < size; i++) {
            if (params[i] != null && params[i].getClass() != parameterTypes[i]) {
                params[i] = convert(params[i].toString(), parameterTypes[i]);
            }
        }
        try {
            return method.invoke(object, params);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new JustSmdException(e);
        }
    }

    /**
     * 将字符串转换成对应对象
     *
     * @param str 字符串
     * @param cla 目标类型
     * @return 字符串转换成的目标类型实例
     */
    protected abstract Object convert(String str, Class<?> cla);

    /**
     * 强制参数替代值
     */
    private interface ForceValue {
    }

    /**
     * 获取基础类型的默认值
     *
     * @param cla 基础类型类
     * @return 基础类型对应的默认值
     */
    private Object getPrimitiveValue(Class<?> cla) {
        if (cla == int.class) {
            return 0;
        } else if (cla == float.class) {
            return 0f;
        } else if (cla == long.class) {
            return (long) 0;
        } else if (cla == char.class) {
            return ' ';
        } else if (cla == double.class) {
            return 0d;
        } else if (cla == boolean.class) {
            return false;
        } else if (cla == byte.class) {
            return (byte) 1;
        } else if (cla == short.class) {
            return (short) 0;
        } else return null;
    }
}
