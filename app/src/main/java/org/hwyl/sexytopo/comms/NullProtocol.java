package org.hwyl.sexytopo.comms;

import org.hwyl.sexytopo.control.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;


public class NullProtocol extends DistoXProtocol {

    @Override
    public void go(DataInputStream inStream, DataOutputStream outStream) {
        Log.e("Error: tried to run Null Strategy in DistoX communication loop");
    }
}
