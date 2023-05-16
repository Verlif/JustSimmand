package idea.verlif.justsimmand.pretreatment;

import java.util.ArrayList;
import java.util.List;

public class PretreatmentLink {

    private final List<SmdLinePretreatment> links;

    public PretreatmentLink() {
        links = new ArrayList<>();
    }

    public String handle(String smdLine) {
        for (SmdLinePretreatment pretreatment : links) {
            smdLine = pretreatment.handle(smdLine);
            if (smdLine == null) {
                return null;
            }
        }
        return smdLine;
    }

    public void addPretreatment(SmdLinePretreatment pretreatment) {
        links.add(pretreatment);
    }

    public void addPretreatment(int index, SmdLinePretreatment pretreatment) {
        links.add(index, pretreatment);
    }

    public void clear() {
        links.clear();
    }
}
