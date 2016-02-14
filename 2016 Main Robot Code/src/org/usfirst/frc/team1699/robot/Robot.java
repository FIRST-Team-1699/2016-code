package org.usfirst.frc.team1699.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.RobotDrive.MotorType;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {
    final String defaultAuto = "Default";
    final String customAuto = "My Auto";
    String autoSelected;
    SendableChooser chooser;
    
    //Camera Server
    CameraServer cam;
    
    //Joysticks
    Joystick extreme3d;
    Joystick attack3;
    Joystick xbox;
    
    //Motor Control
    //Drive Motors
    CANTalon rightDrive1;
    CANTalon rightDrive2;
    CANTalon leftDrive1;
    CANTalon leftDrive2;
    
    // Robot Drive for easier motor control
    RobotDrive rDrive;
    
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
    iniReader teleopIni = new iniReader("1699-config.ini");
    
    //Moar things
    
    // Joystick speed after "gearing"
    double xSpeed1;
    double xSpeed2;
    
    // Current "gear"
    double gearRatio;
    
    //Ini vars
    final double gear1 = teleopIni.getValue("gear1");
    final double gear2 = teleopIni.getValue("gear2");
    final double gear3 = teleopIni.getValue("gear3");
    
    boolean notHeld;
    
    // Current "gear" number (current options: 1-3) (initializes at 2)
    int cGear = 2; 
    
    
    public void robotInit() {  	
        chooser = new SendableChooser();
        chooser.addDefault("Default Auto", defaultAuto);
        chooser.addObject("My Auto", customAuto);
        SmartDashboard.putData("Auto choices", chooser);
        
        //Human Controls
        extreme3d = new Joystick(0);
        attack3 = new Joystick(1);
        xbox = new Joystick(2);
        
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
        
        //Drive
        rDrive = new RobotDrive(leftDrive1, leftDrive2, rightDrive1, rightDrive2);        
        
        notHeld = false;
        
        //Camera
        cam = CameraServer.getInstance();
        cam.setQuality(50);
        cam.startAutomaticCapture("cam0");
    }
    
    public void autonomousInit() {
    	autoSelected = (String) chooser.getSelected();
		System.out.println("Auto selected: " + autoSelected);
    }

    public void autonomousPeriodic() {
    	switch(autoSelected) {
    	case customAuto:
        // alksdjf   
            break;
    	case defaultAuto:
    		
    	default:
    	//Put default auto code here
            break;
    	}
    }

    public void teleopPeriodic() {
//    	Joystick 1 (extreme3d):
//    	    x-axis: drive right side
//    	Joystick 2 (attack3):
//    	    x-axis: drive left side
//    	    button 3: pickup
//    	Driver 2:
//    	Joystick 1 (xbox):
//    	    axis 3: shooter up/down
//    	    button 1-4: shooter speeds
//    	    button 5/6: camera switch
    	
    	// gearing should go near robotDrive call
    	xSpeed1 = extreme3d.getRawAxis(1) * gearRatio;
    	xSpeed2 = -1 * attack3.getRawAxis(1) * gearRatio;
    	
    	rDrive.tankDrive(xSpeed2, xSpeed1); // check call and logic, did on the fly 
    	
    	/*if(attack3.getRawButton(3)){
    		//pickup
    		// all motors (except for drive) (or anything that we will never change) should be revived from the ini
    		// call to get value example below
    		leftPickup.set(teleopIni.getValue("leftPickupSpeed"));
    		rightPickup.set(.8);
    	}else{
    		//set all 0
    		leftPickup.set(0);
    		rightPickup.set(0);
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
    	*/
    	//Camera control
    	if(xbox.getRawButton(5)){
    		//camera 1
    	}else if(xbox.getRawButton(6)){
    		//camera 2
    	}
    	
    	//Gearing control
    	if(extreme3d.getTrigger() && !notHeld)
    	{
    		//gear up
    		if (cGear == 1)
    		{
    			cGear += 1;
    			notHeld = true;
    		}
    		else if (cGear == 2)
    		{
    			cGear += 1;
    			notHeld = true;
    		}
    	}
    	else if(attack3.getTrigger() && !notHeld)
    	{
    		if (cGear == 3)
    		{
    			cGear = 2;
    			notHeld = true;
    		}
    		else if (cGear == 2 && !notHeld)
    		{
    			cGear = 1;
    			notHeld = true;
    		}
    	}
    	if (cGear == 1) {gearRatio = gear1;}
    	else if (cGear == 2) {gearRatio = gear2;}
    	else if (cGear == 3) {gearRatio = gear3;}
    	else {gearRatio = 0.0;}
    	
    	if(!extreme3d.getTrigger() && notHeld)
    	{
    		notHeld = false;
    	}
    	System.out.println(gearRatio);
    }
    
    public void disabledInit()
    {
    }
    
    public void testPeriodic() {
    
    }
    
}
