package idea.verlif.justsimmand;

import idea.verlif.parser.cmdline.ArgParser;
import idea.verlif.parser.cmdline.ArgValues;

import java.util.ArrayList;
import java.util.List;

public class SmdArgParser implements ArgParser {

    protected final String prefix;

    public SmdArgParser(String prefix) {
        this.prefix = prefix;
    }

    public ArgValues parseLine(String line) {
        return this.parser(this.lineToArray(line));
    }

    public ArgValues parser(String[] args) {
        ArgValues argValues = new ArgValues();

        for(int i = 0; i < args.length; i++) {
            String key = this.getKeyFromArg(args[i]);
            if (key == null) {
                argValues.add(null, args[i]);
            } else {
                int next = i + 1;
                if (next == args.length) {
                    argValues.add(null, key);
                } else if (this.getKeyFromArg(args[next]) == null) {
                    argValues.add(key, args[next]);
                    i = next;
                } else {
                    argValues.add(null, key);
                }
            }
        }

        return argValues;
    }

    public String[] lineToArray(String line) {
        char[] chars = line.toCharArray();
        StringBuilder stb = new StringBuilder();
        List<String> lines = new ArrayList<>();
        boolean noStr = true; // 是否不在引号字符串中，若不在则进行空格解析
        boolean ignoredNext = false; // 是否忽略下一个字符，用于转移符号
        for (char c : chars) {
            if (c == '\\') {
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
            } else if (noStr && c == ' ') { // 断开
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

    protected String getKeyFromArg(String arg) {
        if (arg.startsWith(this.prefix)) {
            String key = arg.substring(this.prefix.length());
            if (key.length() > 0) {
                return key;
            }
        }

        return null;
    }
}
