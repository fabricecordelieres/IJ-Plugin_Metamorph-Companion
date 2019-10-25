/*
 *  adaptStgPos.java
 * 
 *  Created on 7 mai 2013, 15:53:44
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
import stgFile.stgFileTransformer;

/**
 * This plugin aims at adapting a STG file, based on a reference STG file
 * @author fab
 */
public class adaptStgPos implements PlugIn{
    /** Open dialog **/
    OpenDialog od=null;
    /** STG reference file **/
    public stgFile stgRef=null;
    /** STG file to adapt **/
    public stgFile stg2Adapt=null;
    
    /**
     * This method is called when the plugin is loaded.
     * @param arg argument specified for this plugin in IJ_Props.txt (may be blank)
     */
    @Override
    public void run(String arg) {
        if(openRef()) if(openStg2Adapt()) if(GUI()) saveFile();
    }
    
    /**
     * Open the reference STG file
     * @return true if everything went well, false otherwise
     */
    public boolean openRef(){
        od=new OpenDialog("Reference : open STG file", null);
        
        if(od.getFileName()==null){
            IJ.showStatus("--- Stage positions file adaptation cancelled ---");
            return false;
        }
        
        stgRef=new stgFile(od.getDirectory(), od.getFileName());
        if(stgRef.nStagePositions()<3){
            IJ.error("Adapt Stage Position File", "The reference stage position file\n"
                                                + "should contain at least 3 positions.");
            IJ.showStatus("--- Stage positions file adaptation cancelled ---");
            return false;
        }
        return true;
    }
    
    /**
     * Open the STG file to adapt
     * @return true if everything went well, false otherwise
     */
    public boolean openStg2Adapt(){
        od=new OpenDialog("File_to_adapt : open STG file", null);
        
        if(od.getFileName()==null){
            IJ.showStatus("--- Stage positions file adaptation cancelled ---");
            return false;
        }
        
        stg2Adapt=new stgFile(od.getDirectory(), od.getFileName());
        if(stg2Adapt.nStagePositions()<4){
            IJ.error("Adapt Stage Position File", "The stage position file to adapt\n"
                                                + "should contain at least 4 positions\n"
                                                + "(3 reference positions and\n"
                                                + "a position to adapt).");
            IJ.showStatus("--- Stage positions file adaptation cancelled ---");
            return false;
        }
        return true;
    }
    
    /**
     * Graphical user interface, asking the correspondance between the reference STG and the STG to adapt
     * @return true if the dialog was OKed, false otherwise
     */
    public boolean GUI(){
        GenericDialog gd=new GenericDialog("Adapt Stage Position File");
        gd.addMessage("Reference positions file");
        gd.addChoice("Ref_Reference_position_1_(TOP_LEFT)", stgRef.getStagePositionNames(), stgRef.getStagePositionNames()[0]);
        gd.addChoice("Ref_Reference_position_2_(TOP_RIGHT)", stgRef.getStagePositionNames(), stgRef.getStagePositionNames()[1]);
        gd.addChoice("Ref_Reference_position_3_(BOTTOM_LEFT)", stgRef.getStagePositionNames(), stgRef.getStagePositionNames()[2]);
        gd.addMessage("Stage positions file to adapt");
        gd.addChoice("Adapt_Reference_position_1", stg2Adapt.getStagePositionNames(), stg2Adapt.getStagePositionNames()[0]);
        gd.addChoice("Adapt_Reference_position_2", stg2Adapt.getStagePositionNames(), stg2Adapt.getStagePositionNames()[1]);
        gd.addChoice("Adapt_Reference_position_3", stg2Adapt.getStagePositionNames(), stg2Adapt.getStagePositionNames()[2]);
        
        gd.showDialog();
        
        if(gd.wasOKed()){
            int[] refPos=new int[3];
            int[] initPos=new int[3];
            
            refPos[0]=gd.getNextChoiceIndex();
            refPos[1]=gd.getNextChoiceIndex();
            refPos[2]=gd.getNextChoiceIndex();
            
            initPos[0]=gd.getNextChoiceIndex();
            initPos[1]=gd.getNextChoiceIndex();
            initPos[2]=gd.getNextChoiceIndex();
            
            stgFileTransformer sft=new stgFileTransformer(stg2Adapt);
            sft.adapt(stgRef, refPos, initPos);
            return true;
        }else{
            return false;
        }
    }
    
    /**
     * Save the adapted STG file
     * @return true if everything went well, false otherwise
     */
    public boolean saveFile(){
        SaveDialog sd=new SaveDialog("Save : where to save the adapted STG file?", "Adapted_"+od.getFileName(), ".STG");
            if(sd.getFileName()!=null){
                stg2Adapt.write(sd.getDirectory()+sd.getFileName());
                IJ.showStatus("--- Adapted stage positions file saved ---");
                return true;
            }else{
                IJ.showStatus("--- Stage positions file adaptation cancelled ---");
                return false;
            }
    }
}
