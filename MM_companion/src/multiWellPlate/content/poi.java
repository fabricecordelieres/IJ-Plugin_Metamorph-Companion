/**
 *
 *  poi v1, 23 févr. 2013 
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

/**
 * This class is aimed at storing and manipulating all informations about a 
 * point of interest within a well from a multi-well plate
 * @author fab
 */
public class poi {
    /** POI name **/
    public String name="";
    
    /** X coordinate of the point of interest **/
    public double xCoord=0;
    
    /** Y coordinate of the point of interest **/
    public double yCoord=0;
    
    /**
     * Creates a new POI, asking the users all the POI parameters
     */
    public poi(){
        GenericDialog gd=new GenericDialog("Add a POI");
        gd.addStringField("POI_name", "Well");
        gd.addNumericField("POI_x_coordinate", 0, 0);
        gd.addNumericField("POI_y_coordinate", 0, 0);
        gd.showDialog();
        
        if(gd.wasOKed()){
            name=gd.getNextString();
            xCoord=gd.getNextNumber();
            yCoord=gd.getNextNumber();
        }
    }
    
    /**
     * Creates a new POI, based on available informations
     * @param name POI name
     * @param xCoord X coordinate of the POI
     * @param yCoord Y coordinater of the POI
     */
    public poi(String name, double xCoord, double yCoord){
        this.name=name;
        this.xCoord=xCoord;
        this.yCoord=yCoord;
    }

}
