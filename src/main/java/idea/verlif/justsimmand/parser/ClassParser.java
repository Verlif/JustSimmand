package idea.verlif.justsimmand.parser;

import idea.verlif.justsimmand.JustSmdException;
import idea.verlif.parser.ParamParser;

public class ClassParser extends ParamParser<Class<?>> {

    @Override
    public Class<?>[] match() {
        return new Class[]{Class.class};
    }

    @Override
    protected Class<?> convert(String param) {
        try {
            return Class.forName(param);
        } catch (ClassNotFoundException e) {
            throw new JustSmdException(e);
        }
    }

}
