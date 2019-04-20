package com.sst.nt.lms.borrower.dao;

import java.sql.SQLException;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sst.nt.lms.borrower.model.Author;
import com.sst.nt.lms.borrower.model.Book;
import com.sst.nt.lms.borrower.model.Publisher;

/**
 * A Data Access Object interface to access the table of books.
 *
 * @author Salem Ozaki
 * @author Jonathan Lovelace
 */
@Repository
public interface BookDao extends JpaRepository<Book, Integer> {
	/**
	 * Create a book and add it to the database.
	 *
	 * @param author    the author of the book.
	 * @param publisher the publisher of the book.
	 * @return the created book
	 * @throws SQLException on unexpected error dealing with the database
	 */
	default Book create(final String title, final Author author, final Publisher publisher) {
		return save(new Book(0, title, author, publisher));
	}
}
