package org.app.demojdbc;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.app.demojdbc.database.DBConnect;
import org.app.demojdbc.entities.Student;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "StudentServet", urlPatterns = {"/students/*"})
public class StudentServlet extends HttpServlet {
    Connection conn = null;
    @Override
    public void init() throws ServletException {
        DBConnect dbConnect = new DBConnect();
        conn = dbConnect.getConnect();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getPathInfo();
        if (uri == null) {
            uri = "";
        }

        switch (uri) {
            case "/":
            case "":
                showListUserPage(req, resp);
                break;
            case "/delete":
                deleteStudent(req, resp);
                break;
        }
    }

    public void showListUserPage(HttpServletRequest request,
                                 HttpServletResponse response) throws ServletException {
        // get data
        try {
            String sql = "SELECT * FROM students";
            PreparedStatement statement = conn.prepareStatement(sql);

            // doi voi cau len select thi tra ve 1 doi tuong ResultSet
            ResultSet resultSet = statement.executeQuery();
            // get data tu resultSet
            List<Student> list = new ArrayList<>();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int gender = resultSet.getInt("gender");
                String email = resultSet.getString("email");
                String phone = resultSet.getString("phone");

                Student student = new Student(id, name, gender, email, phone);
                list.add(student);
            }
            request.setAttribute("listStudent", list);
            RequestDispatcher requestDispatcher = request.getRequestDispatcher("/views/students/list.jsp");
            requestDispatcher.forward(request, response);
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void deleteStudent(HttpServletRequest request, HttpServletResponse response) {
        // get id tu req
        String id = request.getParameter("id");
        try {
            String sql = "DELETE FROM students WHERE id = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            // gan du lieu vao tham so cua preparedStatement
            preparedStatement.setInt(1, Integer.parseInt(id));
            preparedStatement.execute();

            // quay lai trang /students
            response.sendRedirect("/students");
        }catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }

    }
}
