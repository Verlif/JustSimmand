package idea.verlif.justsimmand;

public class NoSuchGroupException extends RuntimeException {

    public NoSuchGroupException(String group) {
        super(group);
    }
}
