package com.example.newapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.example.newapp.DataModel.SpaceShip;
import com.example.newapp.R;

import java.util.ArrayList;

public class SpaceShipAdapter extends RecyclerView.Adapter<SpaceShipAdapter.SpaceShipHolder> {

    private ArrayList<SpaceShip> spaceships;
    private Context context;
    private OnSpaceShipClickListener onSpaceshipClickListener;

    public SpaceShipAdapter(ArrayList<SpaceShip> spaceships, Context context, OnSpaceShipClickListener onSpaceshipClickListener) {
        this.spaceships = spaceships;
        this.context = context;
        this.onSpaceshipClickListener = onSpaceshipClickListener;
    }

    public interface OnSpaceShipClickListener {
        void onSpaceShipsClicked(int position);
    }

    @NonNull
    @Override
    public SpaceShipAdapter.SpaceShipHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.spaceship_holder, parent, false);
        return new SpaceShipAdapter.SpaceShipHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SpaceShipAdapter.SpaceShipHolder holder, int position) {

        holder.spaceShipName.setText(spaceships.get(position).getSpaceShipName());
        holder.price.setText(String.valueOf(spaceships.get(position).getPrice()));
        holder.desc_spaceShip.setText(spaceships.get(position).getDescription());
        int ratingVal = (int)Float.parseFloat(spaceships.get(position).getSpaceShipRating());
        if(ratingVal==1){
            holder.oneStar_spaceShip_holder.setVisibility(View.VISIBLE);
        }
        else if(ratingVal==2){
            holder.twoStar_spaceShip_holder.setVisibility(View.VISIBLE);
        }
        else if(ratingVal==3){
            holder.threeStar_spaceShip_holder.setVisibility(View.VISIBLE);
        }
        else if(ratingVal==4){
            holder.fourStar_spaceShip_holder.setVisibility(View.VISIBLE);
        }
        else if(ratingVal==5){
            holder.fiveStar_spaceShip_holder.setVisibility(View.VISIBLE);
        }
        else{

        }
//        holder.ratings.setText(spaceships.get(position).getRatings());
//        holder.seatAvailability.setText(spaceships.get(position).getSeatAvailability());
//        holder.busyTime.setText(String.valueOf(spaceships.get(position).getBusyTime()));
//
        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(context);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();
            //Using Glide library to put image of pet in imageView.
//            Glide.with(context).load(spaceships.get(position).getSpaceShipImageUrl()).error(R.drawable.account_img)
//                    .placeholder(circularProgressDrawable).into(holder.spaceShipPic);

    }

    @Override
    public int getItemCount() {
        return spaceships.size();
    }


    class SpaceShipHolder extends RecyclerView.ViewHolder {

        TextView spaceShipName;
//        TextView ratings;
        //        TextView feedback;
        TextView seatAvailability;
//        TextView rideSharing;
//        TextView busyTime;
        TextView price;
//        ImageView spaceShipPic;

        TextView desc_spaceShip;

        TextView oneStar_spaceShip_holder;
        TextView twoStar_spaceShip_holder;
        TextView threeStar_spaceShip_holder;
        TextView fourStar_spaceShip_holder;
        TextView fiveStar_spaceShip_holder;

        public SpaceShipHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onSpaceshipClickListener.onSpaceShipsClicked(getAdapterPosition());
                }
            });

            spaceShipName = itemView.findViewById(R.id.spaceShipName_SpaceShipList);
            desc_spaceShip = itemView.findViewById(R.id.desc_glider_holder_details);
            price = itemView.findViewById(R.id.spaceShip_price_SpaceShipList);
            oneStar_spaceShip_holder = itemView.findViewById(R.id.oneStar_spaceShip_holder);
            twoStar_spaceShip_holder = itemView.findViewById(R.id.twoStar_spaceShip_holder);
            threeStar_spaceShip_holder = itemView.findViewById(R.id.threeStar_spaceShip_holder);
            fourStar_spaceShip_holder = itemView.findViewById(R.id.fourStar_spaceShip_holder);
            fiveStar_spaceShip_holder = itemView.findViewById(R.id.fiveStar_spaceShip_holder);

//            feedback = itemView.findViewById(R.id.sp);
//            seatAvailability = itemView.findViewById(R.id.seats_spaceShip_SpaceShipList);
//            rideSharing = itemView.findViewById(R.id.spaceShip_rideSharing_SpaceShipList);
//            busyTime = itemView.findViewById(R.id.spaceShip_busyTime_SpaceShipList);
//            spaceShipPic = itemView.findViewById(R.id.img_SpaceShip);

        }
    }

}
