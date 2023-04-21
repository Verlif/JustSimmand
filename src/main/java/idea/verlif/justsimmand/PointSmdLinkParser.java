package idea.verlif.justsimmand;

import java.util.ArrayList;
import java.util.List;

/**
 * 点式构造器，允许通过小数点来确定指令链接的区域分隔。<br>
 * 例如使用 {@code new PointSmdLinkParser()} 可以将
 * groupA methodA paramA.methodB paramB.methodC paramC 拆解成以下部分：<br>
 * <ul>
 *     <li>groupA methodA paramA</li>
 *     <li>methodB paramB</li>
 *     <li>methodC paramC</li>
 * </ul>
 * 此时，第二条指令的返回值会作为第二条的指令对象，第二条指令的返回值会作为第三条指令的指定对象，以此类推。
 */
public class PointSmdLinkParser implements SmdLinkParser {

    @Override
    public String[] parse(String smdLink) {
        char[] chars = smdLink.trim().toCharArray();
        StringBuilder stb = new StringBuilder();
        List<String> lines = new ArrayList<>();
        boolean ignoredNext = false; // 是否忽略下一个字符，用于转移符号
        boolean noStr = true; // 是否不在字符串内
        for (char c : chars) {
            if (c == '\\') {
                stb.append(c);
                ignoredNext = true;
            } else if (ignoredNext) {
                stb.append(c);
                ignoredNext = false;
            } else if (c == '\"') {
                if (stb.length() > 0 && noStr) {
                    stb.append(c);
                } else {
                    noStr = !noStr;
                }
            }else if (noStr && c == '.') {
                lines.add(stb.toString());
                stb.setLength(0);
            } else {
                stb.append(c);
            }
        }
        if (stb.length() > 0) {
            lines.add(stb.toString());
        }
        return lines.toArray(new String[0]);
    }
}
