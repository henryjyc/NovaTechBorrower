package com.sst.nt.lms.borrower.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.sst.nt.lms.borrower.dao.BookDao;
import com.sst.nt.lms.borrower.dao.BorrowerDao;
import com.sst.nt.lms.borrower.dao.CopiesDao;
import com.sst.nt.lms.borrower.dao.LibraryBranchDao;
import com.sst.nt.lms.borrower.exception.TransactionException;
import com.sst.nt.lms.borrower.model.Book;
import com.sst.nt.lms.borrower.model.Borrower;
import com.sst.nt.lms.borrower.model.Branch;
import com.sst.nt.lms.borrower.model.Loan;
import com.sst.nt.lms.borrower.service.BorrowerService;

/**
 * Tests of the borrower service class.
 *
 * @author Salem Ozaki
 * @author Jonathan Lovelace (integration and polishing)
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class SalemBorrowerServiceTest {
	/**
	 * Sample book title for tests.
	 */
	private static final String SAMPLE_TITLE = "The Book Title";

	/**
	 * Sample branch name for tests.
	 */
	private static final String SAMPLE_BRANCH_NAME = "The Branch Name";
	/**
	 * Sample branch address for tests.
	 */
	private static final String SAMPLE_BRANCH_ADDRESS = "601 New Jersey Ave, Washington, DC 20001";

	/**
	 * Sample borrower name for tests.
	 */
	private static final String SAMPLE_PATRON_NAME = "The Borrower Name";
	/**
	 * Sample borrower address for tests.
	 */
	private static final String SAMPLE_PATRON_ADDRESS = "650 New Jersey Ave, Washington, DC 20001";
	/**
	 * Sample borrower phone for tests.
	 */
	private static final String SAMPLE_PATRON_PHONE = "1234567890";

	/**
	 * Sample number of copies for tests.
	 */
	private static int noOfCopies = 50;

	/**
	 * Stored borrower from tests.
	 *
	 * <p>(TODO: Is this ever read without being first written to in the same test?)
	 */
	private Borrower testBorrower;
	/**
	 * Stored book from tests.
	 *
	 * <p>(TODO: Is this ever read without being first written to in the same test?)
	 */
	private Book testBook;
	/**
	 * Stored loan from tests.
	 *
	 * <p>(TODO: Is this ever read without being first written to in the same test?)
	 */
	private Loan testLoan;
	/**
	 * Stored branch from tests.
	 *
	 * <p>(TODO: Is this ever read without being first written to in the same test?)
	 */
	private Branch testBranch;

	/**
	 * Borrower service instance under test.
	 */
	@Autowired
	private BorrowerService borrowerService;
	
	/**
	 * Borrower Dao instance under test
	 */
	@Autowired
	BorrowerDao borrowerDao;
	
	/**
	 * Branch Dao instance under test
	 */
	@Autowired
	LibraryBranchDao branchDao;
	
	/**
	 * Book Dao instance under test
	 */
	@Autowired
	BookDao bookDao;
	/**
	 * Copies Dao instance under test
	 */
	@Autowired
	CopiesDao copiesDao;

	/**
	 * Set up database connection, service objects, and test data before each test.
	 *
	 * @throws SQLException         on database error
	 * @throws TransactionException on error caught by a service
	 * @throws IOException          on I/O error reading database schema from file
	 */
	@BeforeEach
	public void init() throws SQLException, TransactionException, IOException {
		testBorrower = borrowerDao.create(SAMPLE_PATRON_NAME, SAMPLE_PATRON_ADDRESS, SAMPLE_PATRON_PHONE);
		testBook = bookDao.create(SAMPLE_TITLE, null, null);
		testBranch = branchDao.create(SAMPLE_BRANCH_NAME, SAMPLE_BRANCH_ADDRESS);
		copiesDao.setCopies(testBranch, testBook, noOfCopies);
		// due date is two weeks from now
		testLoan = borrowerService.borrowBook(testBorrower, testBook, testBranch,
				LocalDate.now().atStartOfDay(), LocalDate.now().plusWeeks(2));
		borrowerService.commit();
	}

	/**
	 * Delete test data and tear down database connection after each test.
	 *
	 * @throws SQLException         on database error
	 * @throws TransactionException on error caught by a service
	 */
	@AfterEach
	public void tearThis() throws SQLException, TransactionException {
		// FIXME?: WARNING maybe something that doesn't call the method we are trying
		// to test
		borrowerService.returnBook(testBorrower, testBook, testBranch,
				LocalDate.now());
		copiesDao.setCopies(testBranch, testBook, 0);
		borrowerDao.delete(testBorrower);
		bookDao.delete(testBook);
		branchDao.delete(testBranch);
	}

	/**
	 * Test that returning a book works.
	 *
	 * @throws TransactionException on error caught by the service
	 */
	@DisplayName("Can return a book because not over the due date and no. of copies goes back up")
	@Test
	public void returnBookTest() throws TransactionException {
		final int copiesBeforeReturning = copiesDao.getCopies(testBranch, testBook);
		assertTrue(
				borrowerService.returnBook(testBorrower, testBook, testBranch,
						LocalDate.now().plusWeeks(1)),
				"returning book one week before due date is accepted");
		borrowerService.commit();
		final int copiesAfterReturning = copiesDao.getCopies(testBranch, testBook);

		assertEquals(copiesBeforeReturning + 1, copiesAfterReturning,
				"returning increments copy count");

		borrowerService.borrowBook(testBorrower, testBook, testBranch,
				LocalDate.now().atStartOfDay(), LocalDate.now().plusWeeks(2));
	}

	/**
	 * Test that returning fails if loan not found.
	 *
	 * @throws TransactionException on error caught by the service
	 */
	@DisplayName("Cannot return book if it cannot find that loan")
	@Test
	public void returnNullBookTest() throws TransactionException {
		final Book fakeBook = new Book(Integer.MAX_VALUE, "Some Title", null, null);
		assertNull(
				borrowerService.returnBook(testBorrower, fakeBook, testBranch,
						LocalDate.now().plusWeeks(1)),
				"return fails if loan not found");
	}

	/**
	 * Test that borrowBook() returns null if there are no copies available.
	 *
	 * @throws TransactionException on error caught by the service
	 */
	@DisplayName("borrow returns null if there are no copies of that book")
	@Test
	public void borrowBookNullTest() throws TransactionException {
		final Book newBook = bookDao.create(SAMPLE_TITLE, null, null);
		assertNull(
				borrowerService.borrowBook(testBorrower, newBook, testBranch,
						LocalDateTime.now(), LocalDate.now().plusWeeks(2)),
				"Borrowing book with no copies returns null instead of loan");
		bookDao.delete(newBook);
	}

	/**
	 * Test that borrowing a book decrements number of copies.
	 *
	 * @throws TransactionException on error caught by the service
	 */
	@DisplayName("borrow a book and # of copies goes down")
	@Test
	public void borrowBookAndNoOfCopiesDown() throws TransactionException {
		// returning 1 week before it is due
		borrowerService.returnBook(testBorrower, testBook, testBranch,
				LocalDate.now().plusWeeks(1));
		borrowerService.commit();
		final int copiesBeforeBorrowing = copiesDao.getCopies(testBranch, testBook);

		borrowerService.borrowBook(testBorrower, testBook, testBranch,
				LocalDateTime.now(), LocalDate.now().plusWeeks(2));
		borrowerService.commit();
		assertEquals(copiesBeforeBorrowing - 1,
				copiesDao.getCopies(testBranch, testBook),
				"borrowing decremented number of copies");
	}

	/**
	 * Test that getting all branches with loans works.
	 *
	 * @throws TransactionException on error caught by the service
	 */
	@Test
	public void testGetAllBranchesWithLoan() throws TransactionException {
		final List<Branch> listOfBranchesWithLoans = borrowerService
				.getAllBranchesWithLoan(testBorrower);
		assertTrue(listOfBranchesWithLoans.contains(testBranch),
				"test branch is in list of branches with loans to the borrower");
		assertEquals(1, listOfBranchesWithLoans.size(),
				"that branch is the only branch with a loan to test borrower");
	}

	/**
	 * Test that getting all of a borrower's loans works.
	 *
	 * @throws TransactionException on error caught by the service
	 */
	@Test
	public void testGetAllBorrowedBooks() throws TransactionException {
		final List<Loan> listOfAllBorrowed = borrowerService
				.getAllBorrowedBooks(testBorrower);
		assertTrue(listOfAllBorrowed.contains(testLoan),
				"list of all loans contains test loan");
		assertEquals(1, listOfAllBorrowed.size(),
				"that is the borrower's only loan");
	}

	/**
	 * Test that getting a book works.
	 * @throws SQLException on database error
	 * @throws TransactionException on error caught by the service
	 */
	@Test
	public void testGetBook() throws SQLException, TransactionException {
		final Book foundBook = bookDao.create("50 down", null, null);

		assertEquals(foundBook.getTitle(), bookDao.findById(foundBook.getId()).get().getTitle(),
				"retrieved book has expected title");
	}

	/**
	 * Test that getting a branch works.
	 * @throws SQLException on database error
	 * @throws TransactionException on error caught by the service
	 */
	@Test
	public void testGetBranch() throws SQLException, TransactionException {
		final Branch branch = branchDao.create("Branch 1457", "ADR45");
		assertEquals(branch.getName(), branchDao.findById(branch.getId()).get().getName(),
				"retrieved branch has expected name");
	}

}
