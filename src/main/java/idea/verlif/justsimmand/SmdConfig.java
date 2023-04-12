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

    public boolean isClassNameGroup() {
        return classNameGroup;
    }

    public void setClassNameGroup(boolean classNameGroup) {
        this.classNameGroup = classNameGroup;
    }

}
