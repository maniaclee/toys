package psyco.toy.builder;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Properties;

/**
 * Created by lipeng on 15/7/18.
 */
public class VelocityUtil {
//    static {
//        Velocity.init(properties(VelocityUtil.class.getResourceAsStream("/builder/velocity.properties")));
//    }

    public static Properties properties(InputStream inputStream) {
        assert inputStream != null;
        Properties re = new Properties();
        try {
            re.load(inputStream);
            return re;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
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

    @Test
    public void test() {
        System.out.println("sdfsd");
    }

}
