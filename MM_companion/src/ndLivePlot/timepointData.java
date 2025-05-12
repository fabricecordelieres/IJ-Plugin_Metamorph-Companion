package ndLivePlot;

/**
*
*  timepointData v1, 3 nov. 2020 
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

import java.util.Date;

import ij.process.ImageStatistics;

/**
 * This class implements a way to store informations realted to a timepoint and statistics associated to the image
 * @author fab
 *
 */
public class timepointData implements Comparable<timepointData>{
	/** Timepoint number **/
	int timepointNumber=1;
	
	/** Image creation date **/
	Date realTime=null;
	
	/** ImageStatistics from the image **/
	ImageStatistics is=null;
	
	/**
	 * Creates a new timepointData based on input data
	 * @param timepointNumber the timepoints number
	 * @param realTime the creation date of the image
	 * @param is the ImageStatistics object associated to the image
	 */
	public timepointData(int timepointNumber, Date realTime, ImageStatistics is) {
		this.timepointNumber=timepointNumber;
		this.realTime=realTime;
		this.is=is;
	}

	@Override
	public int compareTo(timepointData o) {
		if(o.timepointNumber==timepointNumber && o.realTime==realTime) return 0;
		if(o.timepointNumber>timepointNumber) return -1;
		return 1;
	}

}
