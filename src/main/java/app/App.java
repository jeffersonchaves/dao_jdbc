package app;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import connection.ConnectionFactory;

public class App {
	
	
	public static void main(String[] args) {
		
		//ConnectionFactory connFactory = new ConnectionFactory();
		
		Connection conn = ConnectionFactory.getConnection();
		
		
		try {
			
			Statement stmt = conn.createStatement();
			
			ResultSet result =  stmt.executeQuery("SELECT * FROM department;");
			
			while (result.next()) {
				
				Integer id = result.getInt("Id");
				String name = result.getString("Name");
				
				System.out.println("id: " + id + " nome:" + name);
			}
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
	}

}
