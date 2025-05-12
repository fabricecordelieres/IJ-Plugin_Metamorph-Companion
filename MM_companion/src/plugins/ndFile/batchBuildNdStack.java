/**
 *
 *  buildNdStack v1, 12 mai 2013 
    Fabrice P Cordelieres, fabrice.cordelieres at gmail.com
    
    Copyright (C) 2013 Fabrice P. Cordelieres
  
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

package plugins.ndFile;

import ij.IJ;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.io.DirectoryChooser;
import ij.plugin.PlugIn;
import ij.plugin.ZProjector;
import java.awt.AWTEvent;
import java.awt.Choice;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Vector;
import ndFile.ndFile;
import ndFile.ndStackBuilder;

/**
 * This plugin allows batch building (hyper)stack(s), based on the informations 
 * retrieved from a Metamorph nd files
 * @author fab
 */
public class batchBuildNdStack implements PlugIn, DialogListener{
    /** Input directory **/
    public String inDirectory=null;
    
    /** nd filename **/
    public String fileName=null;
    
    /** List of all nd files to process **/
    public String[] ndFilesList=null;
    
    /** Z mode **/
    String[] zMode={"All slices", "Z project"};
    
    /** Save mode **/
    String[] saveMode={"All channels together", "Each channel separately"};
    
    /** GenericDialog **/
    GenericDialog gd=null;
    
    /** Vector of choices from the GenericDialog **/
    Vector<Choice> c=null;
    
    /** Output directory **/
    public String outDirectory=null;
    
    /** Index of the chosen z mode to use **/
    public int zModeIndex=0;
    
    /** Index of the chosen projection mode to use **/
    public int projModeIndex=0;
    
    /** Index of the chosen save mode to use **/
    public int saveModeIndex=0;

    /**
     * This method is called when the plugin is loaded.
     * @param arg argument specified for this plugin in IJ_Props.txt (may be blank)
     */
    @Override
    public void run(String arg) {
        if(open()) GUI();
    }
    
    /**
     * Select the folder where the nd files should be found
     * @return true if the dialog was OKed, false otherwise
     */
    public boolean open(){
        DirectoryChooser dc=new DirectoryChooser("Source folder containing nd files ?");
        inDirectory=dc.getDirectory();
        File f=new File(inDirectory);
        ndFilesList=f.list(new FilenameFilter() {
            @Override
            public boolean accept(File folder, String fileName) {
                return fileName.toLowerCase().endsWith(".nd");
            }
        });
        return ndFilesList.length!=0;
    }
    
    /**
     * Graphical user interface, asking for the parameters to use
     * @return true if the dialog was OKed, false otherwise
     */
    @SuppressWarnings("unchecked")
	public boolean GUI(){
        gd=new GenericDialog("Batch nd stack builder");
        gd.addChoice("Z_mode", zMode, zMode[0]);
        gd.addChoice("Projection_mode", ZProjector.METHODS, ZProjector.METHODS[0]);
        gd.addChoice("Save_mode", saveMode, saveMode[0]);
        
        c=gd.getChoices();
        
        gd.addDialogListener(this);
        dialogItemChanged(gd, null);
        
        gd.showDialog();
        
        if(gd.wasOKed()){
            getParameters();
            if(save()) for(int i=0; i<ndFilesList.length; i++) buildStack(ndFilesList[i]);
            IJ.showStatus("--- batch nd stack building done ---");
            return true;
        }else{
            IJ.showStatus("--- batch nd stack building cancelled ---");
            return false;
        }
    }
    
    /**
     * Select the folder where the (hyper)stack(s) will be saved
     * @return true if the dialog was OKed, false otherwise
     */
    public boolean save(){
        DirectoryChooser dc=new DirectoryChooser("Output folder ?");
        outDirectory=dc.getDirectory();
        return outDirectory!=null;
    }

    @Override
    public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
        getParameters();
        c.elementAt(1).setEnabled(zModeIndex==1);                   //Projection mode
        return true;
    }
    
    /**
     * Builds and saves (hyper)stack(s), based on the input fileName
     * @param fileName name of the nd file to open
     */
    public void buildStack(String fileName){
        ndFile nd=new ndFile(inDirectory, fileName);
        ndStackBuilder nsb=new ndStackBuilder(nd);
        nsb.setZParameters(1, nd.NZSteps, zModeIndex==1?projModeIndex:ndStackBuilder.NO_PROJECTION);
        nsb.setOutputParameters(true, true, false);
        
        if(saveModeIndex==1){
            for(int i=0; i<nd.NWavelengths; i++){
                nsb.setWavesParameters(i, i);
                nsb.buildStacks(outDirectory);
            }
        }else{
        	nsb.buildStacks(outDirectory);
        }
    }
    
    /**
     * Retrieves all parameters as set in the GUI
     */
    public void getParameters(){
        zModeIndex=gd.getNextChoiceIndex();
        projModeIndex=gd.getNextChoiceIndex();
        saveModeIndex=gd.getNextChoiceIndex();
    }

}
