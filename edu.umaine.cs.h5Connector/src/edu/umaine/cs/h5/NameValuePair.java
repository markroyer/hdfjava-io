/**
 * Copyright (C) Feb 19, 2014 Mark Royer
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

import java.io.Serializable;

/**
 * Represents a name and value pair.
 * 
 * @author Mark Royer
 *
 */
public class NameValuePair implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String name;
	
	Object value;

	/**
	 * @param name (Not null)
	 * @param value (Not null)
	 */
	public NameValuePair(String name, Object value) {
		super();
		this.name = name;
		this.value = value;
	}

	/**
	 * @return the name (Never null)
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the value (Never null)
	 */
	public Object getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "{ \"" + name + "\" : " + value + " }";
	}
	
	
	
}
