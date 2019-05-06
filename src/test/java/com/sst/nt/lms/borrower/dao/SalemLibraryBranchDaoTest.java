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

import com.sst.nt.lms.borrower.dao.LibraryBranchDao;
import com.sst.nt.lms.borrower.model.Branch;

/**
 * Tests of the library-branch DAO.
 * @author Salem Ozaki
 * @author Jonathan Lovelace (integration and polishing)
 */
@ExtendWith(SpringExtension.class)
@DataJpaTest
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class SalemLibraryBranchDaoTest {
	/**
	 * Sample branch name for tests.
	 */
	private static final String SAMPLE_BRANCH_NAME = "The Branch";
	/**
	 * Sample branch address for tests.
	 */
	private static final String SAMPLE_BRANCH_ADDRESS = "601 New Jersey Ave, Washington, DC 20001";

	/**
	 * Branch DAO under test.
	 */
	@Autowired
	private LibraryBranchDao branchDaoImpl;
	/**
	 * Stored branch from tests.
	 *
	 * <p>(TODO: Is this ever read without being first written to in the same test?)
	 */
	private Branch testBranch;

	/**
	 * Set up database connection, DAO, and test data before each test.
	 * @throws SQLException on database error
	 * @throws IOException  on I/O error reading the database schema from file
	 */
	@BeforeEach
	public void init() throws SQLException, IOException {
		testBranch = branchDaoImpl.create(SAMPLE_BRANCH_NAME, SAMPLE_BRANCH_ADDRESS);
	}

	/**
	 * Delete test data and tear down the database connection after each test.
	 * @throws SQLException on database error
	 */
	@AfterEach
	public void tearThis() throws SQLException {
		// FIXME?: WARNING maybe something that doesn't call the method we are trying to test
		branchDaoImpl.delete(testBranch);
	}

	/**
	 * Test that creating a branch works.
	 * @throws SQLException on database error
	 */
	@Test
	public void createBranchTest() throws SQLException {
		branchDaoImpl.delete(testBranch);

		final int previousSize = branchDaoImpl.findAll().size();

		testBranch = branchDaoImpl.create(SAMPLE_BRANCH_NAME, SAMPLE_BRANCH_ADDRESS);

		final int currentSize = branchDaoImpl.findAll().size();

		assertTrue(previousSize < currentSize, "creating a branch creates a row");
		assertEquals(SAMPLE_BRANCH_NAME, testBranch.getName(),
				"new branch has expected name");
		assertEquals(SAMPLE_BRANCH_ADDRESS, testBranch.getAddress(),
				"new branch has expected address");
	}

	/**
	 * Test that deleting a branch works.
	 * @throws SQLException on database error
	 */
	@Test
	public void deleteBranchTest() throws SQLException {
		final int previousSize = branchDaoImpl.findAll().size();

		branchDaoImpl.delete(testBranch);

		final int currentSize = branchDaoImpl.findAll().size();

		assertTrue(previousSize > currentSize, "deleting a branch removes a row");
		assertFalse(branchDaoImpl.findById(testBranch.getId()).isPresent(),
				"deleted branch is gone from database");
	}

	/**
	 * Test that updating a branch works.
	 * @throws SQLException on database error
	 */
	@DisplayName("Update Branch Correctly")
	@Test
	public void updateBranchTest() throws SQLException {
		final String newBranchName = "Branch Person";
		final String newBranchAddress = "123 new address in VA";
		final Branch newBranch = new Branch(testBranch.getId(), newBranchName,
				newBranchAddress);

		branchDaoImpl.save(newBranch);

		final Branch updatedBranch = branchDaoImpl.findById(newBranch.getId()).get();

		assertNotNull(updatedBranch, "updated row is still present");
		assertEquals(updatedBranch, newBranch, "updated row has expected fields");
	}

	/**
	 * Test that retrieving a branch by ID works.
	 * @throws SQLException on database error
	 */
	@DisplayName("Get correctly")
	@Test
	public void testGetBranch() throws SQLException {
		final Branch foundBranch = branchDaoImpl.findById(testBranch.getId()).get();
		assertNotNull(foundBranch, "retrieved branch was not null");
		assertEquals(testBranch, foundBranch, "expected branch was retrieved");
	}

	/**
	 * Test that null is retrieved for unknown ID.
	 * @throws SQLException on database error
	 */
	@DisplayName("Return null if entry not found")
	@Test
	public void testGetNotFoundBranch() throws SQLException {
		assertFalse(branchDaoImpl.findById(Integer.MAX_VALUE).isPresent(),
				"null is retrieved for unknown ID.");
	}
}
