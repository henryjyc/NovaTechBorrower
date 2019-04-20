package com.sst.nt.lms.borrower.model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * An object representing the loan of a book. Unlike every other model class,
 * this has no ID field; instead, its identity consists in the intersection of
 * the book, branch, and borrower.
 *
 * @author Salem Ozaki
 * @author Jonathan Lovelace
 */
@Entity
@Table(name = "tbl_book_loans")
public class Loan {
	/**
	 * The three fields that together constitute the identity of a loan.
	 */
	@EmbeddedId
	private LoanIdentity id;

	/**
	 * When the book was checked out.
	 */
	@Column
	private LocalDateTime dateOut;
	/**
	 * When the book is due.
	 */
	@Column
	private LocalDate dueDate;

	/**
	 * No-arg constructor required for JPA.
	 */
	protected Loan() {
		this(null, null, null, null, null);
	}

	/**
	 * To construct a Loan object, the caller must supply the book, borrower, and
	 * branch that identify the loan in question and the dates the book was checked
	 * out and is due.
	 *
	 * @param book     the book that was checked out
	 * @param borrower the borrower who checked it out
	 * @param branch   the branch from which it was borrowed
	 * @param dateOut  when it was checked out
	 * @param dueDate  when it is due
	 */
	public Loan(final Book book, final Borrower borrower, final Branch branch,
			final LocalDateTime dateOut, final LocalDate dueDate) {
		if (book == null && branch == null && borrower == null) {
			id = null;
		} else {
			id = new LoanIdentity(book, borrower, branch);
		}
		this.dateOut = dateOut;
		this.dueDate = dueDate;
	}

	/**
	 * Get the book that is involved in this loan.
	 * @return the book that was checked out
	 */
	public Book getBook() {
		if (id == null) {
			return null;
		} else {
			return id.getBook();
		}
	}

	/**
	 * Get the borrower involved in this loan.
	 * @return the borrower who checked the book out.
	 */
	public Borrower getBorrower() {
		if (id == null) {
			return null;
		} else {
			return id.getBorrower();
		}
	}

	/**
	 * Get the branch involved in this loan.
	 * @return the branch from which the book was borrowed.
	 */
	public Branch getBranch() {
		if (id == null) {
			return null;
		} else {
			return id.getBranch();
		}
	}

	/**
	 * Get when the book was checked out.
	 * @return the date (and time) the book was checked out.
	 */
	public LocalDateTime getDateOut() {
		return dateOut;
	}

	/**
	 * Change when the book was checked out.
	 * @param dateOut the new checked-out date, which must not be null.
	 */
	public void setDateOut(final LocalDateTime dateOut) {
		this.dateOut = dateOut;
	}

	/**
	 * Get the book's due date.
	 * @return the date by which the book must be returned.
	 */
	public LocalDate getDueDate() {
		return dueDate;
	}

	/**
	 * Change the book's due date.
	 * @param dueDate the new due date, which must not be null.
	 */
	public void setDueDate(final LocalDate dueDate) {
		this.dueDate = dueDate;
	}

	/**
	 * We use a combination of the hash codes of the book, borrower, and branch for
	 * this object's hash code.
	 */
	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	/**
	 * An object is equal to this one iff it is a Loan involving an equal book,
	 * borrower, and branch and its date out and due date are equal.
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof Loan) {
			return Objects.equals(id, ((Loan) obj).id)
					&& timesEqual(dateOut, ((Loan) obj).getDateOut())
					&& Objects.equals(dueDate, ((Loan) obj).getDueDate());
		} else {
			return false;
		}
	}

	/**
	 * Test whether the difference between two timestamps is less than two hours; if
	 * either is null, returns true iff the other is also null.
	 *
	 * @param first  one timestamp
	 * @param second another timestamp
	 * @return whether the two are close enough to equal for our purposes.
	 */
	private static boolean timesEqual(final LocalDateTime first, final LocalDateTime second) {
		if (first == null) {
			return second == null;
		} else if (second == null) {
			return false;
		} else {
			return Duration.between(first, second).abs().toHours() < 2;
		}
	}

	@Override
	public String toString() {
		return String.format("Loan: %s by %s borrowed from %s by %s on %s, due %s",
				getBook().getTitle(),
				Optional.ofNullable(getBook().getAuthor()).map(Author::getName)
						.orElse("an unknown author"),
				getBranch().getName(), getBorrower().getName(),
				Objects.toString(dateOut, "an unknown date"),
				Objects.toString(dueDate, "never"));
	}
}
