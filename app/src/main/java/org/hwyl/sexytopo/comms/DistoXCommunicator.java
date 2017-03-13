package org.hwyl.sexytopo.comms;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

import org.hwyl.sexytopo.R;
import org.hwyl.sexytopo.SexyTopo;
import org.hwyl.sexytopo.control.Log;
import org.hwyl.sexytopo.control.SurveyManager;
import org.hwyl.sexytopo.model.survey.Leg;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hwyl.sexytopo.control.activity.DeviceActivity.DISTO_X_PREFIX;


public class DistoXCommunicator extends Thread {

    private static final int POLLING_FREQUENCY = 5 * 1000;
    private static final int INTER_PACKET_DELAY = 1 * 100;

    private static final BluetoothAdapter BLUETOOTH_ADAPTER = BluetoothAdapter.getDefaultAdapter();

    private SurveyManager surveyManager;
    private BluetoothDevice bluetoothDevice;
    private Context context;

    private BluetoothSocket socket;

    private boolean isAlive = true;

    byte[] previousPacket = null;

    private static DistoXCommunicator instance = null;

    private DistoXCommunicator(Context context, SurveyManager surveyManager) {
        this.context = context;
        this.surveyManager = surveyManager;
        this.bluetoothDevice = getDistoX();
    }

    public static synchronized DistoXCommunicator getInstance(
            Context context, SurveyManager dataManager) {
        if (instance == null) {
            instance = new DistoXCommunicator(context, dataManager);
        }
        return instance;
    }


    @Override
    public void run() {

        while(isAlive) {
            try {

                if (!connectIfNotConnected()) {
                    sleep(POLLING_FREQUENCY);
                    continue;
                }

                List<Leg> legs = slurpAllData(socket);
                surveyManager.updateSurvey(legs);

                sleep(POLLING_FREQUENCY);

            } catch (Exception e) {
                Log.device("General error: " + e);
                disconnect();
            }
        }


    }

    public void kill() {
        isAlive = false;
        disconnect();
    }


    private boolean connectIfNotConnected() {

        if (socket == null || !socket.isConnected()) {
            try {
                Log.device(context.getString(R.string.device_log_connecting));
                socket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(SexyTopo.DISTO_X_UUID);

                socket.connect(); // blocks until connection is complete or fails with an exception
            } catch(Exception exception) {
                if (exception.getMessage().contains("socket might closed or timeout")) {
                    try {
                        Log.device(context.getString(R.string.device_trying_fallback));
                        socket = createFallbackSocket();
                        socket.connect();
                    } catch (Exception e) {
                        Log.device("Failed to create fallback socket: " + e.getMessage());
                    }
                } else {
                    Log.device("Error connecting: " + exception.getMessage());
                }
            } finally {
                if (socket.isConnected()) {
                    Log.device(context.getString(R.string.device_log_connected));
                } else {
                    Log.device(context.getString(R.string.device_log_not_connected));
                }
            }
        }

        return socket.isConnected();
    }

    private BluetoothSocket createFallbackSocket() throws Exception {
        BluetoothSocket socket = (BluetoothSocket)
                bluetoothDevice.getClass()
                        .getMethod("createRfcommSocket", new Class[]{int.class})
                        .invoke(bluetoothDevice, 1);
        return socket;
    }

    private void disconnect() {
        try {
            if (socket != null && socket.isConnected()) {
                socket.close();
            }
        } catch (Exception e) {
            Log.device("Error disconnecting: " + e.getMessage());
        }
    }



    public List<Leg> slurpAllData(BluetoothSocket socket) {

        Log.device(context.getString(R.string.device_log_waiting));

        List<Leg> legs = new ArrayList<>();

        DataInputStream inStream = null;
        DataOutputStream outStream = null;


        try {

            inStream = new DataInputStream(socket.getInputStream());
            outStream = new DataOutputStream(socket.getOutputStream());

            while (isAlive) {
                byte[] packet = new byte[8];
                inStream.readFully(packet, 0, 8);

                Log.d("Received data: " + MeasurementProtocol.describeDataPacket(packet));

                byte type = (byte) (packet[0] & 0x3f);

                byte[] acknowledgePacket = MeasurementProtocol.createAcknowledgementPacket(packet);
                outStream.write(acknowledgePacket, 0, acknowledgePacket.length);
                Log.d("Sent Ack: " + MeasurementProtocol.describeAcknowledgementPacket(acknowledgePacket));


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
                    Leg leg = MeasurementProtocol.parseDataPacket(packet);
                    legs.add(leg);
                    previousPacket = packet;
                    continue;
                }

                //pauseForDistoXToCatchUp();
            }

        } catch (IOException e) {
            if (e.getMessage().toLowerCase().contains("bt socket closed")) {
                //Log.device("Connection closed"); not sure if we need to bother the user with this..
                disconnect();
            } else {
                Log.device("Communication error: " + e.getMessage());
                disconnect();
            }

        } catch (Exception e) {
            Log.device("General error: " + e);
            disconnect();

        } finally {
            try {
                inStream.close();
                outStream.close();
            } catch (Exception e) {
                // ignore any errors; they are expected if the socket has been closed
            }

            // whatever happens, return the precious data :)
            return legs;
        }
    }

    private void pauseForDistoXToCatchUp() throws InterruptedException {
        sleep(INTER_PACKET_DELAY);
    }

    private boolean arePacketsTheSame(byte[] packet0, byte[] packet1) {

        if (packet0 == null ^ packet1 == null) {
            return false;
        }

        for (int i = 0; i < packet0.length; i++) {
            if (packet0[i] != packet1[i]) {
                return false;
            }
        }

        return true;
    }


    private void writeCommandPacket(byte[] packet) throws IOException, Exception {
        final int ATTEMPTS = 3;
        for (int i = 0; i < ATTEMPTS; i++) {
            boolean isConnected = connectIfNotConnected();
            if (!isConnected) {
                continue;
            }
            DataOutputStream outStream = new DataOutputStream(socket.getOutputStream());
            outStream.write(packet, 0, packet.length);
            return;
        }
        throw new Exception("Can't connect");
    }


    public void startCalibration() throws Exception {
        Log.device("Starting calibration");
        byte[] packet = CalibrationProtocol.getStartCalibrationPacket();
        writeCommandPacket(packet);

    }


    public void stopCalibration() throws Exception {
        byte[] packet = CalibrationProtocol.getStopCalibrationPacket();
        writeCommandPacket(packet);
    }





    private static BluetoothDevice getDistoX() {
        Set<BluetoothDevice> distoXes = getPairedDistos();

        if (distoXes.size() != 1) {
            throw new IllegalStateException(distoXes.size() + " DistoXes paired");
        }

        return distoXes.toArray(new BluetoothDevice[]{})[0];
    }


    private static Set<BluetoothDevice> getPairedDistos() {

        if (BLUETOOTH_ADAPTER == null) {
            return new HashSet<>(0);
        }

        Set<BluetoothDevice> pairedDistoXes = new HashSet<>();
        Set<BluetoothDevice> pairedDevices = BLUETOOTH_ADAPTER.getBondedDevices();
        for (BluetoothDevice device : pairedDevices) {
            if (isDistoX(device)) {
                pairedDistoXes.add(device);
            }
        }

        return pairedDistoXes;
    }

    private static boolean isDistoX(BluetoothDevice device) {
        String name = device.getName();
        return name.toLowerCase().contains(DISTO_X_PREFIX.toLowerCase());
    }

}
