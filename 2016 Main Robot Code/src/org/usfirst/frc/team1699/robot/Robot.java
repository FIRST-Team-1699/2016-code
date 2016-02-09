
package org.usfirst.frc.team1699.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
    final String defaultAuto = "Default";
    final String customAuto = "My Auto";
    String autoSelected;
    SendableChooser chooser;
    
    //Joysticks
    Joystick extreme;
    Joystick logitech;
    Joystick xbox;
    
    //Motor Control
    //Drive Motors
    CANTalon rightDrive1;
    CANTalon rightDrive2;
    CANTalon leftDrive1;
    CANTalon leftDrive2;
    
    //Shooter Motors
    VictorSP leftShoot;
    VictorSP rightShoot;
    VictorSP bottomShoot;
    VictorSP topShoot;
    
    //Shooter Adjustment
    VictorSP shootAdjust;
    
    //Ball pickup
    VictorSP leftPickup;
    VictorSP rightPickup;
	
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
        chooser = new SendableChooser();
        chooser.addDefault("Default Auto", defaultAuto);
        chooser.addObject("My Auto", customAuto);
        SmartDashboard.putData("Auto choices", chooser);
        
        //Human Controls
        extreme = new Joystick(1);
        logitech = new Joystick(2);
        xbox = new Joystick(3);
        
        //Motor Control
        //Drive Motors
        rightDrive1 = new CANTalon(10);
        rightDrive2 = new CANTalon(11);
        leftDrive1 = new CANTalon(12);
        leftDrive2 = new CANTalon(13);
        
        //Shooter Motors
        leftShoot = new VictorSP(0);
        rightShoot = new VictorSP(1);
        bottomShoot = new VictorSP(2);
        topShoot = new VictorSP(3);
        
        //Shooter Adjustment
        shootAdjust = new VictorSP(4);
        
        //Ball pickup
        leftPickup = new VictorSP(5);
        rightPickup = new VictorSP(6);
    }
    
	/**
	 * This autonomous (along with the chooser code above) shows how to select between different autonomous modes
	 * using the dashboard. The sendable chooser code works with the Java SmartDashboard. If you prefer the LabVIEW
	 * Dashboard, remove all of the chooser code and uncomment the getString line to get the auto name from the text box
	 * below the Gyro
	 *
	 * You can add additional auto modes by adding additional comparisons to the switch structure below with additional strings.
	 * If using the SendableChooser make sure to add them to the chooser code above as well.
	 */
    public void autonomousInit() {
    	autoSelected = (String) chooser.getSelected();
//		autoSelected = SmartDashboard.getString("Auto Selector", defaultAuto);
		System.out.println("Auto selected: " + autoSelected);
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
    	switch(autoSelected) {
    	case customAuto:
        //Put custom auto code here   
            break;
    	case defaultAuto:
    	default:
    	//Put default auto code here
            break;
    	}
    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
//    	Joystick 1 (extreme 3d):
//    	    x-axis: drive right side
//    	Joystick 2 (logitech):
//    	    x-axis: drive left side
//    	    button 3: pickup
//    	    button 4/5: up/down pickup mechanism 
//    	Driver 2:
//    	Joystick 1 (xbox):
//    	    axis 3: shooter up/down
//    	    button 1-4: shooter speeds
//    	    button 5/6: camera switch
    	
    	if(extreme.getX() == 1){
    		//right forward
    	}else if(extreme.getX() == -1){
    		//right backward
    	}else{
    		//set all to 0
    	}
    	
    	if(logitech.getX() == 1){
    		//left forward
    	}else if(logitech.getX() == -1){
    		//right backward
    	}else{
    		//set all to 0
    	}
    	
    	if(logitech.getRawButton(3)){
    		//pickup
    	}else{
    		//set all 0
    	}
    	
    	if(logitech.getRawButton(4)){
    		//pickup up
    	}else if(logitech.getRawButton(5)){
    		//pickup down
    	}else{
    		//set all 0
    	}
    	
    	if(xbox.getZ() == 1){
    		//shooter up
    	}else if(xbox.getZ() == -1){
    		//shooter down
    	}else{
    		//set all 0
    	}
    	
    	if(xbox.getRawButton(1)){
    		//shooter speed 1
    	}else if(xbox.getRawButton(2)){
    		//shooter speed 2
    	}else if(xbox.getRawButton(3)){
    		//shooter speed 3
    	}else if(xbox.getRawButton(4)){
    		//shooter speed 4
    	}else{
    		//set all 0
    	}
    	
    	if(xbox.getRawButton(5)){
    		//camera 1
    	}else if(xbox.getRawButton(6)){
    		//camera 2
    	}
    	
    	if(extreme.getTrigger()){
    		//gear up
    	}else if(logitech.getTrigger()){
    		//gear down
    	}
    }	
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
    
    }
    
}
