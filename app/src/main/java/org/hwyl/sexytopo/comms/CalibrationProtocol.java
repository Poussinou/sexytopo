package org.hwyl.sexytopo.comms;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import org.hwyl.sexytopo.control.DataManager;
import org.hwyl.sexytopo.control.Log;
import org.hwyl.sexytopo.model.calibration.CalibrationReading;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public class CalibrationProtocol extends DistoXProtocol {

    private static final int ACCELERATION_ADMIN = 0;
    private static final int ACCELERATION_GX_LOW_BYTE = 1;
    private static final int ACCELERATION_GX_HIGH_BYTE = 2;
    private static final int ACCELERATION_GY_LOW_BYTE = 3;
    private static final int ACCELERATION_GY_HIGH_BYTE = 4;
    private static final int ACCELERATION_GZ_LOW_BYTE = 5;
    private static final int ACCELERATION_GZ_HIGH_BYTE = 6;
    private static final int ACCELERATION_ZERO_BYTE = 7;

    private static final int MAGNETIC_ADMIN = 0;
    private static final int MAGNETIC_MX_LOW_BYTE = 1;
    private static final int MAGNETIC_MX_HIGH_BYTE = 2;
    private static final int MAGNETIC_MY_LOW_BYTE = 3;
    private static final int MAGNETIC_MY_HIGH_BYTE = 4;
    private static final int MAGNETIC_MZ_LOW_BYTE = 5;
    private static final int MAGNETIC_MZ_HIGH_BYTE = 6;
    private static final int MAGNETIC_ZERO_BYTE = 7;

    private static final byte START_CALIBRATION = 0x31; // 00110001
    private static final byte STOP_CALIBRATION = 0x30; // 00110000

    private static final byte ACCELERATION_SENSOR = 0x2; // X0000010 assuming first bit is 0
    private static final byte MAGNETIC_SENSOR = 0x3; // X0000011 assuming first bit is 0


    public CalibrationProtocol(
            Context context, BluetoothDevice bluetoothDevice, DataManager dataManager) {
        super(context, bluetoothDevice, dataManager);
    }

    @Override
    public void go(DataInputStream inStream, DataOutputStream outStream)
            throws IOException, Exception {

        CalibrationReading calibrationReading = new CalibrationReading();

        try {
            getAccelerationReading(calibrationReading, inStream, outStream);
            getMagneticFieldReading(calibrationReading, inStream, outStream);

        } finally {
            if (calibrationReading.getState() == CalibrationReading.State.COMPLETE) {
                dataManager.addCalibrationReading(calibrationReading);
            }
        }

    }

    private void getAccelerationReading(CalibrationReading calibrationReading,
                                        DataInputStream inStream, DataOutputStream outStream)
            throws IOException, Exception {

        while(isActive()) {
            byte[] packet = readPacket(inStream);
            String packetString = Integer.toBinaryString(packet[0]);
            int readByte = readByte(packet, 0);
            String parsedPacketString = Integer.toBinaryString(readByte);
            Log.d("Received data: " + MeasurementProtocol.describePacket(packet));

            if (isAccelerationSensorPacket(packet)) {
                if (calibrationReading.getState() == CalibrationReading.State.UPDATE_ACCELERATION) {
                    updateAccelerationSensorReading(packet, calibrationReading);
                }
                acknowledge(outStream, packet);

            } else if (isMagneticFieldSensorPacket(packet) &&
                    calibrationReading.getState() == CalibrationReading.State.UPDATE_MAGNETIC) {
                break; // don't acknowledge; we'll do that in the next step

            } else {
                throw new IllegalStateException("Received calibration packet out of order");
            }
        }

    }

    private void getMagneticFieldReading(CalibrationReading calibrationReading,
                                        DataInputStream inStream, DataOutputStream outStream)
            throws IOException, Exception {

        while(isActive()) {
            byte[] packet = readPacket(inStream);
            String packetString = Integer.toBinaryString(packet[0]);
            int readByte = readByte(packet, 0);
            String parsedPacketString = Integer.toBinaryString(readByte);
            Log.d("Received data: " + MeasurementProtocol.describePacket(packet));

            if (isMagneticFieldSensorPacket(packet)) {
                if (calibrationReading.getState() == CalibrationReading.State.UPDATE_MAGNETIC) {
                    updateMagneticSensorReading(packet, calibrationReading);
                }
                acknowledge(outStream, packet);

            } else if (isAccelerationSensorPacket(packet) &&
                    calibrationReading.getState() == CalibrationReading.State.UPDATE_ACCELERATION) {
                break; // don't acknowledge; we'll do that in the next step

            } else {
                throw new IllegalStateException("Received calibration packet out of order");
            }
        }

    }

    public void startNewCalibration() throws Exception {
        dataManager.clearCalibrationReadings();
        writeCommandPacket(new byte[]{START_CALIBRATION});
    }

     public static byte[] getStartCalibrationPacket() {
        return new byte[] {START_CALIBRATION};
    }

    public static byte[] getStopCalibrationPacket() {
        return new byte[] {STOP_CALIBRATION};
    }




    private static boolean isAccelerationSensorPacket(byte[] packet) {
        // 0xFE = ignore last bit
        // 0x7F = ignore first bit
        return (readByte(packet, ACCELERATION_ADMIN) & 0x7F) == ACCELERATION_SENSOR;
    }

    private static boolean isMagneticFieldSensorPacket(byte[] packet) {
        return (readByte(packet, MAGNETIC_ADMIN) & 0x7F) == MAGNETIC_SENSOR;
    }


    public static void updateAccelerationSensorReading(byte[] packet, CalibrationReading reading)
            throws Exception {
        int gx = readDoubleByte(packet, ACCELERATION_GX_LOW_BYTE, ACCELERATION_GX_HIGH_BYTE);
        int gy = readDoubleByte(packet, ACCELERATION_GY_LOW_BYTE, ACCELERATION_GZ_HIGH_BYTE);
        int gz = readDoubleByte(packet, ACCELERATION_GZ_LOW_BYTE, ACCELERATION_GZ_HIGH_BYTE);
        int zero = readByte(packet, ACCELERATION_ZERO_BYTE);
        if (zero != 0) {
            throw new Exception("Something went wrong; malformed acceleration sensor packet");
        }
        reading.updateAccelerationValues(gx, gy, gz);
    }

    public static void updateMagneticSensorReading(byte[] packet, CalibrationReading reading)
            throws Exception {
        int mx = readDoubleByte(packet, MAGNETIC_MX_LOW_BYTE, MAGNETIC_MX_HIGH_BYTE);
        int my = readDoubleByte(packet, MAGNETIC_MY_LOW_BYTE, MAGNETIC_MY_HIGH_BYTE);
        int mz = readDoubleByte(packet, MAGNETIC_MZ_LOW_BYTE, MAGNETIC_MZ_HIGH_BYTE);
            int zero = readByte(packet, MAGNETIC_ZERO_BYTE);
            if (zero != 0) {
                throw new Exception("Something went wrong; malformed magnetic sensor packet");
            }
        reading.updateMagneticValues(mx, my, mz);
    }

}
