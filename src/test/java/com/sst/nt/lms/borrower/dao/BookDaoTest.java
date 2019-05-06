package com.sst.nt.lms.borrower.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.sst.nt.lms.borrower.dao.AuthorDao;
import com.sst.nt.lms.borrower.dao.BookDao;
import com.sst.nt.lms.borrower.dao.BookLoansDao;
import com.sst.nt.lms.borrower.dao.BorrowerDao;
import com.sst.nt.lms.borrower.dao.CopiesDao;
import com.sst.nt.lms.borrower.dao.LibraryBranchDao;
import com.sst.nt.lms.borrower.dao.PublisherDao;
import com.sst.nt.lms.borrower.model.Author;
import com.sst.nt.lms.borrower.model.Book;
import com.sst.nt.lms.borrower.model.Borrower;
import com.sst.nt.lms.borrower.model.Branch;
import com.sst.nt.lms.borrower.model.BranchCopies;
import com.sst.nt.lms.borrower.model.Publisher;

/**
 * A test case for the book DAO class.
 *
 * @author Jonathan Lovelace
 */
@ExtendWith(SpringExtension.class)
@DataJpaTest
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public final class BookDaoTest {
	/**
	 * The DAO being tested.
	 */
	@Autowired
	private BookDao testee;
	/**
	 * Author DAO used in tests.
	 */
	@Autowired
	private AuthorDao authorDao;
	/**
	 * Publisher DAO used in tests.
	 */
	@Autowired
	private PublisherDao publisherDao;
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
	 * Test that updating authors through the DAO works as expected.
	 *
	 * @throws SQLException if something goes very wrong
	 */
	@Test
	public void testUpdate() throws SQLException {
		final Author author = authorDao.create("author name");
		final Publisher publisher = publisherDao.create("publisher name", "", "");
		final Book book = testee.create("book title", null, null);
		book.setAuthor(author);
		book.setPublisher(publisher);
		book.setTitle("changed title");
		testee.save(book);
		assertEquals(
				Collections.singletonList(
						new Book(1, "changed title", author, publisher)),
				testee.findAll(), "update propagated to database");
	}

	/**
	 * Test that deleting authors through the DAO works as expected.
	 *
	 * @throws SQLException if something goes very wrong
	 */
	@Test
	public void testDelete() throws SQLException {
		final Author author = authorDao.create("author name");
		final Publisher publisher = publisherDao.create("publisher name", "", "");
		final Book toDelete = new Book(1, "book to delete", author, publisher);
		final List<Book> expected = Arrays.asList(
				new Book(2, "first book", author, publisher),
				new Book(3, "second book", null, publisher),
				new Book(4, "third book", author, null));
		testee.create(toDelete.getTitle(), toDelete.getAuthor(), toDelete.getPublisher());
		for (final Book book : expected) {
			assertEquals(book,
					testee.create(book.getTitle(), book.getAuthor(),
							book.getPublisher()),
					"Created book matches what we intended");
		}
		assertEquals(4, testee.findAll().size(), "Has correct row count before delete");
		testee.delete(toDelete);
		assertEquals(new HashSet<>(expected), new HashSet<>(testee.findAll()),
				"Deleted book is gone");
		assertFalse(testee.findById(1).isPresent(), "Deleted book is gone");
	}

	/**
	 * Test that when the DAO removes a book, all copies of that book are
	 * removed.
	 *
	 * @throws SQLException if something goes very wrong
	 */
	@Test
	public void testDeleteCopiesCascade() throws SQLException {
		final Book toRemove = testee.create("book to remove", null, null);
		final Book toKeep = testee.create("book to keep", null, null);
		final Branch branch = branchDao.create("branch name", "");
		copiesDao.setCopies(branch, toKeep, 3);
		copiesDao.setCopies(branch, toRemove, 2);
		assertEquals(5,
				copiesDao.getAllBranchCopies(branch).stream()
						.mapToInt(BranchCopies::getCopies).sum(),
				"Expected number of copies in the database");
		testee.delete(toRemove);
		copiesDao.flush();
		assertEquals(3,
				copiesDao.getAllBranchCopies(branch).stream()
						.mapToInt(BranchCopies::getCopies).sum(),
				"Expected number of copies in the database after deleting book");
	}
	/**
	 * Test that when the DAO removes a book, all copies of that book are
	 * removed.
	 *
	 * @throws SQLException if something goes very wrong
	 */
	@Test
	public void testDeleteLoansCascade() throws SQLException {
		final Book toRemove = testee.create("book to remove", null, null);
		final Book toKeep = testee.create("book to keep", null, null);
		final Branch branch = branchDao.create("branch name", "");
		final Borrower borrower = borrowerDao.create("borrower", "", "");
		loansDao.create(toKeep, borrower, branch, null, null);
		loansDao.create(toRemove, borrower, branch, null, null);
		assertEquals(2, loansDao.findAll().size(),
				"Two outstanding loans before deletion");
		testee.delete(toRemove);
		testee.flush();
		loansDao.flush();
		assertEquals(1, loansDao.findAll().size(),
				"Loan of deleted book was also removed");
	}
	/**
	 * Test that getting an author through the DAO works properly.
	 *
	 * @throws SQLException if something goes very wrong
	 */
	@Test
	public void testGet() throws SQLException {
		final List<Book> expected = Arrays.asList(
				new Book(1, "one book", null, null),
				new Book(2, "two book", null, null),
				new Book(3, "red book", null, null),
				new Book(4, "blue book", null, null));
		for (final Book book : expected) {
			testee.create(book.getTitle(), null, null);
		}
		assertEquals(expected.get(0), testee.findById(1).get(),
				"get() returns first author as expected");
		assertEquals(expected.get(2), testee.findById(3).get(),
				"get() returns third author as expected");
		assertEquals(expected.get(1), testee.findById(2).get(),
				"get() returns second author as expected");
		assertFalse(testee.findById(5).isPresent(), "get() returns null on nonexistent author");
	}
	/**
	 * Test that getting all authors through the DAO works as expected.
	 *
	 * @throws SQLException if something goes very wrong
	 */
	@Test
	public void testGetAll() throws SQLException {
		assertTrue(testee.findAll().isEmpty(),
				"Before adding any books, getAll() returns empty list");
		final List<Book> expected = Arrays.asList(new Book(1, "first book", null, null),
				new Book(2, "second book", null, null), new Book(3, "third book", null, null));
		for (final Book book : expected) {
			testee.create(book.getTitle(), null, null);
		}
		assertEquals(new HashSet<>(expected), new HashSet<>(testee.findAll()),
				"getAll() returns expected books");
	}
	/**
	 * Test that creating an author through the DAO works as expected.
	 *
	 * @throws SQLException if something goes very wrong
	 */
	@Test
	public void testCreate() throws SQLException {
		final Book book = testee.create("test book", null, null);
		assertEquals("test book", book.getTitle(),
				"created book has expected title");
		assertEquals(1, book.getId(), "created book has expected ID");
		final Book another = testee.create("test book", null, null);
		assertEquals(2, another.getId(), "second book has expected ID");
		final Book third = testee.create("another book", null, null);
		assertEquals(3, third.getId(), "third book has expected ID");
		assertEquals("another book", third.getTitle(),
				"third book has expected title");
		assertEquals(3, testee.findAll().size(), "table has expected number of rows");
	}
}
