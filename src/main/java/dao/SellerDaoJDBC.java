package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import connection.ConnectionFactory;
import exceptions.DatabaseException;
import models.Department;
import models.Seller;

public class SellerDaoJDBC implements SellerDao {
	
	private Connection connection;
	
	public SellerDaoJDBC(Connection connection) {
		this.connection = connection;
	}

	@Override
	public void insert(Seller seller) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(Seller seller) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteById(Integer id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Seller findById(Integer id) {
		
		PreparedStatement statement = null;
		ResultSet result = null;
		
	
		try {
			
			String sql = "SELECT seller.*, department.Name as DepName "
					+ "FROM seller "
					+ "INNER JOIN department "
					+ "ON seller.DepartmentId = department.Id "
					+ "WHERE seller.Id = ?";
			
			statement = connection.prepareStatement(sql);
			
			statement.setInt(1, id);
			
			result = statement.executeQuery();
			
			if(result.next()) {
				
				Department department= new Department();
				
				department.setId(result.getInt("DepartmentId"));
				
				department.setName(result.getString("DepName"));
				
				
				Seller seller = new Seller();
				
				seller.setId(result.getInt("Id"));
				
				seller.setName(result.getString("Name"));
				
				seller.setEmail(result.getString("Email"));
				
				seller.setBirthdate(result.getDate("BirthDate"));
				
				seller.setBaseSalary(result.getDouble("BaseSalary"));
				
				seller.setDepartment(department);
				
				return seller;
			}
			
		} catch (SQLException e) {
		
			throw new DatabaseException(e.getMessage());
		
		} finally {
			ConnectionFactory.closeStatement(statement);
			ConnectionFactory.closeResultSet(result);
		}
		
		return null;
		
	}

	@Override
	public List<Seller> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Seller> findByDepartment(Department dep) {
		
		
		PreparedStatement statement = null;
		ResultSet result = null;
		List<Seller> sellers = new ArrayList<Seller>();
		
	
		try {
			
			String sql = "SELECT seller.*,department.Name as DepName "
					+ "FROM seller INNER "
					+ "JOIN department "
					+ "ON seller.DepartmentId = department.Id "
					+ "WHERE DepartmentId = ? ORDER BY Name";
			
			statement = connection.prepareStatement(sql);
			
			statement.setInt(1, dep.getId());
			
			result = statement.executeQuery();
			
			
			while(result.next()) {
				
				Department department = createDepartment(result);
				
				Seller seller = createSeller(result, department);
				
				sellers.add(seller);
			}
			
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage());
		}
		
		return sellers;
	}
	
	private Seller createSeller(ResultSet result, Department department) throws SQLException {
		
		Seller seller = new Seller();
		
		seller.setId(result.getInt("Id"));
		
		seller.setName(result.getString("Name"));
		
		seller.setEmail(result.getString("Email"));
		
		seller.setBirthdate(result.getDate("BirthDate"));
		
		seller.setBaseSalary(result.getDouble("BaseSalary"));
		
		seller.setDepartment(department);
		
		return seller;
	}
	
	private Department createDepartment(ResultSet result) throws SQLException {
		
		Department department= new Department();
		
		department.setId(result.getInt("DepartmentId"));
		
		department.setName(result.getString("DepName"));
		
		return department;
	}

}
