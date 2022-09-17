package org.qty.crawler;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.qty.crawler.uidata.UIDataModel;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.qty.crawler.Storage.loadPreviousTopics;

public interface Storage {
    static List<Topic> loadPreviousTopics() throws IOException {
        File dataFile = new File("data.json");
        if (!dataFile.exists()) {
            return new ArrayList<>();
        }
        String data = FileUtils.readFileToString(dataFile, "utf-8");
        List<Topic> previousTopics = new Gson().fromJson(data, new TypeToken<List<Topic>>() {
        }.getType());

        return previousTopics;
    }

    List<Topic> loadSavedTopics() throws IOException;

    void saveTopics(List<Topic> savedTopics) throws IOException;
}


class DefaultStorage implements Storage {
    @Override
    public List<Topic> loadSavedTopics() throws IOException {
        return loadPreviousTopics();
    }

    @Override
    public void saveTopics(List<Topic> savedTopics) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Collections.sort(savedTopics, Comparator.comparing(Topic::getUrl));
        FileUtils.write(new File("data.json"), gson.toJson(savedTopics), "utf-8");
        FileUtils.write(new File("ui-data.json"), gson.toJson(UIDataModel.convertForUI(savedTopics)), "utf-8");
    }
}

class S3Storage implements Storage {

    String today = ZonedDateTime.now(ZoneId.of("UTC+8")).format(DateTimeFormatter.ofPattern("YYYYMMdd"));

    String profileName = System.getenv("ithome_crawler_aws_profile");
    String s3Bucket = System.getenv("ithome_crawler_s3_bucket");

    AmazonS3 client;

    public S3Storage() {
        checkPrecondition();

        AWSCredentialsProviderChain chain = new AWSCredentialsProviderChain(
                new EnvironmentVariableCredentialsProvider(),
                profileName == null ? new ProfileCredentialsProvider() : new ProfileCredentialsProvider(profileName)
        );
        client = AmazonS3ClientBuilder.standard().withCredentials(chain).build();
        verifyS3Configuration();
    }

    private void checkPrecondition() {
        if (s3Bucket == null) {
            throw new RuntimeException("env[ithome_crawler_s3_bucket] is required.");
        }

    }

    private void verifyS3Configuration() {
        try {
            ListObjectsRequest req = new ListObjectsRequest();
            req.setBucketName(s3Bucket);
            req.setMaxKeys(1);
            client.listObjects(req);

            String filename = String.format("%d.txt", System.currentTimeMillis());
            client.putObject(s3Bucket, filename, "");
            client.deleteObject(s3Bucket, filename);
        } catch (SdkClientException e) {
            throw new RuntimeException("Can not access S3, please check the configuration");
        }
    }


    @Override
    public List<Topic> loadSavedTopics() throws IOException {
        S3Object object = client.getObject(s3Bucket, "2022/data.json");
        StringWriter sw = new StringWriter();
        IOUtils.copy(object.getObjectContent(), sw, "utf-8");
        List<Topic> previousTopics = new Gson().fromJson(sw.toString(), new TypeToken<List<Topic>>() {
        }.getType());
        return previousTopics;
    }

    @Override
    public void saveTopics(List<Topic> savedTopics) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // classic data.json
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("application/json");
        PutObjectRequest putOriginData = new PutObjectRequest(s3Bucket, "2022/data.json",
                new ByteArrayInputStream(gson.toJson(savedTopics).getBytes("utf-8")),
                metadata);
        putOriginData.setCannedAcl(CannedAccessControlList.PublicRead);
        client.putObject(putOriginData);

        putOriginData = new PutObjectRequest(s3Bucket, String.format("archives/%s/data.json", today),
                new ByteArrayInputStream(gson.toJson(savedTopics).getBytes("utf-8")),
                metadata);
        client.putObject(putOriginData);

        // v2 data.json
        PutObjectRequest putUIV2Data = new PutObjectRequest(s3Bucket, "2022v2/ui_data.json",
                new ByteArrayInputStream(gson.toJson(UIDataModel.convertForUI(savedTopics)).getBytes("utf-8")),
                metadata);
        putUIV2Data.setCannedAcl(CannedAccessControlList.PublicRead);
        client.putObject(putUIV2Data);
    }

    public static void main(String[] args) throws IOException {
//        System.out.println(new S3Storage().loadSavedTopics().size());
//        new S3Storage().saveTopics(new S3Storage().loadSavedTopics());
        System.out.println(new S3Storage().today);
    }
}