/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package plugins.stgPos;

import ij.IJ;
import ij.io.OpenDialog;
import ij.io.SaveDialog;
import ij.plugin.PlugIn;
import stgFile.stgFile;

/**
 * This plugin aims at removing positions from a STG file, based on an input text tag
 * @author fab
 */
public class tagBasedPosRemoval implements PlugIn{
    /** OpenDialog **/
    OpenDialog od=null;
    
    /** The stg file to process **/
    stgFile stg=null;

    /**
     * This method is called when the plugin is loaded.
     * @param arg argument specified for this plugin in IJ_Props.txt (may be blank)
     */
    @Override
    public void run(String arg) {
        if(open()){
            stg.removePosition(IJ.getString("Tag Based Position Removal", "Ref_"));
            save();
        }
    }
    
    /**
     * Open a STG file
     * @return true if everything went well, false otherwise
     */
    public boolean open(){
        od=new OpenDialog("Where_is_the_STG_file ?", null);
        stg=new stgFile(od.getDirectory(), od.getFileName());
        if(od.getFileName()==null){
            IJ.showStatus("--- Tag Based Position Removal cancelled ---");
            return false;
        }
        return true;
    }
    
    /**
     * Save the adapted STG file
     * @return true if everything went well, false otherwise
     */
    public boolean save(){
        SaveDialog sd=new SaveDialog("Where_to_save_the_modified_stage_positions_file?", "Modified_"+od.getFileName(), ".STG");
            if(sd.getFileName()!=null){
                stg.write(sd.getDirectory()+sd.getFileName());
                IJ.showStatus("--- Modified stage positions file saved ---");
                return true;
            }else{
                IJ.showStatus("--- Tag Based Position Removal cancelled ---");
                return false;
            }
    }    
}
