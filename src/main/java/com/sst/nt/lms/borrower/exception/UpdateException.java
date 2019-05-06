package com.sst.nt.lms.borrower.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * An exception class to report the failure of an 'update' operation from the
 * service layer to the application layer.
 *
 * @author Salem Ozaki
 * @author Jonathan Lovelace
 */
@SuppressWarnings("serial")
@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Could not update correctly")
public class UpdateException extends TransactionException {
	/**
	 * To throw an instance of this exception class, the caller must supply the
	 * exception message.
	 *
	 * @param errorMessage the exception message
	 */
	public UpdateException(final String errorMessage) {
		super(errorMessage);
	}
	/**
	 * To throw an instance of this exception class, the caller must supply the
	 * exception message and may supply the cause.
	 *
	 * @param errorMessage the exception message
	 * @param cause the exception that caused this one
	 */
	public UpdateException(final String errorMessage, final Throwable cause) {
		super(errorMessage, cause);
	}
}
