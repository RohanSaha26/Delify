package com.sahaprojects.drivechat.HandleDrive;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DriveServiceHelper {
    private final Executor mExecuter = Executors.newSingleThreadExecutor();
    private Drive mDriveService;

    public DriveServiceHelper(Drive mDriveService) {
        this.mDriveService = mDriveService;
    }

    public Task<String> createFile(String filepath)
    {
        return Tasks.call(mExecuter,() -> {

            File fileMetadata = new File();
            fileMetadata.setName("Drivechat");
            java.io.File filePath = new java.io.File(filepath);
            FileContent mediaContent = new FileContent("*/*", filePath);
            File file = mDriveService.files().create(fileMetadata, mediaContent)
//                    .setFields("id")
                    .execute();

            if (file == null)
            {
                System.out.println("Null result when requesting file creation.");
                throw new IOException("Null result when requesting file creation.");
            }
            System.out.println("File ID: " + file.getId());

            return file.getId();
        });
    }
}
