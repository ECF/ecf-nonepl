/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.android;


/**
 * A unique ID class based upon Long/long
 * 
 */
public class LongID extends BaseID {
	private static final long serialVersionUID = 4049072748317914423L;

	Long value = null;

	public static class LongNamespace extends Namespace {
		private static final long serialVersionUID = -1580533392719331665L;

		public LongNamespace() {
			super(LongID.class.getName(), "LongID Namespace"); //$NON-NLS-1$
		}

		private String getInitFromExternalForm(Object[] args) {
			if (args == null || args.length < 1 || args[0] == null)
				return null;
			if (args[0] instanceof String) {
				String arg = (String) args[0];
				if (arg.startsWith(getScheme() + Namespace.SCHEME_SEPARATOR)) {
					int index = arg.indexOf(Namespace.SCHEME_SEPARATOR);
					if (index >= arg.length())
						return null;
					return arg.substring(index + 1);
				}
			}
			return null;
		}

		/**
		 * @param args must not be <code>null></code>
		 * @return ID created.  Will not be <code>null</code>.
		 * @throws IDCreateException never thrown
		 */
		@Override
		public ID createInstance(Object[] args) throws IDCreateException {
			try {
				String init = getInitFromExternalForm(args);
				if (init != null)
					return new LongID(this, Long.decode(init));
				return new LongID(this, (Long) args[0]);
			} catch (Exception e) {
				throw new IDCreateException(NLS.bind("{0} createInstance()", getName()), e); //$NON-NLS-1$
			}
		}

		@Override
		public String getScheme() {
			return LongID.class.toString();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.ecf.core.identity.Namespace#getSupportedParameterTypesForCreateInstance()
		 */
		@Override
		public Class[][] getSupportedParameterTypes() {
			return new Class[][] {{Long.class}};
		}
	}

	protected LongID(Namespace n, Long v) {
		super(n);
		value = v;
	}

	protected LongID(Namespace n, long v) {
		super(n);
		value = new Long(v);
	}

	@Override
	protected int namespaceCompareTo(BaseID o) {
		Long ovalue = ((LongID) o).value;
		return value.compareTo(ovalue);
	}

	@Override
	protected boolean namespaceEquals(BaseID o) {
		if (!(o instanceof LongID))
			return false;
		LongID obj = (LongID) o;
		return value.equals(obj.value);
	}

	@Override
	protected String namespaceGetName() {
		return value.toString();
	}

	@Override
	protected int namespaceHashCode() {
		return value.hashCode();
	}

	public long longValue() {
		return value.longValue();
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("LongID["); //$NON-NLS-1$
		sb.append(value).append("]"); //$NON-NLS-1$
		return sb.toString();

	}
}