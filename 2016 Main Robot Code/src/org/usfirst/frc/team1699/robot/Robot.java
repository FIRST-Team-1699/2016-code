
package org.usfirst.frc.team1699.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

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

    // Open config files
    iniReader auto1Ini = new iniReader("1699-autonomous1.ini");
    iniReader teleopIni = new iniReader("1699-preferences.ini");
    
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
    
    public void autonomousInit() {
    	autoSelected = (String) chooser.getSelected();
		System.out.println("Auto selected: " + autoSelected);
    }

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

    public void teleopPeriodic() {
//    	Joystick 1 (extreme 3d):
//    	    x-axis: drive right side
//    	Joystick 2 (logitech):
//    	    x-axis: drive left side
//    	    button 3: pickup
//    	Driver 2:
//    	Joystick 1 (xbox):
//    	    axis 3: shooter up/down
//    	    button 1-4: shooter speeds
//    	    button 5/6: camera switch
    	
    	//Control for right motors
    	if(extreme.getX() == 1){
    		//right forward
    		rightDrive1.set(1);
    		rightDrive2.set(1);
    	}else if(extreme.getX() == -1){
    		//right backward
    		rightDrive1.set(-1);
    		rightDrive2.set(-1);
    	}else{
    		//set all to 0
    		rightDrive1.set(0);
    		rightDrive2.set(0);
    	}
    	
    	//Control for left drive
    	if(logitech.getX() == 1){
    		//left forward
    		leftDrive1.set(1);
    		leftDrive2.set(1);
    	}else if(logitech.getX() == -1){
    		//right backward
    		leftDrive1.set(-1);
    		leftDrive2.set(-1);
    	}else{
    		//set all to 0
    		leftDrive1.set(0);
    		leftDrive2.set(0);
    	}
    	
    	if(logitech.getRawButton(3)){
    		//pickup
    		leftPickup.set(.8);
    		rightPickup.set(.8);
    	}else{
    		//set all 0
    		leftPickup.set(0);
    		rightPickup.set(0);
    	}
    	
    	//Move shooter
    	if(xbox.getZ() == 1){
    		//shooter up
    		shootAdjust.set(.4);
    	}else if(xbox.getZ() == -1){
    		//shooter down
    		shootAdjust.set(-.4);
    	}else{
    		//set all 0
    		shootAdjust.set(0);
    	}
    	
    	//Shoot with different speeds
    	if(xbox.getRawButton(1)){
    		//shooter speed 1
    		leftShoot.set(.7);
    		rightShoot.set(.7);
    		topShoot.set(.7);
    		bottomShoot.set(.7);
    	}else if(xbox.getRawButton(2)){
    		//shooter speed 2
    		leftShoot.set(.8);
    		rightShoot.set(.8);
    		topShoot.set(.8);
    		bottomShoot.set(.8);
    	}else if(xbox.getRawButton(3)){
    		//shooter speed 3
    		leftShoot.set(.9);
    		rightShoot.set(.9);
    		topShoot.set(.9);
    		bottomShoot.set(.9);
    	}else if(xbox.getRawButton(4)){
    		//shooter speed 4
    		leftShoot.set(1);
    		rightShoot.set(1);
    		topShoot.set(1);
    		bottomShoot.set(1);
    	}else{
    		//set all 0
    		leftShoot.set(0);
    		rightShoot.set(0);
    		topShoot.set(0);
    		bottomShoot.set(0); 
    	}
    	
    	//Camera control
    	if(xbox.getRawButton(5)){
    		//camera 1
    	}else if(xbox.getRawButton(6)){
    		//camera 2
    	}
    	
    	//Gearing control
    	if(extreme.getTrigger()){
    		//gear up
    	}else if(logitech.getTrigger()){
    		//gear down
    	}
    }
    
    public void testPeriodic() {
    
    }
    
}
