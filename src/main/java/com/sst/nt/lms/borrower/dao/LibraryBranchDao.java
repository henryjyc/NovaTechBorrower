package com.sst.nt.lms.borrower.dao;

import java.sql.SQLException;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sst.nt.lms.borrower.model.Branch;

/**
 * A Data Access Object class to access the table of library branches.
 *
 * @author Salem Ozaki
 * @author Jonathan Lovelace
 */
@Repository
public interface LibraryBranchDao extends JpaRepository<Branch, Integer> {
	/**
	 * Create a library-branch object and add it to the database.
	 *
	 * @param branchName the name of the branch
	 * @param branchAddress the address of the branch
	 * @return the newly created branch object
	 * @throws SQLException on unexpected error in dealing with the database
	 */
	default Branch create(final String branchName, final String branchAddress) {
		return save(new Branch(0, branchName, branchAddress));
	}
}
