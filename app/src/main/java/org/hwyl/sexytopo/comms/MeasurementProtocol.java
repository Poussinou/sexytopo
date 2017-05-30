package org.hwyl.sexytopo.comms;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import org.hwyl.sexytopo.R;
import org.hwyl.sexytopo.control.DataManager;
import org.hwyl.sexytopo.control.Log;
import org.hwyl.sexytopo.model.survey.Leg;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public class MeasurementProtocol extends DistoXProtocol {

    private static final int DISTANCE_LOW_BYTE = 1;
    private static final int DISTANCE_HIGH_BYTE = 2;
    private static final int AZIMUTH_LOW_BYTE = 3;
    private static final int AZIMUTH_HIGH_BYTE = 4;
    private static final int INCLINATION_LOW_BYTE = 5;
    private static final int INCLINATION_HIGH_BYTE = 6;
    private static final int ROLL_ANGLE_HIGH_BYTE = 7;


    public MeasurementProtocol(
            Context context, BluetoothDevice bluetoothDevice, DataManager dataManager) {
        super(context, bluetoothDevice, dataManager);
    }


    public static Leg parseDataPacket(byte[] packet) {

        int d0 = packet[ADMIN] & 0x40;
        int d1 = readByte(packet, DISTANCE_LOW_BYTE);
        int d2 = readByte(packet, DISTANCE_HIGH_BYTE);
        double distance = (d0 * 1024 + d2 * 256 + d1) / 1000.0;

        double azimuth_reading =
                readDoubleByte(packet, AZIMUTH_LOW_BYTE, AZIMUTH_HIGH_BYTE);
        double azimuth = azimuth_reading * 180.0 / 32768.0;

        double inclinationReading =
                readDoubleByte(packet, INCLINATION_LOW_BYTE, INCLINATION_HIGH_BYTE);
        double inclination = inclinationReading * 90.0 / 16384.0;
        if (inclinationReading >= 32768) {
            inclination = (65536 - inclinationReading) * -90.0 / 16384.0;
        }

        Leg leg = new Leg(distance, azimuth, inclination);
        return leg;
    }

    public static String describePacket(byte[] packet) {
        String description = "[";
        for (int i = 0; i < packet.length; i++) {
            if (i == ADMIN) {
                description += Integer.toBinaryString(packet[i] & 0xFF);
            } else {
                description += ", " + packet[i];
            }
        }
        description += "]";
        return description;
    }

    public void go(DataInputStream inStream, DataOutputStream outStream)
            throws IOException, Exception {

        while (isActive()) {

            byte[] packet = readPacket(inStream);

            Log.d("Received data: " + MeasurementProtocol.describePacket(packet));

            byte type = (byte) (packet[0] & 0x3f);

            acknowledge(outStream, packet);


            if ((packet[0] & 0x03) == 0) {
                // Think this means the acknowledgment has been accepted
                break;
            } else if (type != 0x01) {
                Log.device("Unexpected data type");
                continue;
            }

            if (arePacketsTheSame(packet, previousPacket)) {
                continue;

            } else {
                Log.device(context.getString(R.string.device_log_received));
                Leg leg = parseDataPacket(packet);
                dataManager.updateSurvey(leg);
                previousPacket = packet;
                continue;
            }
        }

    }


    public static boolean isDataPacket(byte[] dataPacket) {
        return ((dataPacket[ADMIN] & 0x3F) == 1);
    }


}
