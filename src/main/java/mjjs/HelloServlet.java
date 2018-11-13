package mjjs;

import java.io.*;

import java.util.Properties;
import java.util.Enumeration;

import javax.servlet.http.*;
import javax.servlet.*;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;


public class HelloServlet extends HttpServlet {
  public void doGet (HttpServletRequest req,
                     HttpServletResponse res)
    throws ServletException, IOException
  {
    PrintWriter out = res.getWriter();
    out.println("<pre>");
    out.println("<h1>Welcome to  dev envronment !</h1>");
    //out.println("");
    //out.println("Reading /application.properties ...");

    Properties prop = new Properties();
    InputStream in = getClass().getResourceAsStream("/application.properties");
    if ( in == null ) {
        out.println("Missing application.properties in the war.");
    } else {
        prop.load(in);
        in.close();
    }

    String jdbc = prop.getProperty("mjjs.datasource.url");
    String username = prop.getProperty("mjjs.datasource.username");
    String password = prop.getProperty("mjjs.datasource.password");
    String className = prop.getProperty("mjjs.datasource.driverClassName");

    if ( jdbc == null | username == null || 
        password == null || className == null ) {
        out.println("Example properties:");
        out.println("mjjs.datasource.url=jdbc:mysql://localhost:8889/mjjs");
        out.println("mjjs.datasource.username=ltiuser");
        out.println("mjjs.datasource.password=ltipassword");
        out.println("mjjs.datasource.driverClassName=com.mysql.jdbc.Driver");
        out.println("</pre>");
        out.close();
        return;
    }

    try {
        Class.forName(className);
    } catch (ClassNotFoundException e) {
        out.println("Missing JDBC Driver: "+className);
        System.out.println("Missing JDBC Driver: "+className);
        e.printStackTrace();
        return;
    }

    Connection connection = null;
    try {
        connection = DriverManager
        .getConnection(jdbc, username, password);
 
    } catch (SQLException e) {
        out.println("Your database server may be down.  Or if it is up");
        out.println("your database is missing or inaccessible.");
        out.println("");
        out.println("CREATE DATABASE mjjs DEFAULT CHARACTER SET utf8;");
        out.println("GRANT ALL ON mjjs.* TO 'mjjsuser'@'localhost' IDENTIFIED BY 'mjjspassword';");
        out.println("GRANT ALL ON mjjs.* TO 'mjjsuser'@'127.0.0.1' IDENTIFIED BY 'mjjspassword';");
        e.printStackTrace();
        return;
    }
 
    if (connection == null) {
        out.println("Connection Failed!");
        return;
    }

    Statement stmt = null;
    String query = "SELECT *  FROM deployment_history ;";

    try {
        stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        int count = 0;
	out.println("<table>");
	out.println("<tr>");
	out.println("<th>VERSION</th>");
	out.println("<th>DEPLOYED DATE</th>");
	out.println("</tr>");
	out.println("<tr>");
        while (rs.next()) {
            String date = rs.getString("date");
	    String version = rs.getString("version");
            out.println("<td>"+version+"</td>");
	    out.println("<td>"+date+"</td>");
            count++;
        }
	out.println("</tr>");
	out.println("</table>");
        out.println("Successfully read "+count+" rows from the database");
    } catch (SQLException e ) {
        out.println("Your database table is either missing or incorectly formatted");
        out.println("");
        out.println("CREATE TABLE mjjs (name TEXT) ENGINE = InnoDB DEFAULT CHARSET=utf8;");
        out.println("INSERT INTO mjjs (name) VALUES ('tsugi');");
        e.printStackTrace();
    }

    try {
        if (stmt != null) { stmt.close(); }
        connection.close();
    } catch (SQLException e ) {
        e.printStackTrace();
    }

    out.println("</pre>");
    out.close();
  }
}
