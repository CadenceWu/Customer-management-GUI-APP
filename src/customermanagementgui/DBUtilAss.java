package customermanagementgui;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
//Controller
//DBUtil class provides general database utility methods

public class DBUtilAss {

    private static final String URL_DB = "jdbc:mysql://localhost:3306/smtbiz";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private static Connection con = null;

    public static void connectDatabase() {
        try {
            con = DriverManager.getConnection(URL_DB, USER, PASSWORD);
            //Use the commit() method for database transaction integrity 
            //Set the AutoCommit to false first.
            con.setAutoCommit(false);
        } catch (SQLException ex) {
            System.out.println("SQLException on database connection: " + ex.getMessage());
        }
    }

    public static void closeDatabase() {
        try {
            if (con != null && !con.isClosed()) {
                con.close();
            }
        } catch (SQLException ex) {
            System.out.println("SQLException on database close: " + ex.getMessage());
        }
    }

    public static ResultSet executeQuery(String queryStmt) {
        Statement stmt = null;
        ResultSet resultSet = null;
        CachedRowSet crs = null;

        try {
            connectDatabase();
            stmt = con.createStatement();
            resultSet = stmt.executeQuery(queryStmt);
            crs = RowSetProvider.newFactory().createCachedRowSet();
            crs.populate(resultSet);
        } catch (SQLException ex) {
            System.out.println("SQLException on executeQuery: " + ex.getMessage());
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                closeDatabase();
            } catch (SQLException ex) {
                System.out.println("SQLException caught on database closing: " + ex.getMessage());
            }
        }
        return crs;
    }

    public static int executeUpdate(String sqlStmt) {
        Statement stmt = null;
        int count;

        try {
            connectDatabase();
            stmt = con.createStatement();
            count = stmt.executeUpdate(sqlStmt);
            con.commit();
            return count;
        } catch (SQLException ex) {
            System.out.println("SQLException on executeUpdate: " + ex.getMessage());
            return 0;
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                closeDatabase();
            } catch (SQLException ex) {
                System.out.println("SQLException caught on database closing: " + ex.getMessage());
            }
        }
    }
}
