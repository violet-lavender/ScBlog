package com.wyz.resource.service;

import com.obs.services.exception.ObsException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;

public interface OBSService {

    /**
     * 上传文件
     *
     * @param inputStream 数据流
     * @param fileName    文件名，默认不指定name的情况下，以文件内容的hash值作为文件名
     * @param bucketName  桶名称
     * @return 资源链接
     * @throws ObsException             上传失败
     * @throws NoSuchAlgorithmException 文件名hash值计算失败
     * @throws IOException              数据流读取失败
     */
    String upload(InputStream inputStream, String fileName, String bucketName) throws ObsException, NoSuchAlgorithmException, IOException;

    /**
     * 上传文件
     *
     * @param multipartFile 文件
     * @param bucketName    桶名称
     * @return 资源链接
     * @throws IOException              数据流读取失败
     * @throws NoSuchAlgorithmException 文件名hash值计算失败
     */
    String upload(MultipartFile multipartFile, String bucketName) throws IOException, NoSuchAlgorithmException;

    /**
     * 上传文件
     *
     * @param multipartFile 文件
     * @param fileName      文件名称
     * @param bucketName    桶名称
     * @return 资源链接
     * @throws IOException              数据流读取失败
     * @throws NoSuchAlgorithmException 文件名hash值计算失败
     */
    String upload(MultipartFile multipartFile, String fileName, String bucketName) throws IOException, NoSuchAlgorithmException;
}
