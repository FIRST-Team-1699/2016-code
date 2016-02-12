/*
FIRST Team 1699's ini Reader

v1.0rc1, published 2/10/16
*/
package org.usfirst.frc.team1699.robot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.io.BufferedReader;

public class iniReader
{
	@SuppressWarnings("rawtypes")
	ArrayList<ArrayList> iniContents;
	File iniFile;
	BufferedReader reader;
	
	// Initializers for object
	public iniReader(String file)
	{
		iniFile = new File("/home/lvuser/" + file);
	}
	
	public iniReader(String dir, String file)
	{
		iniFile = new File("" + dir + file);
	}
	
	// This method returns the entire ArrayList
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ArrayList<ArrayList> getFile(){
		// Declarations and clearing variables for multiple file use
		int count1;
		char indexCh;
		String section1, section2;
		reader = null;
		iniContents = new ArrayList<ArrayList>();
		
		// Open file
		try 
		{
			// Make BufferedReader and get new line, then start reading all the lines
			reader = new BufferedReader(new FileReader (iniFile));
			String line = reader.readLine();
			while (line != null) 
			{
				// Tests for blank line or newline
				if ((line.equals("")) || (line.equals(" ")) || (line.equals("\n")) || (line.equals("\r\n")))
				{
					line = reader.readLine();
					continue;
				}
				
				// Checks for comment
				if ((line.substring(0, 2).equals("//")) || (line.substring(0, 1).equals("#")))
				{
					line = reader.readLine();
					continue;
				}
				
				// Makes ArrayList with string and double
				ArrayList lineData = new ArrayList();
				count1 = 0;
				while (count1 != line.length())
				{
					indexCh = line.charAt(count1);
					if (indexCh == ':')
					{
						// Checks for a space between the definition and the colon
						indexCh = line.charAt(count1 - 1);
						if (indexCh == ' ') {section1 = line.substring(0, count1 - 1);}
						else {section1 = line.substring(0, count1);}
						// Checks for a space after the colon
						indexCh = line.charAt(count1 + 1);
						if (indexCh == ' ') {section2 = line.substring(count1 + 2, line.length());}
						else {section2 = line.substring(count1 + 1, line.length());}
						lineData.add(section1);
						lineData.add(Double.parseDouble(section2));
						break;
					}
					count1 += 1;
				}
				iniContents.add(lineData);
				
				// Must be at the end to read a new line
				line = reader.readLine();
				
			}
		} 
		// Exceptions and debugging
		catch (FileNotFoundException e) {e.printStackTrace();}
		catch (IOException e) {e.printStackTrace();}
		finally {
			try {
				// Closes file
				if (reader != null) {reader.close();}
			} 
			catch (IOException e) {e.printStackTrace();}
			}
		return iniContents;
	}
	
	// This method only returns the value attached to a String
	public double getValue(String name)
	{
		// Initializes variables
		int count1 = 0;
		double result = -101.314159;
		
		// Checks if getFile() has been run
		try {
			if (reader.equals(null)) {count1 = 0;}
		} catch (NullPointerException e) {this.getFile();}
				
		// Runs through ArrayList
		while (count1 != (iniContents.size()))
		{
			// Checks for equality to parameter
			if (iniContents.get(count1).get(0).equals(name))
			{
				result = (double) iniContents.get(count1).get(1);
				break;
			}
		}
		// Return, nothing special here
		return result;
	}
}