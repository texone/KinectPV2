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

import java.util.ArrayList;
import java.util.List;

import KinectPV2.KOSkeleton.SkeletonJointType;

/**
 * Initilice Device
 * @author Thomas Sanchez Lengeling
 *
 */
public class Device {
		
	static { 
		int arch = Integer.parseInt(System.getProperty("sun.arch.data.model"));
		String platformName = System.getProperty("os.name");
		platformName = platformName.toLowerCase();
		System.out.println(arch +" "+platformName);
		if (arch == 64) {
			System.loadLibrary("Kinect20.Face");
			System.loadLibrary("KinectPV2");
			System.out.println("Loading KinectV2");
		} else {
			throw new RuntimeException("error loading 32bits");
		}
	}
	
	public final static int BODY_COUNT = 6;

	public final static int COLOR_WIDTH  = 1920;
	public final static int COLOR_HEIGHT = 1080;
	
	public final static int DEPTH_WIDTH  = 512;
	public final static int DEPTH_HEIGHT = 424;
	
	public final static int Int32 = 0;
	public final static int Float = 1;
	
	private KOSkeleton   [] skeletonDepth;
	private KOSkeleton   [] skeleton3d;
	private KOSkeleton   [] skeletonColor;
	
	private KOFaceData   [] faceData;
	
	protected boolean runningKinect;
	
	private long ptr;
	
	private boolean startSensor;
	
	
	/**
	 * Start device
	 * @param _p PApplet
	 */
	public Device(){
		
		skeletonDepth 		= new KOSkeleton[BODY_COUNT];
		for(int i = 0; i < BODY_COUNT; i++){
			skeletonDepth[i] = new KOSkeleton();
		}
		
		skeleton3d 		= new KOSkeleton[BODY_COUNT];
		for(int i = 0; i < BODY_COUNT; i++){
			skeleton3d[i] = new KOSkeleton();
		}
		
		skeletonColor 	= new KOSkeleton[BODY_COUNT];
		for(int i = 0; i < BODY_COUNT; i++){
			skeletonColor[i] = new KOSkeleton();
		}
		
		
		faceData 		= new KOFaceData[BODY_COUNT];
		for(int i = 0; i < BODY_COUNT; i++){
			faceData[i] = new KOFaceData();
		}
				
		//FloatBuffer.allocate( WIDTHDepth * HEIGHTDepth * 3);
		startSensor = false;
		jniDevice();
		
	}
	
	public void start(){
		startSensor = jniInit();
		if(startSensor == false){
			throw new RuntimeException("ERROR STARTING KINECT V2");
		}
		
		String load = jniVersion();
		System.out.println("Version: "+load);
	}
	
	public boolean update(){
		return jniUpdate();
	}

	public void stop(){
		skeleton3d = null;
		skeletonDepth = null;
		skeletonColor = null;
		jniStopDevice();
	}
	
	public static interface KDataListener{
		public void onData(int [] rawData);
	}
	
	
	//COPY IMAGES TYPES FROM JNI FUNTIONS
	
	private List<KDataListener> _myColorListener = new ArrayList<>();
	
	public void addColorListener(KDataListener theColorListener){
		_myColorListener.add(theColorListener);
		jniEnableColorFrame(true);
	}
	
	public void removeColorListener(KDataListener theColorListener){
		_myColorListener.remove(theColorListener);
	}
	
	private void copyColorImg(int [] rawData){
		for(KDataListener myListener:new ArrayList<>(_myColorListener)){
			myListener.onData(rawData);
		}
	}
	
	private List<KDataListener> _myDepthListener = new ArrayList<>();
	
	public void addDepthListener(KDataListener theColorListener){
		_myDepthListener.add(theColorListener);
		jniEnableDepthFrame(true);
	}
	
	public void removeDepthListener(KDataListener theColorListener){
		_myDepthListener.remove(theColorListener);
	}
	
	private void copyDepthImg(int [] rawData){
		System.out.println(rawData);
		for(KDataListener myListener:new ArrayList<>(_myDepthListener)){
			myListener.onData(rawData);
		}
	}
	
	private List<KDataListener> _myDepthMaskListener = new ArrayList<>();
	
	public void addDepthMaskListener(KDataListener theColorListener){
		_myDepthMaskListener.add(theColorListener);
		jniEnableDepthMaskFrame(true);
	}
	
	public void removeDepthMaskListener(KDataListener theColorListener){
		_myDepthMaskListener.remove(theColorListener);
	}
	
	private void copyDepthMaskImg(int [] rawData){
		for(KDataListener myListener:new ArrayList<>(_myDepthMaskListener)){
			myListener.onData(rawData);
		}
	}
	
	private List<KDataListener> _myInfraredListener = new ArrayList<>();
	
	public void addInfraredListener(KDataListener theColorListener){
		_myInfraredListener.add(theColorListener);
		jniEnableInfraredFrame(true);
	}
	
	public void removeInfraredListener(KDataListener theColorListener){
		_myInfraredListener.remove(theColorListener);
	}
	
	private void copyInfraredImg(int [] rawData){
		for(KDataListener myListener:new ArrayList<>(_myInfraredListener)){
			myListener.onData(rawData);
		}
	}
	
	private List<KDataListener> _myBodyTrackListener = new ArrayList<>();
	
	public void addBodyTrackListener(KDataListener theColorListener){
		_myBodyTrackListener.add(theColorListener);
		jniEnableBodyTrackFrame(true);
	}
	
	public void removeBodyTrackListener(KDataListener theColorListener){
		_myBodyTrackListener.remove(theColorListener);
	}
	
	private void copyBodyTrackImg(int [] rawData){
		for(KDataListener myListener:new ArrayList<>(_myBodyTrackListener)){
			myListener.onData(rawData);
		}
	}
	
	private List<KDataListener> _myLongExposureListener = new ArrayList<>();
	
	public void addLongExposureListener(KDataListener theColorListener){
		_myLongExposureListener.add(theColorListener);
		jniEnableLongExposureInfrared(true);
	}
	
	public void removeLongExposureListener(KDataListener theColorListener){
		_myLongExposureListener.remove(theColorListener);
	}
	
	private void copyLongExposureImg(int [] rawData){
		for(KDataListener myListener:new ArrayList<>(_myLongExposureListener)){
			myListener.onData(rawData);
		}
	}
	
	private List<KDataListener> _myPointCloudListener = new ArrayList<>();
	
	public void addPointCloudListener(KDataListener theColorListener){
		_myPointCloudListener.add(theColorListener);
		jniEnablePointCloud(true);
	}
	
	public void removePointCloudListener(KDataListener theColorListener){
		_myPointCloudListener.remove(theColorListener);
	}
	
	//POINT CLOUD DEPTH
	private void copyPointCloudImage(int [] rawData) {
		for(KDataListener myListener:new ArrayList<>(_myPointCloudListener)){
			myListener.onData(rawData);
		}
	}
	
	private List<KDataListener> _myRawDepthListener = new ArrayList<>();
	
	public void addRawDepthListener(KDataListener theColorListener){
		_myRawDepthListener.add(theColorListener);
		jniEnableDepthFrame(true);
	}
	
	public void removeRawDepthListener(KDataListener theColorListener){
		_myRawDepthListener.remove(theColorListener);
	}
	
	private void copyRawDepthImg(int [] rawData){
		for(KDataListener myListener:new ArrayList<>(_myRawDepthListener)){
			myListener.onData(rawData);
		}
	}
	
	public static interface KFloatDataListener{
		public void onData(float [] rawData);
	}
	
	private List<KFloatDataListener> _myPointCloudPosListener = new ArrayList<>();
	
	public void addPointCloudPosListener(KFloatDataListener theColorListener){
		_myPointCloudPosListener.add(theColorListener);
		jniEnablePointCloud(true);
	}
	
	public void removePointCloudPosListener(KFloatDataListener theColorListener){
		_myPointCloudPosListener.remove(theColorListener);
	}
	
	private void copyPointCloudPos(float [] rawData){
		for(KFloatDataListener myListener:new ArrayList<>(_myPointCloudPosListener)){
			myListener.onData(rawData);
		}
	}

	private List<KFloatDataListener> _myPointCloudColorListener = new ArrayList<>();
	
	public void addPointCloudColorListener(KFloatDataListener theColorListener){
		_myPointCloudColorListener.add(theColorListener);
		jniEnablePointCloudColor(true);
	}
	
	public void removePointCloudColorListener(KFloatDataListener theColorListener){
		_myPointCloudColorListener.remove(theColorListener);
	}
	
	private void copyPointCloudColor(float [] rawData){
		for(KFloatDataListener myListener:new ArrayList<>(_myPointCloudColorListener)){
			myListener.onData(rawData);
		}
	}
	
	private final static int JOINTSIZE = Device.BODY_COUNT * (SkeletonJointType.values().length + 1) * 9;
	
	//SKELETON DEPTH
	private void copySkeletonDepthData(float [] rawData){
		if(rawData.length == JOINTSIZE){
			for(int i = 0; i < BODY_COUNT; i++){
				skeletonDepth[i].createSkeletonDepth(rawData, i);
			}
		}
	}
	
	//Color Data 
	private void copySkeletonColorData(float [] rawData){
		if(rawData.length == JOINTSIZE)
			for(int i = 0; i < BODY_COUNT; i++){
				skeletonColor[i].createSkeletonDepth(rawData, i);
			}
	}
	
	//SKELETON 3D
	private void copySkeleton3DData(float [] rawData){
		if(rawData.length == JOINTSIZE) {
			for(int i = 0; i < BODY_COUNT; i++){
				skeleton3d[i].createSkeleton3D(rawData, i);
			}
		}
	}
	
	private final static int FACESIZE  = Device.BODY_COUNT * (36);
	
	private void copyFaceRawData(float [] rawData){
		if(rawData.length == FACESIZE){
			for(int i = 0; i < BODY_COUNT; i++)
				faceData[i].createFaceData(rawData, i);
		}
	}
	
	/**
	 * Get Face Data, up to 6 users
	 * @return Array of FaceData 
	 */
	public KOFaceData [] getFaceData(){
		return faceData;
	}
	
	/**
	 * Get Skeleton as Joints with Positions and Tracking states
	 * in 3D, (x,y,z) joint and orientation,  Skeleton up to 6 users
	 * @return Skeleton []
	 */
	public KOSkeleton [] getSkeleton3d(){
		return skeleton3d;
	}
	
	/**
	 * Get Skeleton as Joints with Positions and Tracking states
	 * base on Depth Image, Skeleton with only (x, y) skeleton position mapped
	 * to the depth Image, get z value from the Depth Image.
	 * @return Skeleton []
	 */
	public KOSkeleton [] getSkeletonDepthMap(){
		return skeletonDepth;
	}
	
	/**
	 * Get Skeleton as Joints with Positions and Tracking states
	 * base on color Image, 
	 * @return Skeleton []
	 */
	public KOSkeleton [] getSkeletonColorMap(){
		return skeletonColor;
	}
	
	/**
	 * Enable or Disable Skeleton tracking
	 * @param boolean toggle 
	 */
	public void enableSkeleton(boolean toggle){
		jniEnableSkeleton(toggle);
	}
	
	/**
	 * Enable or Disable Skeleton tracking Color Map
	 * @param boolean toggle 
	 */
	public void enableSkeletonColorMap(boolean toggle){
		jniEnableSkeletonColorMap(toggle);
	}
	
	/**
	 * Enable or Disable Skeleton tracking 3d Map
	 * @param boolean toggle 
	 */
	public void enableSkeleton3dMap(boolean toggle){
		jniEnableSkeleton3dMap(toggle);
	}
	
	/**
	 * Enable or Disable Skeleton tracking Depth Map
	 * @param boolean toggle 
	 */
	public void enableSkeletonDepthMap(boolean toggle){
		jniEnableSkeletonDepthMap(toggle);
	}
	
	
	/**
	 * Enable or Disable Face Tracking
	 * @param boolean toggle 
	 */
	public void enableFaceDetection(boolean toggle){
		jniEnableFaceDetection(toggle);
	}
	
	/**
	 * Set Threshold Depth Value Z for Point Cloud
	 * @param float val
	 */
	public void setLowThresholdPC(float val){
		jniSetLowThresholdDepthPC(val);
	}
	
	/**
	 * Get Threshold Depth Value Z from Point Cloud
	 * Default 1.9
	 * @return default Threshold
	 */
	public float  getLowThresholdDepthPC(){
		return jniGetLowThresholdDepthPC();
	}
	
	
	/**
	 * Set Threshold Depth Value Z for Point Cloud
	 * @param float val
	 */
	public void setHighThresholdPC(float val){
		jniSetHighThresholdDepthPC(val);
	}
	
	/**
	 * Get Threshold Depth Value Z from Point Cloud
	 * Default 1.9
	 * @return default Threshold
	 */
	public float  getHighThresholdDepthPC(){
		return jniGetHighThresholdDepthPC();
	}
	
	
	/*
	public void enablePointCloudColor(boolean toggle){
		jniEnablePointCloudColor(toggle);
	}
	*/
	
	/*
	public void enableMirror(boolean toggle){
		jniSetMirror(toggle);
	}
	*/
	
	//------JNI FUNCTIONS
	private native void jniDevice();
	
	private native boolean jniInit();
	
	private native String jniVersion();
	
	private native boolean jniUpdate();
	
	private native void jniStopDevice();
	
	private native void jniEnableColorFrame(boolean toggle);
	
	private native void jniEnableDepthFrame(boolean toggle);
	
	private native void jniEnableDepthMaskFrame(boolean toggle);
	
	private native void jniEnableInfraredFrame(boolean toggle);
	
	private native void jniEnableBodyTrackFrame(boolean toggle);
	
	private native void jniEnableLongExposureInfrared(boolean toggle);
	
	
	private native void jniEnableSkeleton(boolean toggle);
	
	private native void jniEnableSkeletonColorMap(boolean toggle);
	
	private native void jniEnableSkeletonDepthMap(boolean toggle);
	
	private native void jniEnableSkeleton3dMap(boolean toggle);
	
	
	private native void jniEnableFaceDetection(boolean toggle);
	
	private native void jniEnablePointCloud(boolean toggle);
	
	private native void jniEnablePointCloudColor(boolean toggle);
	
	
	
	private native void jniSetLowThresholdDepthPC(float val);
	
	private native float jniGetLowThresholdDepthPC();
	

	private native void jniSetHighThresholdDepthPC(float val);
	
	private native float jniGetHighThresholdDepthPC();
	

	
}
