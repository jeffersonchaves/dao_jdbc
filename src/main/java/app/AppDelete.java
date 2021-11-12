package app;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import connection.ConnectionFactory;
import exceptions.DatabaseException;

public class AppDelete {

	public static void main(String[] args) {
		
		Connection conn = ConnectionFactory.getConnection();
		
		try {
			
			PreparedStatement st = conn.prepareStatement(""
					+ "DELETE FROM department WHERE Id = ?");
			
			st.setInt(1, 1);
			
			st.executeUpdate();
		
		
		} catch (SQLException e) {
			
			throw new DatabaseException(e.getMessage());
			
		}
		
		
		
		
	}
	
}
