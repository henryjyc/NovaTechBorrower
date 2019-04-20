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
 * A branch of a library.
 *
 * @author Salem Ozaki
 * @author Jonathan Lovelace
 */
@Entity
@Table(name = "tbl_library_branch")
public class Branch implements Serializable {
	/**
	 * Serialization version. Increment on any change to class structure that is
	 * pushed to production.
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * The ID number used to identify this branch in the database.
	 */
	@Id
	@Column(name = "branchId")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private final int id;
	/**
	 * The name of the branch.
	 */
	@Column(name = "branchName")
	private String name;
	/**
	 * The address of the branch.
	 */
	@Column(name = "branchAddress")
	private String address;

	// Uncommenting this field causes branch deletions to not be cascaded properly.
//	/**
//	 * list of loans from this branch
//	 */
//	@JsonBackReference
//    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
//	@OneToMany(fetch = FetchType.LAZY, mappedBy = "id.branch")
//	private List<Loan> loans;

	/**
	 * No-arg constructor required for JPA.
	 */
	protected Branch() {
		this(0, "", "");
	}

	/**
	 * To construct a branch object, callers must supply its ID number, name, and
	 * address.
	 *
	 * @param id      the ID number to identify this branch.
	 * @param name    The name of the branch
	 * @param address The address of the branch
	 */
	public Branch(final int id, final String name, final String address) {
		this.id = id;
		this.name = name;
		this.address = address;
	}

//	/**
//	 * Get a list of loans from this branch.
//	 *
//	 * <p>TODO: return a copy instead
//	 * @return	get list of loans from this branch
//	 */
//	public List<Loan> getLoans() {
//		return loans;
//	}

	/**
	 * Get the name of the branch, which will not be null.
	 * @return the name of the branch
	 */
	public String getName() {
		return name;
	}

	/**
	 * Change the name of the branch.
	 * @param name The new name of the branch, which must not be null.
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * Get the address of the branch as a string, which will not be null.
	 * @return the address of the branch
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * Change the address of the branch.
	 * @param address the branch's new address, which must not be null.
	 */
	public void setAddress(final String address) {
		this.address = address;
	}

	/**
	 * The ID number that identifies this branch in the database.
	 * @return this branch's ID number.
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
	 * An object is equal to this one iff it is a Branch with equal ID, name, and
	 * address.
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof Branch) {
			return id == ((Branch) obj).getId()
					&& Objects.equals(name, ((Branch) obj).getName())
					&& Objects.equals(address, ((Branch) obj).getAddress());
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return "Branch: " + name + "(" + id + ") at " + address;
	}
}
