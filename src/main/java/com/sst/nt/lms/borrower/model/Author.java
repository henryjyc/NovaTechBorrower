package com.sst.nt.lms.borrower.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * An author of books.
 *
 * @author Salem Ozaki
 * @author Jonathan Lovelace
 */
@Entity
@Table(name = "tbl_author")
public class Author implements Serializable {
	/**
	 * Serialization version. Increment on any change to class structure that is
	 * pushed to production.
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * The author's ID in the database.
	 */
	@Id
	@Column(name = "authorId")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private final int id;
	/**
	 * The author's name.
	 */
	@Column(name = "authorName")
	private String name;
	/**
	 * No-arg constructor required for JPA.
	 */
	protected Author() {
		this(0, "");
	}

	/**
	 * Constructing the author object requires its ID and name.
	 *
	 * @param id   the ID used to reference the author in the database.
	 * @param name the author's name
	 */
	public Author(final int id, final String name) {
		this.id = id;
		this.name = name;
	}

	/**
	 * Get the author's name, which will not be null.
	 * @return the author's name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the author's name, which should not be null.
	 *
	 * @param name the author's new name.
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * Get the ID used to refer to this author in the database.
	 * @return the ID used to refer to this author in the database.
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
	 * An object is equal to this one iff it is an Author with the same ID and name.
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof Author) {
			return id == ((Author) obj).getId()
					&& Objects.equals(name, ((Author) obj).getName());
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return "Author: " + name + "(" + id + ")";
	}
}
