package com.sst.nt.lms.borrower.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.sst.nt.lms.borrower.exception.TransactionException;
import com.sst.nt.lms.borrower.model.Book;
import com.sst.nt.lms.borrower.model.Borrower;
import com.sst.nt.lms.borrower.model.Branch;
import com.sst.nt.lms.borrower.model.BranchCopies;
import com.sst.nt.lms.borrower.model.Loan;
import com.sst.nt.lms.borrower.service.BorrowerService;

@RestController
public class BorrowerController {

	/**
	 * Borrower service.
	 */
	@Autowired
	BorrowerService borrowerService;

	/**
	 * Logger for handling errors in the DAO layer.
	 */
	private static final Logger LOGGER = Logger.getLogger(BorrowerService.class.getName());

	/**
	 * Allows a borrower to borrow a book from a branch and lets the client know of
	 * the status.
	 *
	 * @param cardNo   id for borrower
	 * @param branchId id for branch
	 * @param bookId   id for book
	 * @return Loans if created correctly with an appropriate http code, else an
	 *         appropriate http error code
	 */
	@PostMapping(path = "/borrower/{cardNo}/branch/{branchId}/book/{bookId}")
	public ResponseEntity<Loan> borrowBook(@PathVariable("cardNo") final int cardNo,
			@PathVariable("branchId") final int branchId,
			@PathVariable("bookId") final int bookId) {
		try {
			final Borrower foundBorrower = borrowerService.getBorrower(cardNo);
			final Book foundBook = borrowerService.getBook(bookId);
			final Branch foundBranch = borrowerService.getBranch(branchId);
			if (foundBook == null) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find the requested book");
			} else if (foundBorrower == null) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find the requested borrower");
			} else if (foundBranch == null) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find the requested branch");
			} else {
				final Loan foundLoan = borrowerService.getLoan(cardNo, branchId, bookId);
				if (foundLoan == null) {
					final Loan newLoan = borrowerService.borrowBook(foundBorrower,
							foundBook, foundBranch, LocalDateTime.now(),
							LocalDate.now().plusWeeks(1));
					if (newLoan == null) {
					// TODO: Make NoCopiesException get translated to CONFLICT; make it take Book and Branch params
						throw new ResponseStatusException(HttpStatus.CONFLICT,
								"There are no copies for " + foundBook.getTitle()
										+ " at " + foundBranch.getName());
					} else {
						return new ResponseEntity<>(newLoan, HttpStatus.CREATED);
					}
				} else {
					// TODO: Make AlreadyBorrowedException get translated to CONFLICT; make it take Book and Branch params
					throw new ResponseStatusException(HttpStatus.CONFLICT,
							"You have already borrowed " + foundBook.getTitle()
									+ " from " + foundBranch.getName());
				}
			}
		} catch (final TransactionException exception) {
			// TODO: If it's one that's translated to a HTTP status, let it through (unless there's a suppressed exception, as for rollback failing)!
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Something went wrong with our server."
							+ " Please contact your administrator for more information.");
		}
	}

	/**
	 * To retrieve a list of book copies of a particular branch, the client must
	 * supply a branch id, which the server will use to get the associated branch
	 * entity, which is used to fetch the list of book copies of the requested
	 * branch.
	 *
	 * @param branchId used to get a list of book copies associated with the given
	 *                 branchId (branch)
	 * @return a list of book copies associated with the given branch Id if the
	 *         branch associated to the branch id exists
	 * @throws TransactionException A retrieval exception will be thrown if the
	 *                              branch associated to the branch id given does
	 *                              not exist or if the search for the book copies
	 *                              list failed.
	 */
	@GetMapping(path = "/branch/{branchId}/copies")
	public ResponseEntity<List<BranchCopies>> getAllBranchCopies(
			@PathVariable("branchId") final int branchId) {
		try {
			final Branch foundBranch = borrowerService.getBranch(branchId);
			if (foundBranch == null) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find the requested branch");
			}
			final List<BranchCopies> listOfAllBranchCopies = borrowerService
					.getAllBranchCopies(foundBranch);
			return new ResponseEntity<>(listOfAllBranchCopies, HttpStatus.OK);
		} catch (final TransactionException exception) {
			// TODO: If it's one that's translated to a HTTP status, let it through (unless there's a suppressed exception, as for rollback failing)!
			LOGGER.log(Level.SEVERE, "Error Occured while trying to retrieve a list of copies from a branch", exception);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong with our server."
					+ " Please contact your administrator for more information.");
		}
	}

	/**
	 * For client who would like to return a book.
	 *
	 * @param cardNo   id for a particular borrower
	 * @param branchId id for a particular branch
	 * @param bookId   id for a particular book
	 * @return Success message with 204(NO_CONTENT) code if the book was returned
	 *         correctly, 404(NOT_FOUND) if the entry of the given cardNo, branchId,
	 *         and bookId does not exist, 409(CONFLICT) if the book is overdue, or
	 *         returns a 500(INTERNAL_SERVER_ERROR) if the roll back fails
	 * @throws TransactionException Throws an UnknownSQLException if something goes
	 *                              wrong with the book copies and throws a
	 *                              DeleteException if something goes wrong with
	 *                              deleting the entry
	 */
	// FIXME: This should have 'loan' somewhere in the path!
	@DeleteMapping(path = "/borrower/{cardNo}/branch/{branchId}/book/{bookId}")
	public ResponseEntity<String> returnBook(
			@PathVariable("cardNo") final int cardNo,
			@PathVariable("branchId") final int branchId,
			@PathVariable("bookId") final int bookId) {
		try {
			final Borrower borrower = borrowerService.getBorrower(cardNo);
			final Branch branch = borrowerService.getBranch(branchId);
			final Book book = borrowerService.getBook(bookId);
			if (borrower == null) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Requested borrower not found");
			} else if (branch == null) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Requested branch not found");
			} else if (book == null) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Requested book not found");
			} else {
				// none of the given ids were incorrect
				final Boolean success = borrowerService.returnBook(borrower, book,
						branch, LocalDate.now());
				if (success == null) {
					return new ResponseEntity<>("You (" + borrower.getName()
							+ ") do not have " + book.getTitle() + " checkout from "
							+ branch.getName(), HttpStatus.NOT_FOUND);
				} else if (success.booleanValue()) {
					return new ResponseEntity<>(
							"Successfully returned " + book.getTitle(),
							HttpStatus.NO_CONTENT);
				} else {
					return new ResponseEntity<>("This book is overdue",
							HttpStatus.CONFLICT);
				}
			}
		} catch (final TransactionException exception) {
			// TODO: If it's one that's translated to a HTTP status, let it through (unless there's a suppressed exception, as for rollback failing)!
			LOGGER.log(Level.SEVERE, "Something has gone wrong with the server", exception);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong with our server."
					+ " Please contact your administrator for more information.");
		}
	}

	/**
	 * Get all branches from which the borrower has an outstanding book loan.
	 *
	 * @param cardNo id for a particular borrower
	 * @return 200(OK) if the borrower exists in the database and if everything goes
	 *         correctly or will return 500(an internal server error) the roll back
	 *         fails
	 * @throws TransactionException retrieve exception if it cannot find the given
	 *                              borrower
	 */
	@GetMapping(path = "/borrower/{cardNo}/branches") // FIXME: Should somehow indicate this is branches *with an outstanding loan* ...
	public ResponseEntity<List<Branch>> getAllBranchesWithLoan(
			@PathVariable("cardNo") final int cardNo) {
		try {
			final Borrower foundBorrower = borrowerService.getBorrower(cardNo);
			if (foundBorrower == null) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Requested borrower not found");
			}
			final List<Branch> listOfBranchesForBorrowerWithLoans = borrowerService
					.getAllBranchesWithLoan(foundBorrower);
			return new ResponseEntity<>(listOfBranchesForBorrowerWithLoans,
					HttpStatus.OK);
		} catch (final TransactionException exception) {
			// TODO: If it's one that's translated to a HTTP status, let it through (unless there's a suppressed exception, as for rollback failing)!
			LOGGER.log(Level.SEVERE, "Something has gone wrong with the server", exception);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong with our server."
					+ " Please contact your administrator for more information.");
		}
	}

	/**
	 * Get all book loans the borrower has borrowed from any library branch.
	 *
	 * @param cardNo id for a particular borrower
	 * @return 200(OK) if the borrower exists in the database and if everything goes
	 *         correctly or will return 500(an internal server error) the roll back
	 *         fails
	 * @throws TransactionException retrieve exception if it cannot find the given
	 *                              borrower
	 */
	@GetMapping(path = "/borrower/{cardNo}/loans")
	public ResponseEntity<List<Loan>> getAllBorrowedBooks(
			@PathVariable("cardNo") final int cardNo) {
		try {
			final Borrower foundBorrower = borrowerService.getBorrower(cardNo);
			if (foundBorrower == null) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Requested borrower not found");
			}
			final List<Loan> listOfLoansForBorrower = borrowerService
					.getAllBorrowedBooks(foundBorrower);
			return new ResponseEntity<>(listOfLoansForBorrower, HttpStatus.OK);
		} catch (final TransactionException exception) {
			// TODO: If it's one that's translated to a HTTP status, let it through (unless there's a suppressed exception, as for rollback failing)!
			LOGGER.log(Level.SEVERE, "Something has gone wrong with the server", exception);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong with our server."
					+ " Please contact your administrator for more information.");
		}
	}

	/**
	 * Give the client a borrower with a given card number.
	 *
	 * @param cardNo id for a particular borrower
	 * @return a ResponseEntity of a borrower with an ok code or will return 500(an
	 *         internal server error) the roll back fails
	 * @throws TransactionException retrieve exception if it cannot find the
	 *                              requested borrower
	 */
	@GetMapping(path = "/borrower/{cardNo}")
	public ResponseEntity<Borrower> getBorrowerById(
			@PathVariable("cardNo") final int cardNo) {
		try {
			final Borrower foundBorrower = borrowerService.getBorrower(cardNo);
			if (foundBorrower == null) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Requested borrower not found");
			} else {
				return new ResponseEntity<>(foundBorrower, HttpStatus.OK);
			}
		} catch (final TransactionException exception) {
			// TODO: If it's one that's translated to a HTTP status, let it through (unless there's a suppressed exception, as for rollback failing)!
			LOGGER.log(Level.SEVERE, "Something has gone wrong with the server", exception);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong with our server."
					+ " Please contact your administrator for more information.");
		}
	}

	/**
	 * Give the client a branch with a given branchId.
	 *
	 * @param branchId id for a particular branch
	 * @return a ResponseEntity of a branch with an 200(OK) code or will return
	 *         500(an internal server error) the roll back fails
	 * @throws TransactionException retrieve exception if it cannot find the
	 *                              requested branch
	 */
	@GetMapping(path = "/branch/{branchId}")
	public ResponseEntity<Branch> getbranch(
			@PathVariable("branchId") final int branchId) {
		try {
			final Branch foundBranch = borrowerService.getBranch(branchId);
			if (foundBranch == null) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Requested branch not found");
			} else {
				return new ResponseEntity<>(foundBranch, HttpStatus.OK);
			}
		} catch (final TransactionException exception) {
			// TODO: If it's one that's translated to a HTTP status, let it through (unless there's a suppressed exception, as for rollback failing)!
			LOGGER.log(Level.SEVERE, "Something has gone wrong with the server", exception);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong with our server."
					+ " Please contact your administrator for more information.");
		}
	}

	/**
	 * Give the client a book with a given bookId.
	 *
	 * @param bookId id for a particular branch
	 * @return a ResponseEntity of a book with an 200(OK) code or will return 500(an
	 *         internal server error) the roll back fails
	 * @throws TransactionException retrieve exception if it cannot find the
	 *                              requested book
	 */
	@GetMapping(path = "/book/{bookId}")
	public ResponseEntity<Book> getBook(@PathVariable("bookId") final int bookId) {
		try {
			final Book foundBook = borrowerService.getBook(bookId);
			if (foundBook == null) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Requested book not found");
			} else {
				return new ResponseEntity<>(foundBook, HttpStatus.OK);
			}
		} catch (final TransactionException exception) {
			// TODO: If it's one that's translated to a HTTP status, let it through (unless there's a suppressed exception, as for rollback failing)!
			LOGGER.log(Level.SEVERE, "Something has gone wrong with the server", exception);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong with our server."
					+ " Please contact your administrator for more information.");
		}
	}

	/**
	 * Gives client a loan object based on URI given by client.
	 *
	 * @param cardNo   id for a borrower
	 * @param branchId id for a branch
	 * @param bookId   id for a book
	 * @return a ResponseEntity of a loan with an ok code or an appropriate http
	 *         error code
	 * @throws TransactionException send an internal server error code if rollback
	 *                              fails, else sends a not found code
	 */
	@GetMapping(path = "/borrower/{cardNo}/branch/{branchId}/book/{bookId}")
	public ResponseEntity<Loan> getLoanByIds(
			@PathVariable("cardNo") final int cardNo,
			@PathVariable("branchId") final int branchId,
			@PathVariable("bookId") final int bookId) {
		try {
			final Borrower foundBorrower = borrowerService.getBorrower(cardNo);
			final Book foundBook = borrowerService.getBook(bookId);
			final Branch foundBranch = borrowerService.getBranch(branchId);
			if (foundBook == null) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find the requested book");
			} else if (foundBorrower == null) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find the requested borrower");
			} else if (foundBranch == null) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find the requested branch");
			} else {
				final Loan loan = borrowerService.getLoan(cardNo, branchId, bookId);
				if (loan == null) {
					throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Requested loan not found");
				} else {
					return new ResponseEntity<>(loan, HttpStatus.OK);
				}
			}
		} catch (final TransactionException exception) {
			// TODO: If it's one that's translated to a HTTP status, let it through (unless there's a suppressed exception, as for rollback failing)!
			LOGGER.log(Level.SEVERE, "Something has gone wrong with the server", exception);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong with our server."
					+ " Please contact your administrator for more information.");
		}
	}

	/**
	 * Gives client a list of all branches.
	 *
	 * @return a list of all branches
	 * @throws TransactionException if something goes wrong with the execution of
	 *                              the query (throws a criticalError)
	 */
	// TODO: Uncomment once controllers are split for service-discovery refactoring
//	@GetMapping(path = "/branches")
	public ResponseEntity<List<Branch>> getAllBranches() {
		try {
			// TODO: If it's one that's translated to a HTTP status, let it through (unless there's a suppressed exception, as for rollback failing)!
			final List<Branch> listOfAllBranches = borrowerService.getAllBranches();
			return new ResponseEntity<>(listOfAllBranches, HttpStatus.OK);
		} catch (final TransactionException exception) {
			LOGGER.log(Level.SEVERE, "Something has gone wrong with the server", exception);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong with our server."
					+ " Please contact your administrator for more information.");
		}
	}
}
