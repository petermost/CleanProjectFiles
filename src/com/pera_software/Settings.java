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

package com.pera_software;

import java.io.*;
import java.util.*;
import org.kohsuke.args4j.*;
import com.pera_software.aidkit.lang.*;

//##################################################################################################

public final class Settings {

	private static class Attributes {
		@Option( name = "-v", aliases = "--verbose", usage = "show more verbose messages" )
		private boolean isVerbose = false;

		@Option( name = "-s", aliases = "--simulate", usage = "only simulate the deletion" )
		private boolean isSimulation = false;

		@Argument( metaVar = "solutionFileName...", required = true, usage = "the solution file name(s)" )
		private List< String > solutionFileNames = new ArrayList<>();
	}

	private static Attributes _attributes = new Attributes();
	private static CmdLineParser _commandLineParser = new CmdLineParser( _attributes );

	//==============================================================================================

	Settings() {
	}

	//==============================================================================================

	public static boolean parseCommandLine( String commandLineArguments[] ) {
		try {
			_commandLineParser.parseArgument( commandLineArguments );
			return true;
		} catch ( CmdLineException exception ) {
			return false;
		}
	}

	//==============================================================================================

	public static boolean isVerbose() {
		return _attributes.isVerbose;
	}

	//==============================================================================================

	public static boolean isSimulation() {
		return _attributes.isSimulation;
	}

	//==============================================================================================

	public static List< String > solutionFileNames() {
		return _attributes.solutionFileNames;
	}

	//==============================================================================================

	public static List< String > usage() {
		StringWriter usageWriter = new StringWriter();
		_commandLineParser.printUsage( usageWriter, null );
		String usage = usageWriter.toString();
		return Arrays.asList( usage.split( SystemProperties.getLineSeparator() ));
	}
}
