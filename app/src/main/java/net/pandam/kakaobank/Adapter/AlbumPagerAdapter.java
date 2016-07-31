package net.pandam.kakaobank.adapter;

import android.content.Context;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import net.pandam.kakaobank.R;
import net.pandam.kakaobank.module.PhotosInfo;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Pandam on 16. 7. 30..
 */
public class AlbumPagerAdapter extends RecyclerView.Adapter<AlbumPagerAdapter.ViewHolder> {
    private ArrayList<PhotosInfo> mdataSet;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView ivPhoto;
        public TextView tvText;
        private AQuery aq;
        private Context context;

        public ViewHolder(View view) {
            super(view);
            aq = new AQuery(view);
            context = view.getContext();
            ivPhoto = (ImageView)view.findViewById(R.id.ivPhoto);
            tvText = (TextView)view.findViewById(R.id.tvText);

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public AlbumPagerAdapter(ArrayList<PhotosInfo> dataSet) {
        mdataSet = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public AlbumPagerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_photos, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final AQuery aq = holder.aq;
        File fileImage = new File(Environment.getExternalStorageDirectory() + "/kakaobank/" + mdataSet.get(position).thumbnail);
        holder.aq.id(holder.tvText).text(mdataSet.get(position).title);
        holder.aq.id(holder.ivPhoto).image(fileImage, 155);
        final Context context = holder.context;
        final String downloadurl = mdataSet.get(position).image;

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mdataSet.size();
    }
}
