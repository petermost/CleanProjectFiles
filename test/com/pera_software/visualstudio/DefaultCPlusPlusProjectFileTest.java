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

import java.nio.file.*;
import java.util.*;

//##################################################################################################

public class DefaultCPlusPlusProjectFileTest extends CPlusPlusProjectFileTest
{
	public static final Path PATH = Resource.getPath( DefaultCPlusPlusProjectFileTest.class,  "CPlusPlusProjectWithDefaultOutputDirectories.vcxproj" );

	//==============================================================================================

	@Override
	public CPlusPlusProjectFile loadProjectFile()
		throws Exception
	{
		return new CPlusPlusProjectFile( PATH );
	}

	//==============================================================================================

	@Override
	public void testFindBuildConfigurationNames()
		throws Exception
	{
		assertBuildConfigurationNames( Arrays.asList( "Debug", "Release" ));
	}

	//==============================================================================================

	@Override
	public void testFindIntermediateDirectoryNames()
		throws Exception
	{
		assertIntermediateDirectoryNames( Arrays.asList( "$(Configuration)\\" ));
	}

	//==============================================================================================

	@Override
	public void testFindTargetName()
		throws Exception
	{
		assertTargetName( "CPlusPlusProjectWithDefaultOutputDirectories" );
	}

	//==============================================================================================

	@Override
	public void testFindOutputDirectoryNames()
		throws Exception
	{
		assertOutputDirectoryNames( Arrays.asList( "$(SolutionDir)..\\deploy\\$(Configuration)\\lib\\" ));
	}

	//==============================================================================================

	@Override
	public void testFindPreBuildCommands()
		throws Exception
	{
		assertPostBuildCommands( Arrays.asList() );
	}

	//==============================================================================================

	@Override
	public void testFindPostBuildCommands()
		throws Exception
	{
		assertPostBuildCommands( Arrays.asList() );
	}

	//==============================================================================================

	@Override
	public void testFindDeployDirectoryNames()
		throws Exception
	{
		assertDeployDirectoryNames( Arrays.asList() );
	}
}
