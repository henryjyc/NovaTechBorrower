package com.sst.nt.lms.borrower.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import com.sst.nt.lms.borrower.model.Author;

/**
 * Tests of the author DAO.
 * @author Salem Ozaki
 * @author Jonathan Lovelace (integration and polishing)
 */
@ExtendWith(SpringExtension.class)
@DataJpaTest
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class SalemAuthorDaoTest {
	/**
	 * Sample author name for tests.
	 */
	private static final String SAMPLE_AUTHOR_NAME = "Robert Jr.";

	/**
	 * The DAO under test.
	 */
	@Autowired
	private AuthorDao authorDaoImpl;
	/**
	 * Stored author from tests.
	 *
	 * <p>(TODO: Is this ever read without being first written to in the same test?)
	 */
	private Author testAuthor;

	/**
	 * Set up the DB connection, the DAO, and test data before running each test.
	 *
	 * @throws SQLException on database errors
	 * @throws IOException  on I/O error reading the database schema from file
	 */
	@BeforeEach
	public void init() throws SQLException, IOException {
		testAuthor = authorDaoImpl.create(SAMPLE_AUTHOR_NAME);
	}

	/**
	 * Remove the test author from the database and tear the database down after each test.
	 * @throws SQLException on DB error
	 */
	@AfterEach
	public void tearThis() throws SQLException {
		// FIXME?: WARNING maybe something that doesn't call the method we are trying to test
		authorDaoImpl.delete(testAuthor);
	}

	/**
	 * Test that author creation works.
	 * @throws SQLException on DB error
	 */
	@Test
	public void createTest() throws SQLException {
		authorDaoImpl.delete(testAuthor);

		final int previousSize = authorDaoImpl.findAll().size();

		testAuthor = authorDaoImpl.create(SAMPLE_AUTHOR_NAME);

		final int currentSize = authorDaoImpl.findAll().size();

		assertTrue(previousSize < currentSize, "Creation adds a row");
		assertEquals(SAMPLE_AUTHOR_NAME, testAuthor.getName(), "new author has expected name");
	}

	/**
	 * Test that deletion works.
	 * @throws SQLException on DB error
	 */
	@Test
	public void deleteTest() throws SQLException {
		final int previousSize = authorDaoImpl.findAll().size();

		final int id = testAuthor.getId();

		authorDaoImpl.delete(testAuthor);

		final int currentSize = authorDaoImpl.findAll().size();

		assertTrue(previousSize > currentSize, "Deletion removes a row");
		assertFalse(authorDaoImpl.findById(id).isPresent(), "row is gone after deletion");
	}

	/**
	 * Test that updating works.
	 * @throws SQLException on DB error
	 */
	@Test
	public void updateTest() throws SQLException {
		final Author newAuthor = new Author(testAuthor.getId(), "Author Person");

		authorDaoImpl.save(newAuthor);

		final Author updatedAuthor = authorDaoImpl.findById(newAuthor.getId()).get();

		assertNotNull(updatedAuthor, "row is still present after update");
		assertEquals(newAuthor, updatedAuthor, "update was propagated to row");
	}

	/**
	 * Test that retrieval works.
	 * @throws SQLException on DB error.
	 */
	@DisplayName("Get correctly")
	@Test
	public void testGet() throws SQLException {
		final Author foundAuthor = authorDaoImpl.findById(testAuthor.getId()).get();
		assertNotNull(foundAuthor, "retrieved row was not null");
		assertEquals(testAuthor, foundAuthor, "retrieved row has expected data");
	}

	/**
	 * Test that retrieval returns null when given the ID of an author not present.
	 * @throws SQLException on DB error.
	 */
	@DisplayName("Return null if entry not found")
	@Test
	public void testGetNotFound() throws SQLException {
		assertFalse(authorDaoImpl.findById(Integer.MAX_VALUE).isPresent(),
				"author for unused ID was null");
	}
}
