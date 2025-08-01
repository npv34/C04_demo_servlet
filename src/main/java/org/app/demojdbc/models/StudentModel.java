package org.app.demojdbc.models;

import org.app.demojdbc.entities.Group;
import org.app.demojdbc.entities.Student;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StudentModel extends BaseModel{
    public List<Student> getAllStudent() throws SQLException {
        String sql = "SELECT `groups`.name as `group_name`, student_info.*\n" +
                "FROM `groups`\n" +
                "JOIN (\n" +
                "\tSELECT s.*, sub.name as `subject_name`\n" +
                "\tFROM students s\n" +
                "\tLEFT JOIN subject_student subs\n" +
                "\tON s.id = subs.student_id\n" +
                "\tLEFT JOIN subjects sub\n" +
                "\tON sub.id = subs.subject_id\n" +
                ") as `student_info`\n" +
                "ON `groups`.id = `student_info`.group_id";
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


        return  list;
    }

    public void deleteByID(int id) throws SQLException {
        String sql = "DELETE FROM students WHERE id = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        // gan du lieu vao tham so cua preparedStatement
        preparedStatement.setInt(1, id);
        preparedStatement.execute();
    }

    public void createStudent(Student newStudent, int groupID) throws SQLException {
        String sql = "INSERT INTO students(name, gender, email, phone, group_id) VALUE (?,?,?,?,?)";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, newStudent.getName());
        preparedStatement.setInt(2, newStudent.getGender());
        preparedStatement.setString(3, newStudent.getEmail());
        preparedStatement.setString(4, newStudent.getPhone());
        preparedStatement.setInt(5, groupID);
        preparedStatement.execute();
    }

    public void editStudent(Student newStudent, int studentId, String groupId) throws SQLException {
        String sql = "UPDATE students SET name = ?, gender =?, email = ?, phone = ?, group_id = ? WHERE id = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, newStudent.getName());
        preparedStatement.setInt(2, newStudent.getGender());
        preparedStatement.setString(3, newStudent.getEmail());
        preparedStatement.setString(4, newStudent.getPhone());
        preparedStatement.setString(5, groupId);
        preparedStatement.setInt(6, studentId);
        preparedStatement.execute();
    }

    public Student findById(int id) throws SQLException {
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
}
