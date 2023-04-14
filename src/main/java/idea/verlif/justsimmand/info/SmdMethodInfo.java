package idea.verlif.justsimmand.info;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 指令方法信息
 */
public class SmdMethodInfo {

    /**
     * 方法key数组
     */
    private String[] key;

    /**
     * 方法描述
     */
    private String description;

    /**
     * 方法示例
     */
    private String example;

    /**
     * 指令参数列表
     */
    private final List<SmdArgInfo> argInfoList;

    public SmdMethodInfo() {
        argInfoList = new ArrayList<>();
    }

    public String[] getKey() {
        return key;
    }

    public void setKey(String[] key) {
        this.key = key;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public List<SmdArgInfo> getArgInfoList() {
        return argInfoList;
    }

    public void addSmdArgInfo(SmdArgInfo smdArgInfo) {
        argInfoList.add(smdArgInfo);
    }

    public void addSmdArgInfo(Collection<SmdArgInfo> collection) {
        argInfoList.addAll(collection);
    }
}
