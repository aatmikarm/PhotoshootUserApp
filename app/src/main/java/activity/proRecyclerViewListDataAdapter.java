package activity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.test1photographerapp.R;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class proRecyclerViewListDataAdapter extends RecyclerView.Adapter<proRecyclerViewListDataAdapter.ViewHolder> {




    private ArrayList<proRecyclerViewListModel> proRecyclerViewListModels;
    private Context context;




    public proRecyclerViewListDataAdapter(Context context, ArrayList<proRecyclerViewListModel> proRecyclerViewListModels) {
        this.context = context;
        this.proRecyclerViewListModels = proRecyclerViewListModels;
    }




    @Override
    public proRecyclerViewListDataAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.photographeritemlist, viewGroup, false);
        return new ViewHolder(view);
    }




    /**
     * gets the image url from adapter and passes to Glide API to load the image
     *
     * @param viewHolder
     * @param position
     */




    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        Glide.with(context).load(proRecyclerViewListModels.get(position).getImageUrl()).into(viewHolder.img);

        viewHolder.iconName.setText(proRecyclerViewListModels.get(position).getImageName());

        viewHolder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                MainActivity.proDetailFragmentManager.popBackStack();
                proFullDeatilFrag proFullDeatilFrag = new proFullDeatilFrag();

                Bundle bundle=new Bundle();
                bundle.putString("proName",proRecyclerViewListModels.get(position).getImageName());
                bundle.putString("proUid",proRecyclerViewListModels.get(position).getUid());
                bundle.putString("proPic",proRecyclerViewListModels.get(position).getImageUrl());
                bundle.putString("proDistance",proRecyclerViewListModels.get(position).getDistance());
                proFullDeatilFrag.setArguments(bundle);
                MainActivity.proDetailFragmentManager.beginTransaction().replace(R.id.pro_profile_Detail_frag_container, proFullDeatilFrag).addToBackStack(null).commit();



//                Intent intent = new Intent(context, post.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.putExtra("postUrl", proRecyclerViewListModels.get(position).getImageUrl());
//                intent.putExtra("postUid",proRecyclerViewListModels.get(position).getUid());
//               // intent.putExtra("postDateandTime",proRecyclerViewListModels.get(position).getCurrentDateandTime());
                //context.startActivity(intent);


            }
        });

    }




    @Override
    public int getItemCount() {
        return proRecyclerViewListModels.size();
    }






    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView img;
        TextView iconName;
        ImageView proDetailFragImageView;
        TextView distanceInKm;

        public ViewHolder(View view) {
            super(view);
            img = view.findViewById(R.id.icon);
            iconName = (TextView) itemView.findViewById(R.id.icon_name);
            proDetailFragImageView = view.findViewById(R.id.proOnJob_profile_img);
            distanceInKm = view.findViewById(R.id.proFullDetail_distance);
        }
    }


}