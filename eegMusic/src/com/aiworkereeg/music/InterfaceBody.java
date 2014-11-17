package com.aiworkereeg.music;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;

class InterfaceBody {
    private Drawable bodyImage;
    private int bodyImageHeight;
    private int bodyImageWidth;

    protected float bodyCenterX;
    protected float bodyCenterY;

    protected float bodyAlpha;
    protected float bodyScale;

    protected boolean debug;

    public InterfaceBody(Drawable image) {
        this(image, 1f);
    }

    public InterfaceBody(Drawable image, float scale) {
        bodyImage = image;
        bodyScale = scale;
        debug = false;

        bodyImageHeight = image.getIntrinsicHeight();
        bodyImageWidth = image.getIntrinsicWidth();
        bodyCenterX = -bodyImageHeight;
        bodyCenterY = -bodyImageWidth;
    }

    public void setDebugFlag(boolean flag) {
        debug = flag;
    }

    public int getImageWidth() {
        return bodyImageWidth;
    }

    public int getImageHeight() {
        return bodyImageHeight;
    }

    public void setCenterX(float centerX) {
        bodyCenterX = centerX;
    }

    public float getCenterX() {
        return bodyCenterX;
    }

    public void setCenterY(float centerY) {
        bodyCenterY = centerY;
    }

    public float getCenterY() {
        return bodyCenterY;
    }

    public void setCenterCoordinates(float centerX, float centerY) {
        setCenterX(centerX);
        setCenterY(centerY);
    }

    public void setCenterCoordinates(int[] coordinates) {
        setCenterCoordinates(coordinates[0], coordinates[1]);
    }

    public int[] getCenterCoordinates() {
        int[] coords_list = {bodyImageWidth, bodyImageHeight};
        return coords_list;
    }

    public void setScale(float scale) {
        bodyScale = scale;
    }

    public float getScale() {
        return bodyScale;
    }

    public void setAlpha(float alpha) {
        bodyAlpha = alpha;
    }

    public double getAlpha() {
        return bodyAlpha;
    }

    public void drawTo(Canvas canvas) {
        int xLeftTop = (int) bodyCenterX - bodyImageWidth / 2;
        int yLeftTop = (int) bodyCenterY - bodyImageHeight / 2;
        /*if (boundToCanvas) {
            xLeftTop -= (int) 
            yLeftTop -= (int) 
            }*/
        canvas.save();

        canvas.scale(bodyScale, bodyScale, bodyCenterX, bodyCenterY);
        //canvas.rotate(bodyAlpha, xLeftTop, yLeftTop);
        canvas.rotate(bodyAlpha, bodyCenterX, bodyCenterY);
        bodyImage.setBounds(xLeftTop, yLeftTop, xLeftTop + bodyImageWidth, yLeftTop + bodyImageHeight);
        bodyImage.draw(canvas);

        canvas.restore();
        
        
       /* int xMarsLeft = (int) MarsX - MarsWidth / 2;
        int yMarsLeft = (int) MarsY - MarsHeight / 2;
        canvas.save();
        canvas.rotate((float) MarsAlpha, (float) MarsXcenter, (float)MarsYcenter);
        canvas.scale(MarsScale, MarsScale, (float)MarsXcenter , (float)MarsYcenter); // scale
        MarsImage.setBounds(xMarsLeft, yMarsLeft, xMarsLeft + MarsWidth, yMarsLeft + MarsHeight);
        MarsImage.draw(canvas); */
        
        
        if (debug) {
            Log.w(this.getClass().getName(),
                  String.format("draw to canvas: scale=%f, Cx=%f, Cy=%f, LTx=%d, LTy=%d",
                                bodyScale, bodyCenterX, bodyCenterY, xLeftTop, yLeftTop));
        }
    }

    public void updatePhysics(float rot_alpha, double pos_alpha, float CircleRadius,
    		double pX, double pY) {
    	//bodyAlpha += 0.6;
    	//bodyAlpha += rot_alpha;
    	bodyAlpha = rot_alpha;
    	
    	float centerX; float centerY = 0;
    	// -- calculate new center coordinates based on radius and angle
    	centerX  = (float) (pX + CircleRadius * Math.sin(Math.toRadians(pos_alpha)) );
    	centerY  = (float) (pY + CircleRadius * Math.cos(Math.toRadians(pos_alpha)) );
    	    	
    	
        setCenterX(centerX);        setCenterY(centerY);

    }
    
    public void updateDNA(float CursorX1, double pY) {
    	float centerX; float centerY = 0;
    	// -- calculate new center coordinates based on radius and angle
    	centerX  = (float) (CursorX1 );
    	centerY  = (float) (pY);  	    	
    	
        setCenterX(centerX);        setCenterY(centerY);
    }
    
    public void setImage(Drawable image) {
    	bodyImage = image;
    }
}
