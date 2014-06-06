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

import java.util.*;
import java.nio.file.*;
import java.util.concurrent.*;
import com.pera_software.aidkit.*;
import com.pera_software.aidkit.DirectoryTreeDeleter.DeletionMode;
import com.pera_software.aidkit.nio.file.Paths;
import com.pera_software.visualstudio.*;

//##################################################################################################

public class ProjectFileCleaner
{
	//==============================================================================================

	public static void main( String arguments[] )
		throws Exception
	{
		Console.printStatus( "CleanProjectFiles (c) by P. Most, PERA Software Solutions GmbH" );
		if ( !Settings.instance().parseCommandLine( arguments )) {
			showUsage( Settings.instance().usage() );
			return;
		}
		// Iterate over the given solution file names and delete the temporary files from the projects:

		List< Path > outputDirectories = new ArrayList<>();
		for ( String solutionFileName : Settings.instance().solutionFileNames() ) {
			Path solutionFilePath = java.nio.file.Paths.get( solutionFileName );
			SolutionFile solutionFile = new SolutionFile( solutionFilePath );
			List< ProjectFile > projectFiles = solutionFile.findProjects();
			Console.printStatus( "Deleting temporary files of %d projects in '%s'", projectFiles.size(), solutionFilePath );

			// Get the output directories from the projects:

			for ( ProjectFile projectFile : projectFiles ) {
				List< Path > projectOutputDirectories = projectFile.findAllOutputDirectories( solutionFilePath );
				projectOutputDirectories = Paths.normalize( projectOutputDirectories );
				projectOutputDirectories = Paths.removeDuplicates( projectOutputDirectories );
				projectOutputDirectories = Paths.removeOverlaps( projectOutputDirectories );
				outputDirectories.addAll( projectOutputDirectories );

				// Show the directories per project:

				Console.printStatus( "Output directories for '%s':", projectFile.path() );
				for ( Path projectOutputDirectory : projectOutputDirectories ) {
					Console.printStatus( "\t%s", projectOutputDirectory );
				}
			}
		}
		// Remove redundant directories:

		DeletionMode deletionMode = Settings.instance().isSimulation() ? DeletionMode.Simulation : DeletionMode.Real;
		outputDirectories = Paths.removeDuplicates( outputDirectories );
		outputDirectories = Paths.removeOverlaps( outputDirectories );
		deleteDirectories( outputDirectories, deletionMode );
		Console.printStatus( "Finished deleting." );
	}

	//==============================================================================================

	private static void showUsage( List< String > usageLines )
	{
		Console.printError( "Usage:" );
		for ( String usageLine : usageLines ) {
			Console.printError( "\t%s", usageLine );
		}
	}

	//==============================================================================================

	private static void fileDeletionFailed( Path file, Exception exception )
	{
		if ( exception instanceof DirectoryNotEmptyException ) {
			Console.printError( "Deleting the directory '%s' failed because it is not empty!", file );
		} else {
			Console.printError( "Deleting '%s' failed because: '%s'!", file, exception.getClass().getSimpleName() );
		}
	}

	//==============================================================================================

	private static void fileDeleted( Path file )
	{
		Console.printStatus( "Deleted '%s'", file );
	}

	//==============================================================================================

	private static void deleteDirectories( List< Path > outputDirectories, DeletionMode deletionMode )
		throws Exception
	{
		// Prepare the directory deleter tasks:

		List< Callable< Object >> deleters = new ArrayList<>();
		for ( final Path outputDirectory : outputDirectories ) {
			if ( !outputDirectory.toFile().exists() ) {
				Console.printError( "Skipping non-existing directory '%s'.", outputDirectory );
				continue;
			}
			deleters.add( () -> {
				Console.printStatus( "Deleting directory '%s'", outputDirectory );
				DirectoryTreeDeleter treeDeleter = new DirectoryTreeDeleter( deletionMode );
				treeDeleter.fileDeletionFailedSignal.connect( ProjectFileCleaner::fileDeletionFailed );

				if ( Settings.instance().isVerbose() ) {
					treeDeleter.fileDeletedSignal.connect( ProjectFileCleaner::fileDeleted );
				}
				Files.walkFileTree( outputDirectory, treeDeleter );
				return null;
			});
		}
		// Execute all deleter tasks:

		if ( deletionMode == DeletionMode.Real ) {
			ExecutorService executor = Executors.newFixedThreadPool( Runtime.getRuntime().availableProcessors() );
			executor.invokeAll( deleters );
			executor.shutdown();
		}
	}
}
