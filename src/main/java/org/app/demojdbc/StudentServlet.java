package org.app.demojdbc;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.app.demojdbc.database.DBConnect;
import org.app.demojdbc.entities.Group;
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
            String sql = "SELECT students.*, `groups`.name as 'group_name' FROM students\n" +
                        "JOIN `groups` ON students.group_id = `groups`.id";
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

                int groupId = resultSet.getInt("group_id");
                String groupName = resultSet.getString("group_name");
                Group group = new Group(groupId, groupName);

                student.setGroup(group);
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
            String groupId = request.getParameter("group_id");
            Student newStudent = new Student(name, gender, email, phone);


            String sql = "UPDATE students SET name = ?, gender =?, email = ?, phone = ?, group_id = ? WHERE id = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, newStudent.getName());
            preparedStatement.setInt(2, newStudent.getGender());
            preparedStatement.setString(3, newStudent.getEmail());
            preparedStatement.setString(4, newStudent.getPhone());
            preparedStatement.setString(5, groupId);
            preparedStatement.setInt(6, studentEdit.getId());
            preparedStatement.execute();

            response.sendRedirect("/students");

        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Student findStudentById(int id) throws SQLException {
        String sql = "SELECT students.*, `groups`.name as 'group_name' FROM students\n" +
                "JOIN `groups` ON students.group_id = `groups`.id WHERE students.id = ?";
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
            int groupId = resultSet.getInt("group_id");
            String groupName = resultSet.getString("group_name");

            Group group = new Group(groupId, groupName);

            s = new Student(id, name, gender, email, phone);
            s.setGroup(group);

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
        List<Group> list = new ArrayList<>();
        try {
            String sql = "SELECT * FROM `groups`";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");

                Group g = new Group(id, name);
                list.add(g);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }
}
