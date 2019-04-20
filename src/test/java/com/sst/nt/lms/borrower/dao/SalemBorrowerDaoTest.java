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

import com.sst.nt.lms.borrower.dao.BorrowerDao;
import com.sst.nt.lms.borrower.model.Borrower;

/**
 * Tests of the borrower DAO.
 *
 * @author Salem Ozaki
 * @author Jonathan Lovelace (integration and polishing)
 */
@ExtendWith(SpringExtension.class)
@DataJpaTest
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class SalemBorrowerDaoTest {
	/**
	 * Sample borrower name for tests.
	 */
	private static final String SAMPLE_PATRON_NAME = "Jack Blaze";
	/**
	 * Sample borrower address for tests.
	 */
	private static final String SAMPLE_PATRON_ADDRESS = "601 New Jersey Ave, Washington, DC 20001";
	/**
	 * Sample borrower phone for tests.
	 */
	private static final String SAMPLE_PATRON_PHONE = "1234567890";

	/**
	 * Stored borrower from tests.
	 *
	 * <p>(TODO: Is this ever read without being first written to in the same test?)
	 */
	private Borrower testBorrower;
	/**
	 * Borrower DAO under test.
	 */
	@Autowired
	private BorrowerDao borrowerDaoImpl;

	/**
	 * Set up the database connection, the DAO, and the test data before each test.
	 *
	 * @throws SQLException on database error.
	 * @throws IOException  on I/O error reading the database schema from file
	 */
	@BeforeEach
	public void init() throws SQLException, IOException {
		testBorrower = borrowerDaoImpl.create(SAMPLE_PATRON_NAME,
				SAMPLE_PATRON_ADDRESS, SAMPLE_PATRON_PHONE);
	}

	/**
	 * Delete test data and tear down the database after each test.
	 *
	 * @throws SQLException on database error.
	 */
	@AfterEach
	public void tearThis() throws SQLException {
		borrowerDaoImpl.delete(testBorrower);
	}

	/**
	 * Test that creating a borrower works.
	 *
	 * @throws SQLException on database error
	 */
	@Test
	public void createBorrowerTest() throws SQLException {
		borrowerDaoImpl.delete(testBorrower);

		final int previousSize = borrowerDaoImpl.findAll().size();

		testBorrower = borrowerDaoImpl.create(SAMPLE_PATRON_NAME,
				SAMPLE_PATRON_ADDRESS, SAMPLE_PATRON_PHONE);

		final int currentSize = borrowerDaoImpl.findAll().size();

		assertTrue(previousSize < currentSize, "creating borrower adds a row");
		assertEquals(SAMPLE_PATRON_NAME, testBorrower.getName(),
				"created borrower has expected name");
		assertEquals(SAMPLE_PATRON_ADDRESS, testBorrower.getAddress(),
				"created borrower has expected address");
		assertEquals(SAMPLE_PATRON_PHONE, testBorrower.getPhone(),
				"created borrower has expected phone");
	}

	/**
	 * Test that deleting a borrower works.
	 *
	 * @throws SQLException on database error.
	 */
	@Test
	public void deleteBorrowerTest() throws SQLException {
		final int previousSize = borrowerDaoImpl.findAll().size();

		borrowerDaoImpl.delete(testBorrower);

		final int currentSize = borrowerDaoImpl.findAll().size();

		assertTrue(previousSize > currentSize, "deleting borrower deletes row");
		assertFalse(borrowerDaoImpl.findById(testBorrower.getCardNo()).isPresent(),
				"borrower is gone after deletion");
	}

	/**
	 * Test that updating a borrower works.
	 *
	 * @throws SQLException on database error.
	 */
	@DisplayName("Update Correctly")
	@Test
	public void updateBorrowerTest() throws SQLException {
		final String newBorrowerName = "New Borrower Name";
		final String newBorrowerAddress = "New Address";
		final String newBorrowerPhone = "4567891230";

		final Borrower newBorrower = new Borrower(testBorrower.getCardNo(),
				newBorrowerName, newBorrowerAddress, newBorrowerPhone);

		borrowerDaoImpl.save(newBorrower);

		final Borrower updatedborrower = borrowerDaoImpl
				.findById(newBorrower.getCardNo()).get();

		assertNotNull(updatedborrower, "updated row is still there");
		assertEquals(newBorrower, updatedborrower, "updated row has expected data");
	}

	/**
	 * Test that retrieval works.
	 *
	 * @throws SQLException on database error
	 */
	@DisplayName("Get Borrower correctly")
	@Test
	public void testGetBorrower() throws SQLException {
		final Borrower foundBorrower = borrowerDaoImpl
				.findById(testBorrower.getCardNo()).get();
		assertNotNull(foundBorrower, "retrieved row was present");
		assertEquals(testBorrower, foundBorrower, "retrieved row has expected data");
	}

	/**
	 * Test that retrieval returns null if ID not present.
	 *
	 * @throws SQLException on database error
	 */
	@DisplayName("Return null if entry not found")
	@Test
	public void testGetNotFoundBorrower() throws SQLException {
		assertFalse(borrowerDaoImpl.findById(Integer.MAX_VALUE).isPresent(),
				"row was not found");
	}
}
