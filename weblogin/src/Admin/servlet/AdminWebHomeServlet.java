package Admin.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;


/**
 * Displays the administrator home page after authentication.
 *
 * @author Yutong Yao
 * @version 2.0
 */
public class AdminWebHomeServlet extends HttpServlet {
    /**
     * Forwards authenticated administrators to the home JSP.
     *
     * @param req current HTTP request
     * @param resp current HTTP response
     * @throws ServletException if forwarding to the JSP fails
     * @throws IOException if the response cannot be written
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!AdminWebAuthGuard.ensureAuthenticated(req, resp)) {
            return;
        }

        req.getRequestDispatcher("/jsp/admin/home.jsp").forward(req, resp);
    }
}

