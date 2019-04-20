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
import com.sst.nt.lms.borrower.dao.BookDao;
import com.sst.nt.lms.borrower.dao.PublisherDao;
import com.sst.nt.lms.borrower.model.Author;
import com.sst.nt.lms.borrower.model.Book;
import com.sst.nt.lms.borrower.model.Publisher;

/**
 * Tests of book DAO.
 *
 * @author Al Amine Ahmed Moussa
 * @author Jonathan Lovelace (integration and polishing)
 */
@ExtendWith(SpringExtension.class)
@DataJpaTest
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class AlBookDaoTest {
	/**
	 * The DAO under test.
	 */
	@Autowired
	private BookDao bookDao;

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
	 * Test that creating a book works.
	 * @throws SQLException on database error
	 */
	@Test
	public void createTest() throws SQLException {
		final String str1 = "Title1";

		final Author a = authorDao.findById(1).orElse(null);
		final Publisher p = publisherDao.findById(1).orElse(null);

		final Book book = bookDao.create(str1, a, p);
		assertEquals(str1, book.getTitle(), "created book has expected title");
	}

	/**
	 * Test that getting a book works.
	 * @throws SQLException on database error
	 */
	@Test
	public void testGet() throws SQLException {
		final Author a = authorDao.findById(1).orElse(null);
		final Publisher p = publisherDao.findById(1).orElse(null);
		final Book b = bookDao.create("50 down", a, p);

		assertEquals(b.getTitle(), bookDao.findById(b.getId()).get().getTitle(),
				"retrieved book has expected title");
	}
}
