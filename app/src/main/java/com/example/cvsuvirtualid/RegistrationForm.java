package com.example.cvsuvirtualid;

import java.io.File;
import static androidx.core.content.PermissionChecker.checkSelfPermission;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegistrationForm#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegistrationForm extends Fragment {
    private PDFView pdfView;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseUser user;
    String pdfUrl, fileid,Filelink, FileName;
    Button download;
    private static final int WRITE_EXTERNAL_STORAGE= 1;



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RegistrationForm() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Profile.
     */
    // TODO: Rename and change types and number of parameters
    public static RegistrationForm newInstance(String param1, String param2) {
        RegistrationForm fragment = new RegistrationForm();
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
        View v = inflater.inflate(R.layout.fragment_regristrationform, container, false);

        // initializing variable for pdf view.
        pdfView = v.findViewById(R.id.pdfView);
        download = v.findViewById(R.id.download);

        // below line is used to get the instance
        // of our Firebase database.
        firebaseDatabase = FirebaseDatabase.getInstance();

        // below line is used to get reference for our database.
        databaseReference = firebaseDatabase.getReference("url");
        user = FirebaseAuth.getInstance().getCurrentUser();

        Query query = FirebaseDatabase.getInstance().getReference("Students").orderByChild("uid").equalTo(user.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    pdfUrl = "" + ds.child("RegFilelink").getValue();
                    fileid = "" + ds.child("RegFileId").getValue();
                    Filelink = "" + ds.child("RegFilelink").getValue();
                    FileName = "" + ds.child("RegFileName").getValue();

                    new RetrivedPdffromFirebase().execute(pdfUrl);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED){
                        String [] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(permission,WRITE_EXTERNAL_STORAGE);
                    }else {
                        savefile();
                    }
                }else {
                    savefile();
                }
            }
        });

        return v;
    }

    class RetrivedPdffromFirebase extends AsyncTask<String, Void, InputStream> {
        // we are calling async task and performing
        // this task to load pdf in background.
        @Override
        protected InputStream doInBackground(String... strings) {
            // below line is for declaring
            // our input stream.
            InputStream pdfStream = null;
            try {
                // creating a new URL and passing
                // our string in it.
                URL url = new URL(strings[0]);

                // creating a new http url connection and calling open
                // connection method to open http url connection.
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                if (httpURLConnection.getResponseCode() == 200) {
                    // if the connection is successful then
                    // we are getting response code as 200.
                    // after the connection is successful
                    // we are passing our pdf file from url
                    // in our pdfstream.
                    pdfStream = new BufferedInputStream(httpURLConnection.getInputStream());
                }

            } catch (IOException e) {
                // this method is
                // called to handle errors.
                return null;
            }
            // returning our stream
            // of PDF file.
            return pdfStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            // after loading stream we are setting
            // the pdf in your pdf view.
            pdfView.fromStream(inputStream).load();
        }
    }

    private void savefile() {
        if (!Filelink.equals("")){
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse("https://drive.google.com/uc?export=download&id="+fileid));
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
            request.setTitle(FileName);
            request.setDescription("Downloading File..");

            File file = new File(Environment.getExternalStorageDirectory()+"/Download",FileName);

            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE | DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationUri(Uri.fromFile(file));
            DownloadManager manager= (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
            manager.enqueue(request);

            Toast.makeText(getActivity(), "Downloading File..",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                } else {
                    Toast.makeText(getActivity(), "Permission Granted", Toast.LENGTH_SHORT);
                }
            }

        }
    }
}