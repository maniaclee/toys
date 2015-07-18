package psyco.toy.builder;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.StringWriter;

/**
 * Created by lipeng on 15/7/18.
 */
public class BuilderUtil {
    static {
        Velocity.init("src/psyco/toy/builder/velocity.properties");
    }

    public static String parseTemplate(VelocityContext context, Template template) {
        StringWriter sw = new StringWriter();
        template.merge(context, sw);
        sw.flush();
        return sw.toString();
    }

    public String run() {
        VelocityContext context = new VelocityContext();
        context.put("title", "HelloWorld");
        context.put("author", "arthinking");
        return parseTemplate(context, Velocity.getTemplate("helloworld.vm"));
    }

}
