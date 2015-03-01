import com.google.common.base.Joiner;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.base.Throwables;
import com.google.common.io.Files;
import groovy.lang.GroovyClassLoader;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 */
public class Main {

    public static void main(String[] args) throws Exception {
        Server server = new Server(23000);
        ServletContextHandler handler = new ServletContextHandler();


        String filename = args[0];
        Supplier<String> sourceCodeSupplier = Suppliers.memoizeWithExpiration(new FileSupplier(filename), 30, TimeUnit.SECONDS);
        GroovyClassLoader groovyClassLoader = new GroovyClassLoader(ClassLoader.getSystemClassLoader());

        ServletHolder servlet = new ServletHolder(new MirrorServlet(sourceCodeSupplier, groovyClassLoader));
        handler.addServlet(servlet, "/mirror");
        server.setHandler(handler);


        server.start();
    }

    private static class FileSupplier implements Supplier<String> {
        private final String filename;

        private FileSupplier(String filename) {
            this.filename = filename;
        }

        @Override
        public String get() {
            try {
                List<String> strings = Files.readLines(new File(filename), StandardCharsets.UTF_8);
                return Joiner.on('\n').join(strings);
            } catch (IOException e) {
                throw Throwables.propagate(e);
            }
        }
    }
}
