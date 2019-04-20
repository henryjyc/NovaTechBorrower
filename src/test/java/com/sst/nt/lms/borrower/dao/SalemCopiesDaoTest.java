package com.sst.nt.lms.borrower.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
 * Tests of the book-copies DAO.
 * @author Salem Ozaki
 * @author Jonathan Lovelace (integration and polishing)
 */
@ExtendWith(SpringExtension.class)
@DataJpaTest
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class SalemCopiesDaoTest {
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
	 * Sample number of copies for tests.
	 */
	private static final int NUM_COPIES = 50;

	/**
	 * Book DAO involved in tests.
	 */
	@Autowired
	private BookDao bookDaoImpl;
	/**
	 * Branch DAO involved in tests.
	 */
	@Autowired
	private LibraryBranchDao branchDaoImpl;
	/**
	 * Copies DAO under test.
	 */
	@Autowired
	private CopiesDao copiesDaoImpl;

	/**
	 * Stored book from tests.
	 *
	 * <p>(TODO: Is this ever read without being first written to in the same test?)
	 */
	private Book testBook;
	/**
	 * Stored branch from tests.
	 *
	 * <p>(TODO: Is this ever read without being first written to in the same test?)
	 */
	private Branch testBranch;

	/**
	 * Set up the connection to the database, the DAOs, and the test data before each test.
	 * @throws SQLException on database error
	 * @throws IOException  on I/O error reading the database schema from file
	 */
	@BeforeEach
	public void init() throws SQLException, IOException {
		testBook = bookDaoImpl.create(SAMPLE_TITLE, null, null);
		testBranch = branchDaoImpl.create(SAMPLE_BRANCH_NAME, SAMPLE_BRANCH_ADDRESS);
		copiesDaoImpl.setCopies(testBranch, testBook, NUM_COPIES);
	}

	/**
	 * Delete test data and tear down the database after each test.
	 * @throws SQLException on database error
	 */
	@AfterEach
	public void tearThis() throws SQLException {
		bookDaoImpl.delete(testBook);
		branchDaoImpl.delete(testBranch);
	}

	/**
	 * Test that getting the number of copies works properly.
	 * @throws SQLException on database error
	 */
	@DisplayName("Get correctly")
	@Test
	public void testGetCopies() throws SQLException {
		assertEquals(NUM_COPIES, copiesDaoImpl.getCopies(testBranch, testBook),
				"number of copies is as expected");
	}

	/**
	 * Test that the number of copies defaults to 0 if not found.
	 * @throws SQLException on database error
	 */
	@DisplayName("return 0 if not found")
	@Test
	public void testGetNonExistingCopies() throws SQLException {
		final Branch nonExistingBranch = new Branch(Integer.MAX_VALUE,
				SAMPLE_BRANCH_NAME, SAMPLE_BRANCH_ADDRESS);
		assertEquals(0, copiesDaoImpl.getCopies(nonExistingBranch, testBook),
				"number of copies at an unknown branch is 0");
	}

	/**
	 * Test that setting number of copies to 0 deletes an existing entry.
	 * @throws SQLException on database error
	 */
	@DisplayName("Deleting an entry if noOfCopies is 0")
	@Test
	public void setEntryWithNoOfCopiesTest() throws SQLException {
		final int previousSize = copiesDaoImpl.findAll().size();
		copiesDaoImpl.setCopies(testBranch, testBook, 0);
		assertEquals(previousSize - 1, copiesDaoImpl.findAll().size(),
				"setting number of copies to 0 deletes row");
	}

	/**
	 * Test that setting number of copies updates an existing entry.
	 * @throws SQLException on database error
	 */
	@DisplayName("Updating an entry if it exists")
	@Test
	public void setEntryWithNewNoOfCopies() throws SQLException {
		final int newNoOfCopies = 100;
		final int previousSize = copiesDaoImpl.findAll().size();
		copiesDaoImpl.setCopies(testBranch, testBook, newNoOfCopies);
		final int currentSize = copiesDaoImpl.findAll().size();
		final int foundNoOfCopies = copiesDaoImpl.getCopies(testBranch, testBook);

		assertEquals(previousSize, currentSize,
				"changing number of copies doesn't create new record");
		assertEquals(newNoOfCopies, foundNoOfCopies,
				"change to number of copies propagated to database");
	}

	/**
	 * Test that getting all branch copies works.
	 * @throws SQLException on database error
	 */
	@Test
	public void testGetAllBranchCopies() throws SQLException {
		final List<BranchCopies> allBranchCopies = copiesDaoImpl
				.getAllBranchCopies(testBranch);
		assertTrue(
				allBranchCopies.contains(
						new BranchCopies(testBook, testBranch, NUM_COPIES)),
				"branch's records includes correct copy count for test book at test branch");
	}

	/**
	 * Test that getting copies of a book in all branches works.
	 * @throws SQLException on database error
	 */
	@Test
	public void testGetAllBookCopies() throws SQLException {
		final List<BranchCopies> allBookCopies = copiesDaoImpl.getAllBookCopies(testBook);
		assertTrue(
				allBookCopies.contains(
						new BranchCopies(testBook, testBranch, NUM_COPIES)),
				"branch's records includes correct copy count for test book at test branch");
	}

	/**
	 * Test that getting all records works.
	 * @throws SQLException on database error
	 */
	@Test
	public void testGetAllCopies() throws SQLException {
		final List<BranchCopies> allCopies = copiesDaoImpl.getAllCopies();
		assertTrue(
				allCopies.stream().anyMatch(
						copies -> Objects.equals(testBranch, copies.getBranch())),
				"all-copies database includes records for test branch");
		assertTrue(
				allCopies.stream().anyMatch(
						copies -> Objects.equals(testBook, copies.getBook())),
				"all-copies database includes record for test book");
		assertTrue(
				allCopies.contains(
						new BranchCopies(testBook, testBranch, NUM_COPIES)),
				"all-copies database has right count for test book at test branch");
	}
}
