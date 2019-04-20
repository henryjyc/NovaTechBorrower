package com.sst.nt.lms.borrower.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.sql.SQLException;

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

import com.sst.nt.lms.borrower.dao.AuthorDao;
import com.sst.nt.lms.borrower.dao.BookDao;
import com.sst.nt.lms.borrower.dao.PublisherDao;
import com.sst.nt.lms.borrower.model.Author;
import com.sst.nt.lms.borrower.model.Book;
import com.sst.nt.lms.borrower.model.Publisher;

/**
 * Tests of the book DAO.
 * @author Salem Ozaki
 * @author Jonathan Lovelace (integration and polishing)
 */
@ExtendWith(SpringExtension.class)
@DataJpaTest
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class SalemBookDaoTest {
	/**
	 * Title of a sample book for the tests.
	 */
	private static final String SAMPLE_TITLE = "The Book Title";

	/**
	 * Sample publisher name for tests.
	 */
	private static final String SAMPLE_PUBLISHER_NAME = "The Publisher";
	/**
	 * Sample publisher address for tests.
	 */
	private static final String SAMPLE_PUBLISHER_ADDRESS = "601 New Jersey Ave, Washington, DC 20001";
	/**
	 * Sample publisher phone for tests.
	 */
	private static final String SAMPLE_PUBLISHER_PHONE = "1234567890";

	/**
	 * Sample author name for tests.
	 */
	private static final String SAMPLE_AUTHOR_NAME = "Author Name";

	/**
	 * Book DAO under test.
	 */
	@Autowired
	private BookDao bookDaoImpl;
	/**
	 * Publisher DAO involved in tests.
	 */
	@Autowired
	private PublisherDao publisherDaoImpl;
	/**
	 * Author DAO involved in tests.
	 */
	@Autowired
	private AuthorDao authorDaoImpl;
	/**
	 * Stored book from tests.
	 *
	 * <p>(TODO: Is this ever read without being first written to in the same test?)
	 */
	private Book testBook;
	/**
	 * Stored author from tests.
	 *
	 * <p>(TODO: Is this ever read without being first written to in the same test?)
	 */
	private Author testAuthor;
	/**
	 * Stored publisher from tests.
	 *
	 * <p>(TODO: Is this ever read without being first written to in the same test?)
	 */
	private Publisher testPublisher;

	/**
	 * Set up the database connection, the DAOs, and the test data before each test.
	 *
	 * @throws SQLException on database errors
	 * @throws IOException  on I/O error reading the database schema from file
	 */
	@BeforeEach
	public void init() throws SQLException, IOException {
		testAuthor = authorDaoImpl.create(SAMPLE_AUTHOR_NAME);
		testPublisher = publisherDaoImpl.create(SAMPLE_PUBLISHER_NAME, SAMPLE_PUBLISHER_ADDRESS, SAMPLE_PUBLISHER_PHONE);
		testBook = bookDaoImpl.create(SAMPLE_TITLE, testAuthor, testPublisher);
	}

	/**
	 * Remove test data from the database and tear it down after each test.
	 * @throws SQLException on DB error
	 */
	@AfterEach
	public void tearThis() throws SQLException {
		authorDaoImpl.delete(testAuthor);
		publisherDaoImpl.delete(testPublisher);
		bookDaoImpl.delete(testBook);
	}

	/**
	 * Test that creating a new book works.
	 * @throws SQLException on DB error
	 */
	@Test
	public void createBookTest() throws SQLException {
		bookDaoImpl.delete(testBook);

		final int previousSize = bookDaoImpl.findAll().size();

		testBook = bookDaoImpl.create(SAMPLE_TITLE, testAuthor, testPublisher);

		final int currentSize = bookDaoImpl.findAll().size();

		assertTrue(previousSize < currentSize, "creating book adds a row");
		assertEquals(SAMPLE_TITLE, testBook.getTitle(), "created book has expected title");
		assertEquals(testAuthor, testBook.getAuthor(), "created book has expected author");
		assertEquals(testPublisher, testBook.getPublisher(), "created book has expected publisher");
	}

	/**
	 * Test that deleting a book works.
	 * @throws SQLException on DB error
	 */
	@Test
	public void deleteBookTest() throws SQLException {
		final int previousSize = bookDaoImpl.findAll().size();

		bookDaoImpl.delete(testBook);

		final int currentSize = bookDaoImpl.findAll().size();

		assertTrue(previousSize > currentSize, "deletion removes a row");
		assertFalse(bookDaoImpl.findById(testBook.getId()).isPresent(),
				"deleted row is gone from database");
	}

	/**
	 * Test that updating a book works.
	 * @throws SQLException on DB error.
	 */
	@DisplayName("Update Correctly")
	@Test
	public void updateBookTest() throws SQLException {
		final String newTitle = "New Title";
		final String newAuthorName = "New Author Name";
		final String newPublisherName = "New Publisher Name";
		final String newPublisherAddress = "New Address";
		final String newPublisherPhone = "4567891230";

		final Author newAuthor = authorDaoImpl.create(newAuthorName);
		final Publisher newPublisher = publisherDaoImpl.create(newPublisherName,
				newPublisherAddress, newPublisherPhone);
		final Book newBook = new Book(testBook.getId(), newTitle, newAuthor, newPublisher);

		bookDaoImpl.save(newBook);

		final Book updatedbook = bookDaoImpl.findById(newBook.getId()).get();

		assertNotNull(updatedbook, "row is present after update");
		assertEquals(newBook, updatedbook, "update propagates data to table");
	}

	/**
	 * Test that updating works even if the book's author is null.
	 * @throws SQLException on DB error.
	 */
	@DisplayName("Update even if author is null")
	@Test
	public void testUpdateWithAuthorNull() throws SQLException {
		final String newTitle = "New Title";
		final String newPublisherName = "New Publisher Name";
		final String newPublisherAddress = "New Address";
		final String newPublisherPhone = "4567891230";

		final Publisher newPublisher = publisherDaoImpl.create(newPublisherName,
				newPublisherAddress, newPublisherPhone);

		final Book newBook = new Book(testBook.getId(), newTitle, null, newPublisher);

		bookDaoImpl.save(newBook);

		final Book updatedBook = bookDaoImpl.findById(newBook.getId()).get();

		assertNotNull(updatedBook, "row is present after update");
		assertEquals(newBook, updatedBook, "update propagates data to table");
		assertNull(updatedBook.getAuthor(), "update propagates null author");
	}

	/**
	 * Test that updating works even if publisher is null.
	 * @throws SQLException on DB error
	 */
	@DisplayName("Update even if publisher is null")
	@Test
	public void testUpdateWithPublisherNull() throws SQLException {
		final String newTitle = "New Title";
		final String newAuthorName = "New Author Name";

		final Author newAuthor = authorDaoImpl.create(newAuthorName);

		final Book newBook = new Book(testBook.getId(), newTitle, newAuthor, null);

		bookDaoImpl.save(newBook);

		final Book updatedBook = bookDaoImpl.findById(newBook.getId()).get();

		assertNotNull(updatedBook, "row is present after update");
		assertEquals(newBook, updatedBook, "update propagates data to table");
		assertNull(updatedBook.getPublisher(), "update propagates null publisher");
	}

	/**
	 * Test that retrieval works.
	 * @throws SQLException on DB error
	 */
	@DisplayName("Get correctly")
	@Test
	public void testGetBook() throws SQLException {
		final Book foundBook = bookDaoImpl.findById(testBook.getId()).get();
		assertNotNull(foundBook, "retrieval finds book");
		assertEquals(testBook, foundBook, "retrieval finds expected book");
	}

	/**
	 * Test that retrieving by ID returns null if ID not found.
	 * @throws SQLException on DB error
	 */
	@DisplayName("Return null if entry not found")
	@Test
	public void testGetNotFoundBook() throws SQLException {
		assertFalse(bookDaoImpl.findById(Integer.MAX_VALUE).isPresent(),
				"retrieving book with absent ID returns null");
	}
}
