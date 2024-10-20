
package com.cinntra.ledure.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.cinntra.ledure.R;
import com.cinntra.ledure.globals.Globals;
import com.cinntra.ledure.model.Inventory;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Inventory_Detail_Fragment extends DialogFragment implements View.OnClickListener {

Inventory inventory;

    @BindView(R.id.item_name)
    EditText item_name;
    @BindView(R.id.itemcode)
    EditText itemcode;
    @BindView(R.id.per_unit)
    EditText per_unit;

    @BindView(R.id.in_stock_quantity)
    EditText in_stock_quantity;
    @BindView( R.id.total_amnt)
    EditText total_amnt;
    @BindView( R.id.description)
    EditText description;
    @BindView( R.id.add)
    Button add;


    public Inventory_Detail_Fragment()
    {
        //Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static Inventory_Detail_Fragment newInstance(String param1, String param2) {
        Inventory_Detail_Fragment fragment = new Inventory_Detail_Fragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle b      = getArguments();
            inventory =(Inventory) b.getSerializable("inventory_data");
        }
    }

  /*  @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_inventory_item, container, false);
        ButterKnife.bind(this,v);
        head_title.setText("Inventory Detail");
        setData();
        return v;
    }*/

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_inventory_item, null);
        ButterKnife.bind(this,view);

        setData();
        builder.setView(view);
        Dialog dialog = builder.create();

        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));

        return dialog;
    }

    private void setData() {
        add.setOnClickListener(this);
        item_name.setText(inventory.getItemName());
        itemcode.setText(inventory.getItemCode());
        in_stock_quantity.setText(Globals.changeDecemal(inventory.getQuantity()));
        total_amnt.setText(Globals.changeDecemal(inventory.getPrice()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add:
                getDialog().dismiss();
                break;
        }
    }
}
