package my.jtatest.zl;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import java.sql.*;
import javax.sql.*;
import java.util.*;
import javax.transaction.*;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class MyServlet extends HttpServlet {


	@Override
	protected void doGet(HttpServletRequest reqest, HttpServletResponse response) throws ServletException, IOException {
                
	}
	
	@Override
	public void init() throws ServletException {
     
       try{
        Context ctx = new InitialContext();
        DataSource ds = (DataSource)ctx.lookup("jdbc/mypool");
        Connection conn = ds.getConnection("MYSQL", "MYSQL");
        Statement stmt = null; // Non-transactional statement
        Statement stmtx = null; // Transactional statement
        Properties dbProperties = new Properties();
        javax.transaction.UserTransaction txn = null ;
        /* JNDIのlookupメソッドを使用して、UserTransactionを獲得します */
           try {
              javax.naming.Context initialContext = new javax.naming.InitialContext();
              txn =(UserTransaction)initialContext.lookup("java:comp/UserTransaction");
           } catch(NamingException ex) {
    
           }

/////////////           
             try {
            stmt = conn.createStatement();  // non-tx statement
            
            // Check the database connection.
            try {
                stmt.executeUpdate("DROP TABLE test_table");
                stmt.executeUpdate("DROP TABLE test_table2");
            }
            catch (Exception e) {
                // assume not in database.
            }
            
            try {
                stmt.executeUpdate("CREATE TABLE test_table (a INTEGER,b INTEGER)");
                stmt.executeUpdate("CREATE TABLE test_table2 (a INTEGER,b INTEGER)");
            }
            catch (Exception e) {
            }

            try {
                System.out.println("Starting top-level transaction.");
                
                txn.begin();
                
                stmtx = conn.createStatement(); // will be a tx-statement

                // First, we try to roll back changes
                
                System.out.println("\nAdding entries to table 1.");
                
                stmtx.executeUpdate("INSERT INTO test_table (a, b) VALUES (1,2)");
                
                ResultSet res1 = null;
                
                System.out.println("\nInspecting table 1.");
                
                res1 = stmtx.executeQuery("SELECT * FROM test_table");
                
                while (res1.next()) {
                    System.out.println("Column 1: "+res1.getInt(1));
                    System.out.println("Column 2: "+res1.getInt(2));
                }
                System.out.println("\nAdding entries to table 2.");

                stmtx.executeUpdate("INSERT INTO test_table2 (a, b) VALUES (3,4)");
                res1 = stmtx.executeQuery("SELECT * FROM test_table2");

                System.out.println("\nInspecting table 2.");

                while (res1.next()) {
                    System.out.println("Column 1: "+res1.getInt(1));
                    System.out.println("Column 2: "+res1.getInt(2));
                }

                System.out.print("\nNow attempting to rollback changes.");

                txn.rollback();

                // Next, we try to commit changes
                txn.begin();
                stmtx = conn.createStatement();
                ResultSet res2 = null;

                System.out.println("\nNow checking state of table 1.");

                res2 = stmtx.executeQuery("SELECT * FROM test_table");

                while (res2.next()) {
                    System.out.println("Column 1: "+res2.getInt(1));
                    System.out.println("Column 2: "+res2.getInt(2));
                }

                System.out.println("\nNow checking state of table 2.");

                stmtx = conn.createStatement();

                res2 = stmtx.executeQuery("SELECT * FROM test_table2");

                while (res2.next()) {
                    System.out.println("Column 1: "+res2.getInt(1));
                    System.out.println("Column 2: "+res2.getInt(2));
                }

                txn.commit();
            }
            catch (Exception ex) {
                ex.printStackTrace();
                System.exit(0);
            }
        }
        catch (Exception sysEx) {
            sysEx.printStackTrace();
            System.exit(0);
        }

//////////

        }catch(Exception e){

        }


        }

	

	@Override
	public void destroy() {
		System.out.println("Servlet " + this.getServletName() + " has stopped");
	}
	
}

