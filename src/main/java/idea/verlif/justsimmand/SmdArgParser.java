package idea.verlif.justsimmand;

import idea.verlif.parser.cmdline.ArgParser;
import idea.verlif.parser.cmdline.ArgValues;

import java.util.ArrayList;

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

        for(int i = 0; i < args.length; ++i) {
            String key = this.getKeyFromArg(args[i]);
            if (key == null) {
                argValues.add(null, args[i]);
            } else {
                int next = i + 1;
                if (next == args.length) {
                    argValues.add(null, key);
                } else if (this.getKeyFromArg(args[next]) == null) {
                    argValues.add(key, args[next]);
                    ++i;
                } else {
                    argValues.add(null, key);
                }
            }
        }

        return argValues;
    }

    public String[] lineToArray(String line) {
        ArrayList<String> list = new ArrayList<>();
        boolean isOneParam = false;
        StringBuilder sb = new StringBuilder();
        char[] chars = line.toCharArray();

        for (char c : chars) {
            if (isOneParam) {
                if (c == '"') {
                    isOneParam = false;
                    list.add(sb.toString());
                    sb.delete(0, sb.length());
                } else {
                    sb.append(c);
                }
            } else if (c == '"') {
                isOneParam = true;
            } else if (c == ' ') {
                if (sb.length() > 0) {
                    list.add(sb.toString());
                    sb.delete(0, sb.length());
                }
            } else {
                sb.append(c);
            }
        }

        if (sb.length() > 0) {
            list.add(sb.toString());
            sb.delete(0, sb.length());
        }

        return list.toArray(new String[0]);
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
