package activity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.test1photographerapp.R;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class user_upload_grid_pics_recycler_view_Adapter extends RecyclerView.Adapter<user_upload_grid_pics_recycler_view_Adapter.ViewHolder> {

    private ArrayList<userRecyclerViewListModel> userRecyclerViewListModels;
    private Context context;
   // private LayoutInflater mInflater;
   // private ItemClickListener mClickListener;


    // data is passed into the constructor
    public user_upload_grid_pics_recycler_view_Adapter(Context context, ArrayList<userRecyclerViewListModel> userRecyclerViewListModels) {
        //this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.userRecyclerViewListModels = userRecyclerViewListModels;
    }

    // inflates the cell layout from xml when needed
    @Override
    public user_upload_grid_pics_recycler_view_Adapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_upload_grid_pics_recycler_view_item, viewGroup, false);

        return new ViewHolder(view);
    }

    /**
     * gets the image url from adapter and passes to Glide API to load the image
     *
     * @param viewHolder
     * @param position
     */
    // binds the data to the TextView in each cell
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        Glide.with(context).load(userRecyclerViewListModels.get(position).getImageUrl()).into(viewHolder.img);

        viewHolder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, "url = "+proRecyclerViewListModels.get(position).getImageUrl(), Toast.LENGTH_LONG).show();
                //add(position , proRecyclerViewListModels.get(1));
                //remove(position);
               Intent intent = new Intent(context, post.class);
               intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
               intent.putExtra("postUrl", userRecyclerViewListModels.get(position).getImageUrl());
               intent.putExtra("postUid",userRecyclerViewListModels.get(position).getUid());
               intent.putExtra("postDateandTime",userRecyclerViewListModels.get(position).getCurrentDateandTime());
               context.startActivity(intent);


            }
        });

    }

    public void add(int position, userRecyclerViewListModel item) {
        userRecyclerViewListModels.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        userRecyclerViewListModels.remove(position);
        notifyItemRemoved(position);
    }

    // total number of cells
    @Override
    public int getItemCount() {
        return userRecyclerViewListModels.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView img;

        ViewHolder(View view) {
            super(view);
            img = view.findViewById(R.id.icon);
        }
    }


}