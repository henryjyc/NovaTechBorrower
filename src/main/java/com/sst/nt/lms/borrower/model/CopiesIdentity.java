package com.sst.nt.lms.borrower.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonBackReference;

/**
 * A class to be the primary key of a {@link BranchCopies} for JPA, which
 * requires every entity to have a single primary key.
 *
 * <p>Declared to implement Serializable because "Composite-id class must implement
 * Serializable."
 *
 * @author Jonathan Lovelace
 */
@Embeddable
public class CopiesIdentity implements Serializable {
	/**
	 * Serialization version. Increment on any change to class structure that is
	 * pushed to production.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The branch that owns the copies.
	 */
	@JsonBackReference
//    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	@ManyToOne
	@JoinColumn(name = "branchId", insertable = false, updatable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private final Branch branch;

	/**
	 * The book that this represents copies of.
	 */
	@JsonBackReference
//    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	@ManyToOne
	@JoinColumn(name = "bookId")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private final Book book;

	/**
	 * No-arg constructor required for JPA.
	 */
	protected CopiesIdentity() {
		this(null, null);
	}

	/**
	 * To construct an instance of this class reliably, the caller must supply the
	 * book and branch in question.
	 *
	 * @param book the book in question
	 * @branch branch the branch in question
	 */
	public CopiesIdentity(final Book book, final Branch branch) {
		this.branch = branch;
		this.book = book;
	}

	/**
	 * Get the book the containing object represents the number of copies of.
	 */
	public Book getBook() {
		return book;
	}

	/**
	 * Get the branch the containing object represents copies in.
	 */
	public Branch getBranch() {
		return branch;
	}

	/**
	 * Test whether an object is equal to this one.
	 * @param obj an object
	 * @return true iff it is a CopiesIdentity with equal book and branch
	 */
	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof CopiesIdentity) {
			return Objects.equals(book, ((CopiesIdentity) obj).getBook())
					&& Objects.equals(branch, ((CopiesIdentity) obj).getBranch());
		} else {
			return false;
		}
	}

	/**
	 * Calculate a hash value for this object.
	 * @return a hash value based on the book and branch.
	 */
	@Override
	public int hashCode() {
		return Objects.hash(book, branch);
	}
}
