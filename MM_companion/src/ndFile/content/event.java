package ndFile.content;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import utilities.tools;

/*
 *  event.java
 * 
 *  Created on 19 juil. 2010, 22:30:49
 * 
 *  Copyright (C) 2010 Fabrice P. Cordelieres
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

/**
 * This class stores an event extracted from/to be written to an nd file
 * @author fab
 */
public class event {
    /** Names of the different types of events **/
    public static final String[] TYPE={"Custom", "Stimulation", "Washout", "Injection", "Laser Pulse", "Stimulation"};

    /** **/
    public static final String EVENT_DISPLAY="Event type\tDescription\tTime Point\tStage Position(s)\tElapsed Time\tColor";

    /** Type of the event **/
    public String Type="";

    /** Description associated with the event **/
    public String Description="";

    /** Time of the event **/
    public Date Time=null;

    /** Event on several frames: equal to 1 if this is a punctual event, -9999 otherwise **/
    /*
     * If EventOnAllPositionsFromNow is 1, the event start at the start of one round
     * of multiple position visiting (will look like "all positions at timepoint XX" under Metamorph)
     * If EventOnAllPositionsFromNow is -9999, the event start at the current position
     * and timepoint and stops once returning to this position position visiting
     * (will look like "position XX, timepoint YY to position XX-1, timepoint YY+1" under Metamorph)
     */
    public int EventOnAllPositionsFromNow=0;

    /** TimePoint **/
    public int TimePoint=0;

    /** Position, if not all positions, 1 otherwise **/
    public int Position=0;

    /** All positions ? TRUE or FALSE**/
    public boolean AllPositions=false;


    /** Color (decimal value) **/
    /*
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
     * White:   |    255/255/255   |  0
     * Black:   |    000/000/000   |  16777215
     */
    public int Color=0;

    /**
     * Creates a new event, based on information contained in a String
     * @param argument String cotaining all information
     */
    public event(String argument){
        argument.replaceAll("\"", "");
        String[] in=argument.split(", ");
        if (in.length!=8) throw new IllegalArgumentException("The number of argument within an Event should be exactly 8");
        Type=in[0];
        Description=in[1];
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd hh:mm:ss");
        try {
            Time = sdf.parse(in[2]);
        } catch (ParseException ex) {
            Logger.getLogger(event.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        EventOnAllPositionsFromNow=Integer.parseInt(in[3]);
        TimePoint=Integer.parseInt(in[4]);
        Position=Integer.parseInt(in[5]);
        AllPositions=Boolean.parseBoolean(in[6]);
        Color=Integer.parseInt(in[7]);
    }

    /**
     * Returns a tab delimited String containing a human readable version of the event
     * stored in the nd file
     * @param StartTime experiment start time
     * @param nTime number of timepoints
     * @param nPos number of positions
     * @return a tab delimited String containing a human readable version of the event
     */
    public String toString(Date StartTime, int nTime, int nPos){
        return  Type+"\t"+Description+"\t"+
                (Position==1?
                TimePoint+"\tall positions at timepoint "+TimePoint:
                +TimePoint+(TimePoint<nTime?", "+(TimePoint+1):"")+"\tposition "+Position+", timepoint "+TimePoint+(TimePoint<nTime?" to position "+(Position-1)+", timepoint "+(TimePoint+1):((TimePoint==nTime && Position!=nPos)?" to position "+nPos+", timepoint "+nTime:"")))+"\t"
                +tools.elapsedTime(StartTime, Time)+"\t"+Color;
    }
}
