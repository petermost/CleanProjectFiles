// Copyright 2014 Peter Most, PERA Software Solutions GmbH
//
// This file is part of the CleanProjectFiles program.
//
// CleanProjectFiles is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// CleanProjectFiles is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with CleanProjectFiles.  If not, see <http://www.gnu.org/licenses/>.

package com.pera_software.visualstudio;

import java.net.*;
import java.nio.file.*;

public class Resource
{
	public static Path getPath( Class< ? > parentClass, String resourceName )
	{
		try {
			URL resourceUrl = parentClass.getResource( resourceName );
			return Paths.get( resourceUrl.toURI() );
		} catch ( Exception exception ) {
			// This method is mostly used to initialize a static final variable were you can't
			// handle exceptions, so we throw an error instead:

			throw new Error( exception );
		}
	}
}
