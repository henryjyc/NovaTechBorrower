package com.sst.nt.lms.borrower.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.sst.nt.lms.borrower.dao.BookDao;
import com.sst.nt.lms.borrower.dao.CopiesDao;
import com.sst.nt.lms.borrower.dao.LibraryBranchDao;
import com.sst.nt.lms.borrower.model.Book;
import com.sst.nt.lms.borrower.model.Branch;
import com.sst.nt.lms.borrower.model.BranchCopies;
/**
 * Test of the copies DAO.
 * @author Jonathan Lovelace
 */
@ExtendWith(SpringExtension.class)
@DataJpaTest
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
class CopiesDaoTest {
	/**
	 * The DAO being tested.
	 */
	@Autowired
	private CopiesDao testee;
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
	 * Test single-row retrieval.
	 * @throws SQLException if something goes wrong
	 */
	@Test
	public final void testGetCopies() throws SQLException {
		final Book firstBook = bookDao.create("book one", null, null);
		final Book secondBook = bookDao.create("book two", null, null);
		final Branch firstBranch = branchDao.create("branch one", "address one");
		final Branch secondBranch = branchDao.create("branch two", "");
		assertEquals(0, testee.getCopies(firstBranch, firstBook),
				"No copies when table is empty");
		testee.setCopies(firstBranch, firstBook, 2);
		assertEquals(2, testee.getCopies(firstBranch, firstBook),
				"Expected number of copies returned");
		assertEquals(0, testee.getCopies(firstBranch, secondBook),
				"Copy counts are not shared between books");
		assertEquals(0, testee.getCopies(secondBranch, firstBook),
				"Copy counts are not shared between branches");
	}

	/**
	 * Test single-row insertion/update.
	 * @throws SQLException if something goes wrong
	 */
	@Test
	public final void testSetCopies() throws SQLException {
		final Book firstBook = bookDao.create("book one", null, null);
		final Book secondBook = bookDao.create("book two", null, null);
		final Branch firstBranch = branchDao.create("branch one", "address one");
		final Branch secondBranch = branchDao.create("branch two", "");
		assertEquals(0, testee.findAll().size(), "No rows before inserting data");
		testee.setCopies(firstBranch, firstBook, 2);
		testee.setCopies(firstBranch, secondBook, 3);
		testee.setCopies(secondBranch, firstBook, 5);
		assertEquals(3, testee.findAll().size(), "All expected rows present");
		assertEquals(10,
				testee.findAll().stream().mapToInt(BranchCopies::getCopies).sum(),
				"Expected number of copies present");
		testee.setCopies(firstBranch, firstBook, 0);
		assertEquals(2, testee.findAll().size(), "Setting count to 0 removes row");
	}

	/**
	 * Test getting all copies for a given branch.
	 * @throws SQLException if something goes wrong
	 */
	@Test
	public final void testGetAllBranchCopies() throws SQLException {
		final Book firstBook = bookDao.create("first book", null, null);
		final Book secondBook = bookDao.create("second book", null, null);
		final Branch firstBranch = branchDao.create("first branch", "first address");
		final Branch secondBranch = branchDao.create("second branch", "");
		testee.setCopies(firstBranch, firstBook, 2);
		testee.setCopies(firstBranch, secondBook, 3);
		testee.setCopies(secondBranch, firstBook, 5);
		assertEquals(
				new HashSet<>(
						Arrays.asList(new BranchCopies(firstBook, firstBranch, 2),
								new BranchCopies(secondBook, firstBranch, 3))),
				new HashSet<>(testee.getAllBranchCopies(firstBranch)),
				"Expected book copies returned");
		assertEquals(
				Collections
						.singletonList(new BranchCopies(firstBook, secondBranch, 5)),
				testee.getAllBranchCopies(secondBranch),
				"Expected book copies returned");
	}

	/**
	 * Test getting all copies of a given book.
	 * @throws SQLException if something goes wrong.
	 */
	@Test
	public final void testGetAllBookCopies() throws SQLException {
		final Book firstBook = bookDao.create("first book", null, null);
		final Book secondBook = bookDao.create("second book", null, null);
		final Branch firstBranch = branchDao.create("first branch", "first address");
		final Branch secondBranch = branchDao.create("second branch", "");
		testee.setCopies(firstBranch, firstBook, 2);
		testee.setCopies(firstBranch, secondBook, 3);
		testee.setCopies(secondBranch, firstBook, 5);
		assertEquals(
				new HashSet<>(
						Arrays.asList(new BranchCopies(firstBook, firstBranch, 2),
								new BranchCopies(firstBook, secondBranch, 5))),
				new HashSet<>(testee.getAllBookCopies(firstBook)),
				"Expected branch copies returned");
		assertEquals(
				Collections
						.singletonList(new BranchCopies(secondBook, firstBranch, 3)),
				testee.getAllBookCopies(secondBook),
				"Expected branch copies returned");
	}

	/**
	 * Test full-table retrieval.
	 * @throws SQLException if something goes wrong
	 */
	@Test
	public final void testGetAllCopies() throws SQLException {
		final Book firstBook = bookDao.create("first book", null, null);
		final Book secondBook = bookDao.create("second book", null, null);
		final Branch firstBranch = branchDao.create("first branch", "first address");
		final Branch secondBranch = branchDao.create("second branch", "");
		testee.setCopies(firstBranch, firstBook, 2);
		testee.setCopies(firstBranch, secondBook, 3);
		testee.setCopies(secondBranch, firstBook, 5);
		assertEquals(
				new HashSet<>(
						Arrays.asList(new BranchCopies(firstBook, firstBranch, 2),
								new BranchCopies(secondBook, firstBranch, 3),
								new BranchCopies(firstBook, secondBranch, 5))),
				new HashSet<>(testee.getAllCopies()), "Expected values returned");
	}
}
