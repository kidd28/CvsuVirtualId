package com.example.cvsuvirtualid;

import static com.example.cvsuvirtualid.Signup.folderId;

import android.util.Log;
import android.webkit.MimeTypeMap;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.Permission;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DriveServiceHelper {

    private final Executor mExecutor = Executors.newSingleThreadExecutor();
    private final Drive mDriveService;
    private static final String TAG = "GoogleDriveService";
    private final String FOLDER_MIME_TYPE = "application/vnd.google-apps.folder";
    private final String FOLDER_NAME = "Cvsu";
    String uid;

    public DriveServiceHelper(Drive driveService, String uid) {
        this.mDriveService = driveService;
        this.uid = uid;
    }
    /**
     * Upload the file to the user's My Drive Folder.
     */
    public Task<Boolean> uploadFileToGoogleDrive(String path) {
        if (folderId.isEmpty()){
            Log.e(TAG, "uploadFileToGoogleDrive: folder id not present" );
            isFolderPresent().addOnSuccessListener(id -> folderId=id)
                    .addOnFailureListener(exception -> Log.e(TAG, "Couldn't create file.", exception));
        }
        return (Task<Boolean>) Tasks.call(mExecutor, () -> {
            String mimetype = getMimeType(path);
            Log.e(TAG, "uploadFileToGoogleDrive: path: "+path );
            java.io.File filePath = new java.io.File(path);
            FileList result = mDriveService.files().list().setQ("mimeType='application/vnd.google-apps.folder' and trashed=false").execute();
            for (File files : result.getFiles()) {

                    File upFile = new File();
                    upFile.setName(filePath.getName());
                    upFile.setParents(Collections.singletonList(files.getId()));
                    upFile.setMimeType(mimetype);
                    FileContent mediaContent = new FileContent(mimetype, filePath);
                    File file = mDriveService.files().create(upFile, mediaContent)
                            .setFields("id,parents,name ,webContentLink")
                            .execute();
                    mDriveService.permissions().create(file.getId(),new Permission().setRole("reader").setType("anyone").setAllowFileDiscovery(true)).execute();

                    System.out.println("File ID: " + file.getId());
                    System.out.println("link: " + file.getWebContentLink());
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Students");
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("RegFileId", file.getId());
                        hashMap.put("RegFileName", file.getName());
                        hashMap.put("RegFilelink", file.getWebContentLink());
                        reference.child(uid).updateChildren(hashMap);

            }
            return false;
        });
    }
    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }
    /**
     * Check Folder present or not in the user's My Drive.
     */
    public Task<String> isFolderPresent() {
        return Tasks.call(mExecutor, () -> {
            FileList result = mDriveService.files().list().setQ("mimeType='application/vnd.google-apps.folder' and trashed=false").execute();
            for (File file : result.getFiles()) {
                if (file.getName().equals(FOLDER_NAME))
                    return file.getId();
            }
            return "";
        });
    }
    public Task<String> createFolder() {
        return Tasks.call(mExecutor, () -> {
            File metadata = new File()
                    .setParents(Collections.singletonList("root"))
                    .setMimeType(FOLDER_MIME_TYPE)
                    .setName(FOLDER_NAME);
            File googleFolder = mDriveService.files().create(metadata).execute();
            mDriveService.permissions().create(googleFolder.getId(),new Permission().setRole("reader").setType("anyone").setAllowFileDiscovery(true)).execute();

            if (googleFolder == null) {
                throw new IOException("Null result when requesting Folder creation.");
            }
            return googleFolder.getId();
        });
    }

}
