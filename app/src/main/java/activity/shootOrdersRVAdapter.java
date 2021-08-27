package activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.test1photographerapp.R;

import java.util.ArrayList;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class shootOrdersRVAdapter extends RecyclerView.Adapter<shootOrdersRVAdapter.ViewHolder> {
    private ArrayList<shootOrdersRVModelList> shootOrdersRVModelList;
    private Context context;

    public shootOrdersRVAdapter(Context context, ArrayList<shootOrdersRVModelList> shootOrdersRVModelList) {
        this.context = context;
        this.shootOrdersRVModelList = shootOrdersRVModelList;

    }

    @Override
    public shootOrdersRVAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.shoot_orders_rv_item, viewGroup, false);
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
        Glide.with(context).load(shootOrdersRVModelList.get(position).getPhotographer_pic_url()).into(viewHolder.proOrder_profile_img);

        viewHolder.proOrder_proName.setText(shootOrdersRVModelList.get(position).getPhotographer_name());
        viewHolder.proOrder_ArrivalTime.setText(shootOrdersRVModelList.get(position).getPhotographer_arrival_time());
        viewHolder.shootOrder_plan_text.setText(shootOrdersRVModelList.get(position).getShoot_plan());
        viewHolder.shootOrder_type_text.setText(shootOrdersRVModelList.get(position).getShoot_type());
        viewHolder.shootOrder_hours_text.setText(shootOrdersRVModelList.get(position).getShoot_hour());
        viewHolder.proOrder_shootStatus.setText(shootOrdersRVModelList.get(position).getShoot_status());

        switch (shootOrdersRVModelList.get(position).getShoot_plan()) {

            case "smartphone":
                viewHolder.shootOrder_plan_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_smartphone));
                break;
            case "value":
                viewHolder.shootOrder_plan_image.setImageDrawable(context.getResources().getDrawable(R.drawable.camera));
                break;
            case "professional":
                viewHolder.shootOrder_plan_image.setImageDrawable(context.getResources().getDrawable(R.drawable.cameraf));
                break;
            case "premium":
                viewHolder.shootOrder_plan_image.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_pro_round));
                break;

        }

        switch (shootOrdersRVModelList.get(position).getShoot_type()) {

            case "portraits":
                viewHolder.shootOrder_type_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_portrait));
                break;
            case "modelling":
                viewHolder.shootOrder_type_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_modelling));
                break;
            case "socialMedia":
                viewHolder.shootOrder_type_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_socialmedia));
                break;
            case "events":
                viewHolder.shootOrder_type_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_celebrate));
                break;
            case "birthday":
                viewHolder.shootOrder_type_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_birthday));
                break;
            case "wedding":
                viewHolder.shootOrder_type_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_wedding));
                break;
            case "food":
                viewHolder.shootOrder_type_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_food));
                break;
            case "baby":
                viewHolder.shootOrder_type_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baby));
                break;

        }


        viewHolder.shoot_order_whole_card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, "pro name = "+shootOrdersRVModelList.get(position).getPhotographer_name(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, proOnJob.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("selectedProName", shootOrdersRVModelList.get(position).getPhotographer_name());
                intent.putExtra("selectedProPic", shootOrdersRVModelList.get(position).getPhotographer_pic_url());
                intent.putExtra("selectedProUid", shootOrdersRVModelList.get(position).getPhotographer_id());
                intent.putExtra("selectedProShootBookedTime", shootOrdersRVModelList.get(position).getShootBookedTimeKey());
                intent.putExtra("selectedProDistance", shootOrdersRVModelList.get(position).getProDistance());
                intent.putExtra("selectedProShootStatus", shootOrdersRVModelList.get(position).getShoot_status());
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return shootOrdersRVModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView shoot_order_whole_card_view;
        ImageView proOrder_profile_img, shootOrder_plan_image, shootOrder_type_image;
        TextView proOrder_proName, proOrder_shootStatus, proOrder_ArrivalTime, shootOrder_plan_text, shootOrder_type_text, shootOrder_hours_text;


        public ViewHolder(View view) {
            super(view);
            shoot_order_whole_card_view = view.findViewById(R.id.shoot_order_whole_card_view);

            proOrder_profile_img = view.findViewById(R.id.proOrder_profile_img);
            proOrder_proName = view.findViewById(R.id.proOrder_proName);
            proOrder_shootStatus = view.findViewById(R.id.proOrder_proStatus);
            proOrder_ArrivalTime = view.findViewById(R.id.proOrder_ArrivalTime);

            shootOrder_plan_text = view.findViewById(R.id.shootOrder_plan_text);
            shootOrder_type_text = view.findViewById(R.id.shootOrder_type_text);
            shootOrder_hours_text = view.findViewById(R.id.shootOrder_hours_text);

            shootOrder_plan_image = view.findViewById(R.id.shootOrder_plan_image);
            shootOrder_type_image = view.findViewById(R.id.shootOrder_type_image);


        }
    }


}