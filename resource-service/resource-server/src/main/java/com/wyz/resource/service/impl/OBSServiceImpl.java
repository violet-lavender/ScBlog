package com.wyz.resource.service.impl;

import com.obs.services.ObsClient;
import com.obs.services.exception.ObsException;
import com.wyz.resource.config.OBSConfig;
import com.wyz.resource.service.OBSService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * OBS 服务实现类
 */
@Slf4j
@Service
public class OBSServiceImpl implements OBSService {

    @Resource
    private OBSConfig obsConfig;

    @Override
    public String upload(InputStream inputStream, String fileName, String bucketName) throws ObsException, NoSuchAlgorithmException, IOException {
        ObsClient obsClient = null;

        try {
            // 如果 fileName 为 null，基于文件内容生成文件名
            byte[] fileBytes = getBytesFromInputStream(inputStream); // 获取 InputStream 的字节数组
            if (fileName == null) {
                fileName = generateFileNameFromBytes(fileBytes); // 使用字节数组生成文件名
            }

            obsClient = new ObsClient(obsConfig.getAccessKeyId(), obsConfig.getAccessKeySecret(), obsConfig.getEndpoint());
            obsClient.putObject(bucketName, fileName, new ByteArrayInputStream(fileBytes)); // 使用 ByteArrayInputStream 保证流没有被消耗

            // 文件访问路径规则 https://BucketName.Endpoint/ObjectName
            StringBuilder stringBuilder = new StringBuilder("https://");
            stringBuilder
                    .append(bucketName)
                    .append(".")
                    .append(obsConfig.getEndpoint().split("//")[1])
                    .append("/")
                    .append(fileName);
            log.info("文件上传到: " + stringBuilder);

            return stringBuilder.toString();
        } catch (ObsException oe) {
            log.error("Caught an ObsException, which means your request made it to OBS, "
                    + "but was rejected with an error response for some reason.");
            log.error("Error Message: " + oe.getErrorMessage());
            log.error("Error Code: " + oe.getErrorCode());
            log.error("Request ID: " + oe.getErrorRequestId());
            log.error("Host ID: " + oe.getErrorHostId());
            throw oe;
        } finally {
            if (obsClient != null) {
                try {
                    obsClient.close();
                } catch (IOException e) {
                    log.error("Failed to close ObsClient", e);
                }
            }
        }
    }

    @Override
    public String upload(MultipartFile multipartFile, String bucketName) throws IOException, NoSuchAlgorithmException {
        return upload(multipartFile, null, bucketName);
    }

    @Override
    public String upload(MultipartFile multipartFile, String fileName, String bucketName) throws IOException, NoSuchAlgorithmException {
        try (InputStream inputStream = multipartFile.getInputStream()) {
            return upload(inputStream, fileName, bucketName);
        }
    }

    /**
     * 获取 InputStream 中的字节数组，避免流被消耗
     *
     * @param inputStream 输入流
     * @return 字节数组
     * @throws IOException 如果读取输入流失败
     */
    private byte[] getBytesFromInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * 使用文件的字节数组计算哈希值并生成文件名
     *
     * @param fileBytes 文件字节数组
     * @return 文件名（哈希值）
     * @throws NoSuchAlgorithmException 如果没有该算法
     */
    private String generateFileNameFromBytes(byte[] fileBytes) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(fileBytes);
        return Hex.encodeHexString(hash).substring(0, 32); // 取前 32 个字符
    }
}