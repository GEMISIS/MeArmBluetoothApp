package gemisis.mearmbluetoothapp;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

    public enum Commands
    {
        MOVE_FORWARD,
        MOVE_BACK,
        ROTATE_LEFT,
        ROTATE_RIGHT,
        GRAB,
        RELEASE
    }

    public final int BLUETOOTH_REQUEST = 42;

    private BluetoothAdapter bluetoothAdapter;
    private State state = State.DEACTIVATED;

    private BluetoothDevice armDevice = null;

    private BluetoothSocket socket = null;

    private InputStream inputStream = null;
    private OutputStream outputStream = null;

    private Activity activity;

    public BluetoothController(Activity activity){
        this.activity = activity;
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
            Log.i("DEVICE", device.getName());
            if(device.getName().equals("ME_ARM"))
            {
                this.armDevice = device;
                this.CreateSocket();
                return;
            }
        }
        Toast.makeText(this.activity, "Could not find MeArm!", Toast.LENGTH_LONG).show();
    }

    private void CreateSocket()
    {
        if(this.armDevice != null)
        {
            this.bluetoothAdapter.cancelDiscovery();
            try
            {
                this.socket = armDevice.createInsecureRfcommSocketToServiceRecord(armDevice.getUuids()[0].getUuid());
                this.socket.connect();

                this.inputStream = this.socket.getInputStream();
                this.outputStream = this.socket.getOutputStream();
            }
            catch(IOException e) {
                Toast.makeText(this.activity, "Could not connect to MeArm!", Toast.LENGTH_LONG).show();
                Toast.makeText(this.activity, e.getMessage(), Toast.LENGTH_LONG).show();
                this.Disconnect();
            }
        }
    }

    public void SendCommand(Commands command)
    {
        if(this.outputStream != null)
        {
            byte[] data = null;
            switch(command)
            {
                case MOVE_FORWARD:
                    data = ("forward:1\n").getBytes();
                    break;
                case MOVE_BACK:
                    data = ("back:1\n").getBytes();
                    break;
                case ROTATE_LEFT:
                    data = ("rleft:1\n").getBytes();
                    break;
                case ROTATE_RIGHT:
                    data = ("rright:1\n").getBytes();
                    break;
                case GRAB:
                    data = ("grab:1\n").getBytes();
                    break;
                case RELEASE:
                    data = ("release:1\n").getBytes();
                    break;
            }
            try
            {
                this.outputStream.write(data);
            }
            catch(IOException e)
            {
            }
        }
    }

    private void Disconnect()
    {
        try
        {
            this.socket.close();
            this.inputStream = null;
            this.outputStream = null;
        }
        catch(IOException e) {
        }
    }

}
