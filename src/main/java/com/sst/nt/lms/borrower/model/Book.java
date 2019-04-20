package com.sst.nt.lms.borrower.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * A book in a library.
 *
 * @author Salem Ozaki
 * @author Jonathan Lovelace
 */
@Entity
@Table(name = "tbl_book")
public class Book implements Serializable {
	/**
	 * Serialization version. Increment on any change to class structure that is
	 * pushed to production.
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * The ID number used to refer to this book in the database.
	 */
	@Id
	@Column(name = "bookId")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private final int id;
	/**
	 * The title of the book.
	 */
	@Column
	private String title;
	// Uncommenting this field causes book deletions to not be cascaded properly.
//	/**
//	 * List of loans of this book.
//	 */
//	@JsonBackReference
//    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
//	@OneToMany(fetch = FetchType.LAZY, mappedBy = "id.book", cascade = {CascadeType.REMOVE})
//	private List<Loan> loans;
	/**
	 * The author of the book.
	 */
	@ManyToOne
	@JoinColumn(name = "authId")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Author author;
	/**
	 * The publisher of the book.
	 */
	@ManyToOne
	@JoinColumn(name = "pubId")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Publisher publisher;

	/**
	 * No-arg constructor required for JPA.
	 */
	protected Book() {
		this(0, "", null, null);
	}

	/**
	 * Constructing a book object requires its ID number, title, author, and
	 * publisher.
	 *
	 * @param id        the ID number to refer to the book by
	 * @param title     the title of the book, which should not be null
	 * @param author    the author of the book, or null if no author
	 * @param publisher the publisher of the book, or null if no publisher
	 */
	public Book(final int id, final String title, final Author author,
			final Publisher publisher) {
		this.id = id;
		this.title = title;
		this.author = author;
		this.publisher = publisher;
	}

	/**
	 * Get the title of the book, which will not be null.
	 *
	 * @return the title of the book
	 */
	public String getTitle() {
		return title;
	}

//	/**
//	 * Get a list of loans of this book.
//	 *
//	 * <p>TODO: Return a copy instead.
//	 * @return	list of loans of this book
//	 */
//	public List<Loan> getLoans() {
//		return loans;
//	}

	/**
	 * Set the title of the book, which must not be null.
	 * @param title the new title of the book.
	 */
	public void setTitle(final String title) {
		this.title = title;
	}

	/**
	 * Get the author of the book.
	 *
	 * @return the author of the book, or null if no author.
	 */
	public Author getAuthor() {
		return author;
	}

	/**
	 * Set the author of the book.
	 *
	 * @param author the new author of the book, or null if no author
	 */
	public void setAuthor(final Author author) {
		this.author = author;
	}

	/**
	 * Get the publisher of the book.
	 *
	 * @return the publisher of the book, or null if no publisher.
	 */
	public Publisher getPublisher() {
		return publisher;
	}

	/**
	 * Set the publisher of the book.
	 *
	 * @param publisher the new publisher of the book, or null if no publisher.
	 */
	public void setPublisher(final Publisher publisher) {
		this.publisher = publisher;
	}

	/**
	 * Get the ID number used to refer to this book in the database.
	 *
	 * @return the ID used to refer to this book in the database.
	 */
	public int getId() {
		return id;
	}

	/**
	 * We use only the ID for this object's hash-code.
	 */
	@Override
	public int hashCode() {
		return id;
	}

	/**
	 * An object is equal to this one iff it is a Book with the same ID, title,
	 * author, and publisher.
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof Book) {
			return id == ((Book) obj).getId()
					&& Objects.equals(title, ((Book) obj).getTitle())
					&& Objects.equals(author, ((Book) obj).getAuthor())
					&& Objects.equals(publisher, ((Book) obj).getPublisher());
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return "Book: " + title + " with " + Objects.toString(author, "No Author")
				+ " and " + Objects.toString(publisher, "No Publisher");
	}
}
