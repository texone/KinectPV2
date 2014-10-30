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
 * Skeleton Class
 * @author Thomas Sanchez Lengeling
 *
 */
public class KOSkeleton{
	
	public static enum SkeletonJointType{
		SPINE_BASE,
		SPINE_MID,
		NECK,
		HEAD,
		SHOULDER_LEFT,
		ELBOW_LEFT,
		WRIST_LEFT,
		HAND_LEFT,
		SHOULDER_RIGHT,
		ELBOW_RIGHT,
		WRIST_RIGHT,
		HAND_RIGHT,
		HIP_LEFT,
		KNEE_LEFT,
		ANKLE_LEFT,
		FOOT_LEFT,
		HIP_RIGHT,
		KNEE_RIGHT,
		ANKLE_RIGHT,
		FOOT_RIGHT,
		SPINE_SHOULDER,
		HAND_TIP_LEFT,
		THUMB_LEFT,
		HAND_TIP_RIGHT,
		THUMB_RIGHT;
	}
	
	public static enum TrackingState{
		NOT_TRACKED,
		INFERRED,
		TRACKED
	}
	
	public static enum HandState{
		UNKNOWN,
		NOT_TRACKED,
		OPEN,
		CLOSED,
		LASSO
	}
	
	
	
	public final static int TrackingState_NotTracked = 0;
	public final static int TrackingState_Inferred	 = 1;
	public final static int TrackingState_Tracked	 = 2;
	
	
	public final static int HandState_Unknown		= 0;
    public final static int HandState_NotTracked	= 1;
    public final static int HandState_Open			= 2;
    public final static int HandState_Closed		= 3;
    public final static int HandState_Lasso			= 4;
	
	public class SkeletonJoint {

		public float x;
		public float y;
		public float z;

		public TrackingState state;

		public SkeletonJointType type;

		public KOQuartenion orientation;

		SkeletonJoint(float theX, float theY, float theZ, KOQuartenion theOrientation, TrackingState theState) {
			x = theX;
			y = theY;
			z = theZ;
			orientation = theOrientation;
			state = theState;
		}

		SkeletonJoint() {
		}

	}
	
	protected SkeletonJoint [] kJoints;
	
	private boolean tracked;
	
	private HandState _myLeftHandState;
	private HandState _myRightHandState;
	
	public KOSkeleton(){
		kJoints  = new SkeletonJoint[SkeletonJointType.values().length + 1];
		for(int i = 0; i < SkeletonJointType.values().length + 1; i++){
			kJoints[i] = new SkeletonJoint(0,0,0, new KOQuartenion(), TrackingState.NOT_TRACKED);
		}
	}
	
	/*
	 * if the current skeleton is being tracked
	 */
	public boolean isTracked(){
		return tracked;
	}
	
	/**
	 * get the array of joints of the skeleton
	 * @return  KJoint []
	 */
	public SkeletonJoint [] getJoints(){
		return kJoints;
	}
	
	private TrackingState fromHandState(HandState theHandState){
		switch(theHandState){
		case NOT_TRACKED:
			return TrackingState.NOT_TRACKED;
		default:
			return TrackingState.TRACKED;
		}
	}
	
	public void createSkeleton3D(float [] rawData, int i){
		int index2 = i * (SkeletonJointType.values().length+1) * 9;
		int indexJoint = index2 + (SkeletonJointType.values().length+1) * 9 - 1;
		
		tracked = rawData[indexJoint] == 1.0;
		
		if(!tracked)return;
		
		for(int j = 0; j < SkeletonJointType.values().length; ++j){			
			int index1 = j * 9;
			SkeletonJoint myJoint = kJoints[j];
			myJoint.x = rawData[index2 + index1 + 0];
			myJoint.y = rawData[index2 + index1 + 1];
			myJoint.z = rawData[index2 + index1 + 2];
				
			myJoint.orientation.w = rawData[index2 + index1 + 3];
			myJoint.orientation.x = rawData[index2 + index1 + 4];
			myJoint.orientation.y = rawData[index2 + index1 + 5];
			myJoint.orientation.z = rawData[index2 + index1 + 6];
				
			myJoint.type  =   SkeletonJointType.values()[(int)rawData[index2 + index1 + 8]];
			
			int myState = (int)rawData[index2 + index1 + 7];
			if(myJoint.type == SkeletonJointType.HAND_LEFT || myJoint.type == SkeletonJointType.HAND_RIGHT){
				HandState myHandState = HandState.values()[myState];
				myJoint.state = fromHandState(myHandState);
				switch(myJoint.type){
				case HAND_LEFT:
					_myLeftHandState = myHandState;
					break;
				case HAND_RIGHT:
					_myRightHandState = myHandState;
					break;
				default:
					break;
				}
			}else{
				myJoint.state =	TrackingState.values()[myState];
			}
				
		}
	}
	
	public void createSkeletonDepth(float [] rawData, int i){
		int index2 = i * (SkeletonJointType.values().length + 1) * 9;
		int indexJoint = index2 + (SkeletonJointType.values().length + 1) * 9 - 1;
		
		tracked = rawData[indexJoint] == 1.0;
		
		if(!tracked)return;
		
		for(int j = 0; j < SkeletonJointType.values().length; ++j){			
			int index1 = j * 9;
			SkeletonJoint myJoint = kJoints[j];
			myJoint.x = rawData[index2 + index1 + 0];
			myJoint.y = rawData[index2 + index1 + 1];
			myJoint.z = rawData[index2 + index1 + 2];
		
			myJoint.type  =   SkeletonJointType.values()[(int)rawData[index2 + index1 + 8]];
			
			int myState = (int)rawData[index2 + index1 + 7];
			if(myJoint.type == SkeletonJointType.HAND_LEFT || myJoint.type == SkeletonJointType.HAND_RIGHT){
				HandState myHandState = HandState.values()[myState];
				myJoint.state = fromHandState(myHandState);
				switch(myJoint.type){
				case HAND_LEFT:
					_myLeftHandState = myHandState;
					break;
				case HAND_RIGHT:
					_myRightHandState = myHandState;
					break;
				default:
					break;
				}
			}else{
				myJoint.state =	TrackingState.values()[myState];
			}
		}
	}
	
	public HandState leftHandState(){
		return _myLeftHandState;
	}
	
	public HandState rightHandState(){
		return _myRightHandState;
	}
}
