package com.wyz.resource.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wyz.common.result.RestResult;
import com.wyz.common.web.utils.ResponseUtils;
import com.wyz.resource.config.ResourcePath;
import com.wyz.resource.exception.UploadException;
import com.wyz.resource.mapper.ImageMapper;
import com.wyz.resource.pojo.Image;
import com.wyz.resource.service.ImageService;
import com.wyz.resource.service.MinioService;
import com.wyz.resource.service.OBSService;
import io.minio.errors.MinioException;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;

/**
 * 图片服务实现类
 */
@Slf4j
@Service
public class ImageServiceImpl extends ServiceImpl<ImageMapper, Image> implements ImageService {

	@Resource
	private MinioService minioService;

	@Resource
	private OBSService obsService;

	@Resource
	private ImageMapper imageMapper;

	@Override
	public void getAvatarImage(String file, HttpServletResponse response) {
		getImage(file, "scblogs-avatar", response);
	}

	@Override
	public void getGeneralImage(String file, HttpServletResponse response) {
		boolean image = getImage(file, "image", response);
		// 访问成功的话，图片访问量加1
		if (image) {
			imageMapper.increaseVisit(file);
		}
	}

	@SneakyThrows(value = IOException.class)
	private boolean getImage(String file, String bucketName, @Valid @NotNull HttpServletResponse response) {
		log.debug("getImg, fileName->{}, bucketName->{}", file, bucketName);
		try {
			minioService.download(file, bucketName, response);
			response.setContentType("image/jpeg");
			return true;
		} catch (MinioException | IOException e) {
			try {
				ResponseUtils.objectToJson(response, new RestResult<>(404, "资源不存在"));
			} catch (IllegalStateException ignored) {
				// 忽略连接状态异常引起的报错，由用户多次重复刷新引起
			}
		}
		return false;
	}

	@Override
	public String uploadAvatar(MultipartFile image, String name) {
		try {
			// 上传图片
			return obsService.upload(image, name, "scblogs-avatar");
		} catch (Exception e) {
			log.error("头像上传失败:{},{}", name, e.getMessage());
			throw new UploadException("头像上传失败");
		}
	}

	@Override
	public String uploadBlogImage(MultipartFile image) throws MinioException, IOException {
		return uploadImage(image, "image");
	}

	private String uploadImage(MultipartFile image, String bucketName) throws IOException, MinioException {
		try (InputStream stream = image.getInputStream()) {
			String md5 = DigestUtils.md5DigestAsHex(stream);
			// 存数据库，上传图片
			// 先判断数据库是否存在这条记录，存在的话直接返回链接就行了
			LambdaQueryWrapper<Image> wrapper = new LambdaQueryWrapper<>();
			wrapper.eq(Image::getUrl, md5);
			if (!imageMapper.exists(wrapper)) {
				// 不存在，则新增
				Image img = new Image();
				img.setUrl(md5);
				img.setCreateTime(new Timestamp(System.currentTimeMillis()));
				imageMapper.insert(img);
				// 上传图片
				minioService.upload(image, md5, bucketName);
			}
			// 返回访问链接
			return ResourcePath.imageUrlBase + md5;
		}
	}

}
