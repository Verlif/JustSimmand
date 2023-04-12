package idea.verlif.justsimmand;

public class MissArgException extends RuntimeException {

    public MissArgException(String key) {
        super("Missing arg - " + key);
    }
}
