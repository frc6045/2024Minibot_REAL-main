// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.FunctionalCommand;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.Constants.ClimbConstants;
import frc.robot.Constants.FeederConstants;
import frc.robot.Constants.FieldConstants;
import frc.robot.Constants.IntakeConstants;
import frc.robot.Constants.ShooterConstants;
import frc.robot.commands.closedloop.AimAtSpeaker;
import frc.robot.commands.closedloop.HoldAngle;
import frc.robot.commands.closedloop.OneButtonShooting;
import frc.robot.commands.closedloop.PIDAngleControl;
import frc.robot.commands.closedloop.PIDShooter;
import frc.robot.commands.closedloop.TurnAndAim;
import frc.robot.commands.openloop.AngleOpenLoop;
import frc.robot.commands.openloop.ClimberOpenLoop;
import frc.robot.commands.openloop.FeederOpenLoop;
import frc.robot.commands.openloop.IntakeOpenLoop;
import frc.robot.commands.openloop.ShooterAndFeederOpenLoop;
import frc.robot.commands.openloop.ShooterOpenLoop;
import frc.robot.commands.openloop.TrapOpenLoop;
import frc.robot.subsystems.AngleController;
import frc.robot.subsystems.Climber;
import frc.robot.subsystems.Feeder;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.Pneumatics;
import frc.robot.subsystems.Shooter;
import frc.robot.subsystems.Trap;
import frc.robot.subsystems.swerve.DriveSubsystem;
import frc.robot.util.LookupTables;
import frc.robot.util.PoseMath;

/** Add your docs here. */
// Henry's Comment
// Dominic
public class Bindings {

    private static boolean bCompressorEnabled = false;
    private static boolean bIntakeToggle = true;
    private static boolean bTrapToggle = true;


    public Bindings() {}

        
        public static void InitBindings(XboxController driverController, XboxController operatorController, 
            DriveSubsystem driveSubsystem, 
            Shooter shooter,
            Feeder feeder,
            Pneumatics pneumatics, 
            AngleController angleController,
            Intake intake,
            Climber climber, 
            Trap trap){
        
        //new JoystickButton(driverController, XboxController.Button.kStart.value).onTrue(new InstantCommand(() -> { driveSubsystem.zeroHeading();}, driveSubsystem));
        new Trigger(() -> {return driverController.getStartButtonPressed();}).onTrue(new InstantCommand(() -> {driveSubsystem.zeroHeading();}, driveSubsystem));
        
        new Trigger(() -> {return driverController.getYButtonPressed();}).onTrue(new AimAtSpeaker(driveSubsystem));

        //shooter
        //new Trigger(() -> {return operatorController.getAButton();}).whileTrue(new FeederOpenLoop(feeder, () -> {return FeederConstants.kFeederSpeed;}));
        
        //new Trigger(() -> {return operatorController.getLeftTriggerAxis() > 0;}).whileTrue(new ShooterOpenLoop(shooter, operatorController::getLeftTriggerAxis));

        //new Trigger(() -> {return operatorController.getRightTriggerAxis() > 0;}).whileTrue(new ShooterAndFeederOpenLoop(shooter, feeder, operatorController::getRightTriggerAxis, operatorController::getRightTriggerAxis));
        
        new Trigger(() -> {return operatorController.getXButton();}).whileTrue(new PIDShooter(shooter, feeder, intake, -6000, false));
        
        new Trigger(() -> {return operatorController.getAButton();}).whileTrue(new ParallelCommandGroup(new ShooterOpenLoop(shooter, () -> {return ShooterConstants.kShooterMaxSpeed;}), new FeederOpenLoop(feeder, () -> {return FeederConstants.kFeederSpeed;})));
        //new Trigger(() -> {return driverController.getBackButtonPressed();}).onTrue(new TurnAndAim(angleController, driveSubsystem));
        if(FieldConstants.kVisionEnable){
        //could possibly still work with odometry instead of vision
        new Trigger(() -> {return operatorController.getBackButtonPressed();}).onTrue(new PIDAngleControl(angleController,() -> {return LookupTables.getAngleValueAtDistance(PoseMath.getDistanceToSpeakerBack(driveSubsystem.getPose()));})); //3.9624 works
        } else {
        new Trigger(() -> {return operatorController.getBackButtonPressed();}).onTrue(new PIDAngleControl(angleController,() -> {return ShooterConstants.kAngleCloseSetpoint;})); //number returned is angle setpoint
        }  

        new Trigger(() -> {return operatorController.getStartButtonPressed();}).onTrue(new PIDAngleControl(angleController,() -> {return ShooterConstants.kAnglePodiumSetpoint;})); //was kAngleMidSetpoint

        new Trigger(() -> {return operatorController.getBButtonPressed();}).onTrue(new PIDAngleControl(angleController,() -> {return ShooterConstants.kAngleRestSetpoint;}));

        new Trigger(() -> {return operatorController.getYButtonPressed();}).onTrue(new PIDAngleControl(angleController, () -> {return ShooterConstants.kAngleClimbSetpoint;}));

        //new Trigger(() -> {return driverController.getBackButtonPressed();}).onTrue(new TurnAndAim(angleController, driveSubsystem)); //3.9624 works

        
        //Angle Controller
        new Trigger(() -> {return driverController.getBButton();}).whileTrue(new AngleOpenLoop(angleController, -ShooterConstants.kAngleControlMaxSpeed)).onFalse(new HoldAngle(angleController, () -> {return angleController.getAngleEncoder().getPosition();}));

        new Trigger(() -> {return driverController.getAButton();}).whileTrue(new AngleOpenLoop(angleController, ShooterConstants.kAngleControlMaxSpeed)).onFalse(new HoldAngle(angleController, () -> {return angleController.getAngleEncoder().getPosition();}));

        //Compressor Toggle
        new Trigger(() -> {return driverController.getRightBumper();}).onTrue(new InstantCommand(() -> {
        if(bCompressorEnabled){
            pneumatics.disableCompressor();
            bCompressorEnabled = false;
        } else {
            pneumatics.enableCompressor();
            bCompressorEnabled = true;
        }
        }, pneumatics));

        
        // // B toggle for SingleSolenoid
        
        new Trigger(() -> {return operatorController.getPOV() == 180;}).onTrue(new InstantCommand(() -> {
            pneumatics.ActutateIntakeSolenoid(bIntakeToggle);
            bIntakeToggle = !bIntakeToggle;
        }));
        //intake solenoids (up is down, down is up! i hate it)
        // new Trigger(() -> {return operatorController.getPOV() == 0;}).onTrue(new InstantCommand(() -> {
        //     pneumatics.ActutateIntakeSolenoid(false);
        // }, intake));

        //    new Trigger(() -> {return operatorController.getPOV() == 180;}).onTrue(new InstantCommand(() -> {
        //     pneumatics.ActutateIntakeSolenoid(true);
        // }, intake));

        new Trigger(() -> {return operatorController.getPOV() == 0;}).onTrue(new InstantCommand(() -> {
            pneumatics.ActutateTrapSolenoid(bTrapToggle);
            bTrapToggle = !bTrapToggle;
        }));

        // new Trigger(() -> {return operatorController.getPOV() == 90;}).onTrue(new InstantCommand(() -> {
        //     pneumatics.ActutateTrapSolenoid(true);
        // }, trap));

        // new Trigger(() -> {return operatorController.getPOV() == 270;}).onTrue(new InstantCommand(() -> {
        //     pneumatics.ActutateTrapSolenoid(false);
        // }, trap));

        // // intake toggle
        // do things
        //  new Trigger(() -> {return operatorController.getLeftBumperPressed();}).onTrue(new InstantCommand(() -> {
        //    if(!bIntakeToggle){
        //      intake.intakeIn();
        //      bIntakeToggle = true;
        //    } else {
        //     intake.stopIntake();
        //      bIntakeToggle = false;
        //    }
        //  }, intake));

        

    

         new Trigger(() -> {return operatorController.getRightTriggerAxis() > .05;}).whileTrue(new IntakeOpenLoop(intake, operatorController::getRightTriggerAxis));

         new Trigger(() -> {return operatorController.getLeftTriggerAxis() > .05;}).whileTrue(new IntakeOpenLoop(intake, () -> {return -operatorController.getLeftTriggerAxis();}));

         new Trigger(() -> {return operatorController.getRightBumper();}).whileTrue(new IntakeOpenLoop(intake, () -> {return IntakeConstants.kIntakeSlowSpeed;}));

         new Trigger(() -> {return operatorController.getLeftBumper();}).whileTrue(new IntakeOpenLoop(intake, () -> {return -IntakeConstants.kIntakeSlowSpeed;}));

         //new Trigger(() -> {return operatorController.getRightBumperPressed();}).onTrue(new InstantCommand(() -> {pneumatics.ToggleTrapSolenoid();}, pneumatics));
       
         new Trigger(() -> {return driverController.getPOV() == 90;}).whileTrue(new ClimberOpenLoop(climber, () -> {return ClimbConstants.kClimbMaxSpeed;}));
         new Trigger(() -> {return driverController.getPOV() == 270;}).whileTrue(new ClimberOpenLoop(climber, () -> {return -ClimbConstants.kClimbMaxSpeed;}));

         //new Trigger(() -> {return operatorController.getPOV() == 270;}).whileTrue(new TrapOpenLoop(trap, () -> {return ClimbConstants.kTrapMaxSpeed;}));
         //new Trigger(() -> {return operatorController.getPOV() == 90;}).whileTrue(new TrapOpenLoop(trap, () -> {return -ClimbConstants.kTrapMaxSpeed;}));

         new Trigger(() -> {return operatorController.getPOV() == 270;}).whileTrue(new ParallelCommandGroup(new TrapOpenLoop(trap, () -> {return ClimbConstants.kTrapMaxSpeed;}), new IntakeOpenLoop(intake, () -> {return 1.0;})));
         new Trigger(() -> {return operatorController.getPOV() == 90;}).whileTrue(new ParallelCommandGroup(new TrapOpenLoop(trap, () -> {return -ClimbConstants.kTrapMaxSpeed;}), new IntakeOpenLoop(intake, () -> {return -1.0;})));

        

    }
    public static boolean getCompressorEnabled(){
        return bCompressorEnabled;
    }

    
}
