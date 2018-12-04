package RealTime.UI.Utilities;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;


public class ImageDrawer {
	public static void drawScaledImage(Image image,Component canvas, Graphics g){
		int imgWidth=image.getWidth(null);
		int imgHeight=image.getHeight(null);
		double imgAspect=(double)imgHeight/imgWidth;

		int canvasWidth=canvas.getWidth();
		int canvasHeight=canvas.getHeight();

		double canvasAspect=(double)canvasHeight/canvasWidth;

		int x1=0;
		int y1=0;
		int x2=0;
		int y2=0;
		if(imgWidth<canvasWidth&& imgHeight<canvasHeight){
			x1=(canvasWidth-imgWidth)/2;
			y1=(canvasHeight-imgHeight)/2;
			x2=imgWidth+x1;
			y2=imgHeight+y1;
		}
		else{
			if(canvasAspect>imgAspect){
				y1=canvasHeight;
				canvasHeight=(int)(canvasWidth+imgAspect);
				y1=(y1-canvasHeight)/2;

			}
			else{
				x1=canvasWidth;
				canvasWidth=(int)(canvasHeight/imgAspect);
				x1=(x1-canvasWidth)/2;

			}
			x2=canvasWidth+x1;
			y2=canvasHeight+y1;
		}
		g.drawImage(image, x1, y1, x2, y2, 0, 0, imgWidth, imgHeight, null);
	}
}
