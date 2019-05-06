package com.sst.nt.lms.borrower.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.sst.nt.lms.borrower.exception.TransactionException;
import com.sst.nt.lms.borrower.model.Book;
import com.sst.nt.lms.borrower.model.Borrower;
import com.sst.nt.lms.borrower.model.Branch;
import com.sst.nt.lms.borrower.model.BranchCopies;
import com.sst.nt.lms.borrower.model.Loan;

/**
 * A service interface to ease the creation of a UI for borrowers (library
 * patrons).
 *
 * @author Salem Ozaki
 * @author Jonathan Lovelace
 */
public interface BorrowerService extends Service {
	/**
	 * Create a new loan entry in the database representing the given borrower
	 * checking out the given book from the given branch.
	 *
	 * @param borrower the patron checking out the book
	 * @param book     the book being checked out
	 * @param branch   the branch from which the book is being borrowed
	 * @param dateOut  the date the book is being checked out
	 * @param dueDate  the date the book is due
	 * @return the object representing the loan, or null if either the the borrower
	 *         already has that book out from that branch or that branch has no
	 *         available copies of that book.
	 * @throws TransactionException if something occurs while attempting to borrow a
	 *                              book
	 */
	Loan borrowBook(Borrower borrower, Book book, Branch branch,
			LocalDateTime dateOut, LocalDate dueDate) throws TransactionException;

	/**
	 * Get all book-copy counts for the given branch.
	 *
	 * @param branch the branch in question
	 * @return a list of copies for the requested branch.
	 * @throws TransactionException if something goes wrong with the retrieval
	 */
	List<BranchCopies> getAllBranchCopies(Branch branch) throws TransactionException;

	/**
	 * Handle a returned book: if there is an outstanding loan of the given book to
	 * the given borrower from the given branch, and the book is not overdue, remove
	 * the loan from the database and return true. If it is overdue, return false.
	 *
	 * @param borrower   the borrower returning the book
	 * @param book       the book being returned
	 * @param branch     the branch from which it was borrowed
	 * @param returnDate the date the borrower returned the book
	 * @return true on success, false if the book was overdue, and null if it was
	 *         not present
	 * @throws TransactionException if something occurs while attempting to return a
	 *                              book
	 */
	Boolean returnBook(Borrower borrower, Book book, Branch branch,
			LocalDate returnDate) throws TransactionException;

	/**
	 * Get all branches from which the borrower has an outstanding loan.
	 *
	 * @param borrower in question
	 * @return all branches the borrower owes a book return to.
	 * @throws TransactionException if something goes wrong with the retrieval
	 */
	List<Branch> getAllBranchesWithLoan(Borrower borrower) throws TransactionException;

	/**
	 * Get all book loans the borrower has borrowed from any library branch.
	 *
	 * @param borrower in question
	 * @return the list of book loans the borrower has out from any library.
	 * @throws TransactionException if something goes wrong with the retrieval
	 */
	List<Loan> getAllBorrowedBooks(Borrower borrower) throws TransactionException;

	/**
	 * Get the borrower with the specified card number.
	 *
	 * @param cardNo the borrower's card number
	 * @return the borrower with that card number, or null if none.
	 * @throws TransactionException if something goes wrong with the retrieval or it cannot find it
	 */
	Borrower getBorrower(int cardNo) throws TransactionException;

	/**
	 * Get a branch in the database.
	 *
	 * @return a branch in the database
	 * @throws TransactionException if something goes wrong with the retrieval or it cannot find it
	 */
	Branch getBranch(int branchId) throws TransactionException;

	/**
	 * Get a book in the database.
	 *
	 * @return a book in the database
	 * @throws TransactionException if something goes wrong with the retrieval or it cannot find it
	 */
	Book getBook(int bookId) throws TransactionException;

	/**
	 * Get a Loan of a specific Borrower and Book and Branch in the database.
	 *
	 * @return a Loan in the database
	 * @throws TransactionException if something goes wrong with the retrieval or it cannot find it
	 */
	Loan getLoan(int cardNo, int branchId, int bookId) throws TransactionException;
}
