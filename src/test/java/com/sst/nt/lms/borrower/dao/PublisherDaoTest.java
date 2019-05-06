package com.sst.nt.lms.borrower.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.sst.nt.lms.borrower.dao.BookDao;
import com.sst.nt.lms.borrower.dao.PublisherDao;
import com.sst.nt.lms.borrower.model.Publisher;

/**
 * A test of the publisher DAO class.
 *
 * @author Jonathan Lovelace
 *
 */
@ExtendWith(SpringExtension.class)
@DataJpaTest
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
class PublisherDaoTest {
	/**
	 * The DAO being tested.
	 */
	@Autowired
	private PublisherDao testee;
	/**
	 * Book DAO used in tests.
	 */
	@Autowired
	private BookDao bookDao;

	/**
	 * Test that creating a publisher through the DAO works as expected.
	 *
	 * @throws SQLException if something goes very wrong
	 */
	@Test
	public final void testCreate() throws SQLException {
		final Publisher publisher = testee.create("test publisher", "test address",
				"test phone");
		assertEquals("test publisher", publisher.getName(),
				"created publisher has expected name");
		assertEquals(1, publisher.getId(), "created publisher has expected ID");
		assertEquals("test address", publisher.getAddress(),
				"created publisher has expected address");
		assertEquals("test phone", publisher.getPhone(),
				"created publisher has expected phone");
		final Publisher another = testee.create("test publisher", "test address",
				"test phone");
		assertEquals(2, another.getId(), "second publisher has expected ID");
		final Publisher third = testee.create("another publisher", "second address",
				"");
		assertEquals(3, third.getId(), "third publisher has expected ID");
		assertEquals("another publisher", third.getName(),
				"third publisher has expected name");
		assertEquals(3, testee.findAll().size(), "table has expected number of rows");
	}

	/**
	 * Test record update.
	 * @throws SQLException if something goes wrong
	 */
	@Test
	public final void testUpdate() throws SQLException {
		final Publisher expected = new Publisher(1, "changed value",
				"changed address", "changed phone");
		final Publisher original = new Publisher(1, "original name",
				"original address", "address phone");
		final Publisher created = testee.create("original name", "original address",
				"address phone");
		assertNotEquals(expected, created,
				"Author is not originally equal to changed value");
		assertEquals(original, testee.findById(1).get(),
				"Original is retrievable from database");
		assertNotEquals(expected, testee.findById(1).get(),
				"Changed value is not yet retrievable");
		testee.save(expected);
		assertEquals(expected, testee.findById(1).get(),
				"After update(), changed value is retrievable");
		assertNotEquals(original, testee.findById(1).get(),
				"After update(), original is not retrievable");
	}

	/**
	 * Test record deletion.
	 * @throws SQLException if something goes wrong
	 */
	@Test
	public final void testDelete() throws SQLException {
		final Publisher toDelete = new Publisher(1, "publisher to delete", "", "");
		final List<Publisher> expected = Arrays.asList(
				new Publisher(2, "publisher one"), new Publisher(3, "publisher two"),
				new Publisher(4, "publisher three"),
				new Publisher(5, "publisher 4"));
		testee.create(toDelete.getName(), toDelete.getAddress(), toDelete.getPhone());
		for (final Publisher publisher : expected) {
			testee.create(publisher.getName(), publisher.getAddress(), publisher.getPhone());
		}
		assertEquals(5, testee.findAll().size(), "Has correct size before delete");
		testee.delete(toDelete);
		assertEquals(new HashSet<>(expected), new HashSet<>(testee.findAll()),
				"Deleted publisher is gone");
		assertFalse(testee.findById(1).isPresent(), "Deleted author is gone");
	}

	/**
	 * Test that when the DAO removes a publisher, all of that publisher's books are
	 * removed.
	 *
	 * @throws SQLException if something goes very wrong
	 */
	@Test
	public void testDeleteCascade() throws SQLException {
		final Publisher toDelete = testee.create("author to be deleted", "", "");
		final Publisher toKeep = testee.create("author to keep", "", "");
		bookDao.create("title by keep", null, toKeep);
		bookDao.create("title by delete", null, toDelete);
		assertEquals(2, bookDao.findAll().size(),
				"Two books before removing publisher");
		testee.delete(toDelete);
		bookDao.flush();
		testee.flush();
		assertEquals(1, bookDao.findAll().size(),
				"Removing publisher removes publisher's book");
	}

	/**
	 * Test single-record retrieval.
	 * @throws SQLException if something goes wrong
	 */
	@Test
	public final void testGet() throws SQLException {
		final List<Publisher> expected = Arrays.asList(
				new Publisher(1, "one author", "one address", "one phone"),
				new Publisher(2, "two author", "two address", "two phone"),
				new Publisher(3, "three author", "three address", "three phone"));
		for (final Publisher publisher : expected) {
			testee.create(publisher.getName(), publisher.getAddress(), publisher.getPhone());
		}
		assertEquals(expected.get(0), testee.findById(1).get(),
				"get() returns first author as expected");
		assertEquals(expected.get(2), testee.findById(3).get(),
				"get() returns third author as expected");
		assertEquals(expected.get(1), testee.findById(2).get(),
				"get() returns second author as expected");
		assertFalse(testee.findById(5).isPresent(), "get() returns null on nonexistent author");
	}

	/**
	 * Test full-table retrieval.
	 * @throws SQLException if something goes wrong
	 */
	@Test
	public final void testGetAll() throws SQLException {
		assertTrue(testee.findAll().isEmpty(),
				"Before adding any authors, getAll() returns empty list");
		final List<Publisher> expected = Arrays.asList(
				new Publisher(1, "one author", "one address", "one phone"),
				new Publisher(2, "two author", "two address", "two phone"),
				new Publisher(3, "three author", "three address", "three phone"));
		for (final Publisher publisher : expected) {
			testee.create(publisher.getName(), publisher.getAddress(), publisher.getPhone());
		}
		assertEquals(new HashSet<>(expected), new HashSet<>(testee.findAll()),
				"getAll() returns expected authors");
	}
}
