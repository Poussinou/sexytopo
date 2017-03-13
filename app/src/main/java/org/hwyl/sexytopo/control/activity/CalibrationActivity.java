package org.hwyl.sexytopo.control.activity;

import android.os.Bundle;
import android.util.Pair;
import android.view.View;

import org.hwyl.sexytopo.R;
import org.hwyl.sexytopo.comms.DistoXCommunicator;

import java.util.ArrayList;
import java.util.List;


public class CalibrationActivity extends SexyTopoActivity {

    public static double MAX_ERROR = 0.5;

    private enum Direction {
        FORWARD(R.string.direction_forward),
        RIGHT(R.string.direction_right),
        BACK(R.string.direction_back),
        LEFT(R.string.direction_left),
        UP(R.string.direction_up),
        DOWN(R.string.direction_down),
        FORWARD_LEFT_UP(R.string.direction_forward_left_up),
        FORWARD_LEFT_DOWN(R.string.direction_forward_left_down),
        FORWARD_RIGHT_UP(R.string.direction_forward_right_up),
        FORWARD_RIGHT_DOWN(R.string.direction_forward_right_down),
        BACK_RIGHT_UP(R.string.direction_back_right_up),
        BACK_RIGHT_DOWN(R.string.direction_back_right_down),
        BACK_LEFT_UP(R.string.direction_back_left_up),
        BACK_LEFT_DOWN(R.string.direction_back_left_down);

        int stringId;

        Direction(int stringId) {
            this.stringId = stringId;
        }
    }

    private enum Orientation {
        FACE_UP(R.string.orientation_face_up),
        FACE_RIGHT(R.string.orientation_face_right),
        FACE_DOWN(R.string.orientation_face_down),
        FACE_LEFT(R.string.orientation_face_left);

        int stringId;

        Orientation(int stringId) {
            this.stringId = stringId;
        }
    }

    private static final List<Pair<Direction, Orientation>> positions = new ArrayList<>();

    static {
        setUpPositions();
    }

    private static void setUpPositions() {
        for (Direction direction : Direction.values()) {
            for (Orientation orientation : Orientation.values()) {
                Pair<Direction, Orientation> position = new Pair<>(direction, orientation);
                positions.add(position);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibration);
    }

    public void requestStartCalibration(View view) {
        try {
            DistoXCommunicator comms = DistoXCommunicator.getInstance(this, dataManager);
            comms.startCalibration();
        } catch (Exception exception) {
            showSimpleToast("Error starting calibration: " + exception);
        }
    }


    public void requestStopCalibration(View view) {
        try {
            DistoXCommunicator comms = DistoXCommunicator.getInstance(this, dataManager);
            comms.stopCalibration();
        } catch (Exception exception) {
            showSimpleToast("Error starting calibration: " + exception);
        }
    }


}
