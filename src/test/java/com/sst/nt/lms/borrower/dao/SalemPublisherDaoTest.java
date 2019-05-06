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

import com.sst.nt.lms.borrower.dao.PublisherDao;
import com.sst.nt.lms.borrower.model.Publisher;

/**
 * Tests of the publisher DAO.
 * @author Salem Ozaki
 * @author Jonathan Lovelace (integration and polishing)
 */
@ExtendWith(SpringExtension.class)
@DataJpaTest
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class SalemPublisherDaoTest {
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
	 * Publisher DAO under test.
	 */
	@Autowired
	private PublisherDao publisherDaoImpl;
	/**
	 * Stored publisher from tests.
	 *
	 * <p>(TODO: Is this ever read without being first written to in the same test?)
	 */
	private Publisher testPublisher;

	/**
	 * Set up database connection, DAO, and test data before each test.
	 *
	 * @throws SQLException on database error
	 * @throws IOException  on I/O error reading the database schema from file
	 */
	@BeforeEach
	public void init() throws SQLException, IOException {
		testPublisher = publisherDaoImpl.create(SAMPLE_PUBLISHER_NAME,
				SAMPLE_PUBLISHER_ADDRESS, SAMPLE_PUBLISHER_PHONE);
	}

	/**
	 * Delete test data and tear down database connection after each test.
	 * @throws SQLException on database error
	 */
	@AfterEach
	public void tearThis() throws SQLException {
		// FIXME?: WARNING maybe something that doesn't call the method we are trying to test
		publisherDaoImpl.delete(testPublisher);
	}

	/**
	 * Test that creating a publisher works.
	 * @throws SQLException on database error
	 */
	@Test
	public void createPublisherTest() throws SQLException {
		publisherDaoImpl.delete(testPublisher);

		final int previousSize = publisherDaoImpl.findAll().size();

		testPublisher = publisherDaoImpl.create(SAMPLE_PUBLISHER_NAME,
				SAMPLE_PUBLISHER_ADDRESS, SAMPLE_PUBLISHER_PHONE);

		final int currentSize = publisherDaoImpl.findAll().size();

		assertTrue(previousSize < currentSize,
				"creating a publisher increases record count");
		assertEquals(SAMPLE_PUBLISHER_NAME, testPublisher.getName(),
				"new publisher has expected name");
		assertEquals(SAMPLE_PUBLISHER_ADDRESS, testPublisher.getAddress(),
				"new publisher has expected address");
		assertEquals(SAMPLE_PUBLISHER_PHONE, testPublisher.getPhone(),
				"new publisher has expected phone");
	}

	/**
	 * Test that deleting a publisher works.
	 * @throws SQLException on database error
	 */
	@Test
	public void deletePublisherTest() throws SQLException {
		final int previousSize = publisherDaoImpl.findAll().size();

		final int id = testPublisher.getId();
		publisherDaoImpl.delete(testPublisher);

		final int currentSize = publisherDaoImpl.findAll().size();

		assertTrue(previousSize > currentSize,
				"Deleting a publisher removes the record");
		assertFalse(publisherDaoImpl.findById(id).isPresent(),
				"Deleted publisher is gone from the database");
	}

	/**
	 * Test that updating a publisher works.
	 * @throws SQLException on database error
	 */
	@DisplayName("Update Correctly")
	@Test
	public void updatePublisherTest() throws SQLException {
		final Publisher newPublisher = new Publisher(testPublisher.getId(),
				"Publisher Person", "123 new address in VA", "9876543210");

		publisherDaoImpl.save(newPublisher);

		final Publisher updatedPublisher = publisherDaoImpl
				.findById(newPublisher.getId()).get();

		assertNotNull(updatedPublisher, "updated publisher is still present");
		assertEquals(newPublisher, updatedPublisher,
				"updated publisher has expected fields");
	}

	/**
	 * Test that retrieval works properly.
	 * @throws SQLException on database error
	 */
	@DisplayName("Get correctly")
	@Test
	public void testGetPublisher() throws SQLException {
		final Publisher foundPublisher = publisherDaoImpl
				.findById(testPublisher.getId()).get();
		assertNotNull(foundPublisher, "record successfully retrieved");
		assertEquals(foundPublisher, testPublisher, "record has expected data");
	}

	/**
	 * Retrieval returns null record on unknown ID.
	 * @throws SQLException on database error
	 */
	@DisplayName("Return null if entry not found")
	@Test
	public void testGetNotFoundPublisher() throws SQLException {
		assertFalse(publisherDaoImpl.findById(Integer.MAX_VALUE).isPresent(),
				"null returned for unknown ID");
	}
}
