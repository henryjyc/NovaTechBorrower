package com.sst.nt.lms.borrower.dao;

import java.sql.SQLException;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sst.nt.lms.borrower.model.Borrower;

/**
 * A Data Access Object interface to access the table of borrowers.
 *
 * @author Salem Ozaki
 * @author Jonathan Lovelace
 */
@Repository
public interface BorrowerDao extends JpaRepository<Borrower, Integer> {
	/**
	 * Create a borrower object and add it to the database.
	 *
	 * @param borrowerName    the name of the borrower
	 * @param borrowerAddress the borrower's address
	 * @param borrowerPhone   the borrower's phone number
	 * @return the newly created borrower object
	 * @throws SQLException on unexpected error in dealing with the database
	 */
	default Borrower create(final String borrowerName, final String borrowerAddress, final String borrowerPhone) {
		return save(new Borrower(0, borrowerName, borrowerAddress, borrowerPhone));
	}
}
