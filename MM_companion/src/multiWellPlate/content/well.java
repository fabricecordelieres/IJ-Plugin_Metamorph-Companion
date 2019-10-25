/**
 *
 *  well v1, 23 févr. 2013 
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

package multiWellPlate.content;

import ij.gui.GenericDialog;
import ij.gui.OvalRoi;
import ij.gui.Roi;
import java.util.ArrayList;
import java.util.Random;

/**
 * This class is aimed at storing and manipulating all informations about a 
 * well from a multi-well plate
 * @author fab
 */
public class well {
    
    /** Well name **/
    public String name="";
    
    /** Well type (0 for round, 1 for square)**/
    public int wellType=0;
    
    /** Tag for the round wells plate **/
    public static final int ROUND_WELL=0;
    
    /** Tag for the square wells plate **/
    public static final int SQUARE_WELL=1;
    
    /** Well format **/
    public static final String[] WELL_FORMAT={"Round wells", "Square wells"};
    
    /** X coordinates of the well center **/
    public double xCenter=0;
    
    /** Y coordinates of the well center **/
    public double yCenter=0;
    
    /** Well center diameter **/
    public double diameter=0;
    
    /** List of positions of interest **/
    public ArrayList<poi> pois= new ArrayList<poi>();
    
    /** Distribution for angles used to generate random POI **/
    public Random angleDistrib=new Random();
    
    /** Distribution for radius used to generate random POI **/
    public Random radiusDistrib=new Random();
    
    /** Distribution for x coordinates, used to generate random POI **/
    public Random xDistrib=new Random();
    
    /** Distribution for y coordinates, used to generate random POI **/
    public Random yDistrib=new Random();
    
    /** Enable/Disable the well **/
    public boolean isEnabled=true;
    
    
    /**
     * Creates a new well, asking the users all the well parameters
     */
    public well(){
        GenericDialog gd=new GenericDialog("Add a well");
        gd.addStringField("Well_name", "Well");
        gd.addChoice("Wells_format", well.WELL_FORMAT, well.WELL_FORMAT[0]);
        gd.addNumericField("Well_x_center", 0, 0);
        gd.addNumericField("Well_y_center", 0, 0);
        gd.addNumericField("Well_diameter", 0, 0);
        gd.showDialog();
        
        if(gd.wasOKed()){
            name=gd.getNextString();
            wellType=gd.getNextChoiceIndex();
            xCenter=gd.getNextNumber();
            yCenter=gd.getNextNumber();
            diameter=gd.getNextNumber();
        }
    }
    
    /**
     * Creates a new well, based on available informations
     * @param name well name
     * @param wellType well type (0 for round, 1 for square)
     * @param xCenter X coordinate of the well center
     * @param yCenter Y coordinater of the well center
     * @param diameter diameter of the well
     */
    public well(String name, int wellType, double xCenter, double yCenter, double diameter){
        this.name=name;
        this.wellType=wellType;
        this.xCenter=xCenter;
        this.yCenter=yCenter;
        this.diameter=diameter;
    }
    
    /**
     * Enable the current well
     */
    public void enable(){
        isEnabled=true;
    }
    
    /**
     * Disable the current well
     */
    public void disable(){
        isEnabled=false;
    }
    
    /**
     * Adds a point of interest within the current well
     * @param poi the point of interest to be added
     */
    public void addPoi(poi poi){
        pois.add(poi);
    }
    
    /**
     * Adds a random point of interest within the current well
     * @param name POI name
     * @param pCent percent of the well radius to use
     */
    public void addRandomPoi(String name, double pCent){
        if(pCent<0 || pCent>100) pCent=100;
        
        double x=xCenter;
        double y=yCenter;
        
        switch(wellType){
            case ROUND_WELL:
                double angle=angleDistrib.nextDouble()*2*Math.PI;
                double radius=pCent/100*radiusDistrib.nextDouble()*diameter/2;
                x=radius*Math.cos(angle)+xCenter;
                y=radius*Math.sin(angle)+yCenter;
                break;
            case SQUARE_WELL:
                double size=pCent/100*diameter;
                x=size*(xDistrib.nextDouble()-0.5)+xCenter;
                y=size*(yDistrib.nextDouble()-0.5)+yCenter;
                break;
        }
        pois.add(new poi(name, x, y));
    }
    /**
     * Adds a regularly spaced point of interest within the current well.
     * The newly generated grid of positions is centered around the well's center.
     * @param nColumns number of columns
     * @param nRows number of rows
     * @param spacing spacing between positions of interest
     */
    public void addRegularPois(int nColumns, int nRows, double spacing){
        for(int y=0; y<nRows; y++){
            for(int x=0; x<nColumns; x++){
                    double xPos=xCenter-(nColumns/2.0-x-0.5)*spacing;
                    double yPos=yCenter-(nRows/2.0-y-0.5)*spacing;
                    pois.add(new poi(name+"_"+(x+1)+'-'+(y+1), xPos, yPos));
            }
        }
    }
    
    /**
     * Adds a reference points to the current well. The 4 references corresponds 
     * to all extreme point for a round well, all 4 corner for a square well
     */
    public void addReferencePoi(){
        switch(wellType){
            case ROUND_WELL:
                addPoi(new poi("Ref_Left_"+name, xCenter-diameter/2, yCenter));
                addPoi(new poi("Ref_Top_"+name, xCenter, yCenter-diameter/2));
                addPoi(new poi("Ref_Right_"+name, xCenter+diameter/2, yCenter));
                addPoi(new poi("Ref_Bottom_"+name, xCenter, yCenter+diameter/2));
                break;
            case SQUARE_WELL:
                addPoi(new poi("Ref_TL_"+name, xCenter-diameter/2, yCenter-diameter/2));
                addPoi(new poi("Ref_TR_"+name, xCenter+diameter/2, yCenter-diameter/2));
                addPoi(new poi("Ref_BL_"+name, xCenter-diameter/2, yCenter+diameter/2));
                addPoi(new poi("Ref_BR_"+name, xCenter+diameter/2, yCenter+diameter/2));
                break;
        }
    }
    
    /**
     * Adds centre as poi to the current well
     */
    public void addCentre(){
        addPoi(new poi("Centre_"+name, xCenter, yCenter));
    }
    
    /**
     * Converts the current well to ImageJ Roi
     * @return the well as an ImageJ Roi
     */
    public Roi toRoi(){
        Roi roi=(wellType==ROUND_WELL)?
                new OvalRoi(xCenter-diameter/2, yCenter-diameter/2, diameter, diameter):
                new Roi(xCenter-diameter/2, yCenter-diameter/2, diameter, diameter);
        roi.setName(name);
        return roi;
    }
    
    @Override
    public well clone(){
    	well out=new well();
    	
    	out.name=name;
    	out.wellType=wellType;
        out.xCenter=xCenter;
        out.yCenter=yCenter;
        out.diameter=diameter;
        out.pois=pois;
        out.angleDistrib=angleDistrib;
        out.radiusDistrib=radiusDistrib;
        out.xDistrib=xDistrib;
        out.yDistrib=yDistrib;
        out.isEnabled=isEnabled;
        
        return out;
    }

}
