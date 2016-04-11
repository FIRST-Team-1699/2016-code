/*
 * FIRST Team 1699's 2016 Robot Code
 * 
 * @author thatging3rkid, FIRST Team 1699
 * @author squirlemaster42, FIRST Team 1699 
 * 
 * v0.1.3, published on 4/10/16, used at NE District Championship
 * 
 * Winner of the 2016 Innovation in Controls Award at the NE Hartford District Event
 * 
 */
package org.usfirst.frc.team1699.robot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.apache.commons.io.output.TeeOutputStream;

public class Robot extends IterativeRobot {
    // Autonomous Chooser decelerations    
    final String rockWall = "rock wall";
    final String roughTerrain = "rough terrain";
    final String ramparts = "ramparts";
    final String moat = "moat";
    final String spyBox = "spy box";
    String defenseSelected;
    SendableChooser defenseChooser;
    
    final String turnLeft = "left";
    final String turnRight = "right";
    final String straight = "straight";
    final String noTurn = "no move";
    String autoTurnSelected;
    SendableChooser autoTurnChooser;
    
    final String shootBall = "shot";
    final String noShootBall = "no shot";
    String shotSelected;
    SendableChooser shotChooser;
    
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
    
    // ini retrieved
    double gear1;
    double gear2;
    double gear3;
    double autoSpeed;
    double pickupSpeed;
    double shooterMotorSpeed1;
    double shooterMotorSpeed2;
    double shooterMotorSpeed3;
    double shooterMotorSpeed4;
    double imageCenter;
    
    // Autonomous variables
    double iter;
    int autoCount1;
    double speed = 0;
    double i = 0;
    boolean defenseDone;
    boolean autoTurnDone;
    int autoTurnIter;
    
    // Line up shot method
    int iterJ;
    
    // Shoot method 
    int iterA;
    
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
    
    // Line up shot
    final int LINE_UP = 2;    
    
   //Camera
    CameraServer server;
    
    //Vision
    NetworkTable table;
	double[] centerX;
    
    public void robotInit() { 
    	//Dashboard
    	table = NetworkTable.getTable("GRIP/myContoursReport");
    	this.updateDashboard();
   
    	
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
    	imageCenter = teleopIni.getValue("imageCenter");
    	
        
        // Adds options to the Autonomous chooser
        defenseChooser = new SendableChooser();
        defenseChooser.addObject("Rock Wall", rockWall);
        defenseChooser.addObject("Rough Terrain", roughTerrain);
        defenseChooser.addObject("Ramparts", ramparts);
        defenseChooser.addObject("Moat", moat);
        defenseChooser.addDefault("Spy Box", spyBox);
        
        autoTurnChooser = new SendableChooser();
        autoTurnChooser.addObject("Turn Left", turnLeft);
        autoTurnChooser.addObject("Straight", straight);
        autoTurnChooser.addObject("Turn Right", turnRight);
        autoTurnChooser.addDefault("No Turn", noTurn);
        
        shotChooser = new SendableChooser();
        shotChooser.addObject("Shoot Ball", shootBall);
        shotChooser.addDefault("No Shot", noShootBall);
        
        
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
        rightShoot = new VictorSP(9);
        
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
        server = CameraServer.getInstance();
        server.setQuality(50);
        //the camera name (ex "cam0") can be found through the roborio web interface
        server.startAutomaticCapture("cam0");
        
        
        // Line up method
        iterJ = 0;
    }
    	
    
    // Tells us that the robot is disabled
    public void disabledInit()
    {
    	// easy robot modes first, am I right?
    	System.out.println("|------------------------------------------------------|");
    	System.out.println("| Team 1699 Robot: awating drive mode                  |");
    	System.out.println("|------------------------------------------------------|");
    }    
    
    // Called periodically when disabled
    public void disabledPeriodic()
    {
    	// Update Dashboad values
    	this.updateDashboard();
    }
    
    
    // Runs before autonomous
    public void autonomousInit() 
    {
    	//autoSelected = (String) chooser.getSelected();
		//System.out.println("Auto selected: " + autoSelected);
		
		iter = 0;
    	//autoCommands = new iniReader(autoSelected + ".ini");
    	//commands = autoCommands.getFile();
    	//autoCount1 = 0;
		i = 0;
		speed = 0;
		
		// Gets selected autonomous
		defenseSelected = (String) defenseChooser.getSelected();
		autoTurnSelected = (String) autoTurnChooser.getSelected();
		shotSelected = (String) shotChooser.getSelected();
		
		// Prints out selected auto for debugging
		System.out.println("Auto selected: " + defenseSelected + ", " + autoTurnSelected + ", " + shotSelected);
		
		defenseDone = false;
		autoTurnDone = false;
		autoTurnIter = 0;
    }
    
    
    // Called periodically during autonomous
    public void autonomousPeriodic() 
    {
    	// Cases for each defense
    	// Rock Wall case
    	if ((defenseSelected.equals(rockWall)) && (!defenseDone))
    	{
    		if (speed < 0.9) {speed += .04;}
    		
    		if (i < 100) {rDrive.arcadeDrive(speed, 0);}
    		else
    		{
    			rDrive.arcadeDrive(0, 0);
    			defenseDone = true;
    		}
    	}
    	// Rough Terrain case
    	else if ((defenseSelected.equals(roughTerrain)) && (!defenseDone))
    	{
    		if (speed < .8) {speed += .05;}
    		
    		if (i < 110) {rDrive.arcadeDrive(speed, 0);}
    		else
    		{
    			rDrive.arcadeDrive(0, 0);
    			defenseDone = true;
    		}
    	}
    	// Ramparts case
    	else if ((defenseSelected.equals(ramparts)) && (!defenseDone))
    	{
    		if (speed < .95) {speed += .05;}
    		
    		if (i < 100) {rDrive.arcadeDrive(speed, 0);}
    		else
    		{
    			rDrive.arcadeDrive(0, 0);
    			defenseDone = true;
    		}
    	}
    	// Moat case
    	else if ((defenseSelected.equals(moat)) && (!defenseDone))
    	{
    		if (speed < .6) {speed += .04;}
    		
    		if (i < 160) {rDrive.arcadeDrive(speed, 0);}
    		else
    		{
    			rDrive.arcadeDrive(0, 0);
    			defenseDone = true;
    		}
    	}
    	// Spy Box case
    	else if ((defenseSelected.equals(spyBox)) && (!defenseDone))
    	{
    		speed = 0;
    		rDrive.arcadeDrive(0, 0);
    	}
    	// Iterate the defense loop
    	i++;
    	
    	
    	// Cases for turning after crossing a defense
    	// Turn left case
    	if ((autoTurnSelected.equals(turnLeft)) && (defenseDone) && (!autoTurnDone))
    	{
    		if (autoTurnIter < 10) {rDrive.tankDrive(-.3, .6);}
    		else {autoTurnDone = true;}
    	}
    	// Go straight case
    	else if ((autoTurnSelected.equals(straight)) && (defenseDone) && (!autoTurnDone))
    	{
    		if (autoTurnIter < 10) {rDrive.tankDrive(.6, .6);}
    		else {autoTurnDone = true;}
    	}
    	// Turn right case
    	else if ((autoTurnSelected.equals(turnRight)) && (defenseDone) && (!autoTurnDone))
    	{
    		if (autoTurnIter < 10) {rDrive.tankDrive(.6, -.3);}
    		else {autoTurnDone = true;}
    	}
    	// Do nothing case
    	else if ((autoTurnSelected.equals(noTurn)) && (defenseDone) && (!autoTurnDone))
    	{
    		if (autoTurnIter < 10) {rDrive.tankDrive(0, 0);}
    		else {autoTurnDone = true;}
    	}
    	// Iterate the auto Turn counter
    	autoTurnIter += 1;
		
		// Cases for shooting the ball
    	// Shoot
    	if ((shotSelected.equals(shootBall)) && (defenseDone) && (autoTurnDone))
    	{
    		if (SmartDashboard.getString("Shot Ready").equals("true"))
    		{
    			this.shootBall(3);
    		}
    		else 
    		{
    			this.lineUp();
    		}
    	}

    	// sleep between loops
		try {Thread.sleep(1);}
		catch (InterruptedException e) {e.printStackTrace();}
		
		// Update Dashboad values
		this.updateDashboard();
    }
    
    
    // Called periodically during Teleop
    public void teleopPeriodic() {
    	/*
    	 * Reference Driver Input sheet
    	 * 
    	 * Driver 1:
    	 * Joystick 1 (extreme3d):
    	 *   x-axis: drive right side
    	 *   button 2: line up
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
    	
    	// Gearing "application" logic
    	xSpeed2 = -1 * extreme3d.getRawAxis(1) * gearRatio;
    	xSpeed1 = -1 * attack3.getRawAxis(1) * gearRatio;
    	
    	// Change drive system
    	if (extreme3d.getRawButton(LINE_UP))
    	{
    		this.lineUp();
    	}
    	else if (xSpeed1 == 3.1415) // never true
    	{
    		// space for another condition
    	}
    	else
    	{
    		rDrive.tankDrive(xSpeed1, xSpeed2);
    	}
    	
    	// Ball pickup logic
    	if((attack3.getRawButton(PICK_UP) || xbox.getRawButton(XBOX_UP))){
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
    		//camera.setCamera(0);
    	}else if(xbox.getRawButton(CAM_2)){
    		//camera 2
    		//camera.setCamera(1);
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
    	
    	// Update Dashboad values
    	this.updateDashboard();
    }
    
    
    // --------------- Team methods ---------------------
    // Everything below here is not called automatically
    
    
    // line up shooter
    public void lineUp(){
    	try{
    		// Update Dashboad values
    		this.updateDashboard();
    		
    		double[] defaultValue = new double[0];
    		centerX = table.getNumberArray("centerX", defaultValue);
    		if((centerX[0] + 5 > imageCenter) && (centerX[0] - 5 < imageCenter))
    		{
    			// done by updateDashboard?
    			//SmartDashboard.putString("Shot Ready", "true");
    		}
    		else{
    			iterJ += 1;
    			//SmartDashboard.putString("Shot Ready", "false");
    			if(centerX[0] > imageCenter){
    				//Turn left
    				if ((iterJ % 2) == 0) {rDrive.tankDrive(0.3, -0.8);} // Turn left-back
    				if ((iterJ % 2) == 1) {rDrive.tankDrive(0.8, -0.3);} // Turn left-forward
    			}else if(centerX[0] < imageCenter){
    				//Turn right
    				if ((iterJ % 2) == 0) {rDrive.tankDrive(-0.8, 0.3);} // Turn right-back
    				if ((iterJ % 2) == 1) {rDrive.tankDrive(-0.3, 0.8);} // Turn left-forward
    			}else{
    				//Take shot
    			}
    			Thread.sleep(500);
    		}
    		
    	}catch(ArrayIndexOutOfBoundsException ex){
    		System.out.println("Goal not found.");
    	}catch(Exception e){
    		e.printStackTrace();
    	}
	}
    
    
    // Shoots ball. Requires a setting.
    public void shootBall(int setting)
    {
   		try {
   			// sleep check
   			Thread.sleep(1);
   		
   			// Set motor speed
   			if (setting == 1)
   			{
   				leftShoot.set(1 * shooterMotorSpeed1);
   				rightShoot.set(-1 * shooterMotorSpeed1);
   				topShoot.set(-1 * shooterMotorSpeed1);
   				bottomShoot.set(-1 * shooterMotorSpeed1);
   			}
   			if (setting == 2)
   			{
   				leftShoot.set(1 * shooterMotorSpeed2);
   				rightShoot.set(-1 * shooterMotorSpeed2);
   				topShoot.set(-1 * shooterMotorSpeed2);
   				bottomShoot.set(-1 * shooterMotorSpeed2);
   			}
   			if (setting == 3)
   			{
   				leftShoot.set(1 * shooterMotorSpeed3);
   				rightShoot.set(-1 * shooterMotorSpeed3);
   				topShoot.set(-1 * shooterMotorSpeed3);
   				bottomShoot.set(-1 * shooterMotorSpeed3);
   			}
   			if (setting == 4)
   			{
   				leftShoot.set(1 * shooterMotorSpeed4);
   				rightShoot.set(-1 * shooterMotorSpeed4);
   				topShoot.set(-1 * shooterMotorSpeed4);
   				bottomShoot.set(-1 * shooterMotorSpeed4);
   			}

   			// Let motors spin up
   			Thread.sleep(1250);

   			// Send ball towards its final destiny
   			rightPickup.set(1 * pickupSpeed);

   			// Ensure ball has cleared the pickup mechanism
   			Thread.sleep(550);
   			
   			// Stop motors, prepare for next shot
   			leftShoot.set(0);
   			rightShoot.set(0);
   			topShoot.set(0);
   			bottomShoot.set(0);
   			rightPickup.set(0);
   		}
   		catch (InterruptedException e) {e.printStackTrace();}
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
    
    
    // Updates values on SmartDashboard. Should be called in ALL periodic methods.
    public void updateDashboard()
    {
    	// Prints Gearing data to Smart Dashboard
    	SmartDashboard.putNumber("Current Gear: ", cGear);
    	SmartDashboard.putNumber("Current Gear Ratio: ", gearRatio);
    	
    	double[] defaultValue = new double[0];
		centerX = table.getNumberArray("centerX", defaultValue);
		SmartDashboard.putString("centerX", centerX.toString());
		try
		{
			if((centerX[0] + 5 > imageCenter) && (centerX[0] - 5 < imageCenter)) 
				{SmartDashboard.putString("Shot Ready", "true");}
			else {SmartDashboard.putString("Shot Ready", "false");}
		}
		catch (Exception e){SmartDashboard.putString("Shot Ready", "error: no goal");}
    	
    }
    
    
    // Rarely used by 1699
    public void testPeriodic() 
    {
      	System.out.println("|------------------------------------------------------|");
        System.out.println("| Team 1699 Robot: test mode enabled                   |");
        System.out.println("|------------------------------------------------------|");
    }    
}
