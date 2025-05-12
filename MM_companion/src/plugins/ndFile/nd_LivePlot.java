package plugins.ndFile;

import ij.IJ;
import ij.io.OpenDialog;

/**
*
*  extractLivePlot v1, 29 oct. 2020 
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

import ij.plugin.PlugIn;
import ndLivePlot.ndWatchNPlot;

/**
 * This plugins implements possibilities to live analyze/plot data from a nd acquisition
 * @author fab
 *
 */
public class nd_LivePlot implements PlugIn{
	
	@Override
	public void run(String arg0) {
		OpenDialog od=new OpenDialog("Choose the nd file to analyze");
		
		if(od.getDirectory()!=null) {
			if(od.getFileName().toLowerCase().endsWith(".nd")) {
				ndWatchNPlot nwnp=new ndWatchNPlot(od.getDirectory(), od.getFileName());
				nwnp.watchFolder();
			}else {
				IJ.error("The selected file is not a Metamorph nd file");
			}
		}
	}
}
