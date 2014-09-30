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

import static org.junit.Assert.*;
import java.util.*;
import org.junit.*;
import static org.hamcrest.core.Is.*;

//##################################################################################################

public class SettingsTest
{
	//==============================================================================================

	@Test
	@SuppressWarnings("static-method")
	public void testFailingParseCommandLine()
	{
		String commandLineArguments[] = { "-x" };
		Settings settings = new Settings();
		assertFalse( settings.parseCommandLine( commandLineArguments ));
	}

	//==============================================================================================

	@Test
	@SuppressWarnings("static-method")
	public void testShortVerboseOption()
	{
		String commandLineArguments[] = { "-v" };
		Settings settings = new Settings();
		settings.parseCommandLine( commandLineArguments );
		assertTrue( settings.isVerbose() );
	}

	//==============================================================================================

	@Test
	@SuppressWarnings("static-method")
	public void testLongVerboseOption()
	{
		String commandLineArguments[] = { "--verbose" };
		Settings settings = new Settings();
		settings.parseCommandLine( commandLineArguments );
		assertTrue( settings.isVerbose() );
	}

	//==============================================================================================

	@Test
	@SuppressWarnings("static-method")
	public void testShortSimulationOption()
	{
		String commandLineArguments[] = { "-s" };
		Settings settings = new Settings();
		settings.parseCommandLine( commandLineArguments );
		assertTrue( settings.isSimulation() );
	}

	//==============================================================================================

	@Test
	@SuppressWarnings("static-method")
	public void testLongSimulationOption()
	{
		String commandLineArguments[] = { "--simulate" };
		Settings settings = new Settings();
		settings.parseCommandLine( commandLineArguments );
		assertTrue( settings.isSimulation() );
	}

	//==============================================================================================

	@Test
	@SuppressWarnings("static-method")
	public void testSolutionFileNames()
	{
		String commandLineArguments[] = { "solution1.sln", "solution2.sln" };
		List< String > expectedSolutionFileNames = Arrays.asList( "solution1.sln", "solution2.sln" );
		Settings settings = new Settings();
		settings.parseCommandLine( commandLineArguments );
		assertThat( settings.solutionFileNames(), is( expectedSolutionFileNames ));
	}

	//==============================================================================================

	@Test
	@SuppressWarnings("static-method")
	public void testUsage()
	{
		Settings settings = new Settings();
		List< String > usage = settings.usage();
		List< String > expectedUsage = Arrays.asList(
			" solutionFileName... : the solution file name(s)",
			" -s (--simulate)     : only simulate the deletion",
			" -v (--verbose)      : show more verbose messages"
		);
		assertThat( usage, is( expectedUsage ));
	}

}
