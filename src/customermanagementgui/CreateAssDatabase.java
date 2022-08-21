package customermanagementgui;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateAssDatabase {

    public static void createSmtbizDB() {

        String url = "jdbc:mysql://localhost:3306/"; // 3306 is default port
        String user = "root";
        String password = ""; // The set password when MySQL is installed

        Connection con = null; // JDBC connection
        Statement stmt = null; // SQL statement object
        String query; // SQL query string
        ResultSet result = null; // results after SQL execution

        try {
            con = DriverManager.getConnection(url, user, password);//Connect to MySQL
            stmt = con.createStatement();

            query = "DROP DATABASE IF EXISTS smtbiz;";
            stmt.executeUpdate(query);// execute the SQL query
            query = "CREATE DATABASE smtbiz;";
            stmt.executeUpdate(query);
            query = "USE smtbiz;";
            stmt.executeUpdate(query);
            query = """
                CREATE TABLE customer (
                ID INTEGER,
                Name Varchar(32),
                Email Varchar(32),
                Mobile Varchar(32),
                PRIMARY KEY(ID)
                 );
                """;
            stmt.executeUpdate(query);
            //Inser default 5 customers information into the table
            query = """
                 INSERT INTO customer
                 (ID,Name,Email,Mobile)
                  VALUES
                 (111111,"John Citizen","aaaaa@gmail.com","(04)1234-5675"),
                 (222222,"Helen Jackson","bbbbb@gmail.com","(04)3829-4738"),
                 (333333,"Stephen Curry","ccccc@gmail.com","(04)9985-3728"),
                 (555555,"Klay Thompson","ddddd@gmail.com","(04)2787-3356"),
                 (666666,"Tomas Hanks","eeeee@gmail.com","(04)2887-1236");
                    """;
            stmt.executeUpdate(query);
            query = "SELECT*FROM customer;";
            result = stmt.executeQuery(query);
        } catch (SQLException ex) {
            System.out.println("SQLException in the database creation: " + ex.getMessage());
        } finally {
            // Close all database objects nicely
            try {
                if (result != null) {
                    result.close();
                }

                if (stmt != null) {
                    stmt.close();
                }

                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                System.out.println("SQLException caught: " + ex.getMessage());
            }
        }

    }
}
