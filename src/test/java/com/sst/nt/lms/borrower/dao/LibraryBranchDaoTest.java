package com.sst.nt.lms.borrower.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.sst.nt.lms.borrower.dao.BookDao;
import com.sst.nt.lms.borrower.dao.BookLoansDao;
import com.sst.nt.lms.borrower.dao.BorrowerDao;
import com.sst.nt.lms.borrower.dao.CopiesDao;
import com.sst.nt.lms.borrower.dao.LibraryBranchDao;
import com.sst.nt.lms.borrower.model.Book;
import com.sst.nt.lms.borrower.model.Borrower;
import com.sst.nt.lms.borrower.model.Branch;
import com.sst.nt.lms.borrower.model.BranchCopies;
/**
 * Test of library-branch DAO.
 * @author Jonathan Lovelace
 *
 */
@ExtendWith(SpringExtension.class)
@DataJpaTest
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
class LibraryBranchDaoTest {
	/**
	 * The name of the "branch ID" field in the database table.
	 */
	private static final String BRANCH_ID_FIELD = "id";
	/**
	 * The DAO being tested.
	 */
	@Autowired
	private LibraryBranchDao testee;
	/**
	 * Book DAO used in tests.
	 */
	@Autowired
	private BookDao bookDao;
	/**
	 * Borrower DAO used in tests.
	 */
	@Autowired
	private BorrowerDao borrowerDao;
	/**
	 * Loans DAO used in tests.
	 */
	@Autowired
	private BookLoansDao loansDao;
	/**
	 * Copies DAO used in tests.
	 */
	@Autowired
	private CopiesDao copiesDao;

	/**
	 * Test record insertion.
	 * @throws SQLException if something goes wrong
	 */
	@Test
	public final void testCreate() throws SQLException {
		assertEquals(0, testee.findAll().size(), "No branches before create()");
		testee.create("first branch", "");
		assertEquals(Collections.singletonList(new Branch(1, "first branch", "")),
				testee.findAll(), "first branch successfully inserted");
		testee.create("second branch", "address");
		assertEquals(
				Arrays.asList(new Branch(1, "first branch", ""),
						new Branch(2, "second branch", "address")),
				testee.findAll(new Sort(Direction.ASC, BRANCH_ID_FIELD)),
				"second branch successfully inserted");
	}

	/**
	 * Test record update.
	 * @throws SQLException if something goes wrong
	 */
	@Test
	public final void testUpdate() throws SQLException {
		final Branch branch = testee.create("branch name", "branch address");
		assertEquals(new Branch(1, "branch name", "branch address"),
				testee.findAll(new Sort(Direction.DESC, BRANCH_ID_FIELD)).get(0),
				"branch was inserted");
		branch.setName("changed name");
		testee.save(branch);
		assertEquals(new Branch(1, "changed name", "branch address"),
				testee.findAll(new Sort(Direction.DESC, BRANCH_ID_FIELD)).get(0),
				"only name was changed");
		branch.setAddress("changed address");
		testee.save(branch);
		assertEquals(new Branch(1, "changed name", "changed address"),
				testee.findAll(new Sort(Direction.DESC, BRANCH_ID_FIELD)).get(0),
				"address was changed");
	}

	/**
	 * Test record deletion.
	 * @throws SQLException if something goes wrong
	 */
	@Test
	public final void testDelete() throws SQLException {
		testee.create("to keep", "");
		final Branch toRemove = testee.create("to delete", "");
		assertEquals(2, testee.findAll().size(), "Two rows before deletion");
		testee.delete(toRemove);
		assertEquals(1, testee.findAll().size(), "One row after deletion");
	}

	/**
	 * Test that deleting a branch deletes all records of copy counts at that branch.
	 * @throws SQLException if something goes wrong
	 */
	@Test
	public final void testDeleteCopiesCascade() throws SQLException {
		final Branch toKeep = testee.create("to keep", "");
		final Branch toRemove = testee.create("to remove", "");
		final Book book = bookDao.create("title", null, null);
		copiesDao.setCopies(toKeep, book, 2);
		copiesDao.setCopies(toRemove, book, 3);
		assertEquals(5,
				copiesDao.getAllBookCopies(book).stream()
						.mapToInt(BranchCopies::getCopies).sum(),
				"Expected number of copies before branch removal");
		testee.delete(toRemove);
		copiesDao.flush();
		assertEquals(2,
				copiesDao.getAllBookCopies(book).stream()
						.mapToInt(BranchCopies::getCopies).sum(),
				"Expected number of copies after branch removal");
	}

	/**
	 * Test that deleting a branch deletes all records of loans from that branch.
	 * @throws SQLException if something goes wrong
	 */
	@Test
	public final void testDeleteLoansCascade() throws SQLException {
		final Branch toKeep = testee.create("to keep", "");
		final Branch toRemove = testee.create("to remove", "");
		final Borrower borrower = borrowerDao.create("borrower", "", "");
		final Book book = bookDao.create("title", null, null);
		loansDao.create(book, borrower, toKeep, null, null);
		loansDao.create(book, borrower, toRemove, null, null);
		assertEquals(2, loansDao.findAll().size(), "Two loans before branch removal");
		testee.delete(toRemove);
		loansDao.flush();
		assertEquals(1, loansDao.findAll().size(), "One loan after branch removal");
	}

	/**
	 * Test single-record retrieval.
	 * @throws SQLException if something goes wrong
	 */
	@Test
	public final void testGet() throws SQLException {
		testee.create("first name", "first address");
		testee.create("second name", "second address");
		testee.create("third name", "");
		assertEquals(new Branch(2, "second name", "second address"),
				testee.findById(2).get(), "Expected branch returned by get()");
		assertEquals(new Branch(3, "third name", ""), testee.findById(3).get(),
				"Expected branch returned by get()");
		assertEquals(new Branch(1, "first name", "first address"),
				testee.findById(1).get(), "Expected branch returned by get()");
		assertFalse(testee.findById(6).isPresent(), "get() returns null when no such row");
	}

	/**
	 * Test full-table retrieval.
	 * @throws SQLException if something goes wrong
	 */
	@Test
	public final void testGetAll() throws SQLException {
		assertEquals(Collections.emptyList(), testee.findAll(),
				"getAll() returns empty list when no rows");
		testee.create("name one", "address one");
		testee.create("name two", "address two");
		testee.create("name three", "");
		assertEquals(
				new HashSet<>(Arrays.asList(new Branch(2, "name two", "address two"),
						new Branch(3, "name three", ""),
						new Branch(1, "name one", "address one"))),
				new HashSet<>(testee.findAll()),
				"Expected values returned by getAll()");
	}

}
