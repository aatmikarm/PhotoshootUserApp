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

public class proDetail_recycler_view_Adapter extends RecyclerView.Adapter<proDetail_recycler_view_Adapter.ViewHolder> {

    private ArrayList<proDetail_RV_Model> proDetail_RV_Model;
    private Context context;
   // private LayoutInflater mInflater;
   // private ItemClickListener mClickListener;


    // data is passed into the constructor
    public proDetail_recycler_view_Adapter(Context context, ArrayList<proDetail_RV_Model> proDetail_RV_Model) {
        //this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.proDetail_RV_Model = proDetail_RV_Model;
    }

    // inflates the cell layout from xml when needed
    @Override
    public proDetail_recycler_view_Adapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.pro_detail_rv_image_item, viewGroup, false);

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

        Glide.with(context).load(proDetail_RV_Model.get(position).getImageUrl()).into(viewHolder.img);


//        viewHolder.img.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //Toast.makeText(context, "url = "+proRecyclerViewListModels.get(position).getImageUrl(), Toast.LENGTH_LONG).show();
//                //add(position , proRecyclerViewListModels.get(1));
//                //remove(position);
//               Intent intent = new Intent(context, profile.class);
//               intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//               intent.putExtra("proDetailUid",proDetail_RV_Model.get(position).getUid());
//               context.startActivity(intent);
//
//
//            }
//        });

    }

    // total number of cells
    @Override
    public int getItemCount() {
        return proDetail_RV_Model.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView img;

        ViewHolder(View view) {
            super(view);
            img = view.findViewById(R.id.pro_detail_rv_image_item);
        }
    }


}