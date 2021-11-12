package app;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import connection.ConnectionFactory;
import exceptions.DatabaseException;

public class AppUpdate {
	
	public static void main(String[] args) {
		
		
		Connection conn = ConnectionFactory.getConnection();
		
		try {
			
			conn.setAutoCommit(false);
			
			PreparedStatement st = conn.prepareStatement(""
					+ "UPDATE seller SET BaseSalary = BaseSalary + ? WHERE DepartmentId = ?");
			
			st.setDouble(1, 200);
			st.setInt(2, 4);
			
			st.executeUpdate();
			
			
			if( 2 > 1) {
				throw new SQLException("Fake Error");
			}
			
			
			PreparedStatement st2 = conn.prepareStatement(""
					+ "UPDATE seller SET BaseSalary = BaseSalary + ? WHERE DepartmentId = ?");
			
			st2.setDouble(1, 500);
			st2.setInt(2, 1);
			
			st2.executeUpdate();
			
			conn.commit();
			
		
		} catch (SQLException e) {
			
			try {
				conn.rollback();
				throw new DatabaseException("problema de transacao");
			
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		
		
	}

}
