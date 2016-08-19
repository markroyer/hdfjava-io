/**
 * Copyright (C) Jul 16, 2014 Mark Royer
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

import java.util.Arrays;

/**
 * @author Mark Royer
 *
 */
public class EmptyMatrix {

	private int[] dimensions;

	/**
	 * if dims is omitted, or dims = {}, or dims = {0}, then dims is set to
	 * {0,0}.
	 * 
	 * @param dims
	 * @throws TypeException
	 */
	public EmptyMatrix(int... dims) throws TypeException {
		if (!containsZero(dims) && dims != null && dims.length != 0)
			throw new TypeException(EmptyMatrix.class,
					"Requires at least one dimension to be zero.");

		if (dims == null || dims.length == 0 || dims.length == 1)
			this.dimensions = new int[] { 0, 0 };
		else
			this.dimensions = dims;
	}

	private boolean containsZero(int[] dims) {
		for (int i = 0; i < dims.length; i++) {
			if (dims[i] == 0)
				return true;
		}
		return false;
	}

	public int[] getDimensions() {
		return dimensions;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof EmptyMatrix) {
			EmptyMatrix other = (EmptyMatrix) obj;
			return Arrays.equals(this.dimensions, other.dimensions);
		} else {
			return false;
		}
	}

}
