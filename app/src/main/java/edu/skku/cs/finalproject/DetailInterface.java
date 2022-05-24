package edu.skku.cs.finalproject;

import edu.skku.cs.finalproject.model.DetailModel;

public interface DetailInterface {

    interface presenter{

        void initView(DetailModel detailModel);

        void getData(DetailModel detailModel);
    }

    interface view{

        void setTitle(String title);

        void setID(String ID);


        void updatePrice(String curPrice);
    }
}
