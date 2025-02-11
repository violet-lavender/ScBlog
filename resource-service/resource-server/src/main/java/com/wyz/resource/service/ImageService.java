package com.wyz.resource.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wyz.resource.pojo.Image;
import io.minio.errors.MinioException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 图片服务接口
 */
public interface ImageService extends IService<Image> {

	/**
	 * 获取头像图片
	 *
	 * @param file     文件名称
	 * @param response 响应体
	 */
	void getAvatarImage(String file, HttpServletResponse response);

	/**
	 * 获取普通图片
	 *
	 * @param file     文件名
	 * @param response 响应体
	 */
	void getGeneralImage(String file, HttpServletResponse response);

	/**
	 * 上传头像
	 *
	 * @param image 文件
	 * @param name  文件名
	 * @return 资源链接
	 */
	String uploadAvatar(MultipartFile image, String name);

	/**
	 * 上传博客图片
	 *
	 * @param image 图片
	 * @return 资源链接
	 * @throws MinioException 上传失败
	 * @throws IOException    文件为空
	 */
	String uploadBlogImage(MultipartFile image) throws MinioException, IOException;

}
