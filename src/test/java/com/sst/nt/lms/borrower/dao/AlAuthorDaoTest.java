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

import com.sst.nt.lms.borrower.dao.AuthorDao;
import com.sst.nt.lms.borrower.model.Author;

/**
 * Tests of the author DAO.
 *
 * @author Al Amine Ahmed Moussa
 * @author Jonathan Lovelace (integration and polishing)
 */
@ExtendWith(SpringExtension.class)
@DataJpaTest
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class AlAuthorDaoTest {
	/**
	 * The DAO under test.
	 */
	@Autowired
	private AuthorDao authorDao;

	/**
	 * Test that creating an author works.
	 *
	 * @throws SQLException on database error
	 */
	@Test
	public void createTest() throws SQLException {
		final String str = "Najoua Bahba";
		assertEquals(str, authorDao.create(str).getName(),
				"created author has expected name");
	}

	/**
	 * Test that retrieving an author works.
	 * @throws SQLException on database error
	 */
	@Test
	public void testGet() throws SQLException {
		final Author a = authorDao.create("Ibn Khaldoun");
		assertEquals(a.getName(), authorDao.findById(a.getId()).get().getName(),
				"retrieved author has expected name");
	}
}
