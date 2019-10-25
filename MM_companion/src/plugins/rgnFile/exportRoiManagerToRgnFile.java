/*
 *  exportRoiManagerToRgnFile.java
 * 
 *  Created on 4 sept. 2012, 13:40:39
 * 
 *  Copyright (C) 2012 Fabrice P. Cordelieres
 *   
 *  License:
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.

 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.

 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 
 * 

 */

package plugins.rgnFile;

import ij.IJ;
import ij.gui.Roi;
import ij.plugin.PlugIn;
import ij.plugin.frame.RoiManager;
import rgnFile.content.roi;
import rgnFile.rgnFile;

/**
 * This plugin allows exporting the current RoiManager/Rois selection within the 
 * RoiManager to a Metamroph rgn file
 * @author fab
 */
public class exportRoiManagerToRgnFile implements PlugIn{
    
    /**
     * This method is called when the plugin is loaded.
     * @param arg argument specified for this plugin in IJ_Props.txt (may be blank)
     */
    @Override
    public void run(String arg) {
        RoiManager rm= RoiManager.getInstance();
        if(rm==null){
            IJ.error("Export RoiManager to Metamorph rgn file", "The RoiManager is not opened");
        }else{
            Roi[] rois=rm.getSelectedRoisAsArray();
            if(rois.length==0){
                IJ.error("Export RoiManager to Metamorph rgn file", "No roi to export");
            }else{
                rgnFile rgn=new rgnFile(false);
                for(int i=0; i<rois.length; i++){
                    roi roi=new roi(rois[i]);
                    rgn.addRoi(roi);
                }
                rgn.write();
            }
        }
    }
}
