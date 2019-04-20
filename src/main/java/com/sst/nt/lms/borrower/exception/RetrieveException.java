package com.sst.nt.lms.borrower.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * An exception class to report the failure of a retrieval operation from the
 * service layer to the application layer.
 *
 * @author Salem Ozaki
 * @author Jonathan Lovelace
 */
@SuppressWarnings("serial")
@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Requested item not found")
public class RetrieveException extends TransactionException {
	/**
	 * To throw an instance of this exception class, the caller must supply the
	 * exception message.
	 *
	 * @param errorMessage the exception message
	 */
	public RetrieveException(final String errorMessage) {
		super(errorMessage);
	}
	/**
	 * To throw an instance of this exception class, the caller must supply the
	 * exception message and may supply a cause.
	 *
	 * @param errorMessage the exception message
	 * @param err the exception that caused this one
	 */
	public RetrieveException(final String errorMessage, final Throwable err) {
		super(errorMessage, err);
	}
}
