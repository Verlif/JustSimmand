package idea.verlif.justsimmand.pretreatment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 指令过滤器
 */
public class FilterPretreatment implements SmdLinePretreatment {

    private final List<Pattern> allowedRuleList;
    private final List<Pattern> blockedRuleList;

    public FilterPretreatment() {
        allowedRuleList = new ArrayList<>();
        blockedRuleList = new ArrayList<>();
    }

    @Override
    public String handle(String smdLine) {
        // 优先判断许可列表
        if (allowedRuleList.isEmpty()) {
            if (blockedRuleList.isEmpty()) {
                return smdLine;
            }
            for (Pattern pattern : blockedRuleList) {
                if (pattern.matcher(smdLine).matches()) {
                    return null;
                }
            }
        } else {
            for (Pattern pattern : allowedRuleList) {
                if (pattern.matcher(smdLine).matches()) {
                    return smdLine;
                }
            }
            return null;
        }
        return smdLine;
    }

    public void allow(Pattern... allowedRules) {
        allowedRuleList.addAll(Arrays.asList(allowedRules));
    }

    public void allow(String... regexes) {
        for (String regex : regexes) {
            allowedRuleList.add(Pattern.compile(regex));
        }
    }

    public void block(Pattern... blockedRules) {
        blockedRuleList.addAll(Arrays.asList(blockedRules));
    }

    public void block(String... regexes) {
        for (String regex : regexes) {
            blockedRuleList.add(Pattern.compile(regex));
        }
    }
}
