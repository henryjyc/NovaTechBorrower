package com.sst.nt.lms.borrower.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.sst.nt.lms.borrower.dao.BookDao;
import com.sst.nt.lms.borrower.dao.BookLoansDao;
import com.sst.nt.lms.borrower.dao.BorrowerDao;
import com.sst.nt.lms.borrower.dao.LibraryBranchDao;
import com.sst.nt.lms.borrower.model.Book;
import com.sst.nt.lms.borrower.model.Borrower;
import com.sst.nt.lms.borrower.model.Branch;
import com.sst.nt.lms.borrower.model.Loan;

/**
 * Test of the loans DAO.
 *
 * @author Jonathan Lovelace
 *
 */
@ExtendWith(SpringExtension.class)
@DataJpaTest
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
class BookLoansDaoTest {
	/**
	 * The DAO being tested.
	 */
	@Autowired
	private BookLoansDao testee;
	/**
	 * Book DAO used in tests.
	 */
	@Autowired
	private BookDao bookDao;
	/**
	 * Branch DAO used in tests.
	 */
	@Autowired
	private LibraryBranchDao branchDao;
	/**
	 * Borrower DAO used in tests.
	 */
	@Autowired
	private BorrowerDao borrowerDao;

	/**
	 * Test of loan creation.
	 * @throws SQLException if something goes wrong
	 */
	@Test
	public final void testCreate() throws SQLException {
		final Book book = bookDao.create("borrowed book title", null, null);
		final Branch branch = branchDao.create("library name", "");
		final Borrower borrower = borrowerDao.create("borrower name", "", "");
		final LocalDateTime timeOut = LocalDateTime.now();
		final LocalDate due = LocalDate.now();
		final Loan expected = new Loan(book, borrower, branch, timeOut, due);
		final Loan loan = testee.create(book, borrower, branch, timeOut, due);
		assertEquals(expected, loan, "Creating loan returns expected result");
	}

	/**
	 * Test of loan update.
	 * @throws SQLException if something goes wrong
	 */
	@Test
	public final void testUpdate() throws SQLException {
		final Book book = bookDao.create("borrowed book title", null, null);
		final Branch branch = branchDao.create("library name", "");
		final Borrower borrower = borrowerDao.create("borrower name", "", "");
		final LocalDateTime timeOut = LocalDateTime.now();
		final LocalDate due = LocalDate.now();
		final Loan original = new Loan(book, borrower, branch, timeOut, due);
		final Loan loan = testee.create(book, borrower, branch, timeOut, due);
		assertEquals(original, loan, "Loan is as expected before update");
		loan.setDateOut(timeOut.minusDays(2));
		loan.setDueDate(due.plusWeeks(1));
		testee.save(loan);
		assertNotEquals(original, testee.get(book, borrower, branch),
				"Loan has changed after update");
	}

	/**
	 * Test of deletion.
	 * @throws SQLException if something goes wrong
	 */
	@Test
	public final void testDelete() throws SQLException {
		assertEquals(0, testee.findAll().size(), "Empty table before adding");
		final Book book = bookDao.create("book title", null, null);
		final Branch branch = branchDao.create("branch name", "");
		final Borrower borrower = borrowerDao.create("patron name", "", "");
		final LocalDateTime timeOut = LocalDateTime.now();
		final LocalDate due = LocalDate.now();
		final Loan loan = testee.create(book, borrower, branch, timeOut, due);
		assertEquals(1, testee.findAll().size(), "One row after adding");
		testee.delete(loan);
		assertEquals(0, testee.findAll().size(), "Empty table after removing loan");
	}

	/**
	 * Test of individual-row retrieval.
	 * @throws SQLException if something goes wrong
	 */
	@Test
	public final void testGet() throws SQLException {
		final Book book = bookDao.create("book title", null, null);
		final Branch branch = branchDao.create("branch name", "");
		final Borrower borrower = borrowerDao.create("patron name", "", "");
		final LocalDateTime timeOut = LocalDate.now().atStartOfDay();
		final LocalDate due = LocalDate.now();
		assertNull(testee.get(book, borrower, branch),
				"Null result for loan not in table yet");
		final Loan expected = new Loan(book, borrower, branch, timeOut, due);
		testee.create(book, borrower, branch, timeOut, due);
		assertEquals(expected, testee.get(book, borrower, branch),
				"Result of retrieval is as expected");
	}

	/**
	 * Test of full-table retrieval.
	 * @throws SQLException if something goes wrong
	 */
	@Test
	public final void testGetAll() throws SQLException {
		final Book bookOne = bookDao.create("book title", null, null);
		final Book bookTwo = bookDao.create("book two", null, null);
		final Branch branch = branchDao.create("branch name", "");
		final Borrower borrower = borrowerDao.create("patron name", "", "");
		final LocalDateTime timeOut = LocalDate.now().atStartOfDay();
		final LocalDate due = LocalDate.now();
		final Set<Loan> expected = new HashSet<>(
				Arrays.asList(new Loan(bookOne, borrower, branch, timeOut, due),
						new Loan(bookTwo, borrower, branch, null, null)));
		testee.create(bookOne, borrower, branch, timeOut, due);
		testee.create(bookTwo, borrower, branch, null, null);
		assertEquals(expected, new HashSet<>(testee.findAll()),
				"All loans are returned by getAll");
	}
}
