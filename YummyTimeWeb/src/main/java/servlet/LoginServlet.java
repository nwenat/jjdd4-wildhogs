package servlet;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.infoshareacademy.jjdd4.wildhogs.data.User;
import dao.UsersDao;
import googleApi.IdTokenVerifierAndParser;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {


    @Inject
    UsersDao usersDao;
//    docker run --detach --name=test-db --env="MYSQL_ROOT_PASSWORD=pass123"--env="TZ=Europe/Warsaw" --publish 6603:3306 mysql

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("text/html");

        try {
            String idToken = req.getParameter("id_token");
            GoogleIdToken.Payload payLoad = IdTokenVerifierAndParser.getPayload(idToken);
            String name = (String) payLoad.get("name");
            String email = payLoad.getEmail();
            System.out.println("User name: " + name);
            System.out.println("User email: " + email);

            HttpSession session = req.getSession(true);
            session.setAttribute("username", name);
            session.setAttribute("logged", true);

//            List<User> users = (ArrayList) session.getAttribute("users-list");
//            if (users.isEmpty()) {
//                session.setAttribute("users-list", new ArrayList<>());
//            }
//            users.add(new User(idToken, name, email));
            usersDao.save(new User(idToken, name, email));

            resp.sendRedirect("/welcome");
//            req.getServletContext()
//                    .getRequestDispatcher().forward(req, resp);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}