package org.hwyl.sexytopo.comms;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

import org.hwyl.sexytopo.R;
import org.hwyl.sexytopo.SexyTopo;
import org.hwyl.sexytopo.control.DataManager;
import org.hwyl.sexytopo.control.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public abstract class DistoXProtocol extends Thread {

    private static final int INTER_PACKET_DELAY = 1 * 12; // ms; DISTO repeats every 25 ms


    public static final int ADMIN = 0;
    public static final int SEQUENCE_BIT_MASK = 0x80;
    public static final int ACKNOWLEDGEMENT_PACKET_BASE = 0x55;

    protected Context context;
    private BluetoothDevice bluetoothDevice;

    protected DataManager dataManager;

    protected byte[] previousPacket = new byte[]{};

    protected boolean isActive = false;

    private BluetoothSocket socket;

    protected DistoXProtocol(
            Context context, BluetoothDevice bluetoothDevice, DataManager dataManager) {
        this.context = context;
        this.bluetoothDevice = bluetoothDevice;
        this.dataManager = dataManager;
    }


    private void pauseForDistoXToCatchUp() throws InterruptedException {
        sleep(INTER_PACKET_DELAY);
    }


    /**
     * An acknowledgement packet consists of a single byte; bits 0-7 are 1010101 and bit 7 is the
     * same as the sequence bit of the packet being acknowledged.
     */
    public static byte[] createAcknowledgementPacket(byte[] packet) {
        byte sequenceBit = (byte)(packet[ADMIN] & SEQUENCE_BIT_MASK);
        byte[] acknowledgePacket = new byte[1];
        acknowledgePacket[0] = (byte)(sequenceBit | ACKNOWLEDGEMENT_PACKET_BASE);
        return acknowledgePacket;
    }

    protected void acknowledge(DataOutputStream outStream, byte[] packet) throws IOException {
        byte[] acknowledgePacket = createAcknowledgementPacket(packet);
        outStream.write(acknowledgePacket, 0, acknowledgePacket.length);
        Log.d("Sent Ack: " + describeAcknowledgementPacket(acknowledgePacket));
    }


    protected void writeCommandPacket(byte[] packet) throws IOException, Exception {
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

        throw new Exception("Couldn't send command");
    }


    protected static int readByte(byte[] packet, int index) {
        int value = packet[index];
        int data = value & 0xff;
        if (data < 0) {
            data += 2^8;
        }
        return data;
    }


    protected static int readDoubleByte(byte[] packet, int lowByteIndex, int highByteIndex) {
        int low = readByte(packet, lowByteIndex);
        int high = readByte(packet, highByteIndex);
        return (high * 2^8) + low;
    }


    public void run() {

        isActive = true;

        while (isActive) {

            try {

                connectIfNotConnected();

                DataInputStream inStream = new DataInputStream(socket.getInputStream());
                DataOutputStream outStream = new DataOutputStream(socket.getOutputStream());

                Log.device(context.getString(R.string.device_log_waiting));

                go(inStream, outStream);
                //pauseForDistoXToCatchUp();

            } catch(IOException e){
                if (e.getMessage().toLowerCase().contains("bt socket closed")) {
                    // this is common; not sure if we need to bother the user with this..
                    Log.d("\"bt socket closed\" error; disconnecting...");
                    disconnect();
                } else {
                    Log.device("Communication error: " + e.getMessage());
                    disconnect();
                }

            } catch(Exception exception){
                Log.device("Error in main DistoXCommunicator loop: " + exception.getMessage());
                disconnect();
            }
        }
    }


    public abstract void go(DataInputStream inStream, DataOutputStream outStream)
            throws IOException, Exception;

    public boolean connectIfNotConnected() {

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


    public void disconnect() {
        try {
            if (socket != null && socket.isConnected()) {
                socket.close();
            }
        } catch (Exception e) {
            Log.device("Error disconnecting: " + e.getMessage());
        }
    }


    public boolean isActive() {
        return isActive;
    }


    public void setActive(boolean alive) {
        isActive = alive;
    }


    protected byte[] readPacket(DataInputStream inStream) throws IOException {
        byte[] packet = new byte[8];
        inStream.readFully(packet, 0, 8);
        return packet;
    }

    public static boolean arePacketsTheSame(byte[] packet0, byte[] packet1) {

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


    public static String describeAcknowledgementPacket(byte[] acknowledgementPacket) {
        return "[" + Integer.toBinaryString(acknowledgementPacket[0] & 0xFF) + "]";
    }


}
