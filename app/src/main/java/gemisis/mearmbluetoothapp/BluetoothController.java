package gemisis.mearmbluetoothapp;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

/**
 * Created by GEMISIS on 5/23/2015.
 */
public class BluetoothController {

    public enum State
    {
        ACTIVE,
        DEACTIVATED,
        NO_SUPPORT
    }

    public final int BLUETOOTH_REQUEST = 42;

    private BluetoothAdapter bluetoothAdapter;
    private State state = State.DEACTIVATED;

    private BluetoothDevice armDevice = null;

    private BluetoothSocket socket = null;

    public BluetoothController(Activity activity){
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter == null)
        {
            this.state = State.NO_SUPPORT;
        }

        if(!bluetoothAdapter.isEnabled())
        {
            this.state = State.DEACTIVATED;
            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBluetoothIntent, BLUETOOTH_REQUEST);
        }
    }

    public void ConnectToArm()
    {
        Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
        for(BluetoothDevice device : devices)
        {
            if(device.getName() == "ME_ARM")
            {
                this.armDevice = device;
                this.CreateSocket();
                break;
            }
        }
    }

    private void CreateSocket()
    {
        if(this.armDevice != null)
        {
            try
            {
                this.socket = armDevice.createRfcommSocketToServiceRecord(UUID.fromString("519"));
                this.socket.connect();
            }
            catch(IOException e) {
                this.Disconnect();
            }
        }
    }

    private void Disconnect()
    {
        try
        {
            this.socket.close();
        }
        catch(IOException e) {
        }
    }

}
