package org.qty.crawler;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
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

import java.io.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import static org.qty.crawler.Storage.loadPreviousTopics;

public interface Storage {

    final int YEAR = 2023;

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

    public static final String REGION = "ap-northeast-1";
    String today = ZonedDateTime.now(ZoneId.of("UTC+8")).format(DateTimeFormatter.ofPattern("YYYYMMdd"));

    String profileName = System.getenv("ithome_crawler_aws_profile");
    String s3Bucket = System.getenv("ithome_crawler_s3_bucket");

    AmazonS3 client;

    public S3Storage() {
        checkPrecondition();

        AWSCredentialsProviderChain chain = new AWSCredentialsProviderChain(
                new EnvironmentVariableCredentialsProvider(),
                profileName == null ?
                        new ProfileCredentialsProvider() : new ProfileCredentialsProvider(profileName)
        );

        client = AmazonS3ClientBuilder.standard()
                .withCredentials(chain)
                .withRegion(REGION).build();
        verifyS3Configuration();
    }

    private void checkPrecondition() {
        if (s3Bucket == null) {
            throw new RuntimeException("env[ithome_crawler_s3_bucket] is required.");
        }
        if (System.getenv("AWS_ACCESS_KEY_ID") == null) {
            throw new RuntimeException("env[AWS_ACCESS_KEY_ID] is required.");
        }
        if (System.getenv("AWS_SECRET_ACCESS_KEY") == null) {
            throw new RuntimeException("env[AWS_SECRET_ACCESS_KEY] is required.");
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
        try {
            return doLoadSavedTopics();
        } catch (RuntimeException e) {
            System.out.println("not previous-data");
            return new ArrayList<>();
        }
    }

    private List<Topic> doLoadSavedTopics() throws IOException {
        S3Object object = client.getObject(s3Bucket, String.format("%s/data.json", (YEAR)));
        StringWriter sw = new StringWriter();
        IOUtils.copy(object.getObjectContent(), sw, "utf-8");
        List<Topic> previousTopics = new Gson().fromJson(sw.toString(), new TypeToken<List<Topic>>() {
        }.getType());
        return previousTopics;
    }

    @Override
    public void saveTopics(List<Topic> savedTopics) throws IOException {
        Gson gson = new GsonBuilder().create();

        // classic data.json
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("application/json");
        metadata.setCacheControl("max-age=300");
        uploadToS3WithPublicAclRead(String.format("%s/data.json", YEAR), gson.toJson(savedTopics));
        PutObjectRequest putOriginData;

        putOriginData = new PutObjectRequest(s3Bucket, String.format("archives/%s/data.json", today),
                new ByteArrayInputStream(gson.toJson(savedTopics).getBytes("utf-8")),
                metadata);
        client.putObject(putOriginData);

        String uiData = gson.toJson(UIDataModel.convertForUI(savedTopics));
        uploadToS3WithPublicAclRead(String.format("%sv2/ui-data.json", YEAR), uiData);
        uploadToS3WithPublicAclRead(String.format("%s/ui-data.json", YEAR), uiData);
        uploadToGZipS3WithPublicAclRead(String.format("%s/ui-data-small.json", YEAR), uiData);
    }

    private void uploadToS3WithPublicAclRead(String s3Prefix, String content) throws UnsupportedEncodingException {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("application/json");
        metadata.setCacheControl("max-age=300");

        // v2 data.json
        PutObjectRequest putUIV2Data = new PutObjectRequest(s3Bucket, s3Prefix,
                new ByteArrayInputStream(content.getBytes("utf-8")),
                metadata);
        putUIV2Data.setCannedAcl(CannedAccessControlList.PublicRead);
        client.putObject(putUIV2Data);
    }

    private void uploadToGZipS3WithPublicAclRead(String s3Prefix, String content) throws IOException {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("application/json");
        metadata.setContentEncoding("gzip");
        metadata.setCacheControl("max-age=300");

        byte[] data = content.getBytes("utf-8");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        GZIPOutputStream outputStream = new GZIPOutputStream(buffer);
        outputStream.write(data, 0, data.length);

        PutObjectRequest putUIV2Data = new PutObjectRequest(s3Bucket, s3Prefix,
                new ByteArrayInputStream(buffer.toByteArray()),
                metadata);
        putUIV2Data.setCannedAcl(CannedAccessControlList.PublicRead);
        client.putObject(putUIV2Data);
    }

    public static void main(String[] args) throws IOException {
//        System.out.println(new S3Storage().loadSavedTopics().size());
        new S3Storage().saveTopics(new S3Storage().loadSavedTopics());
//        System.out.println(new S3Storage().today);
    }
}