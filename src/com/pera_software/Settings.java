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
import com.pera_software.aidkit.*;

//##################################################################################################

public class Settings
{
	//==============================================================================================

	private static Settings s_instance = new Settings();

	@Option( name = "-v", aliases = "--verbose", usage = "show more verbose messages" )
	private boolean _isVerbose = false;

	@Option( name = "-s", aliases = "--simulate", usage = "only simulate the deletion" )
	private boolean _isSimulation = false;

	@Argument( metaVar = "solutionFileName...", required = true, usage = "the solution file name(s)" )
	private List< String > _solutionFileNames = new ArrayList<>();

	private CmdLineParser _commandLineParser = new CmdLineParser( this );

	//==============================================================================================

	Settings()
	{
	}

	//==============================================================================================

	public static Settings instance()
	{
		return s_instance;
	}

	//==============================================================================================

	public boolean parseCommandLine( String commandLineArguments[] )
	{
		try {
			_commandLineParser.parseArgument( commandLineArguments );
			return true;
		} catch ( CmdLineException exception ) {
			return false;
		}
	}

	//==============================================================================================

	public boolean isVerbose()
	{
		return _isVerbose;
	}

	//==============================================================================================

	public boolean isSimulation()
	{
		return _isSimulation;
	}

	//==============================================================================================

	public List< String > solutionFileNames()
	{
		return _solutionFileNames;
	}

	//==============================================================================================

	public List< String > usage()
	{
		StringWriter usageWriter = new StringWriter();
		_commandLineParser.printUsage( usageWriter, null );
		String usage = usageWriter.toString();
		return Arrays.asList( usage.split( SystemProperties.getLineSeparator() ));
	}
}
