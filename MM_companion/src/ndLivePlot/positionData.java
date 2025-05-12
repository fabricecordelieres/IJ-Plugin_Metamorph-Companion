package ndLivePlot;

/**
*
*  positionData v1, 3 nov. 2020 
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import ij.process.ImageStatistics;

/**
 * This class is aimed at storing temporal data obtained over a single position
 * @author fab
 *
 */
public class positionData {
	/** Stores the position's name **/
	String positionName="";
	
	/** Stores the waveNames associated to the position **/
	String[] waveName=null;
	
	/** Store the timepointData, per channel **/
	ArrayList<List<timepointData>> data=null;
	
	/**
	 * Creates a new positionData object from input data
	 * @param positionName the name of current position
	 * @param nWaves the number of wavelengths associated to current position
	 */
	public positionData(String positionName, String[] waveName) {
		int nWaves=Math.max(1, waveName.length);
		this.positionName=positionName;
		waveName=new String[waveName.length];
		data=new ArrayList<List<timepointData>>();
		
		for(int i=0; i<nWaves; i++) data.add(new ArrayList<timepointData>());
	}

	/*
	 * Adds a new data point to the set
	 * @param channel the channel number (starting from 0)
	 * @param timepointNumber the timepoints number
	 * @param realTime the creation date of the image
	 * @param is the ImageStatistics object associated to the image
	 */
	public void addData(int channel, int timepointNumber, Date realTime, ImageStatistics is) {
		data.get(channel).add(new timepointData(timepointNumber, realTime, is));
		Collections.sort(data.get(channel)); //Keeps data sorted
	}
	
	/**
	 * Returns the timepoints associated to the input channel
	 * @param channel the channel to focus on
	 * @return the timepoints as an array of double
	 */
	public double[] getTimepoints(int channel) {
		double out[]=new double[data.get(channel).size()];
		for(int i=0; i<out.length; i++) out[i]=data.get(channel).get(i).timepointNumber;
		return out;
	}
	
	/**
	 * Returns the mean intensity associated to the input channel
	 * @param channel the channel to focus on
	 * @return the timepoints as an array of double
	 */
	public double[] getMeanIntensity(int channel) {
		double out[]=new double[data.get(channel).size()];
		for(int i=0; i<out.length; i++) out[i]=data.get(channel).get(i).is.umean;
		return out;
	}
}
