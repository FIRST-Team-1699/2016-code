package org.usfirst.frc.team1699.robot;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.DrawMode;
import com.ni.vision.NIVision.Image;
import com.ni.vision.NIVision.ShapeMode;

import edu.wpi.first.wpilibj.CameraServer;

/*
 * Credit for this class goes to GitHub user Wazzaps
 * https://gist.github.com/Wazzaps/bb9e72696f8980e7e727
 * 
 * This class allowes us to switch between two cameras
 */

public class MultiCameraServer {
	
	private int _session;
	private Image frame;
	private int _cameraCount;
	private int selectedCamera = 0;
	private boolean firstTime = true;
	
	public MultiCameraServer(int camCount){
		frame = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 0);
		_cameraCount = camCount;
	}
	
	public Image run(){
		NIVision.IMAQdxGrab(_session, frame, 1);
		//NIVision.Rect rect = new NIVision.Rect(10, 10, 100, 100);
        //NIVision.imaqDrawShapeOnImage(frame, frame, rect, DrawMode.DRAW_VALUE, ShapeMode.SHAPE_RECT, 0.0f);
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
