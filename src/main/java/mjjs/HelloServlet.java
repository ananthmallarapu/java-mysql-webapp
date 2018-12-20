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
    out.println("<h1>new feature got added with updated version v8 !</h1>");
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

    String jdbc = prop.getProperty("webapp.datasource.url");
    String username = prop.getProperty("webapp.datasource.username");
    String password = prop.getProperty("webapp.datasource.password");
    String className = prop.getProperty("webapp.datasource.driverClassName");
    String deployment_version=prop.getProperty("webapp.datasource.deployment_version");
    String deployment_date=prop.getProperty("webapp.datasource.deployment_date");
    if ( jdbc == null | username == null || 
        password == null || className == null ) {
        out.println("Example properties:");
        out.println("webapp.datasource.url=jdbc:mysql://localhost:3306/sample_app");
        out.println("webapp.datasource.username=sampleuser");
        out.println("webapp.datasource.password=samplepassword");
        out.println("webapp.datasource.driverClassName=com.mysql.jdbc.Driver");
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
        out.println("CREATE DATABASE sample_app DEFAULT CHARACTER SET utf8;");
        out.println("GRANT ALL ON sample_app.* TO 'sampleuser'@'localhost' IDENTIFIED BY 'samplepassword';");
        out.println("GRANT ALL ON sample_app.* TO 'sampleuser'@'127.0.0.1' IDENTIFIED BY 'samplepassword';");
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
	out.println("<p>current deployment version "+deployment_version+"deployed on "+deployment_date+"</p>") ;
	out.println("<table>");
	out.println("<tr>");
	out.println("<th>VERSION</th>");
	out.println("<th>DEPLOYED DATE</th>");
	out.println("<th>LAST_RESTART_TIME</th>");
	out.println("</tr>");
	out.println("<tr>");
        while (rs.next()) {
            String date = rs.getString("deployment_date");
	    String version = rs.getString("deployment_version");
	    String restart_time = rs.getString("last_restart_time");
            out.println("<td>"+version+"</td>");
	    out.println("<td>"+date+"</td>");
	    out.println("<td>"+restart_time+"</td>");
            count++;
        }
	out.println("</tr>");
	out.println("</table>");
        out.println("Successfully read "+count+" rows from the database");
    } catch (SQLException e ) {
        out.println("Your database table is either missing or incorectly formatted");
        out.println("");
        out.println("CREATE TABLE deployment_history (deployment_version TEXT,deployment_date TEXT ,last_restart_time TEXT) ENGINE = InnoDB DEFAULT CHARSET=utf8;");
        out.println("INSERT INTO deployment_history (version,deployed_date,restart_time) VALUES ('beta','12-5-2018',(SELECT NOW()))");
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
