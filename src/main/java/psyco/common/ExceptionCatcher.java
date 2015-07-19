package psyco.common;

/**
 * Created by lipeng on 15/7/19.
 */
public class ExceptionCatcher {

    public static interface Executable {
        void run() throws Exception;
    }

    public static void execVoid(Executable consumer) {
        try {
            consumer.run();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
