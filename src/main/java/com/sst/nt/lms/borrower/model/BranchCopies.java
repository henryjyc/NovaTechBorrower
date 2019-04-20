package com.sst.nt.lms.borrower.model;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * An object representing the number of copies of a book held by a particular
 * branch. Unlike almost every other model class, this has no numeric ID field;
 * instead, its identity consists in the intersection of the book and branch.
 *
 * @author Jonathan Lovelace
 * @author Salem Ozaki
 */
@Entity
@Table(name = "tbl_book_copies")
public class BranchCopies {
	/**
	 * The two fields that together constitute the identity of a copies record.
	 */
	@EmbeddedId
	private CopiesIdentity id;

	/**
	 * The number of copies of the book that the branch currently holds.
	 */
	@Column(name = "noOfCopies")
	private int copies;

	/**
	 * No-arg cosntructor required for JPA.
	 */
	protected BranchCopies() {
		this(null, null, 0);
	}

	/**
	 * To construct a copies record, the caller must supply the book, branch, and
	 * number of copies.
	 *
	 * @param book the book in question
	 * @param branch the branch in question
	 * @param copies how many copies of the book the branch holds
	 */
	public BranchCopies(final Book book, final Branch branch, final int copies) {
		if (book == null && branch == null) {
			id = null;
		} else {
			id = new CopiesIdentity(book, branch);
		}
		// TODO: check that it's nonnegative?
		this.copies = copies;
	}

	/**
	 * Get the book this record counts the number of copies of.
	 * @return the book in question
	 */
	public Book getBook() {
		if (id == null) {
			return null;
		} else {
			return id.getBook();
		}
	}

	/**
	 * Get the branch this record counts the number of copies in.
	 * @return the branch in question
	 */
	public Branch getBranch() {
		if (id == null) {
			return null;
		} else {
			return id.getBranch();
		}
	}

	/**
	 * Get the number of copies of the book held by the branch.
	 * @return the number of copies held by the branch
	 */
	public int getCopies() {
		return copies;
	}

	/**
	 * Change the number of copies of the book held by the branch.
	 * @param copies the new number of copies
	 */
	public void setCopies(final int copies) {
		// TODO: check that it's nonnegative?
		this.copies = copies;
	}

	/**
	 * We use a combination of the hash codes of the book and branch for this
	 * object's hash code.
	 */
	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	/**
	 * An object is equal to this one iff it is a BranchCopies with an equal book,
	 * branch, and number of copies.
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof BranchCopies) {
			return Objects.equals(id, ((BranchCopies) obj).id)
					&& copies == ((BranchCopies) obj).getCopies();
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return getBranch().getName() + " has " + copies + " copies of " + getBook().getTitle() + ".";
	}
}
