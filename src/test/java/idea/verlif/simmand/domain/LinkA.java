package idea.verlif.simmand.domain;

public class LinkA {

    public String say() {
        return "A";
    }

    public void param(Class<?> cl) {
        System.out.println(cl);
    }

    public LinkB b(String b) {
        System.out.println(b);
        return new LinkB();
    }

    public void hi() {
        System.out.println("hi");
    }

    public static class LinkB {

        public String say() {
            return "B";
        }

        public LinkA a(String a) {
            System.out.println(a);
            return new LinkA();
        }
    }
}
