package com.sst.nt.lms.borrower.dao;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sst.nt.lms.borrower.model.Book;
import com.sst.nt.lms.borrower.model.Branch;
import com.sst.nt.lms.borrower.model.BranchCopies;
import com.sst.nt.lms.borrower.model.CopiesIdentity;

/**
 * A Data Access Object interface to access the number of copies of books in
 * branches.
 *
 * @author Salem Ozaki
 * @author Jonathan Lovelace
 */
@Repository
public interface CopiesDao extends JpaRepository<BranchCopies, CopiesIdentity> {
	/**
	 * Get the number of copies of a book held by a particular branch.
	 *
	 * @param branch The branch in question.
	 * @param book   The book in question.
	 * @return the number of copies held by that branch; if none, 0.
	 * @throws SQLException on unexpected error in dealing with the database.
	 */
	default int getCopies(final Branch branch, final Book book) {
		final Optional<BranchCopies> record = findById(new CopiesIdentity(book, branch));
		if (record.isPresent()) {
			return record.get().getCopies();
		} else {
			return 0;
		}
	}

	/**
	 * Set the number of copies of a book held by a particular branch. If the number
	 * is set to 0, the row is deleted from the database.
	 *
	 * @param branch     the branch in question
	 * @param book       the book in question
	 * @param noOfCopies the number of copies held by that branch; must not be
	 *                   negative.
	 * @throws SQLException on unexpected error in dealing with the database.
	 */
	default void setCopies(final Branch branch, final Book book, final int noOfCopies) {
		final CopiesIdentity id = new CopiesIdentity(book, branch);
		final Optional<BranchCopies> record = findById(id);
		if (record.isPresent()) {
			final BranchCopies inner = record.get();
			if (noOfCopies > 0) {
				inner.setCopies(noOfCopies);
				save(inner);
			} else if (noOfCopies == 0) {
				delete(inner);
			} else {
				throw new IllegalArgumentException("Number of copies must be nonnegative");
			}
		} else if (noOfCopies < 0) {
			throw new IllegalArgumentException("Number of copies must be nonnegative");
		} else if (noOfCopies > 0) {
			save(new BranchCopies(book, branch, noOfCopies));
		}
	}

	/**
	 * Retrieve a list of all copies held by the given branch.
	 *
	 * @param branch the branch in question
	 * @return the number of copies of all books the branch holds.
	 * @throws SQLException on unexpected error in dealing with the database.
	 */
	default List<BranchCopies> getAllBranchCopies(final Branch branch) {
		if (branch == null) {
			return Collections.emptyList();
		}
		return findAll().parallelStream()
				.filter(copies -> branch.equals(copies.getBranch()))
				.collect(Collectors.toList());
	}

	/**
	 * Retrieve a list of all copies of the given book held by any branch.
	 *
	 * @param book the book in question
	 * @return the number of copies of that book in each branch that holds it.
	 * @throws SQLException on unexpected error in dealing with the database.
	 */
	default List<BranchCopies> getAllBookCopies(final Book book) {
		if (book == null) {
			return Collections.emptyList();
		}
		return findAll().stream().filter(record -> book.equals(record.getBook()))
				.collect(Collectors.toList());
	}

	/**
	 * Retrieve a list of all copies of all books held by all branches.
	 *
	 * @return the number of copies of all books in all branches.
	 * @throws SQLException on unexpected error in dealing with the database.
	 */
	default List<BranchCopies> getAllCopies() {
		return findAll();
	}
}
