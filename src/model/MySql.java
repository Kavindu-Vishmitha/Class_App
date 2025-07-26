
package model;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSet;

/**
 *
 * @author sanja
 */
public class MySql {

    private static Connection connection;

//    Create Connection
    public static void CreatConnection() throws Exception {

        if (connection == null) {

            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/classapp", "root", "**********");

        }

    }

//    Update
    public static ResultSet executeSearch(String query) throws Exception {

        CreatConnection();
        return connection.createStatement().executeQuery(query);

    }

//   Insert , Delete
    public static Integer executeUpdate(String query) throws Exception {

        CreatConnection();
        return connection.createStatement().executeUpdate(query);

    }

}
