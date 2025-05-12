package ndLivePlot;

import java.awt.Color;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Plot;
import ij.process.ImageStatistics;
import ndFile.ndFile;
import utilities.filenameMatcher;
import utilities.metadataParser;

/**
*
*  ndWatchNPlot v1, 30 oct. 2020 
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

/**
 * This class will monitor a folder and plot informations from the data it will find there, separating in datasets depending on the time/position/channel
 * @author fab
 *
 */
public class ndWatchNPlot {
	/** Path to the folder to monitor**/
	public String path=null;
	
	/** nd file to analyze **/
	public String ndFile=null;
	
	/** Number of wavelength according to the nd file **/
	int nWaves=1;
	
	/** Number of positions according to the nd file **/
	int nPos=1;
	
	/** Data container for measurements **/
	positionData[] data=null;
	
	/** ImageJ plot in which data will be drawn **/
	Plot plot=null;
	
	/** The legend of the plot **/
	String legend="";
	
	/** Update rate in msec **/
	int updateRate=250;
	
	/** Colors to be used for graphs **/
	public static final Color[] COLORS=new Color[] {Color.green, Color.red, Color.blue, Color.cyan, Color.MAGENTA, Color.yellow, Color.gray, Color.black};
	
	/** Shapes to be used for graphs **/
	public static final String[] SHAPES=new String[] {"line", "connected circle", "circle", "box", "triangle", "diamond", "cross", "x", "dot"};

	/** File filter name aimed at only retaining .tif and .TIF files**/ 
	public FilenameFilter tifFilter=null;
	
	/**
	 * Builds a new ndWatchNPlot object, monitoring the input folder for new tif files
	 * @param path path to the folder to monitor
	 * @param ndFile the nd file to analyze
	 */
	public ndWatchNPlot(String path, String ndFile) {
		setPath(path);
		setNdFile(ndFile);
	}
	
	/**
	 * Sets the folder to monitor
	 * @param path path to the folder to monitor
	 */
	public void setPath(String path) {
		this.path=path;
	}
	
	/**
	 * Sets the folder to monitor
	 * @param path path to the folder to monitor
	 */
	public void setNdFile(String ndFile) {
		this.ndFile=ndFile;
	}
	
	/**
	 * Sets the update rate
	 * @param updateRate the update rate, in msec
	 */
	public void setUpdateRate(int updateRate) {
		this.updateRate=updateRate;
	}
	
	public void initPlot() {
		ndFile nd=new ndFile(path, ndFile);
		nWaves=Math.max(1, nd.NWavelengths);
		nPos=Math.max(1, nd.NStagePositions);
		
		data=new positionData[nPos];
		
		for(int i=0; i<nPos; i++) data[i]=new positionData(nd.Stage.get(i), nd.getWaveNames());
		
		plot=new Plot("nd live plot", "Timepoint", "Intensity (AU)");
		
		for(int pos=0; pos<nPos; pos++) {
			for(int wave=0; wave<nWaves; wave++) {
				if(nWaves==1) {
					plot.setColor(Color.black, Color.black);
				}else {
					plot.setColor(COLORS[(wave)%nWaves], COLORS[(wave)%nWaves]);
				}
				plot.add(SHAPES[(pos)%nPos], data[pos].getTimepoints(wave), data[pos].getMeanIntensity(wave));
				plot.update();
				
				legend+=nd.getPosNames()[pos]+(nWaves>1?"_"+nd.getWaveNames()[wave]:"")+"\n";
			}
		}
		plot.setLimitsToFit(true);
		plot.setColor(Color.black);
		plot.setLegend(legend, Plot.AUTO_POSITION);
		plot.show();
	}
	
	/**
	 * This function looks for new tif files in the target folder then launches the analysis.
	 * It stops when the ESC key has been pressed
	 * @param path path to the folder to monitor
	 */
	public void watchFolder() {
		File f=new File(path);
		
		/**
		 * Filter files based on their extension (tif) and their basename which should be the same as the nd file
		 * This allows handling situations such as having several datasets in the same folder
		 */
		tifFilter=new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".tif") && name.toLowerCase().startsWith(ndFile.toLowerCase().replace(".nd", ""));
			}
		};
		if(f.isDirectory()) {
			initPlot();
			HashSet<File> filesOld=new HashSet<File>(Arrays.asList(f.listFiles(tifFilter)));
			for(File fi:filesOld) performAnalysis(fi.getName());
			
			while(!IJ.escapePressed()) {
				HashSet<File> files=new HashSet<File>(Arrays.asList(f.listFiles(tifFilter)));
				@SuppressWarnings("unchecked")
				HashSet<File> newFiles=(HashSet<File>) files.clone();
				newFiles.removeAll(filesOld);
				
				//Sort the files list
				List<String> list = new ArrayList<String>(newFiles.size());
				for(File fi:newFiles) list.add(fi.getName());
				Collections.sort(list);
				
				for(String fi:list) performAnalysis(fi);
				
				
				filesOld=files;
				
				IJ.wait(updateRate);
			}
			IJ.showStatus("nd live plot has been canceled");
		}
	}
	
	/**
	 * Performs the analysis of the input file
	 * @param filename the name of the file to analyze
	 */
	public void performAnalysis(String filename) {
		//Retrieve informations from the filename
		filenameMatcher fnm=new filenameMatcher(path, filename);
		
		int time=fnm.timePoint;
		int wave=fnm.waveNumber;
		int position=fnm.stagePosition;
		
		//Identifies the index of the ArrayList to update
		int index=(position-1)*nWaves+wave;
		
		ImagePlus ip=new ImagePlus(path+filename);
		//Get metadata: creation time
		metadataParser mdp=new metadataParser(ip.getInfoProperty());
				
		ImageStatistics is= ip.getAllStatistics();
		ip.close();
		
		data[position-1].addData(wave-1, time, mdp.getAcquisitionTime(), is);
		
		double[] dataY=null;
		
		//To be modified, depending on the type of data to extract
		dataY=data[position-1].getMeanIntensity(wave-1);
		
		if(nWaves==1) {
			plot.setColor(Color.black, Color.black);
		}else {
			plot.setColor(COLORS[(wave-1)%nWaves], COLORS[(wave-1)%nWaves]);
		}
		plot.replace(index-1, SHAPES[(position-1)%nPos], data[position-1].getTimepoints(wave-1), dataY);
		plot.setLimitsToFit(true);
		plot.setColor(Color.black);
		plot.setLegend(legend, Plot.AUTO_POSITION);
		plot.update();
	}
}
