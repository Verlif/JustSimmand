package idea.verlif.justsimmand.info;

/**
 * 指令参数说明
 */
public class SmdArgInfo {

    /**
     * 参数key
     */
    private String key;

    /**
     * 参数描述
     */
    private String description;

    /**
     * 参数默认值
     */
    private String defaultVal;

    /**
     * 参数是否必填
     */
    private boolean force;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDefaultVal() {
        return defaultVal;
    }

    public void setDefaultVal(String defaultVal) {
        this.defaultVal = defaultVal;
    }

    public boolean isForce() {
        return force;
    }

    public void setForce(boolean force) {
        this.force = force;
    }
}
