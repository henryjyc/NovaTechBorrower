package com.sst.nt.lms.borrower.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.SQLException;

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
 * Tests of branch DAO.
 *
 * @author Al Amine Ahmed Moussa
 * @author Jonathan Lovelace (integration and polishing)
 */
@ExtendWith(SpringExtension.class)
@DataJpaTest
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class AlLibraryBranchDaoTest {
	/**
	 * The DAO under test.
	 */
	@Autowired
	private LibraryBranchDao libraryBranchDao;

	/**
	 * Test that creating a branch works.
	 * @throws SQLException on database error
	 */
	@Test
	public void createTest() throws SQLException {
		final String str1 = "Branch1";
		final String str2 = "AddressTest1";

		final Branch branch = libraryBranchDao.create(str1, str2);
		assertEquals(str1, branch.getName(), "created branch has expected name");
		assertEquals(str2, branch.getAddress(), "created branch has expected address");
	}

	/**
	 * Test that getting a branch works.
	 * @throws SQLException on database error
	 */
	@Test
	public void testGet() throws SQLException {
		final Branch p = libraryBranchDao.create("Branch 1457", "ADR45");
		assertEquals(p.getName(),
				libraryBranchDao.findById(p.getId()).get().getName(),
				"retrieved branch has expected name");
	}
}
