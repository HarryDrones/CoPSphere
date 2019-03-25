Use a Multiwii 2.3 IMU to connect to android device over USB.  Point the IMU at the star Arcturus and tap the far left 1/3 of the 
android device screen.  The red circle of position/circle of equal altitude will move and change size on the globe, as the IMU is pointed.
The green circle is the star Dubhe.  Tap the middle far left of the screen and the green circle will change, as the IMU is pointed.  
The lower far left 1/3 is the blue circle, Procyon is the star.

Update/Note:  The red circle is the star Arcturus, green is Vega, blue is Dubhe.  There is a conceptual flaw that I havent had time to fix and that is that using the heading data of the IMU does not equate to observed azimuth of the star.  If an almanac is used to get GHA and the IMU is turned to represent that GHA, then the combination of GHA and altitude will yield a reasonable(limited by the accuracy of the IMU) Circle of Position.  GHA is not directly observeable and I need to correct it so that the circles are calculated based on an observed azimuth and altitude.  
