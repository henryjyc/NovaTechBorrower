package com.sst.nt.lms.borrower.dao;

import java.sql.SQLException;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sst.nt.lms.borrower.model.Author;

/**
 * A Data Access Object interface to access the table of authors.
 *
 * @author Salem Ozaki
 * @author Jonathan Lovelace
 */
@Repository
public interface AuthorDao extends JpaRepository<Author, Integer> {
	/**
	 * Create an author object and add it to the database.
	 *
	 * @param authorName the name of the author
	 * @return the created author
	 * @throws SQLException on unexpected error dealing with the database
	 */
	default Author create(final String authorName) {
		return save(new Author(0, authorName));
	}
}
