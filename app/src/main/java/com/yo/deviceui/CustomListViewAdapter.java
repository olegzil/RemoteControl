package com.yo.deviceui;

/**
 * Created by oleg on 8/19/15.
 */

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yo.discoverymanagers.DiscoveryManager;
import com.yo.interfaces.DeviceUIInterface;
import com.yo.remotecontrol.R;

import java.util.ArrayList;
import java.util.Hashtable;

/********* Adapter class extends with BaseAdapter and implements with OnClickListener ************/
public class CustomListViewAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList data;
    private static LayoutInflater inflater=null;
    public Resources res;
    ListModel tempValues=null;
    int i=0;

    private static class ViewHolder{

        public TextView description;
        public TextView id;
        public ImageView image;

    }
    static public class ListModel {

        private  String description="";
        private  String image="";
        private  String id="";

        /*********** Set Methods ******************/

        public void setDescription(String description)
        {
            this.description = description;
        }

        public void setImage(String image)
        {
            this.image = image;
        }

        public void setId(String id)
        {
            this.id = id;
        }

        /*********** Get Methods ****************/

        public String getDescription()
        {
            return this.description;
        }

        public String getImage()
        {
            return this.image;
        }

        public String getId()
        {
            return this.id;
        }
    }
    public CustomListViewAdapter(Activity a, ArrayList d) {

        /********** Take passed values **********/
        activity = a;
        data=d;

        /***********  Layout inflator to call external xml layout () ***********/
        inflater = ( LayoutInflater )activity.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {

        if(data.size()<=0)
            return 1;
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        View detailView = convertView;
        ViewHolder holder;

        if(convertView==null){

            /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
            detailView = inflater.inflate(R.layout.roku_app_detail, null);

            /****** View Holder Object to contain tabitem.xml file elements ******/

            holder = new ViewHolder();
            holder.description = (TextView) detailView.findViewById(R.id.text_app_description);
            holder.id=(TextView)detailView.findViewById(R.id.text_app_id);
            holder.image=(ImageView)detailView.findViewById(R.id.image_app_icon);

            /************  Set holder with LayoutInflater ************/
            detailView.setTag(holder);
        }
        else
            holder=(ViewHolder)detailView.getTag();

        if(data.size()<=0)
        {
            holder.description.setText("No Data");

        }
        else {
            /***** Get each Model object from Arraylist ********/
            tempValues = null;
            tempValues = (ListModel) data.get(position);

            /************  Set Model values in Holder elements ***********/

            holder.description.setText(tempValues.getDescription());
            holder.id.setText(tempValues.getId());
//            holder.image.setImageResource(
//                    res.getIdentifier(
//                            "com.androidexample.customlistview:drawable/"+tempValues.getImage()
//                            ,null,null));

        }
        return detailView;
    }

    /**
     * Created by oleg on 8/24/15.
     */
}
