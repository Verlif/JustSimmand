package idea.verlif.simmand;

import idea.verlif.justsimmand.BlockSmdLinkParser;
import idea.verlif.justsimmand.LoadConfig;
import idea.verlif.justsimmand.PointSmdLinkParser;
import idea.verlif.justsimmand.SmdExecutor;
import idea.verlif.justsimmand.info.SmdGroupInfo;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
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
        System.out.println("使用指令前缀别名: " + smdExecutor.run("3"));
        // 这里同样因为 "b" 设定有默认值，所以也不需要输入 "b" 的值
        System.out.println("使用指令设定值: " + smdExecutor.run("math plus"));
        System.out.println("基础调用: " + smdExecutor.run("math ^"));
        // 输出help
        List<SmdGroupInfo> run = (List<SmdGroupInfo>) smdExecutor.run("help");
        for (SmdGroupInfo smdGroupInfo : run) {
            System.out.print(smdGroupInfo);
        }
    }

    @Test
    public void simpleTest() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        // 实例化指令执行器
        SmdExecutor smdExecutor = new SmdExecutor();
        // 加载对象到指令执行器，并设定加载模式为积极模式
        smdExecutor.add(new SimpleMath(), new LoadConfig()
                .loadMode(LoadConfig.LoadMode.POSITIVE));
        // 开始执行方法
        System.out.println("plus : " + smdExecutor.run("SimpleMath plus 2 3"));
        System.out.println("square : " + smdExecutor.run("SimpleMath square 2"));
        System.out.println("square : " + smdExecutor.run("SimpleMath square 2"));
        smdExecutor.setSmdLinkParser(new PointSmdLinkParser());
        System.out.println("linkExecute : " + smdExecutor.execute("SimpleMath getTestB.say \"我说: \\\"你好\\\"\".getTestB.say \\(hihihi\\)"));
        smdExecutor.setSmdLinkParser(new BlockSmdLinkParser('{', '}'));
        System.out.println("linkExecute : " + smdExecutor.execute("{SimpleMath getTestB}{say \"我说: \\\"你好\\\"\"}{getTestB}{say \\(hihihi\\)}"));

        SmdGroupInfo groupInfo = new SmdGroupInfo();
        groupInfo.setKey("add");
        groupInfo.setDescription("add a to b");
        smdExecutor.addSmdGroupInfo(groupInfo);
        // 输出help
        List<SmdGroupInfo> run = (List<SmdGroupInfo>) smdExecutor.run("help");
        for (SmdGroupInfo smdGroupInfo : run) {
            System.out.print(smdGroupInfo);
        }
    }

    @Test
    public void testForStr() {
        PointSmdLinkParser linkParser = new PointSmdLinkParser();
        String[] parse = linkParser.parse("1.2.\"3.4\".5");
        for (String s : parse) {
            System.out.println(s);
        }
    }
}
