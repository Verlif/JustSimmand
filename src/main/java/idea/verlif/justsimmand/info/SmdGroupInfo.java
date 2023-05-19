package idea.verlif.justsimmand.info;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * 指令信息
 */
public class SmdGroupInfo {

    /**
     * 指令组key
     */
    private String key;

    /**
     * 指令组描述
     */
    private String description;

    /**
     * 指令方法集
     */
    private final List<SmdMethodInfo> smdMethodInfoList;

    public SmdGroupInfo() {
        this.smdMethodInfoList = new ArrayList<>();
    }

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

    public List<SmdMethodInfo> getMethodInfoList() {
        return smdMethodInfoList;
    }

    public void addSmdMethodInfo(SmdMethodInfo smdMethodInfo) {
        smdMethodInfoList.add(smdMethodInfo);
    }

    public void addSmdMethodInfo(Collection<SmdMethodInfo> collection) {
        smdMethodInfoList.addAll(collection);
    }

    public SmdGroupInfo copy() {
        SmdGroupInfo groupInfo = new SmdGroupInfo();
        groupInfo.key = this.key;
        groupInfo.description = this.description;
        for (SmdMethodInfo smdMethodInfo : this.smdMethodInfoList) {
            groupInfo.smdMethodInfoList.add(smdMethodInfo.copy());
        }
        return groupInfo;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(key).append(" - ").append(description != null ? description : "暂无描述").append("\n");
        // 遍历指令组的所有方法
        for (SmdMethodInfo methodInfo : smdMethodInfoList) {
            String keys = Arrays.toString(methodInfo.getKey());
            // 指令方法描述
            str.append("\t[ ").append(keys, 1, keys.length() - 1).append(" ]")
                    .append(" --> ").append(methodInfo.getDescription() != null ? methodInfo.getDescription() : "暂无描述").append("\n");
            // 显示指令示例
            if (methodInfo.getExample() != null && methodInfo.getExample().length() > 0) {
                str.append("\t--> ").append(methodInfo.getExample()).append("\n");
            }
            if (!methodInfo.getArgInfoList().isEmpty()) {
                // 遍历指令方法参数
                for (SmdArgInfo smdArgInfo : methodInfo.getArgInfoList()) {
                    String argName = smdArgInfo.getKey();
                    str.append("\t\t");
                    // 强制需要的参数显示为 *arg* , 否则显示为 [arg]
                    str.append(argName).append(" (");
                    if (smdArgInfo.isForce()) {
                        str.append("required");
                    }
                    // 显示默认值
                    if (smdArgInfo.getDefaultVal() != null) {
                        str.append(" | ").append(smdArgInfo.getDefaultVal());
                    }
                    if (str.charAt(str.length() - 1) != '(') {
                        str.append(")");
                    } else {
                        str.setLength(str.length() - 1);
                    }
                    str.append(", ");
                    // 显示参数类型
                    str.append(typeString(smdArgInfo.getType()));
                    if (smdArgInfo.getDescription() == null) {
                        str.append("\n");
                    } else {
                        str.append(" -> ").append(smdArgInfo.getDescription()).append("\n");
                    }
                }
            }
        }
        return str.toString();
    }

    private String typeString(Class<?> type) {
        if (type == null) {
            return "UNKNOWN";
        }
        if (type.isArray()) {
            return typeString(type.getComponentType()) + "[]";
        } else {
            return type.getSimpleName();
        }
    }

    private String fillSpace(int total, String str) {
        StringBuilder strb = new StringBuilder(str);
        for (int i = str.length(); i < total; i++) {
            strb.append(" ");
        }
        return strb.toString();
    }
}
