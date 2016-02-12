package org.usfirst.frc.team1699.robot;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class Vision {

	private BufferedImage image;
	private Boolean[][] imgArr;
	
	public Vision(BufferedImage image){
		this.image = image;
	}
	
	public Boolean[][] lookColor(int red, int green, int blue){
		imgArr = new Boolean[image.getHeight()][image.getWidth()];
		
		 for (int y = 0; y < image.getHeight(); y++) {
             for (int x = 0; x < image.getWidth(); x++) {
            	             	 
            	 int c = image.getRGB(x, y);
            	 Color color = new Color(c);
            	 
            	 if (color.getRed() == red && color.getGreen() == green && color.getBlue() == blue){
            		 imgArr[y][x] = true;
            	 }else{
            		 imgArr[y][x] = false;
            	 }
             }
		 }
		 
		 return imgArr;
	}
	
	public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public Boolean[][] getImgArr() {
		return imgArr;
	}
}
