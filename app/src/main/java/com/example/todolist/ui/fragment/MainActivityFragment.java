package com.example.todolist.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.todolist.R;
import com.example.todolist.common.ContractFragment;
import com.example.todolist.common.Utils;

public class MainActivityFragment extends
        ContractFragment<MainActivityFragment.Contract> {

    interface Contract {
        // add methods
    }

    public MainActivityFragment() { }

    public static MainActivityFragment newInstance(){
        return new MainActivityFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        return view;
    }

    public void addTask(){
        Utils.showToast(getActivity(), "the fragment has spoken");
    }


}
