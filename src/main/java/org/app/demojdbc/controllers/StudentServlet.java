package org.app.demojdbc.controllers;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.app.demojdbc.database.DBConnect;
import org.app.demojdbc.entities.Group;
import org.app.demojdbc.entities.Student;
import org.app.demojdbc.models.GroupModel;
import org.app.demojdbc.models.StudentModel;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@WebServlet(name = "StudentServet", urlPatterns = {"/students/*"})
public class StudentServlet extends HttpServlet {
    Connection conn = null;
    StudentModel studentModel;
    GroupModel groupModel;
    @Override
    public void init() throws ServletException {
        DBConnect dbConnect = new DBConnect();
        conn = dbConnect.getConnect();

        studentModel = new StudentModel();
        groupModel = new GroupModel();
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
            List<Student> list = studentModel.getAllStudent();
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
             studentModel.deleteByID(Integer.parseInt(id));
            // quay lai trang /students
            response.sendRedirect("/students");
        }catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void showCreateStudentPage(HttpServletRequest request, HttpServletResponse response) {
        try {
            List<Group> listGroup = groupModel.getAll();
            request.setAttribute("listGroup", listGroup);
            RequestDispatcher requestDispatcher = request.getRequestDispatcher("/views/students/create.jsp");
            requestDispatcher.forward(request, response);
        } catch (ServletException | IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void storeStudent(HttpServletRequest request, HttpServletResponse response) {
       try {
           String name = request.getParameter("name");
           int gender = Integer.parseInt(request.getParameter("gender"));
           String email = request.getParameter("email");
           String phone = request.getParameter("phone");
           int groupID = Integer.parseInt(request.getParameter("group_id"));
           Student newStudent = new Student(name, gender, email, phone);

           studentModel.createStudent(newStudent, groupID);

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
            String groupId = request.getParameter("group_id");
            Student newStudent = new Student(name, gender, email, phone);

            studentModel.editStudent(newStudent, studentEdit.getId(), groupId);
            response.sendRedirect("/students");

        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Student findStudentById(int id) throws SQLException {
        return studentModel.findById(id);
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

            List<Group> listGroup = getAllGroup();

            // return view edit
            request.setAttribute("studentEdit", studentEdit);
            request.setAttribute("listGroup", listGroup);
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

    public List<Group> getAllGroup() {
        try {
            return groupModel.getAll();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
