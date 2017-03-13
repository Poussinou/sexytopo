package org.hwyl.sexytopo.comms;

public class CalibrationProtocol extends Thread {

    private static final int ACCELERATION_ADMIN = 0;
    private static final int ACCELERATION_MX_LOW_BYTE = 1;
    private static final int ACCELERATION_MX_HIGH_BYTE = 2;
    private static final int ACCELERATION_MY_LOW_BYTE = 3;
    private static final int ACCELERATION_MY_HIGH_BYTE = 4;
    private static final int ACCELERATION_MZ_LOW_BYTE = 5;
    private static final int ACCELERATION_MZ_HIGH_BYTE = 6;
    private static final int ACCELERATION_ZERO_BYTE = 7;



    private static final int MAGNETIC_ADMIN = 0;
    private static final int MAGNETIC_MX_LOW_BYTE = 1;
    private static final int MAGNETIC_MX_HIGH_BYTE = 2;
    private static final int MAGNETIC_MY_LOW_BYTE = 3;
    private static final int MAGNETIC_MY_HIGH_BYTE = 4;
    private static final int MAGNETIC_MZ_LOW_BYTE = 5;
    private static final int MAGNETIC_MZ_HIGH_BYTE = 6;
    private static final int MAGNETIC_ZERO_BYTE = 7;

    // ??
    private static final int SEQUENCE_BIT_MASK = 0x80;
    private static final int ACKNOWLEDGEMENT_PACKET_BASE = 0x55;

    private static final byte START_CALIBRATION = 0x31; // 00110001
    private static final byte STOP_CALIBRATION = 0x30; // 00110000


    public static byte[] getStartCalibrationPacket() {
        return new byte[] {START_CALIBRATION};
    }


    public static byte[] getStopCalibrationPacket() {
        return new byte[] {STOP_CALIBRATION};
    }


    @Override
    public void run() {

    }



}
