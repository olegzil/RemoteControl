package com.yo.directtvdevices;

import com.yo.interfaces.ConnectedDeviceInterface;
import com.yo.interfaces.ResultNotificationInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Created by oleg on 8/12/15.
 */
public class DirectTVServerQuery{
    private static enum QueryState {STATE_CHANNELUP, STATE_CHANNELDOWN, STATE_QUERY, STATE_ONSUCCESS, STATE_GETINFO};
    private ConnectedDeviceInterface mDevice;

    private interface ChannelModifyInterface
    {
        void execute(ResultNotificationInterface rni, int state);
    }

    private class ChannelUp implements  ChannelModifyInterface
    {
        @Override
        public void execute(ResultNotificationInterface rni, int state) {
            mDevice.execute(DirectTVBaseController.CHANNELUP, null, rni, state);
        }
    }
    private class ChannelDown implements ChannelModifyInterface
    {
        @Override
        public void execute(ResultNotificationInterface rni, int state) {
            mDevice.execute(DirectTVBaseController.CHANNELDOWN, null, rni, state);
        }
    }

    private class ChannelControl implements  ResultNotificationInterface{
        private ResultNotificationInterface callback;
        private ChannelModifyInterface mChannelModify;
        private final int MAX_RETRY_COUNT=5;
        private int mRetryCount = 0;
        public ChannelControl(final ResultNotificationInterface c, ChannelModifyInterface cmi)
        {
            callback = c;
            mChannelModify =cmi;
        }

        /**
         * This method is called in response to an error condition raized by a call to the DirectTV server.
         * The problem is that accessing the DirectTV server to modify a channel does not produce consistant results.
         * For example, going from channel 5 to 6 and then querying the channel number may cause the server to report a FileNotFound error. But going back from
         * 6 to 5 will work fine and then from 5 to 6 again will work fine. I beleive it is a timing issue, i.e., the server cannot respond to a command while it is
         * in the midst of executing a command. As a work-around, I've added a one second delay and a retry count. From empirical data, the retry is never taken
         * more then twice. If, after the max number of retries we still get an error condition, then the error is propagated to the caller.
         * @param data -- JSON data returned by the server.
         */
        @Override
        public void onError(JSONObject data) {
            try {
                mRetryCount++;
                int state = data.getInt("user-data");
                if (mRetryCount <= MAX_RETRY_COUNT)
                {
                    try {
                        Thread.sleep(1000);
                        mDevice.execute(DirectTVBaseController.GETTUNED, null, this, state);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    mRetryCount = 0;
                    callback.onError(data);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onSuccess(final JSONObject data) {
            Long channel = new Long(0);
            try {
                int selector = data.getInt("user-data");
                data.put("user-data", -1);
                switch(selector)
                {
                    case 0:
                        channel = data.getLong("major");
                        data.put("debug-data", channel);
                        mChannelModify.execute(this, 1);
                        break;

                    case 1: //get the value of of the channel after it has been incremented. We cannot assume that the channel was actually changed by us.
                        mDevice.execute(DirectTVBaseController.GETTUNED, null, this, 2);
                        break;

                    case 2: //if we got here, all is well, return the new channel number to the original caller
                        channel = data.getLong("major");
                        data.put("debug-data", channel);
                        data.put("value", channel);
                        callback.onSuccess(data);
                        break;

                    case 3: //Occationaly, the DirectTV server responds with an error due to timing issues. This case is used to retry the command.
                        channel = data.getLong("major");
                        data.put("debug-data", channel);
                        data.put("value", channel);
                        callback.onSuccess(data);
                        break;

                    default: //Should never happen.
                        data.put("error", "Final state");
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void done() {

        }

    };

    private void getDeviceState(final ResultNotificationInterface callback){
        mDevice.execute(DirectTVBaseController.GETTUNED, null, new ResultNotificationInterface() {
            @Override
            public void onError(JSONObject data) {
                callback.onError(data); //forward the call to the caller of this method
            }

            @Override
            public void onSuccess(JSONObject serverData) {
                   callback.onSuccess(serverData);
            }

            @Override
            public void done() {}
        }, 0);
    }
    /**
     * Class that provides asynchronous calls to access the DirectTV server
     * @param device An instance of a DirectTV device that was craeted by the DiscoveryManager
     */
    public DirectTVServerQuery(ConnectedDeviceInterface device)
    {
        mDevice = device;
    }

    /**
     * This method reads the current channel tuned.
     * @param callback An instance of a callback object to recieve error and success calls.
     *                 In case of either success or failure, a Hashtable is returned. In case of success, the key is "channel" and the value is the channel number
     *                 In case of failure, the key is "error" and the value is whatever data returned by the server.
     */
    public void getCurrentChannel(final ResultNotificationInterface callback)
    {
        getDeviceState(new ResultNotificationInterface()
        {
            @Override
            public void onError(JSONObject data) {
                callback.onError(data);
            }

            @Override
            public void onSuccess(final JSONObject data)
            {
                final String channel;
                try {
                    channel = data.getString("major");
                    JSONObject retVal = new JSONObject();
                    retVal.put("value", channel);
                    callback.onSuccess(retVal);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void done() {

            }
        });
    }
    public void getCurrentProgramInfo(final ResultNotificationInterface callback)
    {
        getDeviceState(callback);
    }

    public  void nextChannel(final ResultNotificationInterface callback){
        getDeviceState(new ChannelControl(callback, new ChannelUp()));
    }
    public void prevChannel(final ResultNotificationInterface callback)
    {
        getDeviceState(new ChannelControl(callback, new ChannelDown()));
    }

    public void setChannel(int channel)
    {

    }
    public void getDeviceVersion(final ResultNotificationInterface callback)
    {
        mDevice.execute(DirectTVBaseController.GETVERSION, null, new ResultNotificationInterface() {
            @Override
            public void onError(JSONObject data) {
                callback.onError(data);
            }

            @Override
            public void onSuccess(JSONObject response) {
                try {
                    StringBuffer sb = new StringBuffer();
                    for (Iterator<String> iter=response.keys(); iter.hasNext();)
                    {
                        String key = iter.next();
                        String item = null;
                        try {
                            item = String.format("%s = %s\n", key, response.getString(key));
                            sb.append(item);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    JSONObject retVal = new JSONObject();
                    retVal.put("value", sb.toString());
                    callback.onSuccess(retVal);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void done() {

            }
        }, 0);
    }
    public void getConnectedClientList(final ResultNotificationInterface callback)
    {
        mDevice.execute(DirectTVBaseController.GETLOCATION, null, new ResultNotificationInterface() {
            @Override
            public void onError(JSONObject response) {
                callback.onError(response);
            }

            @Override
            public void onSuccess(JSONObject response) {
                StringBuffer sb = new StringBuffer();
                try {
                    JSONArray ja = response.getJSONArray("locations");
                    JSONObject retVal = new JSONObject();
                    for (int i=0; i<ja.length(); i++)
                    {
                        JSONObject item = (JSONObject)ja.get(i);
                        String str = String.format("address: %s    name: %s\n", item.getString("clientAddr"), item.getString("locationName"));
                        sb.append(str);
                    }
                    retVal.put("value", sb.toString());
                    callback.onSuccess(retVal);
                } catch (JSONException e) {
                }
            }

            @Override
            public void done() {

            }
        }, 0);
    }

    public void getSerialNumber(final ResultNotificationInterface callback)
    {
        mDevice.execute(DirectTVBaseController.SERIALNUMBER, null, new ResultNotificationInterface() {
            @Override
            public void onError(JSONObject data) {
                callback.onError(data);
            }

            @Override
            public void onSuccess(JSONObject response) {
                try {
                    final JSONObject retVal = new JSONObject();
                    retVal.put("value", response.getString("serialNum"));
                    callback.onSuccess(retVal);
                } catch (JSONException e) {
                }
            }

            @Override
            public void done() {}
        }, 0);
    }
}
