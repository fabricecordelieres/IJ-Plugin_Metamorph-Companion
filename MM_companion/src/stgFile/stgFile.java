/**
 *
 *  stgFile v1, 16 juil. 2012 
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
package stgFile;

import ij.ImagePlus;
import ij.gui.NewImage;
import ij.process.ImageProcessor;
import java.awt.Color;
import java.awt.Font;
import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import stgFile.content.position;
import utilities.constants;

/**
 * This class allows handeling of stage position lists, as created by Metamorph MDA module
 * @author fab
 */
public class stgFile{
    /** stgFile directory **/
    String directory="";
    
    /** stgFile fileName **/
    String fileName="";
    
    /** Header, first line **/
    String headerLine1="\"Stage Memory List\", Version 6.0";
    
    /** Header, second line **/
    String headerLine2="0, 0, 0, 0, 0, 0, 0, \"um\", \"um\"";
    
    /** Header, third line **/
    String headerLine3="0";
    
    /** Number of stage positions **/
    int nStagePositions=0;
    
    /** Stage positions list **/
    ArrayList<position> stagePositionsList=new ArrayList<position>();
    
    
    /**
     * Creates a new stgFile object, based on default informations
     */
    public stgFile(){
        
    }
    
    /**
     * Creates a new stgFile object, by reading an existing file
     * @param directory stgFile directory
     * @param fileName  stgFile fileName
     */
    public stgFile(String directory, String fileName){
            this.directory=directory;
            this.fileName=fileName;
            read(directory+fileName);
    }
    
    /**
     * Reads an existing stg file
     * @param filePath path to the stg file to read
     */
    public void read(String filePath){
        BufferedReader in=null;
        try {
            in = new BufferedReader(new FileReader(filePath));
            
            if(!in.readLine().equals(headerLine1)) throw new IOException("This file is not a Stage Memory List file");
            headerLine2=in.readLine();
            headerLine3=in.readLine();
            nStagePositions=Integer.parseInt(in.readLine());
            
            for(int i=0; i<nStagePositions; i++) stagePositionsList.add(new position(in.readLine()));
            
        } catch (IOException ex) {
            Logger.getLogger(stgFile.class.getName()).log(Level.SEVERE, "An error occured while reading the Metamorph stg file", ex);
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
                Logger.getLogger(stgFile.class.getName()).log(Level.SEVERE, "An error occured while reading the Metamorph stg file", ex);
            }
        }
    }
    
    /**
     * Returns the stgFile directory
     * @return the stgFile directory
     */
    public String getDirectory(){
        return directory;
    }
    
    /**
     * Returns the stgFile filename
     * @return the stgFile filename
     */
    public String getFileName(){
        return fileName;
    }
    
    /**
     * Returns the index-th position of the list
     * @param index index of the position to return
     * @return the relevent position
     */
    public position getPosition(int index){
        return stagePositionsList.get(index);
    }
    
    /**
     * Returns the stage positions' names as an array of String
     * @return the stage positions' names as an array of String
     */
    public String[] getStagePositionNames(){
        String[] out=new String[nStagePositions];
        for(int i=0; i<nStagePositions; i++) out[i]=stagePositionsList.get(i).stagePositionName;
        return out;
    }
    
    /**
     * Returns the number of stage positions
     * @return the number of stage positions
     */
    public int nStagePositions(){
        return nStagePositions;
    }
    
    /**
     * Changes the number of the stage list file version within the file's header 
     * @param version a String containing the version number
     */
    public void changeVersion(String version){
        headerLine1.replaceFirst("6.0", version);
    }
    
    /**
     * Changes the first unit block within the header's second line
     * @param unit new unit to be set
     */
    public void changeUnit1(String unit){
        changeUnit(7, unit);
    }
    
    /**
     * Changes the second unit block within the header's second line
     * @param unit new unit to be set
     */
    public void changeUnit2(String unit){
        changeUnit(8, unit);
    }
    
    /**
     * Changes the unit block, either the first (index=) or the second (index=) of the header's second line
     * @param index index of the block to be modified
     * @param unit the replacement expression
     */
    private void changeUnit(int index, String unit){
        String[] splitted=headerLine2.split(", ");
        splitted[index]="\""+(unit.replace("\"", ""))+"\"";
        headerLine2=splitted[0];
        for(int i=1; i<splitted.length; i++) headerLine2+=", "+splitted[i];
    }
    
    /**
     * Adds a new position to the stage positions list
     * @param pos the position to be added
     */
    public void addPosition(position pos){
        stagePositionsList.add(pos);
        nStagePositions=stagePositionsList.size();
    }
    
   /**
     * Insert a new position to the stage positions list
     * @param positionNumber the index the new position should have
     * @param pos the position to be added
     */
    public void insertPosition(int positionNumber, position pos){
        stagePositionsList.add(positionNumber, pos);
        nStagePositions=stagePositionsList.size();
    }
    
     /**
     * Removes a position from the stage positions list
     * @param positionNumber the number of the position to be removed
     */
    public void removePosition(int positionNumber){
        stagePositionsList.remove(positionNumber);
        nStagePositions=stagePositionsList.size();
    }
    
     /**
     * Removes all positions containing the input tag from the stage positions list
     * @param tag the tag to look for within the positions names to be removed
     */
    public void removePosition(String tag){
        String[] posNames=getStagePositionNames();
        for(int i=nStagePositions()-1; i>=0; i--) if (posNames[i].contains(tag)) removePosition(i);
    }
    
    /**
     * Removes all the positions from the stage positions list
     */
    public void removeAllPositions(){
        stagePositionsList=new ArrayList<position>();
        nStagePositions=0;
    }
    
    /**
     * Optimizes the STG file so that starting from a defined start positions,
     * the next visited position is the closest one
     * @param indexStartPos index of the start position
     * @return an optimized version of the current STG file, as a STG file
     */
    public stgFile getOptimizedStgFile(int indexStartPos){
        stgFile out=duplicate();
        
        ArrayList<position> inPos=out.stagePositionsList;
        ArrayList<position> outPos=new ArrayList<position>();
        
        position last=inPos.get(indexStartPos);
        inPos.remove(indexStartPos);
        outPos.add(last);
        
        while(inPos.size()>0){
            int index=0;
            double minDist=Double.MAX_VALUE;
            for(int j=0; j<inPos.size(); j++){
                double dist=last.distanceXY(inPos.get(j));
                if(dist<minDist){
                    minDist=dist;
                    index=j;
                }
            }
            last=inPos.get(index);
            inPos.remove(index);
            outPos.add(last);
        }
        out.stagePositionsList=outPos;
        return out;
    }
    
    /**
     * Saves the current stgFile to a file
     * @param filePath path of the stg file to write
     */
    public void write(String filePath){
        if(!filePath.endsWith(".STG")) filePath=filePath+".STG";
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter(filePath));
            out.write(toString());
        } catch (IOException ex) {
            Logger.getLogger(stgFile.class.getName()).log(Level.SEVERE, "An error occured while writing the Metamorph stg file", ex);
        } finally {
            try {
                out.close();
            } catch (IOException ex) {
                Logger.getLogger(stgFile.class.getName()).log(Level.SEVERE, "An error occured while writing the Metamorph stg file", ex);
            }
        }
    }
    
    /**
     * Returns a copy of the current stage positions file
     * @return a copy of the current stage positions file
     */
    public stgFile duplicate(){
        stgFile out=new stgFile();
        
        out.directory=directory;
        out.fileName=fileName;
        out.headerLine1=headerLine1;
        out.headerLine2=headerLine2;
        out.headerLine3=headerLine3;
        out.nStagePositions=nStagePositions;
        
        for(int i=0; i<nStagePositions; i++) out.addPosition(stagePositionsList.get(i).duplicate());
        
        return out;
    }
    
    /**
     * Return a string representation of the stgFile
     * @return a string presenting the full content of the MDA stage list file
     */
    @Override
    public String toString(){
        String out=headerLine1;
        out+="\n"+headerLine2;
        out+="\n"+headerLine3;
        out+="\n"+nStagePositions;
        
        for(int i=0; i<nStagePositions; i++) out+="\n"+stagePositionsList.get(i).toString();
        return out;
    }
    
    /**
     * Export the stage positions map as an ImagePlus
     * @param targetWidth expected image width
     * @param border percent of the width/height dedicated to borders around the image (ex: 25 means 
     * 25% of the images' dimensions are dedicated to borders)
     * @param drawPath true in case path between positions should be drawn on the image
     * @param drawNames true in case positions names should be drawn on the image
     * @param drawBlack true in case positions names should be drawn in black
     * @return the stage positions map as an ImagePlus
     */
    public ImagePlus getImage(int targetWidth, double border, boolean drawPath, boolean drawNames, boolean drawBlack){
        ImagePlus out=null;
        
        border/=100;
        
        if(nStagePositions!=0){
            float xMin=stagePositionsList.get(0).stageXCoordinate;
            float xMax=xMin;
            float yMin=stagePositionsList.get(0).stageYCoordinate;
            float yMax=yMin;
            
            for(int i=1; i<nStagePositions; i++){
                xMin=Math.min(xMin, stagePositionsList.get(i).stageXCoordinate);
                xMax=Math.max(xMax, stagePositionsList.get(i).stageXCoordinate);
                yMin=Math.min(yMin, stagePositionsList.get(i).stageYCoordinate);
                yMax=Math.max(yMax, stagePositionsList.get(i).stageYCoordinate);
            }
            
            float width=xMax-xMin;
            float height=yMax-yMin;
            
            double ratio=(targetWidth/(width*(1+border)));
            
            position pos=new position();
            pos.stageXCoordinate=(float) (-xMin+width*(border/2));
            pos.stageYCoordinate=(float) (-yMin+height*(border/2));
            
            out=NewImage.createImage("Stage map for "+fileName.replace(".stg", "").replace(".STG", ""), (int) (ratio*width*(1+border)), (int) (ratio*height*(1+border)), 1, 24, NewImage.FILL_WHITE);
            ImageProcessor iproc= out.getProcessor();
            
            stgFile tmp=this.duplicate();
            stgFileTransformer trans=new stgFileTransformer(tmp);
            trans.translate(pos);
            
            int fontSize=(int) (out.getHeight()*(1-border/2)/25);
            iproc.setFont(new Font("Arial", Font.BOLD, fontSize));
            
            int oldX=0;
            int oldY=0;
            
            for(int i=0; i<nStagePositions; i++){
                position currPos=tmp.stagePositionsList.get(i);
                
                int x=(int) (currPos.stageXCoordinate*ratio);
                int y=(int) (currPos.stageYCoordinate*ratio);
                
                if(drawPath){
                    if(i>0){
                        iproc.setLineWidth(out.getWidth()/400);
                        iproc.setColor(Color.LIGHT_GRAY);
                        iproc.drawLine(oldX, oldY, x, y);
                    }
                    oldX=x;
                    oldY=y;
                }
                
                iproc.setLineWidth(out.getWidth()/100);
                iproc.setColor(drawBlack?Color.BLACK:constants.COLORS_NAME[i%constants.COLORS_NAME.length]);
                iproc.drawDot(x, y);
                
                if(drawNames) iproc.drawString(currPos.stagePositionName, x-iproc.getStringWidth(currPos.stagePositionName)/2, (int) (y+(1+border)*fontSize));
                
            }
        }
        
        return out;
    }
}