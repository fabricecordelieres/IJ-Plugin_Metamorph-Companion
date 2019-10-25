/**
 *
 *  multiWellPlate v1, 23 févr. 2013 
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

package multiWellPlate;

import ij.ImagePlus;
import multiWellPlate.content.poi;
import multiWellPlate.content.well;
import ij.gui.GenericDialog;
import ij.gui.NewImage;
import ij.gui.OvalRoi;
import ij.gui.Roi;
import ij.process.ImageProcessor;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import stgFile.content.position;
import stgFile.stgFile;
import utilities.tools;

/**
 * This class is aimed at storing and manipulating all informations about a 
 * multi-well plate
 * @author fab
 */
public class multiWellPlate {
    /** Tag for the 6 wells plate **/
    public static final int SIX_WP=0;
    /** Tag for the 12 wells plate **/
    public static final int TWELVE_WP=1;
    /** Tag for the 24 wells plate **/
    public static final int TWENTYFOUR_WP=2;
    /** Tag for the 48 wells plate **/
    public static final int FORTYHEIGHT_WP=3;
    /** Tag for the Ibidi grid-50 plate **/
    public static final int IBIDI_GRID50=4;
    /** Tag for the Matrical square-96 plate **/
    public static final int MATRICAL_SQUARE96=5;
    
    
    /** Tag for the custom wells plate **/
    public static final int CUSTON_WP=6;
    
    /** Well plate formats **/
    public static final String[] FORMAT={"6-well plates", "12-well plates", "24-well plates", "48-well plates", "Ibidi Grid-50", "Matrical 96-square wells plate", "Custom well plate"};
    
    /** Rows names **/
    public static final String[] ROW_NAME={"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
    
    /** Colors names **/
    public static final Color[] COLORS_NAME={Color.red, Color.green, Color.blue, Color.orange, Color.cyan, Color.magenta, Color.yellow, Color.pink};
    
    /** Rows names **/
    public static final String[] REFERENCE_NAME={"4 extreme wells", "All references"};
    
    /** Multi-well plate type **/
    public int type=0;
    
    /** Number of wells per rows **/
    public int nRows=0;
    /** Number of wells per columns **/
    public int nColumns=0;
    
    /** Well type **/
    public int wellType=0;
    
    /** Well diameter (mm) **/
    public double wellDiameter=0;
    
    /** Well list **/
    public ArrayList<well> wells=new ArrayList<well>();
    
    /** Plate width (mm) **/
    public double plateWidth=0;
    /** Plate height (mm) **/
    public double plateHeight=0;
    
    /** Offset from the upper left corner to the center of the upper left well
     * along the Y axis (mm) **/
    public double A1RowOffset=0;
    /** Offset from the upper left corner to the center of the upper left well
     * along the X axis (mm) **/
    public double A1ColumnOffset=0;
    
    /** Well center to well center distance (mm) **/
    public double C2CDistance=0;
    
    
    public multiWellPlate(){}
    
    
    /**
     * Creates a new multiWellPlate object, based on a type passed as argument.
     * The type might be 6-, 12-, 24- or 48- well plate, as well as 0 to get the
     * informations about the plate asked to the users. Any ohter value will throw
     * an illegalArgumentException.
     * @param type migth be 6, 12, 24 or 48 for already defined plates, or 0 for
     * custom design. Any other value will result in an illegal argument exception.
     */
    public multiWellPlate(int type){
        this.type=type;
        
        switch(type){
            case SIX_WP:
                nRows=2; nColumns=3; wellType=0; wellDiameter=34.80;
                plateWidth=127.76; plateHeight=85.47; A1RowOffset=23.16;
                A1ColumnOffset=24.76; C2CDistance=39.12;
                break;
            case TWELVE_WP:
                nRows=3; nColumns=4; wellType=0; wellDiameter=22.11;
                plateWidth=127.89; plateHeight=85.6;
                A1RowOffset=16.79; A1ColumnOffset=24.94; C2CDistance=26.01;
                break;
            case TWENTYFOUR_WP:
                nRows=4; nColumns=6; wellType=0; wellDiameter=15.62;
                plateWidth=127.89; plateHeight=85.6;
                A1RowOffset=13.84; A1ColumnOffset=17.52; C2CDistance=19.03;
                break;
            case FORTYHEIGHT_WP:
                nRows=6; nColumns=8; wellType=0; wellDiameter=11.05;
                plateWidth=127.89; plateHeight=85.6;
                A1RowOffset=10.08; A1ColumnOffset=18.16; C2CDistance=13.08;
                break;
            case IBIDI_GRID50:
                nRows=10; nColumns=10; wellType=1; wellDiameter=50;
                plateWidth=600; plateHeight=600;
                A1RowOffset=55; A1ColumnOffset=55; C2CDistance=55;
                break;
            case MATRICAL_SQUARE96:
                nRows=8; nColumns=12; wellType=1; wellDiameter=8;
                plateWidth=127.76; plateHeight=85.48;
                A1RowOffset=11.24; A1ColumnOffset=14.38; C2CDistance=9;
                break;
            case CUSTON_WP:
                GenericDialog gd= new GenericDialog("Multi-well plate creator");
                gd.addNumericField("Number_of_rows", 0, 0);
                gd.addNumericField("Number_of_columns", 0, 0);
                gd.addChoice("Wells_format", well.WELL_FORMAT, well.WELL_FORMAT[0]);
                gd.addNumericField("Well_diameter_(mm)", 0, 0);
                gd.addNumericField("Plate_length_(mm)", 0, 0);
                gd.addNumericField("Plate_width_(mm)", 0, 0);
                gd.addNumericField("A1_row_offset_(mm)", 0, 0);
                gd.addNumericField("A1_column_offset_(mm)", 0, 0);
                gd.addNumericField("Well_center_to_well_center_distance_(mm)", 0, 0);
                gd.showDialog();
                if(gd.wasOKed()){
                    nRows=(int) gd.getNextNumber();
                    nColumns=(int) gd.getNextNumber();
                    wellType=gd.getNextChoiceIndex();
                    wellDiameter=gd.getNextNumber();
                    plateWidth=gd.getNextNumber();
                    plateHeight=gd.getNextNumber();
                    A1RowOffset=gd.getNextNumber();
                    A1ColumnOffset=gd.getNextNumber();
                    C2CDistance=gd.getNextNumber();
                    break;
                }
            default:
                throw new IllegalArgumentException("multiWellPlate object has not been successfully initialized.");            
        }
        
        //Generation of plate in the normal position
        if(nRows!=0 && nColumns!=0){
            for(int i=0; i<nRows; i++){
                for(int j=0; j<nColumns; j++){
                    double x=A1ColumnOffset+C2CDistance*j;
                    double y=A1RowOffset+C2CDistance*i;
                    wells.add(new well(ROW_NAME[i%ROW_NAME.length]+(j+1), wellType, x, y, wellDiameter));
                }
            }
        }
    }
    
    /**
     * Add to each single well a defined number of random positions of interest
     * @param nPois the number of POIs to generate for each well
     * @param pCent percent of the well radius to use
     */
    public void addRandomPois(int nPois, double pCent){
        for(int i=0; i<wells.size(); i++){
            if(wells.get(i).isEnabled){
                for(int j=0; j<nPois; j++){
                    wells.get(i).addRandomPoi(wells.get(i).name+"_"+(j+1), pCent);
                }
            }
        }
    }
    
    /**
     * Add to each single well a defined number of regularly spaced positions of interest
     * The newly generated grid of positions is centered around the well's center.
     * @param nColumns number of columns
     * @param nRows number of rows
     * @param spacing spacing between positions of interest
     */
    public void addRegularPois(int nColumns, int nRows, double spacing){
        for(int i=0; i<wells.size(); i++){
            if(wells.get(i).isEnabled){
                wells.get(i).addRegularPois(nColumns, nRows, spacing);
            }
        }
    }
    
    /**
     * Get the wells names
     * @return the wells names
     */
    public String[] getWellNames(){
        String[] out=new String[nRows*nColumns];
        for(int i=0; i<out.length; i++) out[i]=wells.get(i).name;
        return out;
    }
    
    /**
     * Get the wells status
     * @return the wells status (enabled/disabled) as a boolean array
     */
    public boolean[] getWellStatus(){
        boolean[] out=new boolean[nRows*nColumns];
        for(int i=0; i<out.length; i++) out[i]=wells.get(i).isEnabled;
        return out;
    }
    
    /**
     * Set the wells status (enabled/disabled)
     * @param status the well status, as a boolean array
     */
    public void setWellStatus(boolean[] status){
        for(int i=0; i<nRows*nColumns; i++) wells.get(i).isEnabled=status[i];
    }
    
    /**
     * Returns an image of the current multi-well plate, including wells and POIs
     * @param resolution the output resolution, in dpi
     * @return an ImagePlus of the current multi-well plate, including wells and POIs
     */
    public ImagePlus getImage(int resolution){
        ImagePlus ip=null;
        if (nRows!=0 && nColumns!=0){
            ip=NewImage.createRGBImage(FORMAT[type], tools.prepareForImage(plateWidth, resolution), tools.prepareForImage(plateHeight, resolution), 1, NewImage.FILL_WHITE);
            
            ImageProcessor iproc=ip.getProcessor();
            int fontSize=16*resolution/72;
            iproc.setFont(new Font("Arial", Font.BOLD, fontSize));
                
            for(int i=0; i<wells.size(); i++){
                well well=wells.get(i);
                int x=tools.prepareForImage(well.xCenter, resolution);
                int y=tools.prepareForImage(well.yCenter, resolution);
                int dim=tools.prepareForImage(well.diameter, resolution);
                
                iproc.setLineWidth(resolution/72);
                iproc.setColor(Color.black);
                
                Roi roi=null;
                switch(wellType){
                    case 0://Round
                        roi=new OvalRoi(x-dim/2, y-dim/2, dim, dim); break;
                    case 1://Square
                        roi=new Roi(x-dim/2, y-dim/2, dim, dim); break;
                }
                
                if(roi!=null) iproc.draw(roi);
                
                iproc.setColor(Color.lightGray);
                iproc.drawString(well.name, x-iproc.getStringWidth(well.name)/2, y+fontSize/2);
                
                if(well.isEnabled){
                    ArrayList<poi> pois=well.pois;
                
                    for(int j=0; j<pois.size(); j++){
                        iproc.setLineWidth(2*resolution/72);
                        iproc.setColor(COLORS_NAME[i%COLORS_NAME.length]);
                        poi poi=pois.get(j);
                        
                        if(poi.name.startsWith("Centre_")){
                            iproc.setLineWidth(resolution/150);
                            iproc.drawLine(x-dim/8, y, x+dim/8, y);
                            iproc.drawLine(x, y-dim/8, x, y+dim/8);
                        }else if(poi.name.startsWith("Ref_")){
                            iproc.setLineWidth(resolution/72);
                            iproc.drawOval(tools.prepareForImage(poi.xCoord,resolution)-dim/40, tools.prepareForImage(poi.yCoord, resolution)-dim/40, dim/20, dim/20);
                        }else{
                            iproc.drawDot(tools.prepareForImage(poi.xCoord,resolution), tools.prepareForImage(poi.yCoord, resolution));
                        }
                    }
                }else{
                    iproc.setLineWidth(resolution/72);
                    iproc.setColor(Color.lightGray); iproc.fill(roi);
                    iproc.setColor(Color.black); iproc.draw(roi);
                    iproc.setColor(Color.white); iproc.drawString(well.name, x-iproc.getStringWidth(well.name)/2, y+fontSize/2);
                }
            }
        }
        return ip;
    }
    
    /**
     * Adds references as POIs to the current multi-well plate (top/bottom/right/left
     * positions on all wells for allReference set to true, top/bottom over the 4 
     * extreme wells otherwise)
     * @param allReferences true to get all teh references, otherwise adds only 
     * top/bottom references to the 4 extreme wells
     */
    public void addReferences(boolean allReferences){
        int[] wNb=new int[allReferences?wells.size():4];
        
        if(allReferences){
            for(int i=0; i<wells.size(); i++) wNb[i]=i;
        }else{
            wNb[0]=0;
            wNb[1]=nColumns-1;
            wNb[2]=wells.size()-nColumns;
            wNb[3]=wells.size()-1;
        }
        
        for(int i=0; i<wNb.length; i++) wells.get(wNb[i]).addReferencePoi();
     }
    
    /**
     * Adds centres as POIs to the current multi-well plate
     */
    public void addCentres(){
        for(int i=0; i<wells.size(); i++) wells.get(i).addCentre();
    }
    
    /**
     * Generates a stage positions list from the wells POIs list, formatted as a 
     * Metamorph STG file
     * @return the POIs list, formatted as a Metamorph STG file
     */
    public stgFile getStageFile(){
        stgFile out=new stgFile();
        
        for(int i=0; i<wells.size(); i++){
            well w=wells.get(i);
            
            if(w.isEnabled){
                ArrayList<poi> pois=w.pois;
                for(int j=0; j<pois.size(); j++){
                    poi poi=pois.get(j);

                    position pos=new position();
                    pos.stagePositionName=poi.name;

                    pos.stageXCoordinate=(float) poi.xCoord;
                    pos.stageYCoordinate=(float) poi.yCoord;

                    out.addPosition(pos);
                }
            }
        }
        
        return out;
    }
    
    /**
     * Enables all wells
     */
    public void enableAllWells(){
        changeAllWellsStatus(true);
    }
    
    /**
     * Disables all wells
     */
    public void disableAllWells(){
        changeAllWellsStatus(false);
    }
    
    /**
     * Changes the status (enabled/disabled) of all wells
     * @param status true to enable all wells, false to disable all wells
     */
    public void changeAllWellsStatus(boolean status){
        for(int i=0; i<nColumns*nRows; i++) wells.get(i).isEnabled=status;
    }
    
    /**
     * Clones the current multiWellPlate
     * @return a copy of the current multiWellPlate
     */
    @Override
    public multiWellPlate clone(){
        multiWellPlate out=new multiWellPlate(type);
        out.wells=new ArrayList<well>();
        
        for(int i=0; i<wells.size(); i++) out.wells.add(wells.get(i).clone());

        return out;
    }
    
    /**
     * Sends all informations about the current multi-well plate to the console
     */
    public void log(){
         System.out.println("nRows="+nRows+
                            "\nnColumns: "+nColumns+
                            "\nwellDiameter: "+wellDiameter+
                            "\nplateWidth: "+plateWidth+
                            "\nplateHeight: "+plateHeight+
                            "\nA1RowOffset: "+A1RowOffset+
                            "\nA1ColumnOffset: "+A1ColumnOffset+
                            "\nC2CDistance: "+C2CDistance);
    }
    

}