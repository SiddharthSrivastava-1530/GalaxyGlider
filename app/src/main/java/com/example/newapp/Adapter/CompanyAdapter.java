package com.example.newapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.example.newapp.DataModel.Company;
import com.example.newapp.R;

import java.util.ArrayList;

public class CompanyAdapter extends RecyclerView.Adapter<CompanyAdapter.CompanyHolder> implements Filterable {

    private ArrayList<Company> companies;
    private ArrayList<Company> companyListBackup;
    private Context context;
    private OnCompanyClickListener onCompanyClickListener;


    public CompanyAdapter(ArrayList<Company> companies, Context context, OnCompanyClickListener onCompanyClickListener) {
        this.companies = companies;
        this.context = context;
        this.onCompanyClickListener = onCompanyClickListener;
        companyListBackup = new ArrayList<>(companies);
    }


    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Company> filteredCompanies = new ArrayList<>();

            if(constraint.toString().isEmpty()){
                filteredCompanies.addAll(companyListBackup);
            }
            else {
                for (Company company : companyListBackup){
                    if(company.getName().toLowerCase().trim().contains(constraint.toString().toLowerCase())) {
                        filteredCompanies.add(company);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredCompanies;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            companies.clear();
            companies.addAll((ArrayList<Company>)results.values);
            notifyDataSetChanged();
        }
    };




    public interface OnCompanyClickListener{
        void onCompaniesClicked(int position);
    }

    @NonNull
    @Override
    public CompanyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.company_holder,parent,false);
        return new CompanyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CompanyHolder holder, int position) {

        holder.name.setText(companies.get(position).getName());
        holder.description.setText(companies.get(position).getDescription());

        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(context);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();
        //Using Glide library to put image of pet in imageView.
        Glide.with(context).load(companies.get(position).getImageUrl()).error(R.drawable.account_img)
                .placeholder(circularProgressDrawable).into(holder.pic);

    }

    @Override
    public int getItemCount() {
        return companies.size();
    }


    class CompanyHolder extends RecyclerView.ViewHolder{

        TextView name;
        TextView description;
        ImageView pic;

        public CompanyHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onCompanyClickListener.onCompaniesClicked(getAdapterPosition());
                }
            });

            name = itemView.findViewById(R.id.companyName_CompanyList);
            description = itemView.findViewById(R.id.companyDescription_CompanyList);
            pic = itemView.findViewById(R.id.img_Company);

        }
    }

}
