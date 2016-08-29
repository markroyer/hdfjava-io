/**
 * Copyright (C) Apr 19, 2014 Mark Royer
 *
 * This file is part of OctaveInterface.
 *
 * OctaveInterface is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OctaveInterface is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with OctaveInterface.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.umaine.cs.h5;

/**
 * @author Mark Royer
 * 
 */
public class TypeException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6286415898272740042L;

	private String type;

	/**
	 * @param type
	 */
	public TypeException(Class<?> type) {
		this(type, "");
	}

	/**
	 * @param type
	 * @param message
	 */
	public TypeException(Class<?> type, String message) {
		this(type.getName(), message);
	}

	public TypeException(String message) {
		super(message);
	}

	public TypeException(String type, String message) {
		this(message + (type != null ? " Type was " + type : ""));
		this.type = type;
	}

	/**
	 * @return May be null if type not specified
	 */
	public String getType() {
		return type;
	}
}
