package com.sst.nt.lms.borrower.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;

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

/**
 * Test of the borrower DAO.
 * @author Jonathan Lovelace
 *
 */
@ExtendWith(SpringExtension.class)
@DataJpaTest
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
class BorrowerDaoTest {
	/**
	 * The DAO being tested.
	 */
	@Autowired
	private BorrowerDao testee;

	/**
	 * Branch DAO used in tests.
	 */
	@Autowired
	private LibraryBranchDao branchDao;
	/**
	 * Book DAO used in tests.
	 */
	@Autowired
	private BookDao bookDao;
	/**
	 * Loans DAO used in tests.
	 */
	@Autowired
	private BookLoansDao loansDao;
	/**
	 * Test creation.
	 * @throws SQLException if something goes wrong.
	 */
	@Test
	public final void testCreate() throws SQLException {
		final Borrower expected = new Borrower(1, "borrower name",
				"borrower address", "borrower phone");
		final Borrower borrower = testee.create("borrower name", "borrower address",
				"borrower phone");
		assertEquals(expected, borrower, "Creation works as expected");
		final Borrower second = new Borrower(2, "second name", "", "");
		assertEquals(second, testee.create("second name", "", ""),
				"Empty strings work");
		assertEquals(2, testee.findAll().size(), "Table has expected number of rows");
	}

	/**
	 * Test update.
	 * @throws SQLException if something goes wrong.
	 */
	@Test
	public final void testUpdate() throws SQLException {
		final String beforePhone = "before phone";
		final Borrower before = new Borrower(1, "before name", "before address",
				beforePhone);
		final String middleName = "middle name";
		final Borrower middle = new Borrower(1, middleName, "before address",
				beforePhone);
		final Borrower later = new Borrower(1, middleName, "later address",
				beforePhone);
		final Borrower after = new Borrower(1, middleName, "later address",
				"after phone");
		final Borrower borrower = testee.create("before name", "before address",
				beforePhone);
		assertEquals(before, testee.findById(1).get(), "initial state as expected");
		borrower.setName(middleName);
		testee.save(borrower);
		assertEquals(middle, testee.findById(1).get(), "update propagated to database");
		borrower.setAddress("later address");
		testee.save(borrower);
		assertEquals(later, testee.findById(1).get(), "second update propagated to database");
		borrower.setPhone("after phone");
		testee.save(borrower);
		assertEquals(after, testee.findById(1).get(), "third update propagated to database");
	}

	/**
	 * Test delete.
	 * @throws SQLException if something goes wrong.
	 */
	@Test
	public final void testDelete() throws SQLException {
		testee.create("first borrower", "", "");
		testee.create("second borrower", "", "");
		testee.create("third borrower", "", "");
		assertEquals(3, testee.findAll().size());
		testee.delete(new Borrower(2, "second borrower", "", ""));
		assertEquals(
				new HashSet<>(
						Arrays.asList(new Borrower(1, "first borrower", "", ""),
								new Borrower(3, "third borrower", "", ""))),
				new HashSet<>(testee.findAll()),
				"Remaining values are as expected after deletion");
	}

	/**
	 * Test retrieval of a single row.
	 * @throws SQLException if something goes wrong.
	 */
	@Test
	public final void testGet() throws SQLException {
		testee.create("patron one", "first address", "first phone");
		testee.create("patron two", "second address", "second phone");
		testee.create("patron three", "", "");
		assertEquals(new Borrower(1, "patron one", "first address", "first phone"),
				testee.findById(1).get(), "get() returns expected borrower");
		assertEquals(new Borrower(3, "patron three", "", ""), testee.findById(3).get(),
				"get() translates nulls to empty strings");
		assertEquals(new Borrower(2, "patron two", "second address", "second phone"),
				testee.findById(2).get(), "get() returns expected borrower");
		assertFalse(testee.findById(5).isPresent(), "get() returns null if no such row");
	}

	/**
	 * Test full-table retrieval.
	 * @throws SQLException if something goes wrong
	 */
	@Test
	public final void testGetAll() throws SQLException {
		testee.create("borrower one", "address one", "phone one");
		testee.create("borrower two", "address two", "phone two");
		testee.create("borrower three", "", "");
		assertEquals(
				new HashSet<>(Arrays.asList(
						new Borrower(1, "borrower one", "address one", "phone one"),
						new Borrower(2, "borrower two", "address two", "phone two"),
						new Borrower(3, "borrower three", "", ""))),
				new HashSet<>(testee.findAll()), "getAll() returns expected rows");
	}

	/**
	 * Test that when the DAO removes a borrower, all that borrower's loans are also
	 * removed.
	 *
	 * @throws SQLException if something goes very wrong
	 */
	@Test
	public final void testDeleteLoansCascade() throws SQLException {
		final Borrower toRemove = testee.create("borrower to remove", "", "");
		final Borrower toKeep = testee.create("borrower to keep", "", "");
		final Branch branch = branchDao.create("branch name", "");
		final Book book = bookDao.create("book title", null, null);
		loansDao.create(book, toRemove, branch, null, null);
		loansDao.create(book, toKeep, branch, null, null);
		assertEquals(2, loansDao.findAll().size(),
				"Two outstanding loans before deletion");
		testee.delete(toRemove);
		loansDao.flush();
		assertEquals(1, loansDao.findAll().size(),
				"Loan of book to deleted borrower was also removed");
	}
}
