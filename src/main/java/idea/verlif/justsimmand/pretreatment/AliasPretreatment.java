package idea.verlif.justsimmand.pretreatment;

import idea.verlif.parser.cmdline.ArgValues;

/**
 * 别名处理器
 */
public class AliasPretreatment implements SmdLinePretreatment {

    /**
     * 别名表。key - 别名；value - 本名
     */
    private final ArgValues aliasMap;

    /**
     * 是否开启正则匹配
     */
    private boolean regularity;

    public AliasPretreatment() {
        aliasMap = new ArgValues();
    }

    @Override
    public String handle(String smdLine) {
        if (regularity) {
            for (int i = 0, size = aliasMap.size(); i < size; i++) {
                String alias = aliasMap.getKey(i);
                String actual = aliasMap.getValue(i);
                smdLine = smdLine.replaceAll(alias, actual);
            }
        } else {
            for (int i = 0, size = aliasMap.size(); i < size; i++) {
                String alias = aliasMap.getKey(i);
                String actual = aliasMap.getValue(i);
                smdLine = smdLine.replace(alias, actual);
            }
        }
        return smdLine;
    }

    public void setRegularity(boolean regularity) {
        this.regularity = regularity;
    }

    public boolean isRegularity() {
        return regularity;
    }

    /**
     * 添加指令前缀替换
     *
     * @param name    指令前缀
     * @param aliases 指令替换字符
     */
    public void addAlias(String name, String... aliases) {
        for (String alias : aliases) {
            aliasMap.add(alias, name);
        }
    }

    /**
     * 获取指令本名
     *
     * @param alias 指令别名
     * @return 指令本名
     */
    public String getOriginalName(String alias) {
        return aliasMap.getValue(alias);
    }
}
