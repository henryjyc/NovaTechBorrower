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

import com.sst.nt.lms.borrower.dao.BorrowerDao;
import com.sst.nt.lms.borrower.model.Borrower;

/**
 * Tests of borrower DAO.
 *
 * @author Al Amine Ahmed Moussa
 * @author Jonathan Lovelace (integration and polishing)
 */
@ExtendWith(SpringExtension.class)
@DataJpaTest
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class AlBorrowerDaoTest {
	/**
	 * The DAO under test.
	 */
	@Autowired
	private BorrowerDao borrowerDao;

	/**
	 * Test that creating a borrower works.
	 * @throws SQLException on database error
	 */
	@Test
	public void createTest() throws SQLException {
		final String str1 = "Borrower1";
		final String str2 = "AddressTest21";
		final String str3 = "PhoneTest1";

		final Borrower borrower = borrowerDao.create(str1, str2, str3);
		assertEquals(str1, borrower.getName(), "created borrower has expected name");
		assertEquals(str2, borrower.getAddress(),
				"created borrower has expected address");
		assertEquals(str3, borrower.getPhone(),
				"created borrower has expected phone");
	}

	/**
	 * Test that retrieval works.
	 * @throws SQLException on database error
	 */
	@Test
	public void testGet() throws SQLException {
		final Borrower b = borrowerDao.create("Ibn Khaldoun", "ADR45", "PHN45");

		assertEquals(b.getName(),
				borrowerDao.findById(b.getCardNo()).get().getName(),
				"borrower has expected name");
	}

}
