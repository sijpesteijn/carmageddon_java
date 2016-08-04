/*
 * Camera.c
 *
 *  Created on: Jun 9, 2016
 *      Author: gijs
 */

#include "Camera.h"
#include <syslog.h>
#include <opencv2/opencv.hpp>

using namespace cv;
using namespace std;

Camera::Camera() {
	syslog(LOG_INFO, "%s", "Setting up camera.");

//	VideoCapture capture(0);
//	if (!capture.isOpened()) {
//		syslog(LOG_INFO,"%s", "Cannot open the video cam");
//		connected = 0;
//	} else {
//	    capture.set(CV_CAP_PROP_FRAME_WIDTH,1920);
//	    capture.set(CV_CAP_PROP_FRAME_HEIGHT,1080);
//	    if(!capture.isOpened()){
//	     cout << "Failed to connect to the camera." << endl;
//	    }
//	    Mat frame, edges;
//	    capture >> frame;
//	    if(frame.empty()){
//	 cout << "Failed to capture an image" << endl;
//	    }
//	    cvtColor(frame, edges, CV_BGR2GRAY);
//	    Canny(edges, edges, 0, 80, 3);
//	    imwrite("edges.png", edges);
//	    imwrite("capture.png", frame);
//	    connected = 1;
//	}

	 VideoCapture cap(0); // open the video camera no. 0

	    if (!cap.isOpened())  // if not success, exit program
	    {
	        cout << "ERROR: Cannot open the video file" << endl;
	    }


	   double dWidth = cap.get(CV_CAP_PROP_FRAME_WIDTH); //get the width of frames of the video
	   double dHeight = cap.get(CV_CAP_PROP_FRAME_HEIGHT); //get the height of frames of the video

	   cout << "Frame Size = " << dWidth << "x" << dHeight << endl;

	   Size frameSize(static_cast<int>(dWidth), static_cast<int>(dHeight));

	 VideoWriter oVideoWriter ("MyVideo.avi", CV_FOURCC('P','I','M','1'), 20, frameSize, true); //initialize the VideoWriter object

	   if ( !oVideoWriter.isOpened() ) //if not initialize the VideoWriter successfully, exit the program
	   {
	        cout << "ERROR: Failed to write the video" << endl;
	   }

	    while (1)
	    {

	        Mat frame;

	        bool bSuccess = cap.read(frame); // read a new frame from video

	        if (!bSuccess) //if not success, break loop
	       {
	             cout << "ERROR: Cannot read a frame from video file" << endl;
	             break;
	        }

	        oVideoWriter.write(frame); //writer the frame into the file

//	        imshow("MyVideo", frame); //show the frame in "MyVideo" window

	        if (waitKey(10) == 27) //wait for 'esc' key press for 30ms. If 'esc' key is pressed, break loop
	       {
	            cout << "esc key is pressed by user" << endl;
	            break;
	       }
	    }

	    syslog(LOG_INFO, "%s", "Here is the camera.");
}

int Camera::isConnected() {
	return connected;
}

Camera* Camera::getInstance() {
	static Camera camera;
	return &camera;
}

