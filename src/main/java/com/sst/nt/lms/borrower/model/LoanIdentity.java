package com.sst.nt.lms.borrower.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * A class to be the primary key of a {@link Loan} for JPA, which requires every
 * Entity to have a single primary key.
 *
 * <p>Declared to implement Serializable because "Composite-id class must implement Serializable."
 *
 * @author Jonathan Lovelace
 */
@Embeddable
public class LoanIdentity implements Serializable {
	/**
	 * Serialization version. Increment on any change to class structure that is
	 * pushed to production.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The book that was borrowed.
	 */
//	@JsonBackReference
//    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	@ManyToOne
	@JoinColumn(name = "bookId")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private final Book book;

	/**
	 * The borrower who checked out the book.
	 */
//	@JsonBackReference
//    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	@ManyToOne
	@JoinColumn(name = "cardNo")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private final Borrower borrower;

	/**
	 * The branch from which the book was checked out.
	 */
//	@JsonBackReference
//    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	@ManyToOne
	@JoinColumn(name = "branchId")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private final Branch branch;

	/**
	 * No-arg constructor required for JPA.
	 */
	protected LoanIdentity() {
		this(null, null, null);
	}

	/**
	 * To construct an instance of this class reliably, the caller must supply the
	 * book, borrower, and branch involved in the loan.
	 * @param book     the book that was checked out
	 * @param borrower the borrower who checked it out
	 * @param branch   the branch from which it was borrowed
	 */
	public LoanIdentity(final Book book, final Borrower borrower, final Branch branch) {
		this.book = book;
		this.borrower = borrower;
		this.branch = branch;
	}

	/**
	 * Get the book that is involved in this loan.
	 * @return the book that was checked out
	 */
	public Book getBook() {
		return book;
	}

	/**
	 * Get the borrower involved in this loan.
	 * @return the borrower who checked the book out.
	 */
	public Borrower getBorrower() {
		return borrower;
	}

	/**
	 * Get the branch involved in this loan.
	 * @return the branch from which the book was borrowed.
	 */
	public Branch getBranch() {
		return branch;
	}

	/**
	 * Test whether an object is equal to this one.
	 * @param obj an object
	 * @return true iff it is a LoanIdentity with equal book, borrower, and branch
	 */
	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof LoanIdentity) {
			return Objects.equals(book, ((LoanIdentity) obj).getBook())
					&& Objects.equals(borrower, ((LoanIdentity) obj).getBorrower())
					&& Objects.equals(branch, ((LoanIdentity) obj).getBranch());
		} else {
			return false;
		}
	}
	/**
	 * Calculate a hash value for this object.
	 * @return a hash value for this object.
	 */
	@Override
	public int hashCode() {
		return Objects.hash(book, borrower, branch);
	}
}
