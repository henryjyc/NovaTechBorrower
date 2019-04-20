package com.sst.nt.lms.borrower.service;

import java.util.List;

import com.sst.nt.lms.borrower.exception.TransactionException;
import com.sst.nt.lms.borrower.model.Branch;

/**
 * A base interface that all service interfaces extend.
 *
 * @author Salem Ozaki
 * @author Jonathan Lovelace
 */
public interface Service {
	/**
	 * Get a list (order should not be relied on) of all the library branches in the
	 * database.
	 *
	 * @return all the borrowers in the database.
	 */
	List<Branch> getAllBranches() throws TransactionException;
	/**
	 * Commit all outstanding operations to the database, if the backend supports transactions.
	 */
	void commit() throws TransactionException;
	/**
	 * Begin a transaction, if not already in one.
	 */
	void beginTransaction() throws TransactionException;
}
