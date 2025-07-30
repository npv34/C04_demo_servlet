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
            case "/create":
                showCreateStudentPage(req, resp);
                break;
            case "/edit":
                showEditStudentPage(req, resp);
                break;
            case "/search":
                searchStudent(req, resp);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getPathInfo();
        if (uri == null) {
            uri = "";
        }

        switch (uri){
            case "/store":
                storeStudent(req, resp);
                break;
            case "/edit":
                editStudent(req, resp);
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

    public void showCreateStudentPage(HttpServletRequest request, HttpServletResponse response) {
        try {
            RequestDispatcher requestDispatcher = request.getRequestDispatcher("/views/students/create.jsp");
            requestDispatcher.forward(request, response);
        } catch (ServletException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void storeStudent(HttpServletRequest request, HttpServletResponse response) {
       try {
           String name = request.getParameter("name");
           int gender = Integer.parseInt(request.getParameter("gender"));
           String email = request.getParameter("email");
           String phone = request.getParameter("phone");
           Student newStudent = new Student(name, gender, email, phone);

           String sql = "INSERT INTO students(name, gender, email, phone) VALUE (?,?,?,?)";
           PreparedStatement preparedStatement = conn.prepareStatement(sql);
           preparedStatement.setString(1, newStudent.getName());
           preparedStatement.setInt(2, newStudent.getGender());
           preparedStatement.setString(3, newStudent.getEmail());
           preparedStatement.setString(4, newStudent.getPhone());
           preparedStatement.execute();

           response.sendRedirect("/students");
       } catch (IOException | SQLException e) {
           throw new RuntimeException(e);
       }

    }

    public void editStudent(HttpServletRequest request, HttpServletResponse response) {
        try {
            int id = Integer.parseInt(request.getParameter("id"));

            // lay student theo id
            Student studentEdit = this.findStudentById(id);
            if (studentEdit == null) {
                // cho ve 404
                return;
            }

            String name = request.getParameter("name");
            int gender = Integer.parseInt(request.getParameter("gender"));
            String email = request.getParameter("email");
            String phone = request.getParameter("phone");
            Student newStudent = new Student(name, gender, email, phone);


            String sql = "UPDATE students SET name = ?, gender =?, email = ?, phone = ? WHERE id = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, newStudent.getName());
            preparedStatement.setInt(2, newStudent.getGender());
            preparedStatement.setString(3, newStudent.getEmail());
            preparedStatement.setString(4, newStudent.getPhone());
            preparedStatement.setInt(5, studentEdit.getId());
            preparedStatement.execute();

            response.sendRedirect("/students");

        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Student findStudentById(int id) throws SQLException {
        String sql = "SELECT * FROM students WHERE id = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();
        Student s = null;
        while (resultSet.next()) {
            int idStudent = resultSet.getInt("id");
            String name = resultSet.getString("name");
            int gender = resultSet.getInt("gender");
            String email = resultSet.getString("email");
            String phone = resultSet.getString("phone");
            s = new Student(id, name, gender, email, phone);
        }
        return s;
    }

    public void showEditStudentPage(HttpServletRequest request, HttpServletResponse response) {
        try {
            int id = Integer.parseInt(request.getParameter("id"));

            // lay student theo id
            Student studentEdit = this.findStudentById(id);
            if (studentEdit == null) {
                // cho ve 404
                return;
            }

            // return view edit
            request.setAttribute("studentEdit", studentEdit);
            RequestDispatcher requestDispatcher = request.getRequestDispatcher("/views/students/edit.jsp");
            requestDispatcher.forward(request, response);
        } catch (SQLException | ServletException | IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void searchStudent(HttpServletRequest request, HttpServletResponse response) {
        try {
            String keyword = request.getParameter("keyword");
            String sql = "SELECT * FROM students WHERE name LIKE ? OR email LIKE ? ";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, '%' + keyword + "%");
            statement.setString(2, "%" + keyword + "%");
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
        } catch (SQLException | IOException | ServletException e) {
            throw new RuntimeException(e);
        }
    }
}
