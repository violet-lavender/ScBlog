package com.wyz.resource.service.impl;

import com.wyz.resource.service.MinioService;
import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.messages.Item;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * Minio服务实现类
 */
@Slf4j
@Component
public class MinioServiceImpl implements MinioService {

    @Resource
    private MinioClient minioClient;

    /**
     * 判断bucket是否存在
     */
    @Override
    public boolean existBucket(String name) throws MinioException {
        try {
            return minioClient.bucketExists(BucketExistsArgs.builder().bucket(name).build());
        } catch (Exception e) {
            throw new MinioException();
        }
    }

    /**
     * 上传文件. 注意关闭inputStream
     */
    @Override
    public void upload(String filePath, String bucketName, InputStream inputStream, long objectSize, long partSize, String contentType)
            throws MinioException {
        try (inputStream) {
            minioClient.putObject(PutObjectArgs
                    .builder()
                    .bucket(bucketName)
                    .object(filePath)
                    // 文件大小和分片大小，填-1默认为5Mib
                    .stream(inputStream, objectSize, partSize)
                    .contentType(contentType)
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
            throw new MinioException("文件上传异常");
        }
    }

    /**
     * 上传文件
     *
     * @param multipartFile 文件
     * @param bucketName    桶名称
     * @throws MinioException minio异常
     * @throws IOException    字节流为空
     */
    @Override
    public void upload(MultipartFile multipartFile, String bucketName) throws MinioException, IOException {
        try (
                InputStream inputStream = multipartFile.getInputStream()
        ) {
            this.upload(
                    multipartFile.getOriginalFilename(),
                    bucketName,
                    inputStream,
                    multipartFile.getSize(),
                    -1,
                    multipartFile.getContentType()
            );
        }
    }

    @Override
    public void upload(MultipartFile multipartFile, String filePath, String bucketName) throws MinioException, IOException {
        try (
                InputStream inputStream = multipartFile.getInputStream()
        ) {
            this.upload(
                    filePath,
                    bucketName,
                    inputStream,
                    multipartFile.getSize(),
                    -1,
                    multipartFile.getContentType()
            );
        }
    }

    /**
     * 下载文件. 这里传递了外部的 ServletOutputStream, 这通常由于调用者控制(如 Http 响应流)
     *
     * @param filePath     文件名(包括路径)
     * @param bucketName   储存桶
     * @param outputStream 输出流
     * @deprecated 请使用 {@link MinioServiceImpl#download(String, String, HttpServletResponse) }
     */
    @Override
    public void download(String filePath, String bucketName, ServletOutputStream outputStream) throws MinioException, IOException {
        InputStream inputStream = this.download(filePath, bucketName);
        try {
            IOUtils.copy(inputStream, outputStream);  // 复制数据到 outputStream
        } finally {
            inputStream.close();  // 确保关闭 inputStream
        }
    }

    /**
     * 下载文件, 使用response
     *
     * @param filePath   文件路径
     * @param bucketName 桶名称
     * @param response   输出的响应体
     */
    @Override
    public void download(String filePath, String bucketName, HttpServletResponse response) throws MinioException, IOException {
        try (InputStream inputStream = this.download(filePath, bucketName)) {
            try (ServletOutputStream outputStream = response.getOutputStream()) {
                // 复制数据到 outputStream
                IOUtils.copy(inputStream, outputStream);
            }
        }
    }

    /**
     * 下载文件. 确保调用者关闭流
     *
     * @param filePath 文件名(包括路径)
     */
    @Override
    public InputStream download(String filePath, String bucketName) throws MinioException {
        try {
            return minioClient.getObject(GetObjectArgs
                    .builder()
                    .bucket(bucketName)
                    .object(filePath)
                    .build());
        } catch (Exception e) {
            throw new MinioException("文件下载异常," + e.getMessage());
        }
    }

    @Override
    public void removeFile(String fileName, String bucketName) throws MinioException {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder().bucket(bucketName).object(fileName).build()
            );
        } catch (Exception e) {
            throw new MinioException("文件删除异常," + e.getMessage());
        }
    }

    @Override
    public void getObjectList(String bucketName) throws Exception {
        Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder().bucket(bucketName).build());
        for (Result<Item> result : results) {
            Item item = result.get();
            log.info(item.objectName());
        }
    }

}
