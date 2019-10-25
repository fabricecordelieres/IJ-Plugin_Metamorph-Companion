/*
 *  adaptStgPos.java
 * 
 *  Created on 10 mai 2013, 20:07:44
 * 
 *  Copyright (C) 2013 Fabrice P. Cordelieres
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

package plugins.stgPos;

import ij.IJ;
import ij.gui.GenericDialog;
import ij.io.OpenDialog;
import ij.io.SaveDialog;
import ij.plugin.PlugIn;
import stgFile.stgFile;

/**
 * This plugin aims at optimizing the sequence of positions within a STG file, 
 * starting from a reference from the STG file
 * @author fab
 */
public class optimizeStgPos implements PlugIn{
    /** Open dialog **/
    OpenDialog od=null;
    /** STG file **/
    public stgFile stg=null;
    
    /**
     * This method is called when the plugin is loaded.
     * @param arg argument specified for this plugin in IJ_Props.txt (may be blank)
     */
    @Override
    public void run(String arg) {
        if(openFile()) if(GUI()) saveFile();
    }
    
    /**
     * Open the STG file to optimized
     * @return true if everything went well, false otherwise
     */
    public boolean openFile(){
        od=new OpenDialog("Where_is_the_reference stage positions file ?", null);
        
        if(od.getFileName()==null){
            IJ.showStatus("--- Stage positions file optimization cancelled ---");
            return false;
        }
        
        stg=new stgFile(od.getDirectory(), od.getFileName());
        return true;
    }
    
    /**
     * Graphical user interface, asking the position to be used as starting point
     * @return true if the dialog was OKed, false otherwise
     */
    public boolean GUI(){
        GenericDialog gd=new GenericDialog("Optimize Stage Position File");
        gd.addChoice("Starting_position", stg.getStagePositionNames(), stg.getStagePositionNames()[0]);
        
        gd.showDialog();
        
        if(gd.wasOKed()){
            int refPosIndex=gd.getNextChoiceIndex();
            stg=stg.getOptimizedStgFile(refPosIndex);
            
            return true;
        }else{
            return false;
        }
    }
    
    /**
     * Save the optimized STG file
     * @return true if everything went well, false otherwise
     */
    public boolean saveFile(){
        SaveDialog sd=new SaveDialog("Where_to_save the optimized stage positions file?", "Optimized_"+od.getFileName(), ".STG");
            if(sd.getFileName()!=null){
                stg.write(sd.getDirectory()+sd.getFileName());
                IJ.showStatus("--- Optimized stage positions file saved ---");
                return true;
            }else{
                IJ.showStatus("--- Stage positions file optimization cancelled ---");
                return false;
            }
    }
}
