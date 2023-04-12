# JustSimmand

中文 | [English](readme.md)

`JustSimmand`，一个简单且直观的指令系统。

在`JustSimmand`中，一个实例对象就是一个指令集，实例对象的每一个方法就是一条指令。
不需要再手动写调用方法，只需要像写Java代码一样写指令即可。

## 举例

有如下一个类，用来做两数相加与求平方：

```java
public class SimpleMath {

    public int plus(int a, int b) {
        return a + b;
    }

    public int square(int a) {
        return a * a;
    }

}
```

此时我们可以通过以下方式来调用这两个方法：

```java
public class MainTest {
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
    }
}
```

这里就会有以下输出：

```text
plus : 5
square : 4
```

## 实现

基础实现是由Java反射提供的，在加载对象时将对象方法进行解构并适配指令执行系统，随后即可在指令执行器中调用。

1. 由 [参数解析器](https://github.com/Verlif/ParamParser) 提供参数解析方案，用于将字符串解析成方法参数对应的类实例。
2. 由 [行命令解析器](https://github.com/Verlif/cmdline-parser) 提供指令解析方案，用于解析输入的指令。
3. 由 [反射工具](https://github.com/Verlif/reflection-kit) 提供反射方法，用于做方法解析。

## 高级

当然，以上只是基础用法，实际上你可以这样使用：

```java
@SmdClass(value = "math", description = "简单的测试指令")
public class Math {

    @SmdOption(value = {"plus", "+"}, description = "两数之和")
    public int plus(
            @SmdParam(value = "a", force = false, description = "相加的第一个数字，可以为空") int a,
            @SmdParam(value = "b", description = "相加的第二个数字，默认值是3", defaultVal = "3") int b) {
        return a + b;
    }

    @SmdOption(value = {"square", "^"}, description = "做平方")
    public int square(int a) {
        return a * a;
    }

}
```

- `SmdClass` 用于说明指令对象信息
- `SmdOption` 给予指令别名与描述信息
- `SmdParam` 指定参数别名、默认值、是否必填与参数描述

并且你也可以这样使用：

```java
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
        System.out.println("基础调用: " + smdExecutor.run("math ^ 2"));
        // 输出help
        List<SmdInfo> run = (List<SmdInfo>) smdExecutor.run("help");
        for (SmdInfo smdInfo : run) {
            System.out.print(smdInfo);
        }
    }
}
```

以下是输出结果：

```text
使用指令前缀别名: 4
使用指令设定值: 3
基础调用: 4
help - 帮助指令，用于查询当前的指令信息
	--> help                	查看当前加载的指令列表
		--[group]             	指定指令指令组名
		--[key]               	指定指令名
math - 简单的测试指令
	--> plus, +             	两数之和
		--[a]                 	相加的第一个数字，可以为空
		**b                   	相加的第二个数字，默认值是3
	--> square, ^           	做平方
		--[a]                 	null
```

**help**输出比较简陋，后续可能会修改。当然，开发者也可以通过反射实现自己的**help**。

## 使用方法

1. 添加Jitpack仓库源

   last-version: [![Release](https://jitpack.io/v/Verlif/just-simmand.svg)](https://jitpack.io/#Verlif/just-simmand)

   Maven
   
   ```xml
   <repositories>
      <repository>
          <id>jitpack.io</id>
          <url>https://jitpack.io</url>
      </repository>
   </repositories>
   ```

   Gradle
   
   ```text
   allprojects {
     repositories {
         maven { url 'https://jitpack.io' }
     }
   }
   ```

2. 添加依赖

   Maven
   
   ```xml
      <dependencies>
          <dependency>
              <groupId>com.github.Verlif</groupId>
              <artifactId>just-simmand</artifactId>
              <version>last-version</version>
          </dependency>
      </dependencies>
   ```

   Gradle
   
   ```text
   dependencies {
     implementation 'com.github.Verlif:just-simmand:last-version'
   }
   ```
