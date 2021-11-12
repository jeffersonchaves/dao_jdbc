package app;

import java.util.List;

import dao.DaoFactory;
import dao.SellerDao;
import models.Department;
import models.Seller;

public class TesteConexaoDao {
	
	public static void main(String[] args) {
		
		System.out.println("======= TESTE 1 (findById)  ============");
		SellerDao dao = DaoFactory.createSellerDao();
		
		Seller seller = dao.findById(2);
		
		System.out.println(seller);
		
		
		
		System.out.println("======= TESTE 2 (findByDepartment) ============");
		
		List<Seller> sellers = dao.findByDepartment(new Department(1, null));
		
		for (Seller s : sellers) {
			System.out.println(s);
		}
		
	}

}
