package org.hwyl.sexytopo.control.calibration.fromtopodroid;

import java.util.List;


public class GroupReadings {

    public static long group(List<CalibCBlock> list, long start_id) {

        float thr = TDMath.cosd( TDSetting.mGroupDistance );
        if ( list.size() < 4 ) {
            return -1;
        }
        long group = 0;
        int cnt = 0;
        float b = 0.0f;
        float c = 0.0f;
        if ( start_id >= 0 ) {
            for ( CalibCBlock item : list ) {
                if ( item.mId == start_id ) {
                    group = item.mGroup;
                    cnt = 1;
                    b = item.mBearing;
                    c = item.mClino;
                    break;
                }
            }
        } else {
            if ( TDSetting.mGroupBy != TDSetting.GROUP_BY_DISTANCE ) {
                group = 1;
            }
        }
        switch ( TDSetting.mGroupBy ) {
            case TDSetting.GROUP_BY_DISTANCE:
                for ( CalibCBlock item : list ) {
                    if ( start_id >= 0 && item.mId <= start_id ) continue;
                    if ( group == 0 || item.isFarFrom( b, c, thr ) ) {
                        ++ group;
                        b = item.mBearing;
                        c = item.mClino;
                    }
                    item.setGroup( group );
                    //mApp.mDData.updateGMName( item.mId, item.mCalibId, Long.toString( item.mGroup ) );
                    // N.B. item.calibId == cid
                }
                break;
            case TDSetting.GROUP_BY_FOUR:
                // TDLog.Log( TDLog.LOG_CALIB, "group by four");
                for ( CalibCBlock item : list ) {
                    if ( start_id >= 0 && item.mId <= start_id ) continue;
                    item.setGroupIfNonZero( group );
                    //mApp.mDData.updateGMName( item.mId, item.mCalibId, Long.toString( item.mGroup ) );
                    ++ cnt;
                    if ( (cnt%4) == 0 ) {
                        ++group;
                        // TDLog.Log( TDLog.LOG_CALIB, "cnt " + cnt + " new group " + group );
                    }
                }
                break;
            case TDSetting.GROUP_BY_ONLY_16:
                for ( CalibCBlock item : list ) {
                    if ( start_id >= 0 && item.mId <= start_id ) continue;
                    item.setGroupIfNonZero( group );
                    //mApp.mDData.updateGMName( item.mId, item.mCalibId, Long.toString( item.mGroup ) );
                    ++ cnt;
                    if ( (cnt%4) == 0 || cnt >= 16 ) ++group;
                }
                break;
        }
        return (int)group-1;
    }

}
