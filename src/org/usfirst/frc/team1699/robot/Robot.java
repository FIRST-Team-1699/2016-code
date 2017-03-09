/*
 * FIRST Team 1699's 2016 Robot Code
 * 
 * @author thatging3rkid, FIRST Team 1699
 * @author squirlemaster42, FIRST Team 1699 
 * 
 * v0.1.3-clean, published on 3/9/17
 * 
 * Winner of the 2016 Innovation in Controls Award at the NE Hartford District Event
 */
package org.usfirst.frc.team1699.robot;

import edu.wpi.first.wpilibj.CANTalon; // Broken in WPILib 2017+
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {
    
	/*
	 * A note about this code:
	 * 
	 * 		Welcome to the mess that is the 2016 code. I have no clue how we won Innovation in Controls, but this 
	 * code did it. As to why this code was a mess, it's difficult to exactly pinpoint. First off, it was our
	 * first season using Java. I knew Java well at the time, but not WPILib for Java. Also, we had a smaller team
	 * than in the past, so the smaller-than-normal software team was asked to do other things. While in the past, we
	 * would have declined, citing our big project that we wanted to get working for this season (usually autonomous 
	 * improvements), but we accepted this work. We prototyped the shooter, then gave it to someone else, telling
	 * them a 30 second summary of what we learned and told them to build it in metal. Safe to say, that did not go well.
	 * And then, we had a weekend where we were snowed out. We didn't start learning WPILib until week 3 or 4. We rushed
	 * learning it, then wrote up basic code, fixed it, then wired the robot. At our first event, we had basic robot code
	 * (driving, shooting) working and a crosshair on our camera feed. After that event, we switched to the Axis camera
	 * and made GRIP work, rewrote autonomous again. But, in Hartford, all our hard work was rewarded, with the Innovation
	 * in Controls award. Not only did we win the award, but we had enough points to go onto District Championship, 
	 * which was a first for the team. 
	 * 
	 * tl;dr started coding late, coding was slow, GRIP was difficult, but somehow, we won Innovation in Controls.
	 * This code is a mess, good luck reading it. I have go and commented it, but this is spaghetti code. 
	 * 
	 * Thanks for reading, and good luck
	 * 		--Connor Henley, aka @thatging3rkid, Controls Lead in 2016
	 */	
	
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
    
    //Camera Server
    CameraServer cam;
    
    //Joysticks
    Joystick extreme3d;
    Joystick attack3;
    Joystick xbox;
    
    //Motor Control
    //Drive Motors
    CANTalon rightDrive1; // Broken in WPILib 2017+
    CANTalon rightDrive2; // Broken in WPILib 2017+
    CANTalon leftDrive1;  // Broken in WPILib 2017+
    CANTalon leftDrive2;  // Broken in WPILib 2017+
    
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
    int iterF;
    
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
    int slowLineUp;
    
   //Camera
    CameraServer server;
    
    //Vision
    NetworkTable table;
	double[] centerX;
	int gripTolerance;
    
    public void robotInit() { 
    	//Dashboard
    	table = NetworkTable.getTable("GRIP/myContoursReport");
    	this.updateDashboard();
      	
    	// iniReader
    	teleopIni = new iniReader("1699-config.ini");
    	gear1 = teleopIni.getValue("gear1");
        gear2 = teleopIni.getValue("gear2");
        gear3 = teleopIni.getValue("gear3");
        pickupSpeed = teleopIni.getValue("pickupSpeed");
        shooterMotorSpeed1 = teleopIni.getValue("shooterMotorSpeed1");
        shooterMotorSpeed2 = teleopIni.getValue("shooterMotorSpeed2");
        shooterMotorSpeed3 = teleopIni.getValue("shooterMotorSpeed3");
        shooterMotorSpeed4 = teleopIni.getValue("shooterMotorSpeed4");
    	imageCenter = teleopIni.getValue("imageCenter");
    	gripTolerance = (int) teleopIni.getValue("gripTolerance");
    	slowLineUp = (int) teleopIni.getValue("slowLineUp");
    	
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
        rightDrive1 = new CANTalon(10); // Broken in WPILib 2017+
        rightDrive2 = new CANTalon(11); // Broken in WPILib 2017+
        leftDrive1 = new CANTalon(12);  // Broken in WPILib 2017+
        leftDrive2 = new CANTalon(13);  // Broken in WPILib 2017+
        
        //Shooter Motors
        leftShoot = new VictorSP(1);
        topShoot = new VictorSP(2);
        bottomShoot = new VictorSP(3);
        rightShoot = new VictorSP(9);
        
        //Ball pickup
        rightPickup = new VictorSP(6);
        
        //Drive
        rDrive = new RobotDrive(leftDrive1, leftDrive2, rightDrive1, rightDrive2); // Broken in WPILib 2017+
        
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
        iterF = 0;
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
    
    
    /**
     * Called periodically (every 20ms) during autnomous, when enabled
     * 
     * @inheritDoc
     */
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
    
    
    /**
     * Called periodically (every 20ms) during teleop, when enabled
     * 
     * @inheritDoc
     */
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
    	
    	/*
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
    	*/
    	
    	// Gearing "back-end"
    	// So after deciding to use the big wheels, we figured out that the robot would go very, very fast.
    	// So, the robot has "gears": 1st, 2nd, and 3rd, that limits the speed of the robot
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
    	
    	// Checks for a user holding the button
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
    
    
    /**
     * Line up the robot to shoot a ball
     * 
     * It works by using a super watered down PID loop. It first calculates if the robot needs to turn left or right, 
     * then by how much. If it needs to turn by a lot, then it sets a higher speed than if it only needs to turn a little.
     * This is what causes the robot to "hump the ground" as Mr. Graham says, as it would jump around, trying to center.
     */
    public void lineUp(){
    	try{
    		// Update Dashboad values
    		this.updateDashboard();
    		
    		// Get values from GRIP over NetworkTables
    		double[] defaultValue = new double[0];
    		centerX = table.getNumberArray("centerX", defaultValue);
    		
    		// The goal has been met, do nothing
    		if(((centerX[0] + gripTolerance) > imageCenter) && ((centerX[0] - gripTolerance) < imageCenter))
    		{
    			// done by updateDashboard?
    			//SmartDashboard.putString("Shot Ready", "true"); // unknown why this was removed
    		}
    		else{
    			// Need to move the robot
    			iterJ += 1;
    			
    			//SmartDashboard.putString("Shot Ready", "false");
    			if (centerX[0] > imageCenter) { // See if the robot needs to turn left
    				// Using the number of iterations, alternate between turning left by powering the left side forward
    				// and by powering the right side backwards, while powering the opposite side a little.
    				//
    				// Turn left-forward
    				if ((iterJ % 2) == 0)
    				{
    					double distance = Math.abs(centerX[0] - imageCenter);
    					if (distance > slowLineUp) 
    					{
    						rDrive.tankDrive(0.3, -0.8);
    						Thread.sleep(500);
    					}
    					else if (distance <= slowLineUp) 
    					{
    						rDrive.tankDrive(0.2, -0.5);
    						Thread.sleep(300);
    					}
    					
    				}
    				// Turn left-back
    				if ((iterJ % 2) == 1) 
    				{
    					double distance = Math.abs(centerX[0] - imageCenter);
    					if (distance > slowLineUp) 
    					{
    						rDrive.tankDrive(0.8, -0.3);
    						Thread.sleep(500);
    					}
    					else if (distance <= slowLineUp) 
    					{
    						rDrive.tankDrive(0.5, -0.2);
    						Thread.sleep(300);
    					}
    			} else if (centerX[0] < imageCenter) { // See if the robot needs to turn right
    				// See the comment above for how it works
    				
    				//Turn right-back
    				if ((iterJ % 2) == 0) {
    					double distance = Math.abs(centerX[0] - imageCenter);
    					if (distance > slowLineUp) 
    					{
    						rDrive.tankDrive(-0.8, 0.3);
    						Thread.sleep(500);
    					}
    					else if (distance <= slowLineUp) 
    					{
    						rDrive.tankDrive(-0.5, 0.2);
    						Thread.sleep(300);
    					}
    				}
    				// Turn left-back
    				if ((iterJ % 2) == 1) {
    					double distance = Math.abs(centerX[0] - imageCenter);
    					if (distance > slowLineUp) 
    					{
    						rDrive.tankDrive(-0.3, 0.8);
    						Thread.sleep(500);
    					}
    					else if (distance <= slowLineUp) 
    					{
    						rDrive.tankDrive(-0.2, 0.5);
    						Thread.sleep(300);
    					}rDrive.tankDrive(-0.3, 0.8);} // Turn left-forward
    				}
    			} else {
    				//Take shot
    			}
    		}
    		
    	} catch (ArrayIndexOutOfBoundsException ex){
    		System.out.println("Goal not found.");
    	} catch (Exception e){
    		e.printStackTrace();
    	}
	}
    
    /**
     * Experimental version of the lineUp() method, never tested
     */
    public void newLineUp()
    {
    	// This method relies on SmartDashboard values, so update them first
    	this.updateDashboard();
    	
    	try {
    		if (SmartDashboard.getString("Shot Ready").equals("false")) {
    			// get GRIP values from NetworkTables
    			double[] defaultValue = new double[0];
        		centerX = table.getNumberArray("centerX", defaultValue);
        		
        		// Calculate the distance between the center (goal) and the current position
        		iterF += 1;
        		double fastVal = .30411 * Math.log(Math.abs(imageCenter - centerX[0])) + .1895;
        		double slowVal = .2411 * Math.log(Math.abs(imageCenter - centerX[0])) + .080857;
        		
        		// Turn the robot at the calculated speed (alternating going forwards and backwards)
        		// Alternating was required due to the large wheels on the drivetrain
        		if (centerX[0] > imageCenter){
    				//Turn left
    				if ((iterJ % 2) == 0) {
    					// Turn left-back
    					rDrive.tankDrive(slowVal, -1 * fastVal);
    				} 
    				if ((iterJ % 2) == 1) {
    					// Turn left-forward
    					rDrive.tankDrive(fastVal, -1 * slowVal);
    				} 
    			} else if (centerX[0] < imageCenter){
    				//Turn right
    				if ((iterJ % 2) == 0) {
    					// Turn right-back
    					rDrive.tankDrive(-1 * fastVal, slowVal);
    				} 
    				if ((iterJ % 2) == 1) {
    					// Turn left-forward
    					rDrive.tankDrive(-1 * slowVal, fastVal);
    				} 
        		}
        	Thread.sleep(450);
    		}
    
    	}
    	catch (Exception e) {
    		// Prevent the code from crashing
    		e.printStackTrace();
    	}
    }
    
    
    /**
     * Shoots a ball
     * 
     * @param setting the speed at which the ball is shot at (1, 2, 3, 4)
     */
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
    
    /**
     *  Updates values on SmartDashboard. Should be called in ALL periodic methods.
     */
    public void updateDashboard()
    {
    	// Prints Gearing data to Smart Dashboard
    	SmartDashboard.putNumber("Current Gear: ", cGear);
    	SmartDashboard.putNumber("Current Gear Ratio: ", gearRatio);
    	
    	double[] gripVals = new double[0];
		centerX = table.getNumberArray("centerX", gripVals);
		SmartDashboard.putString("centerX", centerX.toString());
		try
		{
			if((centerX[0] + gripTolerance > imageCenter) && (centerX[0] - gripTolerance < imageCenter)) 
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
