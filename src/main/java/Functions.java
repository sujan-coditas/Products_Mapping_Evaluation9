
import entities.Product;
import helper.FactoryProvider;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.Query;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;

import entities.Customer;


@WebServlet("/")
public class Functions extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getServletPath();

        switch (action) {
            case "/UserRegistration":
                UserRegistration(req, resp);
                break;
            case "/loginServ":
                UserLogin(req, resp);
                break;
            case "/UserDisplay":
                UserDisplay(req, resp);
                break;
            case "/UserDelete":
                UserDelete(req, resp);
                break;
            case "/UserEdit":
                UserEdit(req, resp);
                break;
            case "/AddProduct":
                AddProduct(req, resp);
                break;
            case "/ShowProduct":
                ShowProduct(req, resp);
                break;
            case "/GenerateBill":
                GenerateBill(req, resp);
                break;
            case "/LogoutServ":
                LogoutServ(req, resp);
                break;

        }
    }

    private void LogoutServ(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        HttpSession hs = req.getSession(false);

        String email = (String) session.getAttribute("email");

        session.invalidate();
        hs.invalidate();
        resp.sendRedirect("login.jsp");
    }

    private void GenerateBill(HttpServletRequest request, HttpServletResponse response) throws IOException {


        int productPrice = Integer.parseInt(request.getParameter("productPrice"));
        int productQuantity = Integer.parseInt(request.getParameter("productQuantity"));

        // Calculate the total bill
        double totalBill = productPrice * productQuantity;

        // Store the total bill in the session
        HttpSession session = request.getSession(false);
        session.setAttribute("totalBill", totalBill);
        // Set the response type to HTML
        response.setContentType("text/html");

        // Get the PrintWriter object to write response
        PrintWriter out = response.getWriter();

        // Write the HTML response
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Bill Generation</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h2>Bill Generation</h2>");
        out.println("<h3>Total Bill: " + totalBill + "</h3>");
        out.println("<form action='deleteProduct' method='post'>");
        out.println("<input type='submit' value='Delete Product'>");
        out.println("</form>");
        out.println("</body>");
        out.println("</html>");
    }

    private void ShowProduct(HttpServletRequest request, HttpServletResponse response) throws IOException {

        PrintWriter out = response.getWriter();
        response.setContentType("text/html");
        Session session = FactoryProvider.getFactory().openSession();
        Transaction tx = session.beginTransaction();
        HttpSession hs = request.getSession(false);
        String email = (String) hs.getAttribute("email");

        Query query = session.createQuery("FROM Product ");

        List<Product> list = query.getResultList();

        out.println("<html>"); // Added opening <html> tag
        out.println("<head>");
        out.println("<title>Product Details</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h1>Product Details</h1>");
        out.println("<table>");
        out.println("<tr>");
        out.println("<th>ID</th>");
        out.println("<th> Name</th>");
        out.println("<th>Price</th>");
        out.println("<th>Quantity</th>");

        out.println("</tr>");

        for (Product product : list) {
            out.println("<tr>");
            out.println("<td>" + product.getPid() + "</td>");
            out.println("<td>" + product.getPname() + "</td>");
            out.println("<td>" + product.getPprice() + "</td>");
            out.println("<td>" + product.getQuantity() + "</td>");

            out.println("</tr>");
        }

        out.println("</table>");
        out.println("<a href='LogoutServ' class='btn btn-danger'>Logout</a>"); // Logout button
        out.println("<td> <a href=showProduct>Back</td>");
        out.println("<td> <a href=GenerateBill>Generate Bill</td>");

        out.println("</body>");
        out.println("</html>"); // Added closing </html> tag

        tx.commit();
        session.close();

    }

    private void AddProduct(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        PrintWriter out=response.getWriter();
        response.setContentType("text/html");
        Session session = FactoryProvider.getFactory().openSession();
        Transaction tx = session.beginTransaction();

        String pname = request.getParameter("productName");
        int pprice = Integer.parseInt(request.getParameter("productPrice"));
        int pcount = Integer.parseInt(request.getParameter("productQuantity"));

        HttpSession hs1= request.getSession();
        hs1.setAttribute("price",pprice);
        hs1.setAttribute("pcount",pcount);

        Product p = new Product();
        p.setPname(pname);
        p.setPprice(pprice);
        p.setQuantity(pcount);
        session.save(p);
        tx.commit();
        session.close();

        out.println("<h6 style='color: green;'>Added Successfully</h6>");
        RequestDispatcher rd = request.getRequestDispatcher("showProduct.jsp");
        rd.forward(request, response);

    }



    protected void UserRegistration(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("text/html");
        Session session = FactoryProvider.getFactory().openSession();
        Transaction tx = session.beginTransaction();

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");


        Customer ci = new Customer();
        ci.setEmail(email);
        ci.setPassword(password);
        ci.setName(name);

        session.save(ci);
        tx.commit();
        session.close();

        out.println("<h6 style='color: green;'>Registered Successfully</h6>");
        RequestDispatcher rd = request.getRequestDispatcher("login.jsp");
        rd.include(request, response);
        out.println("<meta http-equiv='refresh' content='2;url=registration.jsp'>");

    }

    //user Login verification

    protected void UserLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("text/html");
        HttpSession UserLoginSession= request.getSession();
        HttpSession adminLoginSession= request.getSession();

        Session session = FactoryProvider.getFactory().openSession();
        Transaction tx = session.beginTransaction();

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if(email.equals("admin@gmail.com") && password.equals("admin"))
        {
            adminLoginSession.setAttribute("AdminLoginSession",email);
            response.sendRedirect("showAdmin.jsp");
        }
        else {


            Query query = session.createQuery("FROM Customer WHERE email = '" + email + "' and password = '" + password + "'");

            List<Customer> list = query.getResultList();
            if (list.size() > 0) {

                UserLoginSession.setAttribute("UserLoginSession", email);
                out.println("<h6 style='color: green;'>Login Successful</h6>");
                RequestDispatcher rd = request.getRequestDispatcher("showProduct.jsp");
                rd.forward(request, response);

            } else {
                out.println("<h6 style='color: red;'>Login failed ! </h6>");
                RequestDispatcher rd = request.getRequestDispatcher("login.jsp");
                rd.include(request, response);
            }

            tx.commit();
            session.close();
        }

    }
    // User Data Display
    protected void UserDisplay(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("text/html");
        Session session = FactoryProvider.getFactory().openSession();
        Transaction tx = session.beginTransaction();
        HttpSession hs = request.getSession(false);

        Query query = session.createQuery("FROM Customer ");

        List<Customer> list = query.getResultList();

        out.println("<html>"); // Added opening <html> tag
        out.println("<head>");
        out.println("<title>User Details</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h1>User Details</h1>");
        out.println("<table>");
        out.println("<tr>");
        out.println("<th>ID</th>");
        out.println("<th> Name</th>");
        out.println("<th>Email</th>");

        out.println("</tr>");

        for (Customer user : list) {
            out.println("<tr>");
            out.println("<td>" + user.getCid() + "</td>");
            out.println("<td>" + user.getName() + "</td>");
            out.println("<td>" + user.getEmail() + "</td>");
            out.println("<td> <a href=UserDelete>Delete</td>");
            out.println("<td> <a href=UserEdit>Edit</td>");
            out.println("</tr>");
        }

        out.println("</table>");
        out.println("<a href='LogoutServ' class='btn btn-danger'>Logout</a>"); // Logout button

        out.println("</body>");
        out.println("</html>"); // Added closing </html> tag

        tx.commit();
        session.close();


    }

    protected void UserDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out=resp.getWriter();
        resp.setContentType("text/html");
        try{
            HttpSession hs = req.getSession(false);
            String email = (String) hs.getAttribute("email");
            Session s= FactoryProvider.getFactory().openSession();
            Transaction tx=s.beginTransaction();
            Customer todo=s.get(Customer.class,email);
            s.delete(todo);
            tx.commit();
            s.close();
//            resp.sendRedirect("showNotes.jsp");

            out.println("<h6 style='color: red;'>Data Deleted Successfully</h6>");
            RequestDispatcher rd= req.getRequestDispatcher("showAdmin.jsp");
            rd.include(req,resp);

        } catch (Exception e){
            e.printStackTrace();
        }
    }
    protected void UserEdit(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out=resp.getWriter();
        resp.setContentType("text/html");
        try{
            HttpSession hs = request.getSession(false);
            String email = (String) hs.getAttribute("email");


            Session s=FactoryProvider.getFactory().openSession();
            Transaction tx=s.beginTransaction();

            Customer note=s.get(Customer.class, email);


            tx.commit();
            s.close();

            out.println("<h6 style='color: blue;'>Data updated Successfully</h6>");
            RequestDispatcher rd= request.getRequestDispatcher("showNotes.jsp");
            rd.include(request,resp);

            out.println("<meta http-equiv='refresh' content='3;url=showNotes.jsp'>");

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}






