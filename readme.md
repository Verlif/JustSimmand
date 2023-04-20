# JustSimmand

[中文](readme_cn.md) | English

`JustSimmand`, A simple and intuitive command system.

on the `JustSimmand`, An instance object is an instruction set, and each method of an instance object is an instruction.
No longer need to manually write call methods, just write instructions like Java code.

## Example

There is a class for adding and squaring two numbers as follows:

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

At this point we can call both methods by:

```java
public class MainTest {
    @Test
    public void simpleTest() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        // Instantiating instruction executors
        SmdExecutor smdExecutor = new SmdExecutor();
        // Load the object to the command executor and set the load mode to positive mode
        smdExecutor.add(new SimpleMath(), new LoadConfig()
                .loadMode(LoadConfig.LoadMode.POSITIVE));
        // Start execution method
        System.out.println("plus : " + smdExecutor.run("SimpleMath plus 2 3"));
        System.out.println("square : " + smdExecutor.run("SimpleMath square 2"));
    }
}
```

Here the following output will be available:

```text
plus : 5
square : 4
```

## Implement

The base implementation is provided by Java reflection, where the object methods are deconstructed and adapted to the instruction execution system when the object is loaded,
and can then be called in the instruction executor.

1. Parameter parsing scheme provided by [Parameter Parser](https://github.com/Verlif/ParamParser) for parsing strings into instances of classes corresponding to method parameters.
2. The command parsing scheme is provided by [Line Command Parser](https://github.com/Verlif/cmdline-parser) for parsing the incoming commands.
3. Reflection methods are provided by [Reflection Tools](https://github.com/Verlif/reflection-kit) for doing method resolution.

## Advanced

Of course, the above is only the basic usage, you can actually use it like this:

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

- `SmdClass` Used to describe the command object information
- `SmdOption` Giving command aliases and description information
- `SmdParam` Specify parameter alias, default value, required or not, and parameter description

And you can also use it like this:

```java
public class MainTest {

    @Test
    public void test() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        // Instantiating instruction executors
        SmdExecutor smdExecutor = new SmdExecutor();
        // Add command object
        smdExecutor.add(new Math());
        // Set the command prefix alias, here it means that typing "3" or "test" is equivalent to typing "math plus --b 4"
        // The "--b" here means that the "4" that follows is part of the "b" parameter
        smdExecutor.addPrefixReplace("math plus --b 4", "3", "test");
        // The command is called using an alias, where the default value of "0" is given because "a" is not a mandatory parameter and is a base type
        System.out.println("使用指令前缀别名: " + smdExecutor.run("3"));
        // Here again, there is no need to enter the value of "b" because it is set to a default value
        System.out.println("使用指令设定值: " + smdExecutor.run("math plus"));
        System.out.println("基础调用: " + smdExecutor.run("math ^ 2"));
        // Output the help
        List<SmdInfo> run = (List<SmdInfo>) smdExecutor.run("help");
        for (SmdInfo smdGroupInfo : run) {
            System.out.print(smdGroupInfo);
        }
    }
}
```

The following is the output:

```text
使用指令前缀别名: 4
使用指令设定值: 3
基础调用: 4
help - 帮助指令，用于查询当前的指令信息
	[ help ] --> 查看当前加载的指令列表
	--> 输入 help 来显示所有的指令，输入 help --key name 来显示name指令的信息
		[group]             	_NULL_              	指定指令指令组名
		[key]               	_NULL_              	指定指令名
math - 简单的测试指令
	[ plus, + ] --> 两数之和
		[a]                 	_NULL_              	相加的第一个数字，可以为空
		*b*                 	3                   	相加的第二个数字，默认值是3
	[ square, ^ ] --> 做平方
		[a]                 	_NULL_              	null
```

The output is rather sketchy and may be modified subsequently. Of course, developers can also implement their own **help** through reflection.

## SmdLink

SmdLink is a feature enabled for secondary development that allows users to make chain calls.

For example, using `new BlockSmdLinkParser('(', ')')` you can split `(groupA methodA paramA)(methodB paramB)(methodC paramC)` into the following parts:

1. `groupA methodA paramA`
2. `methodB paramB`
3. `methodC paramC`

At this point, the return value of the second instruction will be used as the object of the second instruction, the return value of the second instruction will be used as the specified object of the third instruction, and so on.

## Using

1. Add Jitpack repository source

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

2. Adding dependencies

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
