package org.app.demojdbc.models;

import org.app.demojdbc.entities.Group;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GroupModel extends BaseModel{

    public List<Group> getAll() throws SQLException {
        List<Group> list = new ArrayList<>();
        String sql = "SELECT * FROM `groups`";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");

            Group g = new Group(id, name);
            list.add(g);
        }
        return list;
    }
}
