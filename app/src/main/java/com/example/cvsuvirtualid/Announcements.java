package com.example.cvsuvirtualid;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Announcements#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Announcements extends Fragment {

    RecyclerView recyclerView;
    ArrayList<PostModel> postModels;
    PostAdapter postAdapter;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Announcements() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Announcements.
     */
    // TODO: Rename and change types and number of parameters
    public static Announcements newInstance(String param1, String param2) {
        Announcements fragment = new Announcements();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_announcements, container, false);

        recyclerView = v.findViewById(R.id.rv);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        postModels = new ArrayList<>();

        loadPost();
        return v;
    }

    private void loadPost() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Post");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postModels.clear();
                for (DataSnapshot ds : snapshot.getChildren()){
                    if(ds.child("Publish").getValue().equals("y")){
                        PostModel model = ds.getValue(PostModel.class);
                        postModels.add(model);}
                    Collections.sort(postModels, new Comparator<PostModel>() {
                        @Override
                        public int compare(PostModel o1, PostModel o2) {
                            return Float.compare(Float.parseFloat(o2.getPostId()), Float.parseFloat(o1.getPostId()));
                        }
                    });
                }
                postAdapter = new PostAdapter(getContext(), postModels);
                recyclerView.setAdapter(postAdapter);
                postAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}