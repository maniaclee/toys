package psyco.toy.builder;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Lists;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import psyco.common.ExceptionCatcher;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.net.URI;
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

    private static Logger logger = LoggerFactory.getLogger(CodeGenerators.class);

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
    public static Function<String, Result> urlParameter = (url) -> new Result("vm/url2java.vm", new HashMap() {{
        ExceptionCatcher.execVoid(() -> {
            put("nameValuePairs", URLEncodedUtils.parse(new URI(url), "UTF-8").stream().sorted((e1, e2) -> e1.getName().compareTo(e2.getName())).map(nameValuePair -> {
                String key = nameValuePair.getName();
                String value = nameValuePair.getValue();
                String type = "String";
                if (value.contains("{")) {
                    type = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, key);
                    value = String.format("JSON.parseObject(\"%s\",%s.class)", value.replace("\"", "\\\""), type);
                } else if (value.matches("\\d+")) {
                    type = "long";
                    value += "l";
                } else {
                    value = "\"" + value + "\"";
                }

                return new BasicNameValuePair(String.format("%32s %-32s", type, key), value);
            }).collect(Collectors.toList()));
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
//        Properties ps = VelocityUtil.properties(VelocityUtil.class.getResourceAsStream("/velocity.properties"));
//        System.out.println(run(properties_dot_spring, ps));


        String s = "http://10.128.240.60/m.api?_aid=2&_sig=W8%2BQy05ifZxFCfM5SvNgoXQrjOylD8r%2FZYLyYLW93q20U1CBVnaDbbXok5g4lyaap7EMjHwibiNmD5Iqjm5vatjsQRmEtr%2BpghwzfWahnjyp4isthGyls5Z4HlWl2252iW41zECWnwJCq58QsD6Jao0ez%2FROco0L%2BmfT8wcItwg%3D&_tk=DKtdQZNOONOUYn2artsas0seI37seYnPgtx00uvGb8vjOgChvxprV8wttvd%2BAtzI4QbHFS83g64oGDIz6kwwWNv0LXiYAlz3fNaINzNPfcuvibssjRN4766BeR3H8PPwovBGLg7EvUQrmCqY7spDXU4z53FVsvJjcl2VsQ0DAUiSWyaYg2pcxEjn1zmtE0xZXwU30B9THmmB%2BV9BaHmrxjqdCR%2BP%2B8ET%2Bt2LR6QR3%2BmrTEQp6b9aQQzikUzIUW3kzRSv2OfjBJpgAmlpPW%2BHx%2FDrBhQSAVeIU0ta4lzITsDMHAoFm354zmSLWZeVAh0Cws2cH4RHY8kzYGigw993%2FAyeIP47tnoUY6bcnCJ8q78REHoZtL%2F6j51EZMhV4PCW&_vc=20400&_sm=rsa&_did=285199817561&_uid=1583990306&_chl=AppStore&submitChiefComplaintExtendParam=%7B%22isNewer%22%3Atrue%2C%22price%22%3A0%2C%22onlyUpdatePhone%22%3Afalse%2C%22needPayfor%22%3Atrue%2C%22isLocated%22%3Afalse%7D&chiefComplaintParam=%7B%22description%22%3A%221231313231232131313%22%2C%22age%22%3A360%2C%22gender%22%3A1%2C%22patientName%22%3A%22%22%2C%22dnaReportId%22%3A0%2C%22isDiagnosed%22%3A0%2C%22patientUserId%22%3A1583990306%7D&doctorId=965140506&_mt=trademanage.submitChiefComplaintExternal&_ft=json&";
        System.out.println(run(urlParameter, s));
    }

    @Test
    public void testXXX() throws Exception {
    }

}
