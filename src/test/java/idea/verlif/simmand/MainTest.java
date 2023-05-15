package idea.verlif.simmand;

import idea.verlif.justsimmand.LoadConfig;
import idea.verlif.justsimmand.PointSmdLinkParser;
import idea.verlif.justsimmand.SmdConfig;
import idea.verlif.justsimmand.SmdExecutor;
import idea.verlif.justsimmand.anno.SmdParam;
import idea.verlif.justsimmand.info.SmdGroupInfo;
import idea.verlif.simmand.domain.LinkA;
import idea.verlif.simmand.domain.Math;
import idea.verlif.simmand.domain.SimpleMath;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

public class MainTest {

    @Test
    public void test() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        // 实例化指令执行器
        SmdExecutor smdExecutor = new SmdExecutor();
        // 添加指令对象
        smdExecutor.add(new Math());
        // 设定指令前缀别名，这里表示输入 "3" 或 "test" 就相当于输入了 "math plus --b 4"
        // 这里的 "--b" 表示后面的 "4" 是属于参数 "b" 的
        smdExecutor.addPrefixReplace("math plus --b 4", "3", "test");
        // 使用别名进行指令调用，这里因为 "a" 并不是强制参数且是基础类型，所以会给予默认值 "0"
        System.out.println("使用指令前缀别名: " + smdExecutor.execute("3")); // 结果是4
        // 这里同样因为 "b" 设定有默认值，所以也不需要输入 "b" 的值
        System.out.println("使用指令设定值: " + smdExecutor.execute("math plus")); // 结果是3
        System.out.println("基础调用: " + smdExecutor.execute("math ^")); // 结果是0，因为int的默认值是0
        // 输出help
        List<SmdGroupInfo> run = (List<SmdGroupInfo>) smdExecutor.execute("help");
        for (SmdGroupInfo smdGroupInfo : run) {
            System.out.print(smdGroupInfo);
        }
    }

    @Test
    public void simpleTest() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        // 实例化指令执行器
        SmdExecutor smdExecutor = new SmdExecutor();
        smdExecutor.getSmdConfig().setClassNameGroup(false);
        LoadConfig loadConfig = new LoadConfig().loadMode(LoadConfig.LoadMode.POSITIVE);
        // 加载对象到指令执行器，并设定加载模式为积极模式
        smdExecutor.add(new SimpleMath(), loadConfig);
        smdExecutor.add(new LinkA(), loadConfig);
        smdExecutor.add(new Math(), loadConfig);
        smdExecutor.add(smdExecutor, loadConfig); // 添加自己来试试
        // 开始执行方法
        // help
        List<SmdGroupInfo> help = (List<SmdGroupInfo>) smdExecutor.execute("help");
        for (SmdGroupInfo groupInfo : help) {
            System.out.println(groupInfo);
        }

        System.out.println(Arrays.toString(smdExecutor.allKey().toArray()));

        System.out.println(smdExecutor.execute("SimpleMath plus 5 -3")); // 2
        System.out.println(smdExecutor.execute("SimpleMath square 6")); // 36
        smdExecutor.getSmdConfig().setLinkable(true);
        System.out.println(smdExecutor.execute("(LinkA b 这里是输出的b).(a 这里是输出的\"引号\").(say)")); // A
        smdExecutor.setSmdLinkParser(new PointSmdLinkParser());
        System.out.println(smdExecutor.execute("LinkA b \"这里是输出的b A\".a 这里是输出的\"引号\".say")); // A
        // 重复输出
        System.out.println("------ 以下是重复输出 ------");
        System.out.println(smdExecutor.execute("executor execute \"LinkA b \\\"这里是输出的b A\\\"\\.a 这里是输出的\\\"引号\\\"\\.say\"")); // A
        System.out.println(smdExecutor.execute("LinkA hi 1 \"2 4 5 6\""));
        SmdConfig config = new SmdConfig().linkable(false);
        smdExecutor.variable("config", config);
        smdExecutor.execute("executor setSmdConfig #{config}");
        System.out.println(smdExecutor.execute("LinkA param java.lang.String"));
        System.out.println(smdExecutor.execute("executor help"));
    }

    @Test
    public void testForStr() {
        System.out.println(String.format("#{%s}", "abc"));
    }
}
