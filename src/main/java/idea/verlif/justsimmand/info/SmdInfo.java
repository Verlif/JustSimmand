package idea.verlif.justsimmand.info;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * 指令信息
 */
public class SmdInfo {

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

    public SmdInfo() {
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

    private String cacheToString;

    @Override
    public String toString() {
        if (cacheToString == null) {
            StringBuilder str = new StringBuilder();
            str.append(key).append(" - ").append(description).append("\n");
            for (SmdMethodInfo methodInfo : smdMethodInfoList) {
                String keys = Arrays.toString(methodInfo.getKey());
                str.append("\t--> ").append(fillSpace(20, keys.substring(1, keys.length() - 1)))
                        .append("\t").append(methodInfo.getDescription()).append("\n");
                if (!methodInfo.getArgInfoList().isEmpty()) {
                    for (SmdArgInfo smdArgInfo : methodInfo.getArgInfoList()) {
                        String argName = smdArgInfo.getKey();
                        if (!smdArgInfo.isForce()) {
                            argName = "[" + argName + "]";
                        }
                        str.append("\t\t");
                        if (smdArgInfo.isForce()) {
                            str.append("**");
                        } else {
                            str.append("--");
                        }
                        str.append(fillSpace(20, argName)).append("\t").append(smdArgInfo.getDescription()).append("\n");
                    }
                }
            }
            cacheToString = str.toString();
        }
        return cacheToString;
    }

    private String fillSpace(int total, String str) {
        StringBuilder strb = new StringBuilder(str);
        for (int i = str.length(); i < total; i++) {
            strb.append(" ");
        }
        return strb.toString();
    }
}
