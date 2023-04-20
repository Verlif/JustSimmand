package idea.verlif.justsimmand;

import java.util.ArrayList;
import java.util.List;

/**
 * 块级构造器，允许通过左右符号来确定指令链接的区域分隔。<br>
 * 例如使用 {@code new BlockSmdLinkParser('(', ')')} 可以将
 * (groupA methodA paramA)(methodB paramB)(methodC paramC) 拆解成以下部分：<br>
 * <ul>
 *     <li>groupA methodA paramA</li>
 *     <li>methodB paramB</li>
 *     <li>methodC paramC</li>
 * </ul>
 * 此时，第二条指令的返回值会作为第二条的指令对象，第二条指令的返回值会作为第三条指令的指定对象，以此类推。
 */
public class BlockSmdLinkParser implements SmdLinkParser {

    private final char left;
    private final char right;

    public BlockSmdLinkParser(char left, char right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public String[] parse(String smdLink) {
        smdLink = smdLink.trim();
        // 如果是普通指令则直接返回
        if (smdLink.length() > 0 && smdLink.charAt(0) != left) {
            return new String[]{smdLink};
        }
        char[] chars = smdLink.toCharArray();
        StringBuilder stb = new StringBuilder();
        List<String> lines = new ArrayList<>();
        boolean inLine = false; // 是否关闭
        boolean ignoredNext = false; // 是否忽略下一个字符，用于转移符号
        for (char c : chars) {
            if (c == '\\') {
                stb.append(c);
                ignoredNext = true;
            } else if (ignoredNext) {
                stb.append(c);
                ignoredNext = false;
            } else if (c == left) { // 检测开始
                inLine = true;
            } else if (c == right) {
                lines.add(stb.toString());
                stb.setLength(0);
                inLine = false;
            } else if (inLine) {
                stb.append(c);
            }
        }
        if (stb.length() > 0) {
            lines.add(stb.toString());
        }
        return lines.toArray(new String[0]);
    }
}
