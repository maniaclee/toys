package psyco.toy.builder;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Lists;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.junit.Test;
import psyco.common.ExceptionCatcher;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by lipeng on 15/7/18.
 */
public class CodeGenerators {
    static {
        Velocity.init(VelocityUtil.properties(VelocityUtil.class.getResourceAsStream("/velocity.properties")));
    }

    public static class Result {
        public String templateClasspath;
        public Map<String, Object> map;

        public Result(String templateClasspath, Map<String, Object> map) {
            this.templateClasspath = templateClasspath;
            this.map = map;
        }
    }

    /**
     * 产生某类的Builder类
     */
    public static Function<Class<?>, Result> builder = (clzz) -> new Result("vm/Builder.vm", new HashMap() {{
        ExceptionCatcher.execVoid(() -> {
            put("BuilderClass", clzz.getSimpleName() + "Builder");
            put("TargetClass", clzz.getSimpleName());
            put("fields", Lists.newArrayList(Introspector.getBeanInfo(clzz).getPropertyDescriptors()).stream().filter(e -> !e.getPropertyType().equals(Class.class)).collect(Collectors.toList()));
        });
    }});
    public static Function<Properties, Result> properties_dot_spring = (ps) -> new Result("vm/Properties.vm", new HashMap() {{
        ExceptionCatcher.execVoid(() -> {
            put("properties", ps.keySet().stream().collect(Collectors.toMap(p -> p, p -> CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_UNDERSCORE, p.toString().replace('.', '-')))));
        });
    }});


    public static <T> String run(Function<T, Result> fn, T param) {
        VelocityContext context = new VelocityContext();
        Result re = fn.apply(param);
        re.map.forEach((k, v) -> context.put(k, v));
        return VelocityUtil.parseTemplate(context, Velocity.getTemplate(re.templateClasspath));
    }


    @Test
    public void test() throws IntrospectionException {
        Properties ps = VelocityUtil.properties(VelocityUtil.class.getResourceAsStream("/velocity.properties"));
        System.out.println(run(properties_dot_spring, ps));
//        System.out.println(run(builder, Beantest.class));
    }

}
