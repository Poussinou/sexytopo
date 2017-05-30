/** @file TDSetting.java
 *
 * @author marco corvi
 * @date jan 2014
 *
 * @brief TopoDroid application settings (preferenceces)
 * --------------------------------------------------------
 *  Copyright This sowftare is distributed under GPL-3.0 or later
 *  See the file COPYING.
 * --------------------------------------------------------
 */
package org.hwyl.sexytopo.control.calibration.fromtopodroid;

import android.os.Build;

class TDSetting
{

  // ---------------------------------------------------------
  // PREFERENCES KEYS

  final static int NR_PRIMARY_PREFS = 12;

  static final String[] key = { // prefs keys
    // ------------------------- PRIMARY PREFS
    "DISTOX_EXTRA_BUTTONS",       //  0 TODO move to general options
    "DISTOX_SIZE_BUTTONS",        // "DISTOX_BUTTON_SIZE",         //  1
    "DISTOX_TEXT_SIZE",           //  2
    "DISTOX_MKEYBOARD",           //  3
    "DISTOX_TEAM",                //  4
    "DISTOX_COSURVEY",            //  5
    "DISTOX_INIT_STATION",        //  6 default initial station for sketches
    "DISTOX_AZIMUTH_MANUAL",      //  7

    "DISTOX_DEVICE",              //  8 N.B. indexKeyDeviceName - USED by TopoDroidApp to store the device
    "DISTOX_BLUETOOTH",           //  9

    "DISTOX_LOCALE",              // 10
    "DISTOX_CWD",                 // 11 CWD must be the last of primary preferences

    // ----------------------- DEVICE PREFERNCES 
    "DISTOX_SOCK_TYPE",           // 11
    "DISTOX_COMM_RETRY",          // 12
    "DISTOX_WAIT_LASER",
    "DISTOX_WAIT_SHOT",
    "DISTOX_WAIT_DATA",
    "DISTOX_WAIT_CONN",
    "DISTOX_Z6_WORKAROUND",       // 13
    "DISTOX_CONN_MODE",           // 14
    "DISTOX_AUTO_PAIR",           // 15
    "DISTOX_SOCKET_DELAY",        // 16
    "DISTOX_AUTO_RECONNECT",      // 17

    // ------------------- SURVEY PREFERENCES
    "DISTOX_CLOSE_DISTANCE",      // 18
    "DISTOX_EXTEND_THR2",         // 19
    "DISTOX_VTHRESHOLD",          // 20 LRUD vertical threshold
    "DISTOX_SURVEY_STATION",      // 21 DISTOX_SURVEY_STATIONS must not be used
    "DISTOX_UNIT_LENGTH",         // 22
    "DISTOX_UNIT_ANGLE",          // 23
    "DISTOX_ACCEL_PERCENT",           // 24 shot quality thresholds
    "DISTOX_MAG_PERCENT",
    "DISTOX_DIP_THR",             // 26
    "DISTOX_LOOP_CLOSURE_VALUE",  // 27 whether to close loop
    "DISTOX_CHECK_ATTACHED",      // 28
    "DISTOX_PREV_NEXT",         
    "DISTOX_MAX_SHOT_LENGTH",

    "DISTOX_UNIT_LOCATION",       // 29 
    "DISTOX_CRS",                 // 30
     
    // -------------------- CALIB PREFERENCES
    "DISTOX_GROUP_BY",            // 31
    "DISTOX_GROUP_DISTANCE",      // 32
    "DISTOX_CALIB_EPS",           // 33
    "DISTOX_CALIB_MAX_IT",        // 34
    "DISTOX_RAW_CDATA",           // 35
    "DISTOX_CALIB_ALGO",          // 36

    // -------------------- SKETCH PREFERENCES
    "DISTOX_AUTO_STATIONS",       // 37
    "DISTOX_CLOSENESS",           // 38
    "DISTOX_ERASENESS",           // 38
    "DISTOX_MIN_SHIFT",          
    "DISTOX_POINTING",
    "DISTOX_LINE_SEGMENT",
    "DISTOX_LINE_ACCURACY",
    "DISTOX_LINE_CORNER",         // 41
    "DISTOX_LINE_STYLE",          // 42
    "DISTOX_DRAWING_UNIT",        // 43
    "DISTOX_PICKER_TYPE",         // 44
    "DISTOX_HTHRESHOLD",          // UNUSED
    "DISTOX_STATION_SIZE",        // 46
    "DISTOX_LABEL_SIZE",          // 47
    "DISTOX_LINE_THICKNESS",      // 48
    "DISTOX_AUTO_SECTION_PT",     // 49
    "DISTOX_BACKUP_NUMBER",
    "DISTOX_BACKUP_INTERVAL",

    // -------------------- LASER PREFERENCES
    "DISTOX_SHOT_TIMER",          // 50 // bearing-clino timer
    "DISTOX_BEEP_VOLUME",         // 51
    "DISTOX_LEG_SHOTS",           // 52 nr. of shots to make a leg

    // -------------------- 3D-MODEL PREFERENCES
    "DISTOX_SKETCH_LINE_STEP",    // 53
    "DISTOX_DELTA_EXTRUDE",       // 54
    "DISTOX_COMPASS_READINGS",    // 55

    // -------------------- IMPORT-EXPORT PREFERENCES
    "DISTOX_SPLAY_EXTEND",        // 56 whether to set extend to splay shots
    "DISTOX_BITMAP_SCALE",        // 57
    "DISTOX_THUMBNAIL",           // 58
    "DISTOX_DOT_RADIUS",          // 59
    "DISTOX_FIXED_THICKNESS",     // 60
    "DISTOX_ARROW_LENGTH",        // 61
    "DISTOX_EXPORT_SHOTS",        // 62
    "DISTOX_EXPORT_PLOT",        // 63
    "DISTOX_THERION_MAPS",
    "DISTOX_SVG_GRID",           // export grid in SVG 
    "DISTOX_SVG_IN_HTML",
    "DISTOX_KML_STATIONS",     
    "DISTOX_KML_SPLAYS",

    "DISTOX_SPLAY_VERT_THRS",     // 64 over mSplayVertThrs splays are not displayed in plan view
    "DISTOX_BACKSIGHT",           // 65
    "DISTOX_MAG_ANOMALY",         // 66 whether to compensate magnetic anomaly
    "DISTOX_VERT_SPLAY",          // 68 over this splay are shown with dashed line
    "DISTOX_STATION_PREFIX",      // 69 whether to add cave-name prefix to stations (cSurvey/compass)
    "DISTOX_STATION_NAMES",
    "DISTOX_TROBOT_NAMES",        // station names are TopoRobot
    "DISTOX_ZOOM_CTRL",           // 71
    "DISTOX_SIDE_DRAG",           // 72 whether to enable side-drag
    "DISTOX_DXF_SCALE", 
    // "DISTOX_ACAD_VERSION",
    "DISTOX_BITMAP_BGCOLOR",      // 75
    "DISTOX_SURVEX_EOL",          // 76 survex end of line
    "DISTOX_SURVEX_SPLAY",
    "DISTOX_SURVEX_LRUD",         // 78
    "DISTOX_SWAP_LR",             // swap Left-Right in compass export
    "DISTOX_UNSCALED_POINTS",     // 79 unscaled drawing point items
    "DISTOX_UNIT_GRID",           // 80
    // "DISTOX_XTHERION_AREAS",      // 81 save areas a-la xtherion
    "DISTOX_THERION_SPLAYS",
    "DISTOX_RECENT_NR",           // 82 number of most recent items (item picker)
    "DISTOX_AREA_BORDER",         // 83 area border visibility
    "DISTOX_ORTHO_LRUD",          // 86 orthogonal LRUD ( >=1 disable, min 0 )
    // "DISTOX_SECTION_STATIONS",    //

    "DISTOX_WALLS_TYPE",          // 87
    "DISTOX_WALLS_PLAN_THR",      // 88
    "DISTOX_WALLS_EXTENDED_THR",  // 89
    "DISTOX_WALLS_XCLOSE",        // 90
    "DISTOX_WALLS_XSTEP",         // 91
    "DISTOX_WALLS_CONCAVE",       // 92

    "DISTOX_DXF_BLOCKS", // DISTOX_DXF_BLOCKS

    // "DISTOX_SKETCH_USES_SPLAYS",  // 
    // "DISTOX_SKETCH_BERDER_STEP",
    // "DISTOX_SKETCH_SECTION_STEP", // 

    "DISTOX_ALGO_MIN_ALPHA",
    "DISTOX_ALGO_MIN_BETA",
    "DISTOX_ALGO_MIN_GAMMA",
    "DISTOX_ALGO_MIN_DELTA",

  };

  static boolean mDxfBlocks = true; // DXF_BLOCKS

  static float mAlgoMinAlpha = 0.1f;
  static float mAlgoMinBeta  = 4.0f;
  static float mAlgoMinGamma = 1.0f;
  static float mAlgoMinDelta = 1.0f;

  static String keyDeviceName() { return "DISTOX_DEVICE"; }

  // static final  String EXPORT_TYPE    = "th";    // DISTOX_EXPORT_TH

  // prefs default values
  static String  mDefaultTeam = "";

  static final int LEVEL_BASIC        = 0;
  static final int LEVEL_NORMAL       = 1;
  static final int LEVEL_ADVANCED     = 2;
  static final int LEVEL_EXPERIMENTAL = 3;
  static final int LEVEL_COMPLETE     = 4;
  static int mActivityLevel = 1;
  static boolean mLevelOverBasic        = true;
  static boolean mLevelOverNormal       = false;
  static boolean mLevelOverAdvanced     = false;
  static boolean mLevelOverExperimental = false;

  static int mSizeButtons     = 42;      // action bar buttons scale (either 1 or 2)
  static int mTextSize        = 16;     // list text size 
  static boolean mKeyboard    = true;

  // - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
  // IMPORT EXPORT
  static boolean mLRExtend           = true;   // whether to extend LR or not (Compass/VisualTopo input)

  static String mSurvexEol           = "\n";
  static boolean mSurvexSplay        = false;
  static boolean mSurvexLRUD         = false;
  static boolean mSwapLR             = false; // swap LR in Compass export
  static boolean mOrthogonalLRUD     = false; // whether angle > 0 
  static float mOrthogonalLRUDAngle  = 0;     // angle
  static float mOrthogonalLRUDCosine = 1;     // cosine of the angle

  static boolean mExportStationsPrefix = false;  // whether to prepend cave name to station in cSurvey/compass export
  static boolean mTRobotNames = false;

  // static boolean mXTherionAreas = false;
  static boolean mAutoStations = true; // whether to add stations automatically to scrap therion files
  static boolean mTherionSplays = true; // whether to add splay segments to auto stations

  static float mBitmapScale = 1.5f;
  static float mDxfScale = 1.0f;
  static int mBitmapBgcolor = 0x000000;

  static int mAcadVersion = 13;      // AutoCAD version 9, or 13

  // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
  // LOCATION
  static String mCRS = "Long-Lat";    // default coord ref systen 
  // static final  String UNIT_LOCATION  = "ddmmss";
  static int mUnitLocation = 0; // 0 dec-degree, 1 ddmmss

  // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
  // CALIBRATION

  // static final String GROUP_DISTANCE = "40";
  static float mGroupDistance = 40;

  static final float DISTOX_MAX_EPS  = 0.01f; // hard limit
  static final String CALIB_EPS      = "0.000001";
  static float mCalibEps = 0.000001f; // calibartion epsilon

  static int   mCalibMaxIt = 200;     // calibration max nr of iterations

  // calibration data grouping policies
  static final int GROUP_BY_DISTANCE = 0;
  static final int GROUP_BY_FOUR     = 1;
  static final int GROUP_BY_ONLY_16  = 2;
  // static final String GROUP_BY  = "1";     // GROUP_BY_FOUR
  static int mGroupBy = GROUP_BY_FOUR;  // how to group calib data

  // static boolean mRawData = false;   // whether to display calibration raw data as well
  static int   mRawCData  = 0;
  static int   mCalibAlgo = 0;   // calibration algorithm: 0 auto, 1 linear, 2 non-linear

  // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
  // DEVICE
  final static int CONN_MODE_BATCH      = 0;      // DistoX connection mode
  final static int CONN_MODE_CONTINUOUS = 1;
  final static int CONN_MODE_MULTI      = 2;
  static int mConnectionMode    = CONN_MODE_BATCH; 

  static boolean isConnectionModeBatch() { return mConnectionMode != CONN_MODE_CONTINUOUS; }

  static boolean mZ6Workaround  = true;

  static boolean mAutoReconnect = false;
  static boolean mAutoPair      = true;
  static int mConnectSocketDelay = 0; // wait time if not paired [0.1 sec]

  // static final boolean CHECK_BT = true;
  static int mCheckBT = 1;        // BT: 0 disabled, 1 check on start, 2 enabled

  static final int TD_SOCK_DEFAULT      = 0;    // BT socket type
  static final int TD_SOCK_INSEC        = 1;
  static final int TD_SOCK_PORT         = 2;
  static final int TD_SOCK_INSEC_PORT   = 3;
  // static final int TD_SOCK_INSEC_INVOKE = 4;
  // static int mDefaultSockType = (android.os.Build.MANUFACTURER.equals("samsung") ) ? TD_SOCK_INSEC : TD_SOCK_DEFAULT;
  static String mDefaultSockStrType = (Build.MANUFACTURER.equals("samsung") ) ? "1" : "0";
  static int mSockType = TD_SOCK_DEFAULT;

  static int mCommRetry = 1; 
  static int mCommType  = 0; // 0: on-demand, 1: continuous

  static int mWaitLaser = 1000;
  static int mWaitShot  = 4000;
  static int mWaitData  =  100;  // delay between data
  static int mWaitConn  =  500;  // delay waiting a connection
  static int mWaitCommand = 100;

  static boolean mCheckAttached = false;    // whether to check is there are shots non-attached
  static boolean mPrevNext = true;    // whether to display prev-next buttons in shot dialog

  // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
  // SHOTS
  static float mVThreshold = 80f;   // verticality threshold (LRUD)
  static float mHThreshold;         // horizontal plot threshold

  static int mExportShotsFormat = -1; // DISTOX_EXPORT_NONE
  static int mExportPlotFormat  = -1; // DISTOX_EXPORT_NONE
  static boolean mTherionMaps   = false;
  static boolean mSvgGrid       = false;
  static boolean mSvgInHtml     = false;
  static boolean mKmlStations   = true;
  static boolean mKmlSplays     = false;

  static int     mSurveyStations  = 1;     // automatic survey stations: 0 no, 1 forward-after-splay, 2 backward-after-splay
  static boolean mShotAfterSplays = true;  //                            3 forward-before-splay, 4 backward-before-splay
  static boolean isSurveyForward()  { return (mSurveyStations%2) == 1; }
  static boolean isSurveyBackward() { return mSurveyStations>0 && (mSurveyStations%2) == 0; }

  // static int mScreenTimeout = 60000; // 60 secs
  static int mTimerCount       = 10;    // Acc/Mag timer countdown (secs)
  static int mBeepVolume       = 50;    // beep volume
  static int mCompassReadings  = 4;     // number of compass readings to average

  // static final String CLOSE_DISTANCE = "0.05"; // 50 cm / 1000 cm
  static float   mCloseDistance = 0.05f; 
  static int     mMinNrLegShots = 3;
  static String  mInitStation   = "0";
  static boolean mBacksight     = false;    // whether to check backsight
  static boolean mBacksightShot = false;    // backsight shooting policy
  static boolean mTripodShot    = false;    // tripod shooting policy
  static boolean mTRobotShot    = false;    // TopoRobot shooting policy
  static boolean mMagAnomaly    = false;    // local magnetic anomaly survey
  static float   mSplayVertThrs = 80;
  static boolean mAzimuthManual = false;    // whether to manually set extend / or use reference azimuth
  static float   mVertSplay     = 50;
  static int     mStationNames  = 0;        // type of station names (0: alpha, 1: number)


  static int mLoopClosure = 0;      // loop closure: 0 none, 1 normal, 3 triangles
  
  static final  String UNIT_LENGTH         = "meters";
  static final  String UNIT_ANGLE          = "degrees";
  // static final  String UNIT_ANGLE_GRADS = "grads";
  // static final  String UNIT_ANGLE_SLOPE = "slope";
  // conversion factor from internal units (m) to user units
  static float mUnitLength = 1;
  static float mUnitAngle  = 1;
  static String mUnitLengthStr = "m";    // N.B. Therion syntax: "m", "ft"
  static String mUnitAngleStr  = "deg";  // N.B. Therion syntax: "deg", "grad"

  // static final String EXTEND_THR = "10"; 
  static float mExtendThr = 10;             // extend vertically splays in [90-30, 90+30] of the leg

  static int mThumbSize = 200;               // thumbnail size

  // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
  // SKETCH DRAWING

  // static boolean mZoomControls = false;
  static int mZoomCtrl = 1;
  static boolean mSideDrag = false;

  static float mUnit = 1.4f; // drawing unit

  // selection_radius = cutoff + closeness / zoom
  static final float mCloseCutoff = 0.01f; // minimum selection radius

  static float mCloseness = 24f;             // selection radius
  static float mEraseness = 36f;             // eraser radius
  static int mMinShift = 60;
  static int mPointingRadius = 16;

  // static final String LINE_SHIFT = "20.0";
  static float mUnitGrid  = 1;         // 1: meter, 0.9... yard

  static final int PICKER_RECENT = 0; // Drawing-tools picker type
  static final int PICKER_LIST   = 1; 
  static final int PICKER_GRID   = 2;
  static final int PICKER_GRID_3 = 3;
  static int mPickerType = PICKER_RECENT;
  static int mRecentNr    = 4;        // nr. most recent symbols

  static final int LINE_STYLE_BEZIER = 0;  // drawing line styles
  static final int LINE_STYLE_ONE    = 1;
  static final int LINE_STYLE_TWO    = 2;
  static final int LINE_STYLE_THREE  = 3;
  static final String LINE_STYLE     = "2";     // LINE_STYLE_TWO NORMAL
  static int   mLineStyle = LINE_STYLE_BEZIER;    
  static int   mLineType;        // line type:  1       1     2    3
  static int   mLineSegment   = 10;
  static int   mLineSegment2  = 100;   // square of mLineSegment
  static float mLineAccuracy  = 1f;
  static float mLineCorner    = 20;    // corner threshold

  static float mStationSize    = 20;   // size of station names [pt]
  static float mLabelSize      = 24;   // size of labels [pt]
  static float mFixedThickness = 1;    // width of fixed lines
  static float mLineThickness  = 1;    // witdh of drawing lines
  static boolean mAutoSectionPt = false;
  static int   mBackupNumber   = 5;
  static int   mBackupInterval = 60;
  static float mDotRadius      = 5;
  static float mArrowLength    = 8;

  // NOTE not used, but could set a default for section splays
  // static int mSectionStations = 3; // 1: From, 2: To, 3: both

  static boolean mUnscaledPoints = false;
  static boolean mAreaBorder     = true;

  // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
  // 3D
  static float mSketchSideSize;
  static float mDeltaExtrude;
  // static boolean mSketchUsesSplays; // whether 3D models surfaces use splays
  // static float mSketchBorderStep;
  // static float mSketchSectionStep;

  // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
  // DATA ACCURACY
  static float mAccelerationThr = 1; // acceleration threshold (shot quality)
  static float mMagneticThr     = 1; // magnetic threshold
  static float mDipThr          = 2; // dip threshold
  static float mMaxShotLength   = 50; // max length of a shot (if larger it is overshoot)
  // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
  // WALLS

  static final int WALLS_NONE    = 0;
  static final int WALLS_CONVEX  = 1;
  static final int WALLS_LAST    = 1; // placeholder
  static int   mWallsType        = WALLS_NONE;
  static float mWallsPlanThr     = 70;
  static float mWallsExtendedThr = 45;
  static float mWallsXClose      = 0.1f;
  static float mWallsXStep       = 1.0f;
  static float mWallsConcave     = 0.1f;


}
