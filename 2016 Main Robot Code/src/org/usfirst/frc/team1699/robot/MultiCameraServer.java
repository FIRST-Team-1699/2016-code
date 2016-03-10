/*
 * FIRST Team 1699's MultiCameraServer
 *  
 * Credit for this class goes to GitHub user Wazzaps, FIRST Team 1573
 * https://gist.github.com/Wazzaps/bb9e72696f8980e7e727
 * 
 * @author David Sh. from 1573
 * @author thatging3rkid, FIRST Team 1699
 * @author squirlemaster42, FIRST Team 1699 
 * 
 * This class allows us to switch between two cameras
 */
package org.usfirst.frc.team1699.robot;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.DrawMode;
import com.ni.vision.NIVision.Image;
import com.ni.vision.NIVision.ShapeMode;

public class MultiCameraServer {
	
	private int _session;
	private Image frame;
	private int _cameraCount;
	private int selectedCamera = 0;
	private boolean firstTime = true;
	
	// Reads settings from an ini
	private iniReader cServerSettings = new iniReader("1699-cameraServer.ini");
	private int chairGap = (int) cServerSettings.getValue("chairGap");
	private int chairSize = (int) cServerSettings.getValue("chairSize");
	private int chairLength = (int) cServerSettings.getValue("chairLength");
	private float chairColor = (float) cServerSettings.getValue("chairColor");
	int xCenter = (int) cServerSettings.getValue("xCenter");
	int yCenter = (int) cServerSettings.getValue("yCenter");
	
	// Makes the boxes for a crosshair
	// NIVision.Rect(int top, int left, int height, int width) 
	NIVision.Rect topRect = new NIVision.Rect((xCenter - chairGap - chairLength), (int) (yCenter - (chairSize * .5)), chairLength, chairGap);
	NIVision.Rect rightRect = new NIVision.Rect((int) (xCenter + (chairSize * .5)), (yCenter - chairGap - chairLength), chairGap, chairLength);
	NIVision.Rect botRect = new NIVision.Rect((xCenter + chairGap), (int) (yCenter - (chairSize * .5)), chairLength, chairGap);
	NIVision.Rect leftRect = new NIVision.Rect((int) (xCenter + (chairSize * .5)), (yCenter + chairGap), chairGap, chairLength);
	
	public MultiCameraServer(int camCount){
		frame = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 0);
		_cameraCount = camCount;
	}
	
	public Image run(){
		NIVision.IMAQdxGrab(_session, frame, 1);
		// Writes crosshair boxes
		NIVision.imaqDrawShapeOnImage(frame, frame, topRect, DrawMode.DRAW_VALUE, ShapeMode.SHAPE_RECT, chairColor);
		NIVision.imaqDrawShapeOnImage(frame, frame, rightRect, DrawMode.DRAW_VALUE, ShapeMode.SHAPE_RECT, chairColor);
		NIVision.imaqDrawShapeOnImage(frame, frame, botRect, DrawMode.DRAW_VALUE, ShapeMode.SHAPE_RECT, chairColor);
		NIVision.imaqDrawShapeOnImage(frame, frame, leftRect, DrawMode.DRAW_VALUE, ShapeMode.SHAPE_RECT, chairColor);
        return frame;
	}
	
	public void setCamera(int cam){
		if (cam >= _cameraCount || cam < 0){
			System.out.println("Error: tried to set camera out of bounds.");
			return;
		}
		
		if(!firstTime){
			NIVision.IMAQdxStopAcquisition(_session);
			NIVision.IMAQdxCloseCamera(_session);
		}else{
			firstTime = false;
		}
		
		selectedCamera = cam;
		_session = NIVision.IMAQdxOpenCamera("cam" + selectedCamera, NIVision.IMAQdxCameraControlMode.CameraControlModeController);
		NIVision.IMAQdxConfigureGrab(_session);
		NIVision.IMAQdxStartAcquisition(_session);
	}
	
	public int getCamera() {
		return selectedCamera;
	}
	
	public void toggleCamera() {
		setCamera((selectedCamera + 1) % _cameraCount);
	}
}
