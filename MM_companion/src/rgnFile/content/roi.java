/**
 *
 *  roi v1, 3 sept. 2012 
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

package rgnFile.content;

import ij.IJ;
import ij.gui.OvalRoi;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import java.awt.Color;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.Arrays;
import utilities.tools;


/**
 * This class stores region related informations, extracted from/to be written to
 * an rgn file
 * @author fab
 */
public class roi {
    /** The roi type
     *      1: Rectangle
     *      2: Line, Polyline, Freeline
     *      3: Freehand
     *      4: Not used
     *      5: Oval
     *      6: Not used
     **/
    
    public int roiType=-1;
    
    /** Color (decimal value)
     *
     * Conversions:
     * ------------
     * 
     * RGB>DEC
     * D=B*256^2+G*256+R
     * 
     * DEC>RGB
     * B=INT(D/256^2)
     * G=INT((D-B*256^2)/256)
     * R=D-(B*256^2+G*256)
     * 
     * Ex:
     * 
     * Color:   |    RGB values:   |  Decimal value:
     * ---------|------------------|----------------
     * Red:     |    255/000/000   |  255
     * Green:   |    000/255/000   |  65280
     * Blue:    |    000/000/255   |  16711680
     * Cyan:    |    000/255/255   |  16776960
     * Magenta: |    255/000/255   |  16711935
     * Yellow:  |    255/255/000   |  65565
     * White:   |    255/255/255   |  16777215
     * Black:   |    000/000/000   |  0
     */
    public int color=0;
    
    /** Metamoph default ROI colors 
     * Red, Green, Blue, Cyan, Magenta, Yellow,
     * Dark Red, Dark Green, Dark Blue, Dark Cyan, Dark Magenta, Brown, Black
     
     **/
    public static final int[] MM_COLORS={255, 65280, 16711680, 16776960, 16711935, 65535, 128, 32768, 8388608, 8421376, 8388736, 32896, 83954956, 251856644};
    
    /** Index of the next color to be used when exported rgn file **/
    public static int currColor=0;
    
    /** Roi's top-left corner x coordinate **/
    public int xTL=-1;
    
    /** Roi's top-left corner y coordinate **/
    public int yTL=-1;
    
    /** Metamorph's roi number 3 field value (always 0 0) **/
    public String field3="0 0";
    
    /** Metamorph's roi number 4 field value (always 0) **/
    public String field4="0";
    
    /** Metamorph's roi number 5 field value (always 1) **/
    public String field5="1";
    
    /** Number of xy coordinates couples (top-left corner AND other coordinates) **/
    public int nCoord=-1;
    
    /** The x coordinates or width in case of a rectangle **/
    public int[] xCoord=null;
    
    /** The y coordinates or height in case of a rectangle **/
    public int[] yCoord=null;
    
    /** The Roi number in case of a multiple rois file (starts at 1) **/
    public int roiNb=1;
    
    /**
     * Creates a new empty roi object
     */
    public roi(){
        
    }
    
    /**
     * Creates a new roi object, based on a String
     * @param line string containing a single line of a Metamorph rgn file
     */
    public roi(String line){
        parseMMFormattedRoi(line);
    }
    
    /**
     * Creates a new roi object (Metamorph roi) from a Roi object (ImageJ roi)
     * @param roi the ImageJ Roi to start from
     */
    public roi(Roi roi){
        int type=roi.getType();
        
        Rectangle box=roi.getBounds();
        xTL=box.x;
        yTL=box.y;
                
        Polygon poly=roi.getPolygon();
        
        color=MM_COLORS[currColor++%MM_COLORS.length];
        
        switch(type){
            //Rectangle
            case Roi.RECTANGLE:
                roiType=1;
                nCoord=2;
                xCoord=new int[]{box.width};
                yCoord=new int[]{box.height};
                break;
                
            //Line, Polyline, Freeline
            case Roi.LINE: case Roi.POLYLINE: case Roi.FREELINE: case Roi.ANGLE:
                roiType=2;
                switch(type){
                    case Roi.LINE:
                        nCoord=2;
                        xCoord=new int[]{xTL, xTL+box.width};
                        yCoord=new int[]{yTL, yTL+box.height};
                        break;
                        
                    case Roi.POLYLINE: case Roi.FREELINE: case Roi.ANGLE:
                        nCoord=((PolygonRoi) roi).getNCoordinates();
                        xCoord=Arrays.copyOf(((PolygonRoi) roi).getXCoordinates(), nCoord);
                        yCoord=Arrays.copyOf(((PolygonRoi) roi).getYCoordinates(), nCoord);
                        for(int i=0; i<nCoord; i++){
                            xCoord[i]+=xTL;
                            yCoord[i]+=yTL;
                        }
                        break;
                }
                break;
                
                
            //Polygon, Oval or FreeRoi
            case Roi.POLYGON: case Roi.OVAL: case Roi.FREEROI:
                switch(type){
                    case Roi.POLYGON: roiType=3; break;
                    case Roi.OVAL: roiType=5; break;
                    case Roi.FREEROI: roiType=3; break;
                }
                
                nCoord=poly.npoints;
                xCoord=poly.xpoints;
                yCoord=poly.ypoints;
                break;
                
            //Not yet supported
            default:
                type=-1;
                xTL=-1;
                yTL=-1;
                IJ.log(roi.getTypeAsString()+": This type of ImageJ roi is not yet supported"); break;
        }    
    }
    
    /**
     * Starting from a String supposed to be from a rgn Metamorph file, parses all 
     * informations to the current roi object
     * @param line the input line, formatted as in rgn Metamorph files
     */
    public void parseMMFormattedRoi(String line){
        try{
            String[] fields=line.split(", ");

            for(int i=0; i<fields.length; i++){
                int field=Integer.parseInt(fields[i].split(" ")[0]);
                String argument=fields[i].substring(fields[i].indexOf(" ")+1);

                switch(field){
                    //Roi type
                    case 0: roiType=Integer.parseInt(argument); break;

                    //Color
                    case 1:
                        color=Integer.parseInt(argument); break;

                    // Top-left coordinates
                    case 2:
                        xTL=Integer.parseInt(argument.split(" ")[0]);
                        yTL=Integer.parseInt(argument.split(" ")[1]);
                        break;

                    //Field 3
                    case 3: field3=argument; break;

                    //Field 4
                    case 4: field4=argument; break;

                    //Field 5
                    case 5: field5=argument; break;

                    // Coordinates list
                    case 6:
                        String[] coord=argument.split(" ");
                        nCoord=Integer.parseInt(coord[0]);
                        xCoord=new int[(roiType==1?nCoord-1:nCoord)];
                        yCoord=new int[xCoord.length];
                        
                        int index=1;
                        for(int j=0; j<xCoord.length; j++){
                            xCoord[j]=Integer.parseInt(coord[index++]);
                            yCoord[j]=Integer.parseInt(coord[index++]);
                        }
                        break;

                    //The Roi's number
                    case 7: roiNb=Integer.parseInt(argument); break;
                }
            }
        }catch (Exception ex){
            //Logger.getLogger(roi.class.getName()).log(Level.SEVERE, null, ex);
            //Logger.getLogger(roi.class.getName()).log(Level.SEVERE, "The current roi line is not formatted as in Metamorph rgn files", ex);
            IJ.log("The current roi line is not formatted as in Metamorph rgn files");
        }
    }
    
    /**
     * Converts a roi object (Metamorph roi) to a Roi object (ImageJ roi)
     * @return an ImageJ Roi object
     */
    public Roi getIJRoifromMMRoi(){
        Roi roi=null;
        
        //Translates the Roi in Metamorph format to a Roi in ImageJ format--------
            switch (roiType){
                //Rectangle
                case 1: roi=new Roi(xTL, yTL, xCoord[0], yCoord[0]); break;
                
                //Polyline
                case 2: roi=new PolygonRoi(xCoord, yCoord, nCoord, Roi.POLYLINE); break;
                
                //Polygon
                case 3: roi=new PolygonRoi(xCoord, yCoord, nCoord, Roi.POLYGON); break;
                
                //Not yet supported
                //case 4: Logger.getLogger("This type of roi (4) is not yet supported"); break;
                
                //Oval    
                case 5:
                    int[] tmp=xCoord.clone();
                    Arrays.sort(tmp);
                    int width=tmp[nCoord-1]-tmp[0];
                    tmp=yCoord.clone();
                    Arrays.sort(tmp);
                    int height=tmp[nCoord-1]-tmp[0];
                    roi=new OvalRoi(xTL, yTL, width, height);
                    break;
                
                //Freehand
                case 6: roi=new PolygonRoi(xCoord, yCoord, nCoord, Roi.FREEROI); break;
                    
                //Not yet supported
                default:
                    roi=null;
                    IJ.log("This type of Metamorph roi ("+roiType+") is not yet supported");
                    break;
            }
            
            if(roi!=null){
                int[] RGBcolor=tools.DectoRGB(color);
                roi.setStrokeColor(new Color(RGBcolor[0], RGBcolor[1], RGBcolor[2]));
            }
            
        return roi;
        
    }
    
    /**
     * Builds a string containing the roi informations, formatted as in Metamorph rgn file
     * @param nb the number of the roi, in case of an multiple roi export
     * @return a string containing the roi informations, formatted as in Metamorph rgn file
     */
    public String getMMFormattedRoi(int nb){
        String out="";
        roiNb=nb;
        
        if(roiType!=-1){
            for(int i=0; i<8; i++){
                out+=i+" ";
                switch(i){
                    // Roi type
                    case 0: out+=roiType; break;

                    // Color
                    case 1: out+=color; break;

                    // Top-left coordinates
                    case 2: out+=xTL+" "+yTL; break;

                    // Field 3
                    case 3: out+=field3; break;

                    // Field 4
                    case 4: out+=field4; break;

                    // Field 5
                    case 5: out+=field5; break;

                    // Coordinates list
                    case 6:
                        if(roiType==5){
                            Roi roi=getIJRoifromMMRoi();
                            Rectangle rect=roi.getBounds();
                            double a=(rect.width-.5)/2;
                            double b=(rect.height-.5)/2;
                            
                            out+="100 ";
                            
                            for(int j=0; j<100; j++){
                                out+=((int) ((xTL+a)+a*Math.cos(2*j*Math.PI/100)))+" "+((int) ((yTL+b)+b*Math.sin(2*j*Math.PI/100)));
                                if(j<99) out+=" ";
                            }
                            
                            
                        }else{
                            out+=nCoord+" ";
                            for(int j=0; j<xCoord.length; j++){
                                out+=xCoord[j]+" "+yCoord[j];
                                if(j<xCoord.length-1) out+=" ";
                            }
                        }
                        break;

                    // The Roi's number
                    case 7: out+=roiNb; break;
                }

                out+=i<7?", ":"\n";
            }
        }
        return out;
    }
    
    /**
     * Builds a string containing the roi informations, formatted as in Metamorph rgn file
     * @return a string containing the roi informations, formatted as in Metamorph rgn file
     */
    public String getMMFormattedRoi(){
        return getMMFormattedRoi(1);
    }
    
}
