import com.google.common.base.Supplier;
import groovy.lang.GroovyClassLoader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 */
public class MirrorServlet extends HttpServlet {


    private final Supplier<String> sourceCodeSupplier;
    private final GroovyClassLoader groovyClassLoader;

    public MirrorServlet(Supplier<String> sourceCodeSupplier, GroovyClassLoader groovyClassLoader) {
        this.sourceCodeSupplier = sourceCodeSupplier;
        this.groovyClassLoader = groovyClassLoader;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {


        Class aClass = groovyClassLoader.parseClass(sourceCodeSupplier.get());
        if (StringMirror.class.isAssignableFrom(aClass)) {
            try {
                StringMirror cs = (StringMirror) aClass.newInstance();
                String res = cs.mirror(req.getParameter("input"));
                resp.setStatus(HttpServletResponse.SC_OK);
                PrintWriter writer = resp.getWriter();
                writer.write(res);
                writer.flush();
                writer.close();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }
}
