
import stgFile.content.position;
import stgFile.stgFile;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author fab
 */
public class TestOP{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        stgFile stg=new stgFile();
        
        position pos1=new position();
        pos1.stagePositionName="pos1";
        pos1.stageXCoordinate=0;
        pos1.stageYCoordinate=0;
        pos1.stageZCoordinate=0;
        pos1.stageZ2Coordinate=0;
        stg.addPosition(pos1);
        
        position pos2=new position();
        pos2.stagePositionName="pos2";
        pos2.stageXCoordinate=1;
        pos2.stageYCoordinate=0;
        pos2.stageZCoordinate=0;
        pos2.stageZ2Coordinate=0;
        stg.addPosition(pos2);
        
        position pos3=new position();
        pos3.stagePositionName="pos3";
        pos3.stageXCoordinate=0;
        pos3.stageYCoordinate=1;
        pos3.stageZCoordinate=0;
        pos3.stageZ2Coordinate=0;
        stg.addPosition(pos3);
        
        position pos4=new position();
        pos4.stagePositionName="pos4";
        pos4.stageXCoordinate=1;
        pos4.stageYCoordinate=1;
        pos4.stageZCoordinate=0;
        pos4.stageZ2Coordinate=0;
        stg.addPosition(pos4);
        
        position trans=new position();
        trans.stageXCoordinate=(float) 0.5;
        trans.stageYCoordinate=(float) 0.5;
        
        position ref1=new position();
        ref1.stageXCoordinate=0;
        ref1.stageYCoordinate=0;
        ref1.stageZCoordinate=0;
        ref1.stageZ2Coordinate=0;
        
        position ref2=new position();
        ref2.stageXCoordinate=1;
        ref2.stageYCoordinate=0;
        ref2.stageZCoordinate=2;
        ref2.stageZ2Coordinate=2;
        
        position ref3=new position();
        ref3.stageXCoordinate=0;
        ref3.stageYCoordinate=1;
        ref3.stageZCoordinate=2;
        ref3.stageZ2Coordinate=2;
        
        System.out.println(stg.toString());
        
        System.out.println(stg.toString());

        System.out.println(stg.toString());
        
        
    }
}
