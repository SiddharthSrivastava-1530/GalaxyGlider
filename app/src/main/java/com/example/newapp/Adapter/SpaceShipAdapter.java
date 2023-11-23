package com.example.newapp.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.example.newapp.DataModel.SpaceShip;
import com.example.newapp.R;

import java.util.ArrayList;

public class SpaceShipAdapter extends RecyclerView.Adapter<SpaceShipAdapter.SpaceShipHolder> {

    private ArrayList<SpaceShip> spaceships;
    private Context context;
    private OnSpaceShipClickListener onSpaceshipClickListener;

    private ArrayList<Integer> spaceShipImageResourceIds;

    public SpaceShipAdapter(ArrayList<SpaceShip> spaceships, Context context, OnSpaceShipClickListener onSpaceshipClickListener) {
        this.spaceships = spaceships;
        this.context = context;
        this.onSpaceshipClickListener = onSpaceshipClickListener;
        this.spaceShipImageResourceIds = new ArrayList<>();
        initializeSpaceShipImageResourceIds();
    }

    private void initializeSpaceShipImageResourceIds() {
        for (int i = 0; i < 7; i++) {
            String drawableName = "spaceship" + (i + 1); // Assuming drawables are named spaceship1, spaceship2, etc.
            int drawableResourceId = context.getResources().getIdentifier(drawableName, "drawable", context.getPackageName());

            if (drawableResourceId != 0) {
                spaceShipImageResourceIds.add(drawableResourceId);
            }
        }
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
        holder.price.setText("$"+String.valueOf(spaceships.get(position).getPrice()));
        holder.desc_spaceShip.setText(spaceships.get(position).getDescription());
        float ratingVal = Float.parseFloat(spaceships.get(position).getSpaceShipRating());
        holder.ratingBar.setRating(ratingVal);

        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(context);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();

        // Get a random index to choose a drawable
//        int randomIndex = (int) (Math.random() * spaceShipImageResourceIds.size());
        int spaceShipImgsSize = spaceShipImageResourceIds.size();
        int randomDrawableResourceId = R.drawable.spaceship8;
        if(spaceShipImgsSize>0){
            randomDrawableResourceId = spaceShipImageResourceIds.get(position%spaceShipImgsSize);
        }

        // Load the drawable image using Glide
        Glide.with(context)
                .load(randomDrawableResourceId)
                .apply(new RequestOptions().error(R.drawable.spaceship8))
                .placeholder(circularProgressDrawable)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.spaceShipPic);

    }

    @Override
    public int getItemCount() {
        return spaceships.size();
    }


    class SpaceShipHolder extends RecyclerView.ViewHolder {

        TextView spaceShipName;

        TextView price;
        ImageView spaceShipPic;

        TextView desc_spaceShip;

        RatingBar ratingBar;

        public SpaceShipHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(getItemCount()>0){
                        onSpaceshipClickListener.onSpaceShipsClicked(getAdapterPosition());
                    }

                }
            });

            spaceShipPic = itemView.findViewById(R.id.spaceShip_img);
            spaceShipName = itemView.findViewById(R.id.spaceShipName_SpaceShipList);
            desc_spaceShip = itemView.findViewById(R.id.desc_glider_holder_details);
            price = itemView.findViewById(R.id.spaceShip_price_SpaceShipList);
            ratingBar = itemView.findViewById(R.id.ratingBar_spaceship_holder);

        }
    }

}
