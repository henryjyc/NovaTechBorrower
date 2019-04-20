package com.sst.nt.lms.borrower.dao;

import java.io.Serializable;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

/**
 * An "interceptor" to turn all null strings coming from the database into
 * non-null empty strings.
 *
 * @author StackOverflow user Oliv https://stackoverflow.com/a/26988529
 * @author Jonathan Lovelace
 */
public class EmptyStringInterceptor extends EmptyInterceptor {
	/**
	 * Version ID for serialization.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Convert any nulls for string fields to "" instead.
	 *
	 * @param state objects being passed in or out
	 * @param types the SQL types of those objects
	 * @return whether we modified the state array
	 */
	private boolean convertEmptyStrings(final Object[] state, final Type[] types) {
		boolean modified = false;
		for (int i = 0; i < state.length; i++) {
			if (types[i] instanceof StringType && state[i] == null) {
				state[i] = "";
				modified = true;
			}
		}
		return modified;
	}

	@Override
	public boolean onLoad(final Object entity, final Serializable id,
			final Object[] state, final String[] propertyNames, final Type[] types) {
		return convertEmptyStrings(state, types);
	}

	@Override
	public boolean onFlushDirty(final Object entity, final Serializable id,
			final Object[] currentState, final Object[] previousState,
			final String[] propertyNames, final Type[] types) {
		return convertEmptyStrings(currentState, types);
	}

	@Override
	public boolean onSave(final Object entity, final Serializable id,
			final Object[] state, final String[] propertyNames, final Type[] types) {
		return convertEmptyStrings(state, types);
	}
}
