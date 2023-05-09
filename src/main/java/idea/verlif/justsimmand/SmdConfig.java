package idea.verlif.justsimmand;

/**
 * 指令配置
 */
public class SmdConfig {

    /**
     * 是否启用类名分组。开启后，添加的每个对象指令都会增加此对象名作为指令前缀。<br>
     * 举例：<br>
     * 当classNameGroup为true时，添加Person对象时，其中有方法{@code functionA}，
     * 那么需要输入的指令则是<em>Person functionA [options]<em> 。
     */
    private boolean classNameGroup = true;

    /**
     * 是否开启指令链模式，此模式会导致指令行中的小数点被识别成分隔符，需要使用 "{@code \.}" 替换 "{@code .}"。
     */
    private boolean linkable = false;

    public boolean isClassNameGroup() {
        return classNameGroup;
    }

    public void setClassNameGroup(boolean classNameGroup) {
        this.classNameGroup = classNameGroup;
    }

    public SmdConfig classNameGroup(boolean classNameGroup) {
        this.classNameGroup = classNameGroup;
        return this;
    }

    public boolean isLinkable() {
        return linkable;
    }

    public void setLinkable(boolean linkable) {
        this.linkable = linkable;
    }

    public SmdConfig linkable(boolean linkable) {
        this.linkable = linkable;
        return this;
    }
}
