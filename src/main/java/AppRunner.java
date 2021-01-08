import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

import javax.servlet.ServletException;
import java.io.File;

/**
 * @author musa.khan
 * @since 29/12/2020
 */
public class AppRunner {

    public static void main(String[] args) throws ServletException, LifecycleException {
        runTomcat();
    }

    private static void runTomcat() throws ServletException, LifecycleException {
        String contextPath = "";
        String webappDir = new File("src/main/webapp/").getAbsolutePath();

        Tomcat tomcat = new Tomcat();
        tomcat.setBaseDir("temp");
        tomcat.setPort(7000);
        tomcat.addWebapp(contextPath, webappDir);

        tomcat.start();
        tomcat.getServer().await();
    }
}