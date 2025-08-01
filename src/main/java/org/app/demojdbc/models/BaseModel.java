package org.app.demojdbc.models;

import org.app.demojdbc.database.DBConnect;

import java.sql.Connection;

public class BaseModel {
    protected Connection conn = null;

    public BaseModel() {
        DBConnect dbConnect = new DBConnect();
        conn = dbConnect.getConnect();
    }
}
