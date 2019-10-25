import java.util.ArrayList;

import ij.gui.GenericDialog;
import ij.io.OpenDialog;
import ij.io.SaveDialog;
import ij.plugin.PlugIn;
import ndFile.content.wave;
import ndFile.ndFile;
import stgFile.stgFile;

/**
 *
 *  generateNdFile v1, 29 août 2012 
    Fabrice P Cordelieres, fabrice.cordelieres at gmail.com
    
    Copyright (C) 2012 Fabrice P. Cordelieres
  
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

public class generateNdFile implements PlugIn{
    
    ndFile nfc=new ndFile(false);
    boolean useStageList=false;
    

    @Override
    public void run(String arg) {
        if(globalGUI()){
            if(waveGUI()){
                if(stageGUI()){
                    SaveDialog sd=new SaveDialog("Save nd file", nfc.BaseName, ".nd");
                    nfc.write(sd.getDirectory()+nfc.BaseName);
                }
            }
        }
    }
    
    
    public boolean globalGUI(){
        GenericDialog gd=new GenericDialog("Generate nd file");
        gd.addStringField("Basename", "Experiment1");
        gd.addStringField("NDInfoFile", "Version 2.0");
        gd.addStringField("Description", "nd file generated using ImageJ");
        gd.addCheckbox("DoTimeLapse", false);
        gd.addNumericField("NTimePoints", 0, 0);
        gd.addCheckbox("DoStage", false);
        gd.addNumericField("NStagePositions", 0, 0);
        gd.addCheckbox("Use a stage list file", useStageList);
        gd.addCheckbox("DoWave", false);
        gd.addNumericField("NWavelengths", 0, 0);
        gd.addCheckbox("DoZSeries", false);
        gd.addNumericField("NZSteps", 0, 0);
        gd.addNumericField("ZStepSize", 0, 3);
        gd.addCheckbox("WaveInFileName", false);
        gd.addNumericField("NEvents", 0, 0);
        gd.showDialog();
        
        if(gd.wasCanceled()) return false;
        
        nfc.BaseName=gd.getNextString();
        nfc.NDInfoFile=gd.getNextString();
        nfc.Description=gd.getNextString();
        nfc.DoTimeLapse=gd.getNextBoolean();
        nfc.NTimePoints=(int) gd.getNextNumber();
        nfc.DoStage=gd.getNextBoolean();
        nfc.NStagePositions=(int) gd.getNextNumber();
        useStageList=gd.getNextBoolean();
        nfc.DoWave=gd.getNextBoolean();
        nfc.NWavelengths=(int) gd.getNextNumber();
        nfc.DoZSeries=gd.getNextBoolean();
        nfc.NZSteps=(int) gd.getNextNumber();
        nfc.ZStepSize=gd.getNextNumber();
        nfc.WaveInFileName=gd.getNextBoolean();
        nfc.NEvents=(int) gd.getNextNumber();
        
        return true;
    }
    
    public boolean waveGUI(){
        GenericDialog gd=new GenericDialog("Wavelengths infos");
        for(int i=0; i<nfc.NWavelengths; i++){
            gd.addStringField("WaveName"+(i+1), "Wave_"+(i+1));
            gd.addCheckbox("WaveDoZ"+(i+1), nfc.DoZSeries);
            gd.addStringField("WavePointsCollected", "");
        }
        gd.showDialog();
        
        if(gd.wasCanceled()) return false;
        
        for(int i=0; i<nfc.NWavelengths; i++){
            wave w=new wave();
            w.WaveName=gd.getNextString();
            w.WaveDoZ=gd.getNextBoolean();
            
            String WavePointsCollected=gd.getNextString();
            if(!WavePointsCollected.isEmpty()){
                String[] wavePointsArray=WavePointsCollected.split(",");
                w.WavePointsCollected=new ArrayList<Integer>();
                for(int j=0; j<wavePointsArray.length; j++) w.WavePointsCollected.add(Integer.parseInt(wavePointsArray[j]));
            }
            
            nfc.Waves.add(w);
        }
        
        return true;
    }
    
    
    public boolean stageGUI(){
        if(useStageList){
            OpenDialog od=new OpenDialog("Select the stage list file", null);
            if(od.getFileName()==null) return false;
            stgFile stg=new stgFile(od.getDirectory(), od.getFileName());
            nfc.DoStage=true;
            nfc.NStagePositions=stg.nStagePositions();
            for(int i=0; i<stg.nStagePositions(); i++) nfc.Stage.add(stg.getStagePositionNames()[i]);
        }else{
            GenericDialog gd=new GenericDialog("Stage infos");
            for(int i=0; i<nfc.NStagePositions; i++) gd.addStringField("Stage"+(i+1), "Position"+(i+1));
            gd.showDialog();
        
            if(gd.wasCanceled()) return false;
        
            for(int i=0; i<nfc.NStagePositions; i++) nfc.Stage.add(gd.getNextString());
        }
        return true;
    }

}