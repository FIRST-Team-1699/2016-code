/*
 * FIRST Team 1699's 2016 Robot Code
 * 
 * @author thatging3rkid, FIRST Team 1699
 * @author squirlemaster42, FIRST Team 1699 * 
 * 
 * v0.1.1, published on 3/9/16, used at NE North Shore Event
 */
package org.usfirst.frc.team1699.robot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.apache.commons.io.output.TeeOutputStream;

public class Robot extends IterativeRobot {
    // Autonomous Chooser strings
	final String defaultAuto = "1699-autoDef";
    final String auto1S = "1699-auto1";
    final String auto2S = "1699-auto2";
    final String auto3S = "1699-auto3";
    final String auto4S = "1699-auto4";
    final String auto5S = "1699-auto5";
    final String auto6S = "1699-auto6";
    final String auto7S = "1699-auto7";
    final String auto8S = "1699-auto8";
    String autoSelected;
    SendableChooser chooser;
    
    // Logging decelerations
    File logf;
    Runtime runtime;
    
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
    
    // Encoders
    Encoder frontLeftE;
    Encoder frontRightE;
    
    //Shooter Motors
    VictorSP leftShoot;
    VictorSP rightShoot;
    VictorSP bottomShoot;
    VictorSP topShoot;
    
    //VictorSP leftPickup;
    VictorSP rightPickup;
    
    // Initialize ini file
    iniReader teleopIni;
    
    //Moar things
    // Joystick speed after "gearing"
    double xSpeed1;
    double xSpeed2;
    
    // Joystick single boolean variables
    boolean leftNotHeld;
    boolean rightNotHeld;
    
    // Current "gear" ratio
    double gearRatio;
    
    // Current "gear" number (current options: 1-3) (initializes at 2)
    int cGear = 2; 
    
    //ini retrieved
    double gear1;
    double gear2;
    double gear3;
    double autoSpeed;
    double pickupSpeed;
    double shooterMotorSpeed1;
    double shooterMotorSpeed2;
    double shooterMotorSpeed3;
    double shooterMotorSpeed4;
    
    // Autonomous variables
    double iter;
    int autoCount1;
    iniReader autoCommands;
    @SuppressWarnings("rawtypes")
	ArrayList commands;
    @SuppressWarnings("rawtypes")
	ArrayList cCommand;    
    
    // Joystick bindings
    // ALL should be finals
    //Shooter
    final int TRIGGER_AXIS_1 = 3;
    final int TRIGGER_AXIS_2 = 2;
    final int X_BUTTON = 1;
    final int Y_BUTTON = 2;
    final int A_BUTTON = 3;
    final int B_BUTTON = 4;
    
    //Ball pickup
    final int PICK_UP = 3;
    final int DROP_BALL = 2;
    final int XBOX_UP = 7;
    final int XBOX_DOWN = 8;
    
    //Camera control
    final int CAM_1 = 5;
    final int CAM_2 = 6;
    
    MultiCameraServer camera;
    
    public void robotInit() { 
    	// Logging start
    	this.loggingInit();
    	
    	// iniReader
    	teleopIni = new iniReader("1699-config.ini");
    	gear1 = teleopIni.getValue("gear1");
        gear2 = teleopIni.getValue("gear2");
        gear3 = teleopIni.getValue("gear3");
        autoSpeed = teleopIni.getValue("autoSpeed");
        pickupSpeed = teleopIni.getValue("pickupSpeed");
        shooterMotorSpeed1 = teleopIni.getValue("shooterMotorSpeed1");
        shooterMotorSpeed2 = teleopIni.getValue("shooterMotorSpeed2");
        shooterMotorSpeed3 = teleopIni.getValue("shooterMotorSpeed3");
        shooterMotorSpeed4 = teleopIni.getValue("shooterMotorSpeed4");
    	
    	// Autonomous chooser
        chooser = new SendableChooser();
        chooser.addDefault("Default Auto", defaultAuto);
        chooser.addObject("Autonomous 1", auto1S);
        chooser.addObject("Autonomous 2", auto2S);
        chooser.addObject("Autonomous 3", auto3S);
        chooser.addObject("Autonomous 4", auto4S);
        chooser.addObject("Autonomous 5", auto5S);
        chooser.addObject("Autonomous 6", auto6S);
        chooser.addObject("Autonomous 7", auto7S);
        chooser.addObject("Autonomous 8", auto8S);
        SmartDashboard.putData("Auto Chooser", chooser);
        
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
        leftShoot = new VictorSP(1);
        topShoot = new VictorSP(2);
        bottomShoot = new VictorSP(3);
        rightShoot = new VictorSP(4);
        
        //Ball pickup
        rightPickup = new VictorSP(6);
        
        //Drive
        rDrive = new RobotDrive(leftDrive1, leftDrive2, rightDrive1, rightDrive2);        
        
        // More Encoders
        frontLeftE = new Encoder(1, 2, true, Encoder.EncodingType.k4X);
        frontRightE = new Encoder(3, 4, true, Encoder.EncodingType.k4X);
        frontLeftE.setDistancePerPulse(12.0);
        frontRightE.setDistancePerPulse(12.0);
        
        // Joystick booleans
        leftNotHeld = false;
        rightNotHeld = false;
        
        //Camera
        camera = new MultiCameraServer(2);
        camera.setCamera(0);
    }
    
    // Starts logging, should be called first thing
    // Apache Commons needs to be property linked on the local PC (use libs/libs.md for tutorial)
    public void loggingInit()
    {
        // More initializers, please. 
        runtime = Runtime.getRuntime();
        @SuppressWarnings("unused") // Actually used tho
		Process p;
        boolean logfcont = true;
        Integer logfcount = new Integer(0);
        
        // Looks for non-existent log file
        while (logfcont)
        {
        	logf = new File("/home/lvuser/1699-logs/log-" + logfcount + ".log");
        	if (logf.exists()) 
        	{
        		logfcont = true;
        		logfcount += 1;
        	}
        	else {logfcont = false;}
        }
        
        // Renames current log file to the last number in the list and creates new log file
        try 
        {
        	p = runtime.exec("mv /home/lvuser/1699-logs/log-current.log /home/lvuser/1699-logs/log-" + logfcount.toString() + ".log");
        	p = runtime.exec("touch /home/lvuser/1699-logs/log-current.log");
        }
        catch (Exception e) {e.printStackTrace();}
        
        // Prepares for new log
        logf = new File("/home/lvuser/1699-logs/log-current.log");
        try
        {
        	// Makes new output stream in log-current.log
        	FileOutputStream fos = new FileOutputStream(logf);
        	
        	// Makes Dual-output, called Tee for some reason.
        	TeeOutputStream tos = new TeeOutputStream(System.out, fos);
        	
        	// Makes a PrintStream out of the new, dual-output Tee 
        	PrintStream ps = new PrintStream(tos);
        	
        	// Sets the above PrintStream to System.out
        	System.setOut(ps);
        	System.out.println("Success setting Tee Output Stream.");
        } 
        catch (FileNotFoundException e) {e.printStackTrace();}
        catch (Exception e) {e.printStackTrace();}
    }	
    
    // Tells us that the robot is disabled
    public void disabledInit()
    {
    	System.out.println("|------------------------------------------------------|");
    	System.out.println("| Team 1699 Robot: awating drive mode                  |");
    	System.out.println("|------------------------------------------------------|");
    }    
    
    // Runs before autonomous
    public void autonomousInit() {
    	autoSelected = (String) chooser.getSelected();
		System.out.println("Auto selected: " + autoSelected);
		iter = 0;
    	autoCommands = new iniReader(autoSelected + ".ini");
    	commands = autoCommands.getFile();
    	autoCount1 = 0;
    }

    // Called periodically during autonomous
    public void autonomousPeriodic() 
    {
    	camera.run();
    	
    	// NOTE: this loop's unit is milliseconds!
    	try
    	{
    		cCommand = ((ArrayList) commands.get(autoCount1));
    
    		switch (((String) cCommand.get(0)).toLowerCase()){
    		
    			// Sets drive motors to speed
    			case "drive":
    			{
    				rDrive.arcadeDrive(autoSpeed, 0);
    			}
    		
    			// Sets one side of drive motors to speed (to try and turn)
    			case "rotate":
    			{
    				// Check if positive
    				if ((double) cCommand.get(1) > 0)
    				{
    					rDrive.tankDrive(autoSpeed, 0);
    				}
    			
    				// Check if negative
    				if ((double) cCommand.get(1) < 0)
    				{
    					rDrive.tankDrive(0, autoSpeed);
    				}	
    			}
    		
    			// Stops all robot functions
    			case "sleep":
    			{
    				rDrive.arcadeDrive(0, 0);
    				rightPickup.set(0);
    				leftShoot.set(0);
    				rightShoot.set(0);
    				topShoot.set(0);
    				bottomShoot.set(0);
    			}
    		
    			// Spins up shooter motors at value defined in ini
    			case "shooter-spinup":
    			{
    				leftShoot.set(1 * shooterMotorSpeed2);
    				rightShoot.set(-1 * shooterMotorSpeed2);
    				topShoot.set(-1 * shooterMotorSpeed2);
    				bottomShoot.set(-1 * shooterMotorSpeed2);
    			}
    		
    			// Spins pickup motor, hence firing the shooter
    			case "shooter-fire":
    			{
    				rightPickup.set(1 * pickupSpeed);
    			}
    		
    			// Complain
    			default:
    			{
    				System.out.println("Command not recognized at line: " + autoCount1 + " in file: " + autoSelected + ".ini");
    			}
    	
    		}
    		if (iter > ((double) cCommand.get(1)))
    		{
    			autoCount1 += 1;
    			iter = 0;
    		}
    		
    		// Iterate and sleep for a millisecond
    		iter += 1;    		
    		try {Thread.sleep(1);}
    		catch (InterruptedException e) {e.printStackTrace();}
    	}
    	// For when the file runs out of commands
    	catch (IndexOutOfBoundsException e)
    	{
    		System.out.println("Ran out of commands :/");
    		e.printStackTrace();
    		try {Thread.sleep((long) ((15000 - iter) - 5));}
    		catch (InterruptedException e1) {e1.printStackTrace();}
    	}
    }
    
    public void teleopPeriodic() {
    	/*
    	 * Reference Driver Input sheet
    	 * 
    	 * Driver 1:
    	 * Joystick 1 (extreme3d):
    	 *   x-axis: drive right side
    	 * Joystick 2 (attack3):
    	 *   x-axis: drive left side
    	 *   button 3: pickup
    	 *   
    	 * Driver 2:
    	 * Joystick 1 (xbox):
    	 *   axis 3: shooter up/down
    	 *   button 1-4: shooter speeds
    	 *   button 5/6: camera switch
    	 * 
    	*/
    	
    	CameraServer.getInstance().setImage(camera.run());
    	
    	// Gearing "application" logic
    	xSpeed1 = -1 * extreme3d.getRawAxis(1) * gearRatio;
    	xSpeed2 = -1 * attack3.getRawAxis(1) * gearRatio;
    	
    	// Actually SPIN the drive motors
    	rDrive.tankDrive(xSpeed2, xSpeed1);
    	
    	// Ball pickup logic
    	if(attack3.getRawButton(PICK_UP) || xbox.getRawButton(XBOX_UP)){
    		//pickup
    		rightPickup.set(1 * pickupSpeed);
    	}else if (attack3.getRawButton(DROP_BALL) || xbox.getRawButton(XBOX_DOWN)){
    		//set down
    		rightPickup.set(-1 * pickupSpeed);
    	}else{
    		//set all 0
    		rightPickup.set(0);
    	}
    	
    	//Shoot with different speeds
    	if(xbox.getRawButton(X_BUTTON)){
    		//shooter speed 1
    		leftShoot.set(1 * shooterMotorSpeed1);
			rightShoot.set(-1 * shooterMotorSpeed1);
			topShoot.set(-1 * shooterMotorSpeed1);
			bottomShoot.set(-1 * shooterMotorSpeed1);
    	}else if(xbox.getRawButton(Y_BUTTON)){
    		//shooter speed 2
    		leftShoot.set(1 * shooterMotorSpeed2);
			rightShoot.set(-1 * shooterMotorSpeed2);
			topShoot.set(-1 * shooterMotorSpeed2);
			bottomShoot.set(-1 * shooterMotorSpeed2);
    	}else if(xbox.getRawButton(A_BUTTON)){
    		//shooter speed 3
    		leftShoot.set(1 * shooterMotorSpeed3);
			rightShoot.set(-1 * shooterMotorSpeed3);
			topShoot.set(-1 * shooterMotorSpeed3);
			bottomShoot.set(-1 * shooterMotorSpeed3);
    	}else if(xbox.getRawButton(B_BUTTON)){
    		//shooter speed 4
    		leftShoot.set(1 * shooterMotorSpeed4);
			rightShoot.set(-1 * shooterMotorSpeed4);
			topShoot.set(-1 * shooterMotorSpeed4);
			bottomShoot.set(-1 * shooterMotorSpeed4);
    	}else{
    		//set all 0
    		leftShoot.set(0);
    		rightShoot.set(0);
    		topShoot.set(0);
    		bottomShoot.set(0); 
    	}
    	
    	//Camera control
    	if(xbox.getRawButton(CAM_1) || attack3.getRawButton(4)){
    		//camera 1
    		camera.setCamera(0);
    	}else if(xbox.getRawButton(CAM_2)){
    		//camera 2
    		camera.setCamera(1);
    	}
    	
    	//Shooter up and down
    	if(xbox.getRawAxis(TRIGGER_AXIS_1) == 1){
    		//shooter up
    		//shootAdjust.set(.2);
    		//rightPickup.set(.6);
    	}else if(xbox.getRawAxis(TRIGGER_AXIS_2) == 1){
    		//shooter down
    		//shootAdjust.set(-.2);
    		//rightPickup.set(-.6);
    	}else{
    		//stop movement
    		//shootAdjust.set(0);
    		//rightPickup.set(0);
    	}
    	
    	// Gearing "back-end" from here on
    	//Gearing control
    	if(extreme3d.getTrigger() && !rightNotHeld)
    	{
    		//gear up
    		if (cGear == 1 && !rightNotHeld)
    		{
    			cGear = 2;
    			rightNotHeld = true;
    		}
    		else if (cGear == 2 && !rightNotHeld)
    		{
    			cGear = 3;
    			rightNotHeld = true;
    		}
    	}
    	else if(attack3.getTrigger() && !leftNotHeld)
    	{
    		//gear down
    		if (cGear == 2 && !leftNotHeld)
    		{
    			cGear = 1;
    			leftNotHeld = true;
    		}
    		else if (cGear == 3 && !leftNotHeld)
    		{
    			cGear = 2;
    			leftNotHeld = true;
    		}
    	}
    	
    	// Logic below checks for a user holding the button
    	if(!attack3.getTrigger() && leftNotHeld){
    		leftNotHeld = false;
    	}else if(!extreme3d.getTrigger() && rightNotHeld){
    		rightNotHeld = false;
    	}
    	
    	// Changes gear based on selection above
    	if (cGear == 1) {gearRatio = gear1;}
    	else if (cGear == 2) {gearRatio = gear2;}
    	else if (cGear == 3) {gearRatio = gear3;}
    	else {gearRatio = 0.0;}
    	
    	// Prints Gearing data to Smart Dashboard
    	SmartDashboard.putNumber("Current Gear: ", cGear);
    	SmartDashboard.putNumber("Current Gear Ratio: ", gearRatio);
    }
    
    // Rarely used by 1699
    public void testPeriodic() 
    {
      	System.out.println("|------------------------------------------------------|");
        System.out.println("| Team 1699 Robot: test mode enabled                   |");
        System.out.println("|------------------------------------------------------|");
    }
    
}
