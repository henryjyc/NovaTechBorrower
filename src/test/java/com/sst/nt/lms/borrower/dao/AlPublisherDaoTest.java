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

import com.sst.nt.lms.borrower.dao.PublisherDao;
import com.sst.nt.lms.borrower.model.Publisher;

/**
 * Tests of publisher DAO.
 *
 * @author Al Amine Ahmed Moussa
 * @author Jonathan Lovelace (integration and polishing)
 */
@ExtendWith(SpringExtension.class)
@DataJpaTest
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class AlPublisherDaoTest {
	/**
	 * The DAO under test.
	 */
	@Autowired
	private PublisherDao publisherDao;

	/**
	 * Test that creating a publisher works.
	 * @throws SQLException on database error
	 */
	@Test
	public void createTest() throws SQLException {
		final String str1 = "Publisher2";
		final String str2 = "AddressTest2";
		final String str3 = "PhoneTest2";

		final Publisher publisher = publisherDao.create(str1, str2, str3);
		assertEquals(str1, publisher.getName(),
				"created publisher has expected name");
		assertEquals(str2, publisher.getAddress(),
				"created publisher has expected address");
		assertEquals(str3, publisher.getPhone(),
				"created publisher has expected phone");
	}

	/**
	 * Test that retrieving a publisher works.
	 * @throws SQLException on database error
	 */
	@Test
	public void testGet() throws SQLException {
		final Publisher p = publisherDao.create("Ibn Khaldoun", "ADR45", "PHN45");
		assertEquals(p.getName(), publisherDao.findById(p.getId()).get().getName(),
				"publisher has expected name");
	}

}
