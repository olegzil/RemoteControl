package com.yo.discoverymanagers;


import com.yo.interfaces.ConnectedDeviceAvailableInterface;
import com.yo.interfaces.ConnectedDeviceInterface;
import com.yo.interfaces.DeviceFactoryInterface;
import com.yo.interfaces.DeviceIdentificationInterface;
import com.yo.interfaces.DiscoveryInterface;
import com.yo.interfaces.ResultNotificationInterface;
import com.yo.interfaces.UIThreadAccessInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Hashtable;

public class DiscoveryAssistNetwork implements DiscoveryInterface {
	private java.net.MulticastSocket mMultiCastSocket;
    private int mUDPPort = 1900;
	private Thread mMulticastListenerThread;
	private String mDiscoveryRequest;
	private DeviceIdentificationInterface mIdentify;
	private DeviceFactoryInterface mCreate;
	private ConnectedDeviceAvailableInterface mCallback;
    private UIThreadAccessInterface mUIAccess;
    private boolean mContinueLooping = true;
	public DiscoveryAssistNetwork(String whatToLookFor, DeviceIdentificationInterface devId, DeviceFactoryInterface devInst)
	{
		mDiscoveryRequest = whatToLookFor;
		mIdentify = devId;
		mCreate = devInst;
	}

	@Override
	public boolean Start(ConnectedDeviceAvailableInterface callback, UIThreadAccessInterface uiaccess, final ResultNotificationInterface rni) {
		mCallback = callback;
        mUIAccess = uiaccess;
        mContinueLooping = true;
        mMulticastListenerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                discoverDevices(rni);
            }
        });
        mMulticastListenerThread.start();
        return true;
	}

	@Override
	public boolean Stop() {
        if (mMulticastListenerThread != null && mMulticastListenerThread.isAlive()) {
            mMulticastListenerThread.interrupt();
            mMulticastListenerThread = null;
            return true;
        }
        mContinueLooping = false;
        return false;
	}

    private void test(boolean flag)
    {
        boolean b = false;
        b = flag;
    }
    private void discoverDevices(final ResultNotificationInterface resultNotifier)
    {
        MulticastSocket sendSocket = null;
        MulticastSocket receiveSoket = null;
        String response = null;
        DatagramSocket clientSocket = null;
        String groupTag = "239.255.255.250";
        try {
       /* create byte arrays to hold our send and response data */
            byte[] sendData = new byte[1024];
            byte[] receiveData = new byte[1024];

            sendData = mDiscoveryRequest.getBytes();

            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(groupTag), mUDPPort);

            clientSocket = new DatagramSocket();
            clientSocket.send(sendPacket);

            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);

            response = new String(receivePacket.getData());

            boolean again = true;
            int count = 5;
            while (again && count != 0) {
                String test = response.toLowerCase();
                if ((test.contains("roku") || test.contains("directv")))
                    again = false;
                else
                {
                    clientSocket.send(sendPacket);
                    clientSocket.receive(receivePacket);
                    response = new String(receivePacket.getData());
                }
                --count;
            }

            clientSocket.close();
            clientSocket.disconnect();
            /***************************************************************************************/
            if (response == null)
                return;
            Hashtable<String, String> ret = mIdentify.onDeviceIdentify(response);
            if (ret == null) {
                postResultError(resultNotifier, "device not responding to discovery request");
                return;
            }

            final Hashtable<String, String> retVal = ret;
            mUIAccess.Post(new Runnable() {
                @Override
                public void run() {
                    ConnectedDeviceInterface device = mCreate.onCreate(retVal);
                    if (device != null) {
                        Long key = mCallback.onDeviceFound(device);
                        postResultSuccess(resultNotifier, Long.toString(key));
                    }
                }
            });
            /***************************************************************************************/
        } catch (SocketException e) {
            postResultError(resultNotifier, e.getMessage());
            clientSocket.close();
            clientSocket.disconnect();
        } catch (UnknownHostException e) {
            postResultError(resultNotifier, e.getMessage());
            clientSocket.close();
            clientSocket.disconnect();
        } catch (IOException e) {
            postResultError(resultNotifier, e.getMessage());
            clientSocket.close();
            clientSocket.disconnect();
        }
    }

        private void postResultSuccess(final ResultNotificationInterface srh, final String data) {
            UIThreadAccessInterface uiAccess = DiscoveryManager.getInstance().getUIThreadAccess();
            uiAccess.Post(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (srh == null)
                            return;
                        JSONObject payload = new JSONObject();
                        payload.put("device", data);
                        srh.onSuccess(payload);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        private void postResultError(final ResultNotificationInterface srh, final String data) {
            UIThreadAccessInterface uiAccess = DiscoveryManager.getInstance().getUIThreadAccess();
            uiAccess.Post(new Runnable() {
                @Override
                public void run() {
                    JSONObject payload = new JSONObject();
                    try {
                        payload.put("error", data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (srh != null)
                        srh.onError(payload);
                }
            });
        }
    }
