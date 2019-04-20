package com.sst.nt.lms.borrower.dao;

import java.sql.SQLException;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sst.nt.lms.borrower.model.Publisher;

/**
 * A Data Access Object class to access the table of publishers.
 *
 * @author Salem Ozaki
 * @author Jonathan Lovelace
 */
@Repository
public interface PublisherDao extends JpaRepository<Publisher, Integer> {
	/**
	 * Create a publisher object and add the publisher to the database.
	 * @param publisherName the name of the publisher
	 * @param publisherAddress the publisher's address
	 * @param publisherPhone the publisher's phone number
	 * @return the newly created publisher object
	 * @throws SQLException on unexpected error in dealing with the database
	 */
	default Publisher create(final String publisherName, final String publisherAddress, final String publisherPhone) {
		return save(new Publisher(0, publisherName, publisherAddress, publisherPhone));
	}
}
