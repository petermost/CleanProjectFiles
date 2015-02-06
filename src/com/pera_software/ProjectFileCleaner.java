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
import com.pera_software.aidkit.collection.*;
import com.pera_software.aidkit.nio.file.DirectoryTreeDeleter.DeletionMode;
import com.pera_software.aidkit.nio.file.*;
import com.pera_software.aidkit.nio.file.Paths;
import com.pera_software.aidkit.visualstudio.*;

//##################################################################################################

public class ProjectFileCleaner
{
	//==============================================================================================

	public static void main( String arguments[] )
		throws Exception
	{
		Console.printStatus( "CleanProjectFiles V1.1 (c) by P. Most, PERA Software Solutions GmbH" );
		if ( !Settings.parseCommandLine( arguments )) {
			showUsage( Settings.usage() );
			return;
		}
		// Iterate over the given solution file names and delete the temporary files from the projects:

		List< Path > outputDirectoryPaths = new ArrayList<>();
		for ( String solutionFileName : Settings.solutionFileNames() ) {
			SolutionFile solutionFile = new SolutionFile( Paths.get( solutionFileName ));
			List< ProjectFile > projectFiles = solutionFile.findProjects();

			Console.printStatus( "Deleting temporary files of %d projects in '%s'", projectFiles.size(), solutionFile.path() );
			printOutputDirectories( solutionFile, projectFiles );

			// Get the output directories from the projects:

			for ( ProjectFile projectFile : projectFiles ) {
				List< Path > outputDirectories = collectOutputDirectories( solutionFile, projectFile );
				outputDirectories = Paths.normalize( outputDirectories );
				outputDirectories = Lists.removeDuplicates( outputDirectories );
				outputDirectories = Paths.removeOverlaps( outputDirectories );
				outputDirectoryPaths.addAll( outputDirectories );
			}
		}
		// Remove redundant directories:

		DeletionMode deletionMode = Settings.isSimulation() ? DeletionMode.Simulation : DeletionMode.Real;
		outputDirectoryPaths = Lists.removeDuplicates( outputDirectoryPaths );
		outputDirectoryPaths = Paths.removeOverlaps( outputDirectoryPaths );
		deleteDirectories( outputDirectoryPaths, deletionMode );
		Console.printStatus( "Finished deleting." );
	}

	//==============================================================================================

	private static List< Path > collectOutputDirectories( SolutionFile solutionFile, ProjectFile projectFile )
		throws Exception
	{
		List< Path > outputPaths = new ArrayList<>();
		List< OutputDirectory > outputDirectories = projectFile.collectOutputDirectories( solutionFile.path() );
		for ( OutputDirectory outputDirectory : outputDirectories ) {
			outputPaths.addAll( outputDirectory.paths() );
		}
		return outputPaths;
	}

	//==============================================================================================

	private static void printOutputDirectories( SolutionFile solutionFile, List< ProjectFile > projectFiles )
		throws Exception
	{
		if ( Settings.isVerbose() ) {
			for ( ProjectFile projectFile : projectFiles ) {
				printProjectBuildConfigurations( projectFile );
				List< OutputDirectory > outputDirectories = projectFile.collectOutputDirectories( solutionFile.path() );
				Console.printStatus( "Output directories for '%s': ", projectFile.path() );
				for ( OutputDirectory outputDirectory : outputDirectories ) {
					if ( !outputDirectory.paths().isEmpty() ) {
						Console.printStatus( "\t%s: ", outputDirectory.name() );
						for ( Path outputDirectoryPath : outputDirectory.paths() ) {
							Console.printStatus( "\t\t%s", outputDirectoryPath );
						}
					}
				}
			}
		}
	}

	//==============================================================================================

	private static void printProjectBuildConfigurations( ProjectFile projectFile )
		throws Exception
	{
		if ( Settings.isVerbose() ) {
			List< String > buildConfigurationNames = projectFile.getBuildConfigurationNames();
			String buildConfigurations = String.join( ", ", buildConfigurationNames );
			Console.printStatus( "Build configurations for '%s': { %s }", projectFile.path(), buildConfigurations );
		}
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
			Console.printError( "Deleting the file/directory '%s' failed because: '%s'!", file, exception.getClass().getSimpleName() );
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

				if ( Settings.isVerbose() ) {
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
