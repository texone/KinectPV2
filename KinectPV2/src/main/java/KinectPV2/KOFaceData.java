package KinectPV2;

/*
Copyright (C) 2014  Thomas Sanchez Lengeling.
KinectPV2, Kinect for Windows v2 library for processing

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/



/**
 * Face Data
 * P
 * @author thomas
 *
 */
public class KOFaceData{
	
	public static enum FaceFeature{
		HAPPY,
		ENGAGED,
		LEFT_EYE_CLOSED,
		RIGHT_EYE_CLOSED,
		LOOKING_AWAY,
		MOUTH_MOVED,
		MOUTH_OPEN,
		WEARING_GLASES
	}
	
	public static enum FacePointType{
		LEFT_EYE,
		RIGHT_EYE,
		NOSE,
		LEFT_MOUTH,
		RIGHT_MOUTH;
	}
	//-1
	public static enum FaceFeatureState{
		UNKNOWN,
		NO,
		YES,
		MAYBE
	}
	
	public static class FaceFeatures {
		
		public FaceFeature feature;
		public FaceFeatureState state;
		
		/**
		 * Create Feature
		 * @param feature
		 * @param state
		 */
		FaceFeatures(FaceFeature theFeature, FaceFeatureState theState){
			feature = theFeature;
			state = theState;
		}
		
	}
	
	KOVector [] facePointsColor;
	KOVector [] facePointsInfrared;
	
	FaceFeatures [] facefeatures;
	
	boolean faceTracked;
	
	KORectangle rect;
	
	float pitch;
	float yaw;
	float roll;
	
	public KOFaceData(){
		facePointsColor = new KOVector[5];
		for(int i = 0; i < facePointsColor.length; i++)
			facePointsColor[i] =  new KOVector();
		
		facePointsInfrared = new KOVector[5];
		for(int i = 0; i < facePointsInfrared.length; i++)
			facePointsInfrared[i] =  new KOVector();
		
		rect = new KORectangle(0, 0, 0, 0);
		faceTracked = false;
		
		facefeatures = new FaceFeatures[FaceFeature.values().length];
		
		for(FaceFeature myFeature:FaceFeature.values()){
			facefeatures[myFeature.ordinal()] = new FaceFeatures(myFeature, FaceFeatureState.UNKNOWN);
		}
	}

	
	public void createFaceData(float [] rawData, int iFace){
		int index = iFace * 36;
		if(rawData[index + 35] == 0.0)
			faceTracked = false;
		else
			faceTracked = true;

		for(int i = 0; i < 5; i++){
			facePointsColor[i].x = rawData[index + i*2 + 0];
			facePointsColor[i].y = rawData[index + i*2 + 1];
		}
		
		for(int i = 0; i < 5; i++){
			facePointsInfrared[i].x = rawData[index + i*2 + 10 + 0];
			facePointsInfrared[i].y = rawData[index + i*2 + 10 + 1];
		}
		int index2 = index +20;
		rect.setX(rawData[index2 + 0]);
		rect.setY(rawData[index2 + 1]);
		rect.setWidth(rawData[index2 + 2]);
		rect.setHeight(rawData[index2 + 3]);
		
		pitch = rawData[index2 + 4];
		yaw   = rawData[index2 + 5];
		roll  = rawData[index2 + 6];
		
		for(FaceFeature myFeature:FaceFeature.values()){
			int myState = (int)rawData[index2 + 7 + myFeature.ordinal()] + 1;
			facefeatures[myFeature.ordinal()].state = FaceFeatureState.values()[myState];
		}
	}
	
	/**
	 * Get Face Features
	 * @return FaceFeatures []
	 */
	public FaceFeatures [] getFaceFeatures(){
		return facefeatures;
	}
	
	/**
	 * get Bounding Face Rectangle
	 * @return Rectangle
	 */
	public KORectangle getBoundingRect(){
		return rect;
	}
	
	/**
	 * If Face is being Tracked
	 * @return boolean
	 */
	public boolean isFaceTracked(){
		return faceTracked;
	}
	
	/**
	 * get Face Points mapped to color Space (Color Image)
	 * @return PVector []
	 */
	public KOVector [] getFacePointsColorMap(){
		return facePointsColor;
	}
	
	/**
	 * get Face Points mapped to Infrared Space (InFrared Image)
	 * @return
	 */
	public KOVector [] getFacePointsInfraredMap(){
		return facePointsInfrared;
	}
}
