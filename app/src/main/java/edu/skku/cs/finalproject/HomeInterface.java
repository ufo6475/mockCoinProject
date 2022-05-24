package edu.skku.cs.finalproject;

import java.util.ArrayList;

import edu.skku.cs.finalproject.model.ItemModel;
import edu.skku.cs.finalproject.view.HomeActivity;

public interface HomeInterface {

    interface presenter{


        void getModel(HomeActivity homeActivity);

        void updateItems(HomeActivity homeActivity, String parameter, ArrayList<ItemModel> ItemModels, String cur_state);

        void backPressed();


        void KRWClicked();

        void BTCClicked();

        void USDTClicked();

        void searchPressed();

        void itemClicked(ItemModel curItem);
    }
    interface view{

        void updateItems(ArrayList<ItemModel> itemModels,String cur_state);

        void setItems(ArrayList<ItemModel> itemModels);
    }
}
