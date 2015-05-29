package biz.a7software.slimmyinvoice.init;

import javax.imageio.ImageIO;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * The AppInit class binds a servlet listener.
 */
@WebListener
public class AppInit implements ServletContextListener {

    public void contextInitialized(ServletContextEvent sce) {
        ImageIO.scanForPlugins();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}