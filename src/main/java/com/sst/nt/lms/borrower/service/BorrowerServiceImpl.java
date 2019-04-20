package com.sst.nt.lms.borrower.service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;

import com.sst.nt.lms.borrower.dao.BookDao;
import com.sst.nt.lms.borrower.dao.BookLoansDao;
import com.sst.nt.lms.borrower.dao.BorrowerDao;
import com.sst.nt.lms.borrower.dao.CopiesDao;
import com.sst.nt.lms.borrower.dao.LibraryBranchDao;
import com.sst.nt.lms.borrower.exception.DeleteException;
import com.sst.nt.lms.borrower.exception.InsertException;
import com.sst.nt.lms.borrower.exception.RetrieveException;
import com.sst.nt.lms.borrower.exception.TransactionException;
import com.sst.nt.lms.borrower.exception.UnknownSQLException;
import com.sst.nt.lms.borrower.model.Book;
import com.sst.nt.lms.borrower.model.Borrower;
import com.sst.nt.lms.borrower.model.Branch;
import com.sst.nt.lms.borrower.model.BranchCopies;
import com.sst.nt.lms.borrower.model.Loan;

/**
 * The "service" class to help UIs for borrowers.
 *
 * @author Jonathan Lovelace
 */
@Service("BorrowerService")
public class BorrowerServiceImpl implements BorrowerService {
	/**
	 * The DAO for the "branches" table.
	 */
	@Autowired
	private LibraryBranchDao branchDao;
	/**
	 * The DAO for the "loans" table.
	 */
	@Autowired
	private BookLoansDao loanDao;
	/**
	 * The DAO for the "copies" table.
	 */
	@Autowired
	private CopiesDao copiesDao;
	/**
	 * The DAO for the "borrowers" table.
	 */
	@Autowired
	private BorrowerDao borrowerDao;
	/**
	 * The clock to get "the current time" from.
	 */
	private final Clock clock;
	/**
	 * Logger for handling errors in the DAO layer.
	 */
	private static final Logger LOGGER = Logger.getLogger(BorrowerService.class.getName());

	/**
	 * The DAO for the "books" table.
	 */
	@Autowired
	private BookDao bookDao;
	/**
	 * The currently-active transaction, or null if not in a transaction.
	 */
	private volatile TransactionStatus transaction;
	/**
	 * The transaction manager provided by Spring.
	 */
	@Autowired
	private PlatformTransactionManager transactionManager;

	@Override
	public void beginTransaction() throws TransactionException {
		if (transaction == null) {
			synchronized (this) {
				if (transaction == null) {
					transaction = transactionManager.getTransaction(null);
				}
			}
		}
	}

	/**
	 * To construct this service class, the caller must supply a clock to get "the
	 * current date."
	 *
	 * @param clock a clock (time-zone) to get "the current date".
	 */
	public BorrowerServiceImpl(final Clock clock) {
		this.clock = clock;
	}

	/**
	 * Constructor that uses the default time zone.
	 */
	public BorrowerServiceImpl() {
		this(Clock.systemDefaultZone());
	}

	@Override
	public List<Branch> getAllBranches() throws TransactionException {
		try {
			return branchDao.findAll();
		} catch (final DataAccessException except) {
			LOGGER.log(Level.SEVERE,  "Error while getting all branches", except);
			throw rollback(new UnknownSQLException("Getting all branches failed", except));
		}
	}

	@Transactional
	@Override
	public Loan borrowBook(final Borrower borrower, final Book book,
			final Branch branch, final LocalDateTime dateOut,
			final LocalDate dueDate) throws TransactionException {
		try {
			if (loanDao.get(book, borrower, branch) == null) {
				final int copies = copiesDao.getCopies(branch, book);
				if (copies > 0) {
					copiesDao.setCopies(branch, book, copies - 1);
					return loanDao.create(book, borrower, branch, dateOut, dueDate);
				} else {
					return null;
				}
			} else {
				return null; // TODO: Add getLoan() method to interface
			}
		} catch (final DataAccessException except) {
			LOGGER.log(Level.SEVERE, "SQL error while creating a loan record", except);
			throw rollback(new InsertException("Creating a loan failed", except));
		}
	}

	@Override
	public List<BranchCopies> getAllBranchCopies(final Branch branch)
			throws TransactionException {
		try {
			return copiesDao.getAllBranchCopies(branch);
		} catch (final DataAccessException except) {
			LOGGER.log(Level.SEVERE, "Error while getting branch copies", except);
			throw rollback(new UnknownSQLException("Getting branch copy records failed", except));
		}
	}

	@Transactional
	@Override
	public Boolean returnBook(final Borrower borrower, final Book book,
			final Branch branch, final LocalDate dueDate) throws TransactionException {
		final Optional<Loan> loan;
		try {
			loan = Optional.ofNullable(loanDao.get(book, borrower, branch));
		} catch (final DataAccessException except) {
			LOGGER.log(Level.SEVERE, "SQL error while getting loan details", except);
			throw rollback(new UnknownSQLException("Getting loan details failed", except));
		}
		if (loan.isPresent()) {
			if (LocalDate.now(clock).isAfter(loan.get().getDueDate())) {
				return false;
			} else {
				try {
					final int copies = copiesDao.getCopies(branch, book);
					copiesDao.setCopies(branch, book, copies + 1);
				} catch (final DataAccessException except) {
					LOGGER.log(Level.SEVERE, "SQL error while incrementing copies on return", except);
					throw rollback(new UnknownSQLException("Incrementing copies on return failed", except));
				}
				try {
					loanDao.delete(loan.get());
				} catch (final DataAccessException except) {
					LOGGER.log(Level.SEVERE, "SQL error while removing a loan record", except);
					throw rollback(new DeleteException("Removing loan record failed", except));
				}
				return true;
			}
		} else {
			return null;
		}
	}

	@Override
	public List<Branch> getAllBranchesWithLoan(final Borrower borrower)
			throws TransactionException {
		return getAllBorrowedBooks(borrower).parallelStream().map(Loan::getBranch)
				.collect(Collectors.toList());
	}

	@Override
	public List<Loan> getAllBorrowedBooks(final Borrower borrower)
			throws TransactionException {
		try {
			return loanDao.findAll().parallelStream()
					.filter(loan -> borrower.equals(loan.getBorrower()))
					.collect(Collectors.toList());
		} catch (final DataAccessException except) {
			LOGGER.log(Level.SEVERE, "SQL error while getting loan records", except);
			throw rollback(new RetrieveException("Getting loan records failed", except));
		}
	}

	@Override
	public Borrower getBorrower(final int cardNo) throws TransactionException {
		try {
			return borrowerDao.findById(cardNo).orElse(null);
		} catch (final DataAccessException except) {
			LOGGER.log(Level.SEVERE, "SQL error while getting borrower details", except);
			throw rollback(new RetrieveException("Unable to find the requested borrower", except));
		}
	}

	@Override
	public void commit() throws TransactionException {
		try {
			synchronized (this) {
				if (transaction != null) {
					transactionManager.commit(transaction);
					transaction = null;
				}
			}
		} catch (final DataAccessException except) {
			LOGGER.log(Level.SEVERE, "Error of some kind while committing transaction", except);
			throw new UnknownSQLException("Committing the transaction failed", except);
		}
	}
	private <E extends Exception> E rollback(final E pending) {
		try {
			transactionManager.rollback(transaction);
		} catch (final DataAccessException except) {
			LOGGER.log(Level.SEVERE, "Further error while rolling back transaction", except);
			pending.addSuppressed(except);
		}
		synchronized (this) {
			transaction = null;
		}
		return pending;
	}

	@Override
	public Branch getBranch(final int branchId) throws TransactionException {
		try {
			return branchDao.findById(branchId).orElse(null);
		} catch (final DataAccessException except) {
			LOGGER.log(Level.SEVERE, "SQL error while getting a branch", except);
			throw rollback(new RetrieveException("Unable to find the requested branch", except));
		}
	}

	@Override
	public Book getBook(final int bookId) throws TransactionException {
		try {
			return bookDao.findById(bookId).orElse(null);
		} catch (final DataAccessException except) {
			LOGGER.log(Level.SEVERE, "SQL error while getting a book", except);
			throw rollback(new RetrieveException("Unable to find the requested book", except));
		}
	}

	@Override
	public Loan getLoan(final int cardNo, final int branchId, final int bookId) throws TransactionException {
		try {
			return loanDao.get(bookDao.findById(bookId).get(),
					borrowerDao.findById(cardNo).get(),
					branchDao.findById(branchId).get());
		} catch (final DataAccessException except) {
			LOGGER.log(Level.SEVERE, "SQL error while getting a Loan record", except);
			throw rollback(new RetrieveException("Getting a Loan failed", except));
		}
	}

	/**
	 * Set the number of copies of a book held by a particular branch. If the number
	 * is set to 0, the row is deleted from the database.
	 *
	 * @param branch     the branch in question
	 * @param book       the book in question
	 * @param noOfCopies the number of copies held by that branch; MUST not be
	 *                   negative.
	 * @throws TransationException on unexpected error in dealing with the database. WARNING (NEED to prevent user from passing negative numbers)
	 */
	protected void setCopies(final Branch branch, final Book book, final int noOfCopies) throws TransactionException {
		try {
			if (noOfCopies < 0) {
				throw new IllegalArgumentException(
						"Number of copies must be nonnegative");
			} else if (book == null || branch == null) {
				// TODO: throw IllegalArgumentException?
			} else {
				copiesDao.save(new BranchCopies(book, branch, noOfCopies));
			}
		} catch (final DataAccessException e) {
			throw new UnknownSQLException("Error with setting copies", e);
		}
	}

	/**
	 * Get the copies entity for a given branch and book.
	 *
	 * @param branch	branch in question
	 * @param book		book in question
	 * @return			number of copies for a given branch and book
	 * @throws TransactionException on error in the DAO layer
	 */
	protected int getCopies(final Branch branch, final Book book) throws TransactionException {
		try {
			if (branch == null || book == null) {
				return 0; // TODO: Throw IllegalArgumentException instead?
			} else {
				return copiesDao.getCopies(branch, book);
			}
		} catch (final DataAccessException e) {
			throw new RetrieveException("Error with getting copies");
		}
	}
}
