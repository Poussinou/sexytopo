package org.hwyl.sexytopo.comms;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;

import org.hwyl.sexytopo.control.DataManager;
import org.hwyl.sexytopo.control.Log;
import org.hwyl.sexytopo.control.calibration.fromtopodroid.CalibCBlock;

import java.util.HashSet;
import java.util.Set;

import static org.hwyl.sexytopo.control.activity.DeviceActivity.DISTO_X_PREFIX;


public class DistoXCommunicator {


    private static final BluetoothAdapter BLUETOOTH_ADAPTER = BluetoothAdapter.getDefaultAdapter();

    private DataManager dataManager;
    private Context context;


    private static DistoXCommunicator instance = null;


    private NullProtocol nullStrategy = new NullProtocol();
    private CalibrationProtocol calibrationStrategy;
    private MeasurementProtocol measurementStrategy;
    private DistoXProtocol currentStrategy = nullStrategy;


    private DistoXCommunicator(Context context, DataManager dataManager) {
        this.context = context;
        this.dataManager = dataManager;
        BluetoothDevice bluetoothDevice = getDistoX();

        this.calibrationStrategy = new CalibrationProtocol(context, bluetoothDevice, dataManager);
        this.measurementStrategy = new MeasurementProtocol(context, bluetoothDevice, dataManager);
    }

    public static synchronized DistoXCommunicator getInstance(
            Context context, DataManager dataManager) {
        if (instance == null) {
            instance = new DistoXCommunicator(context, dataManager);
        }
        return instance;
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



    public void startCalibration() throws Exception {

        if (currentStrategy.isActive() || currentStrategy.isAlive()) {
            currentStrategy.setActive(false);
            throw new Exception("Stopping active thread; please wait and try again");
        }

        Log.device("Starting calibration mode");

        CalibrationProtocol calibrationProtocol = new CalibrationProtocol(context, dataManager);
        currentStrategy = calibrationProtocol;

        calibrationProtocol.startNewCalibration();

        currentStrategy.start();
    }


    public void stopCalibration() throws Exception {
        if (currentStrategy instanceof CalibrationProtocol) {
            currentStrategy.setActive(false);
        }

    }


    public void startMeasuring() throws Exception {

        currentStrategy.setActive(false);

        if (isAlive()) {
            throw new Exception("Thread already started");
        }

        Log.device("Starting measurement mode");

        currentStrategy = measurementStrategy;
        currentStrategy.setActive(true);

        start();
    }

    public void stopMeasuring() throws Exception {
        currentStrategy.setActive(false);
        measurementStrategy.setActive(false);
    }

}
