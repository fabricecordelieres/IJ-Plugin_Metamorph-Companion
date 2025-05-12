package utilities;

/**
*
*  filenameMatcher v1, 2 nov. 2020 
   Fabrice P Cordelieres, fabrice.cordelieres at gmail.com
   
   Copyright (C) 2020 Fabrice P. Cordelieres
 
   License:
   This program is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 3 of the License, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program.  If not, see <http://www.gnu.org/licenses/>.
*
*/

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ij.IJ;
import ndFile.ndFile;

/**
 * This class is aimed at extracting and storing basic informations from the input Metamorph file
 * @author fab
 *
 */
public class filenameMatcher {
	/** The working folder **/
	public String path="";
	
	/** The working filename **/
	public String filename="";
	
	
	/** File's basename **/
	public String basename="";
	
	/** Wavelength number **/
	public int waveNumber=1;

	/** Wavelength name **/
	public String waveName="";
	
	/** Stage position number **/
	public int stagePosition=1;
	
	/** Stage position name **/
	public String stagePositionName="";
	
	/** Timepoint number **/
	public int timePoint=1;
	
	/** The structure of the filename according to Metamorph **/
	public Pattern p=Pattern.compile("(?<basename>.*?)_(w(?<waveNumber>\\d)(?<waveName>.*?)_*)*(s(?<stagePosition>\\d*)_*)*(t(?<timePoint>\\d*)_*)*\\.TIF");
	
	/**
	 * Creates a new filenameMatcher objects, parses the filename and extracts relevant informations from the nd file
	 * @param path the folder in which data (images and nd files) are stored
	 * @param filename name of the image to analyze
	 */
	public filenameMatcher(String path, String filename) {
		this.path=path;
		this.filename=filename;
		parseFileName();
	}
	
	/**
	 * Performs the parsing of the filename and extracts relevant informations from the nd file
	 */
	public void parseFileName() {
		/** Prepare to match the filename and the pattern**/
		Matcher m=p.matcher(filename);
		
		if (m.find( )) {
			//Extract informations from the filename
			basename=m.group("basename");
			ndFile nd= new ndFile(path, basename+".nd");
			
			if(m.group("waveNumber")!=null) {
				waveNumber=Integer.parseInt(m.group("waveNumber"));
				waveName=nd.Waves.get(waveNumber-1).WaveName;
			}
			
			if(m.group("stagePosition")!=null){
				stagePosition=Integer.parseInt(m.group("stagePosition"));
				stagePositionName=nd.Stage.get(stagePosition-1);
			}
			timePoint=m.group("timePoint")!=null?Integer.parseInt(m.group("timePoint")):timePoint;
	      } else {
	         IJ.log(filename+": No match found in the filename: are you sure this is a Metamorph MDA-generated file ?");
	      }
		
		
	}
	
	@Override
	public String toString() {
		return 		"Path: "+path+"\n"
				+	"FileName= "+filename+"\n"
				+	"BaseName: "+basename+"\n"
				+	"WaveNumber: "+waveNumber+"\n"
				+	"WaveName: "+waveName+"\n"
				+	"StagePosition: "+stagePosition+"\n"
				+	"StagePositionName: "+stagePositionName+"\n"
				+	"TimePoint: "+timePoint;
	}

}
