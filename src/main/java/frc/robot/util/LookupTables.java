// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.util;


import edu.wpi.first.math.interpolation.InterpolatingDoubleTreeMap;
import edu.wpi.first.math.interpolation.InterpolatingTreeMap;
import edu.wpi.first.math.interpolation.Interpolator;
import edu.wpi.first.math.interpolation.InverseInterpolator;


/** Add your docs here. */
public class LookupTables {
    private static InterpolatingTreeMap<Double, Double> shooterAngleTable = new InterpolatingTreeMap<Double, Double>(InverseInterpolator.forDouble(), Interpolator.forDouble());
    private static InterpolatingTreeMap<Double, Double> shooterSpeedTable = new InterpolatingTreeMap<Double, Double>(InverseInterpolator.forDouble(), Interpolator.forDouble());
    
    // private static InterpolatingTreeMap<Double, Double> shooterAngleTable = new InterpolatingTreeMap<Double, Double>(
    // (up, q, down) -> {
    //     double upperToLower = up.doubleValue() - down.doubleValue();
    //     if(upperToLower <= 0.0){
    //         return 0.0;
    //     }
    //     double queryToLower = q.doubleValue() - down.doubleValue();
    //     if(queryToLower <= 0.0){
    //         return 0.0;
    //     }
    //     return queryToLower/upperToLower;
    // },
    // (val1, val2, d) -> {
    //     double dydx = val2.doubleValue() - val1.doubleValue();
    //     return dydx * d + val1.doubleValue();
    // });
    // private static InterpolatingTreeMap<Double, Double> shooterSpeedTable = new InterpolatingTreeMap<Double, Double>(
    // (up, q, down) -> {
    //     double upperToLower = up.doubleValue() - down.doubleValue();
    //     if(upperToLower <= 0.0){
    //         return 0.0;
    //     }
    //     double queryToLower = q.doubleValue() - down.doubleValue();
    //     if(queryToLower <= 0.0){
    //         return 0.0;
    //     }
    //     return queryToLower/upperToLower;
    // },
    // (val1, val2, d) -> {
    //     double dydx = val2.doubleValue() - val1.doubleValue();
    //     return dydx * d + val1.doubleValue();
    // });



    /**
     * <pre>
     *Inits Values for the Interpolating Tables
     *Key is Distance from Speaker,
     *Value is RPM or Angle Value at that distance
     * </pre>
     */
    public static void InitValues() {
        //                    DISTANCE     ENCODER VALUE
        shooterAngleTable.put(1.3620, .718); // .74663
        shooterAngleTable.put(1.7033, .7414); // .757
        shooterAngleTable.put(2.4447, .7677); // .7658
        shooterAngleTable.put(3.0194, .7824); // .7814
        shooterAngleTable.put(3.4798, .7873); // .783
        shooterAngleTable.put(3.9306, .797); // .8009
        shooterAngleTable.put(4.3053, .8009); // .803
        shooterAngleTable.put(4.6831, .801); // .804
        shooterAngleTable.put(5.3244, .806); // .80
        shooterAngleTable.put(5.7531, .807); // .80


        //shooterAngleTable.put(1.905, .75563); // .74663
        //shooterAngleTable.put(2.2987, .766); // .757
        //shooterAngleTable.put(2.809875, .7748); // .7658
        //shooterAngleTable.put(3.39725, .7904); // .7814
        //shooterAngleTable.put(3.9497, .792); // .783
        //shooterAngleTable.put(4.5085, .8099); // .8009
        //shooterAngleTable.put(5.0927, .812); // .803
        //shooterAngleTable.put(5.7531, .813); // .804

        // new values
       
    
    }

    public static InterpolatingTreeMap<Double, Double> getAngleTable(){
        InitValues(); 
        return shooterAngleTable;
    }

    public static InterpolatingTreeMap<Double, Double> getSpeedTable(){
        InitValues();
        return shooterAngleTable;
    }

    public static double getSpeedValueAtDistance(Double distMeterFromSpeaker) {
        InitValues();
        return (double) shooterSpeedTable.get(distMeterFromSpeaker);
    }

    public static double getAngleValueAtDistance(Double distMeterFromSpeaker) {
        InitValues();
        //System.out.println(shooterAngleTable.get(distMeterFromSpeaker));
        return (double) shooterAngleTable.get(distMeterFromSpeaker);  
    }
    
    
}
