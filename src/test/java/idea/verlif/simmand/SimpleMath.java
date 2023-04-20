package idea.verlif.simmand;

import idea.verlif.justsimmand.anno.SmdParam;

public class SimpleMath {

    public int plus(int a, int b) {
        return a + b;
    }

    public int square(int a) {
        return a * a;
    }

    public TestB getTestB() {
        return new TestB();
    }

    public static class TestB {

        public SimpleMath say(@SmdParam(defaultVal = "hi") String words) {
            System.out.println(words);
            return new SimpleMath();
        }

        public SimpleMath getSimpleMath() {
            return new SimpleMath();
        }
    }
}
