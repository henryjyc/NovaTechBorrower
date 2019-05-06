package com.sst.nt.lms.borrower.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * An exception class to report a "critical" failure (such as the failure of a
 * rollback) from the service layer to the application layer.
 *
 * @author Salem Ozaki
 * @author Jonathan Lovelace
 */
@SuppressWarnings("serial")
@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Something went wrong with our server")
public class CriticalSQLException extends TransactionException {
	/**
	 * To throw an instance of this exception class, the caller must supply the
	 * exception message and the underlying cause exception.
	 *
	 * @param errorMessage the exception message
	 * @param cause the exception that caused this one
	 */
	public CriticalSQLException(final String errorMessage, final Throwable err) {
		super(errorMessage, err);
	}
}
