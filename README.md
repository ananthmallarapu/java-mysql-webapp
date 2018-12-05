
A simple servlet that uses Maven, JDBC, and Jetty to say 'Hello world'

Install The Database
------------------
    sudo apt-get update
    sudo apt-get install mysql-server
    sudo systemctl start mysql
    sudo systemctl enable mysql
    

Setup The Database
------------------

First, make a database, user, table and add a record:
login as root
    
    /usr/bin/mysql -u root -p
    CREATE DATABASE sample_app DEFAULT CHARACTER SET utf8;
    GRANT ALL ON sample_app.* TO 'sampleuser'@'localhost' IDENTIFIED BY 'samplepassword';
    GRANT ALL ON sample_app.* TO 'sampleuser'@'127.0.0.1' IDENTIFIED BY 'samplepassword';
    CREATE TABLE deployment_history (deployment_version TEXT,deployement_date TEXT,last_restart_time TEXT) ENGINE = InnoDB DEFAULT CHARSET=utf8;
    INSERT INTO deployment_history (deployment_version,deployment_date,restart_time) VALUES ('beta','12-5-2018',(SELECT NOW()));
    

If you have changed any of the values in the example SQL above, edit
the file `src/main/resources/application.properties` and edit these
properties:

    webapp.datasource.url=jdbc:mysql://localhost:8889/sample_app
    webapp.datasource.username=sampleuser
    webapp.datasource.password=samplepassword
    webapp.datasource.driverClassName=com.mysql.jdbc.Driver



install java 
------------
    sudo add-apt-repository ppa:webupd8team/java
    sudo apt update; sudo apt install oracle-java8-installer
    sudo apt install oracle-java8-set-default
    
install maven 
--------------
    sudo apt update
    sudo apt install maven
    
Build / Run
-----------

    mvn clean compile install jetty:run

Then navigate to 

    http://localhost:8080/mjjs/hello

If you get this message:

    Reading /application.properties ...
    Your database is missing or inaccessible

Fire up MAMP, go to localhost:8888/phpMyAdmin, click on SQL tab and
copy paste these lines of SQL into the text box and hit "Go"

    CREATE DATABASE sample_app DEFAULT CHARACTER SET utf8;
    GRANT ALL ON sample_app.* TO 'sampleuser'@'localhost' IDENTIFIED BY 'samplepassword';
    GRANT ALL ON sample_app.* TO 'sampleuser'@'127.0.0.1' IDENTIFIED BY 'samplepassword';

Refresh the phpMyAdmin page, and mjjs now appears on the left.
Click on the mjjs database and then click on the SQL tab. 
Copy paste this command into the text box and hit go

    CREATE TABLE deployment_history (version TEXT,deployed_date TEXT,restart_time TEXT) ENGINE = InnoDB DEFAULT CHARSET=utf8;
    INSERT INTO deployment_history (version,deployed_date,restart_time) VALUES ('beta','12-5-2018',(SELECT NOW()));

Refresh this page: http://localhost:8080/mjjs/hello

If all goes well, you will see output like the following:

    Welcome to hello world!
    Reading /application.properties ...
    name=tsugi
    Successfully read 1 rows from the database


Looking at the Source Code
--------------------------

* The [`pom.xml`](https://github.com/csev/maven-jetty-jdbc-servlet/blob/master/pom.xml) file controls the build process - it tells `mvn` where the source files 
are located and what output files to produce.  It also tells `mvn` to download library code
for the declared code libraries that this application "depends on".

* The [`src/main/java/mjjs/HelloServlet.java`](https://github.com/csev/maven-jetty-jdbc-servlet/blob/master/src/main/java/mjjs/HelloServlet.java) contains the source code for our Java Servlet.
If you look at the code, you will see methods for doGet() and doPost() - these methods are
called when there is a GET or POST to the application's URLs.  It defines a class called
`mjjs.HelloServlet` that is referenced in the next file.   You can debug this program by 
adding calls to `System.out.println()` and those print statements will come out on your console.

* The [`src/main/webapp/WEB-INF/web.xml`](https://github.com/csev/maven-jetty-jdbc-servlet/blob/master/src/main/webapp/WEB-INF/web.xml) file tells the servlet container ([Jetty](http://www.eclipse.org/jetty/) in this case)
which URLs are to be handed to which classes.  If you lok at this file, it has two 
sections - one defines a servlet and maps it to the java class and the other takes a URL
pattern and indicates that it needs to be sent to a particular servlet (i.e. which Java class).

    
Background Documentation to Read
--------------------------------

These are some online resources to read that explain how things in this servlet works:

* [How to Write a Hello World Servlet](http://stackoverflow.com/questions/18821227/how-to-write-hello-world-servlet-example)
* HttpServlet interface - [javax.servlet.http.HttpServlet](http://docs.oracle.com/javaee/6/api/javax/servlet/http/HttpServlet.html)
* Request Object interface - [javax.servlet.http.HttpServletRequest](http://docs.oracle.com/javaee/6/api/javax/servlet/http/HttpServletRequest.html)
* Response Object Interface - [javax.servlet.http.HttpServletResponse](http://docs.oracle.com/javaee/6/api/javax/servlet/http/HttpServletResponse.html)
* [Lesson: JDBC Basics](https://docs.oracle.com/javase/tutorial/jdbc/basics/)
* Database Connection interfaces - [java.sql](http://docs.oracle.com/javase/7/docs/api/java/sql/package-summary.html)


Errors
------

If you end up with this error, 

    No plugin found for prefix 'jetty' in the current project and in the
    plugin groups [org.apache.maven.plugins, org.codehaus.mojo] available
    from the repositories


