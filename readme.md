# JustSimmand

指令生成器

将任意的Java对象的方法转换成可以通过指令来执行的指令。  
原理：
1. 通过反射获取对象的公开方法
2. 将允许的方法转化为指令，也就是一个方法会转换成一条指令
3. 指令参数由方法参数生成，使用了 [参数解析器](https://github.com/Verlif/ParamParser) 将指令文本转换成方法参数对象
4. 使用`SimmandManager`来添加或是执行指令

## 使用方法

1. 添加Jitpack仓库源

   [![Release](https://jitpack.io/v/Verlif/just-simmand.svg)](https://jitpack.io/#Verlif/just-simmand)

   1. maven
   
   ```xml
   <repositories>
      <repository>
          <id>jitpack.io</id>
          <url>https://jitpack.io</url>
      </repository>
   </repositories>
   ```

   2. Gradle
   
   ```text
   allprojects {
     repositories {
         maven { url 'https://jitpack.io' }
     }
   }
   ```

2. 添加依赖

   1. maven
   
   ```xml
      <dependencies>
          <dependency>
              <groupId>com.github.Verlif</groupId>
              <artifactId>just-simmand</artifactId>
              <version>最新版本号</version>
          </dependency>
      </dependencies>
   ```

   2. Gradle
   
   ```text
   dependencies {
     implementation 'com.github.Verlif:just-simmand:最新版本号'
   }
   ```

3. 使用
```java
// 获取指令管理器对象
SimmandManager manager = new SimmandManager();
// 向管理器中添加指令对象
manager.add(new Test());
// 执行指令
System.out.println(manager.run("say1 --world Verlif"));
System.out.println(manager.run("plus 13 22"));

// Test类
public class Test {

    public String hello() {
        return "hello";
    }

    @SimmOption({"say1", "say2"})
    public String helloSomething(@SimmParam(defaultVal = "world") String word) {
        return hello() + " " + word;
    }

    public BigInteger plus(int a, @SimmParam(defaultVal = "100") int b) {
        return BigInteger.valueOf(a + b);
    }

}
```

运行结果

```text
hello Verlif
35
```

## 注意

如果方法中使用了 [参数解析器](https://github.com/Verlif/ParamParser) 中未定义的对象时，会抛出`IllegalArgumentException`异常，请使用以下方式添加对应解析器：

```java
// 获取指令管理器中的参数解析器
ParamParserService pps = manager.getParamParserService();
// 添加自己的参数解析器（在添加指令前使用，对之前添加的指令无效）
pps.addOrReplace(new MyParser());

// 自定义解析器
public final class MyParser implements ParamParser<Test> {

    /**
     * 将指令参数文本转换为方法对象
     * 
     * @param s 参数文本值
     * @return 转换成的方法对象
     */
    @Override
    public Test parser(String s) {
        return new Test(s);
    }
}
```