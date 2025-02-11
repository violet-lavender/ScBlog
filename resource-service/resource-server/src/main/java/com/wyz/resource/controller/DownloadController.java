package com.wyz.resource.controller;

import com.wyz.resource.service.ImageService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 获取图片相关接口
 */
@Slf4j
@RestController
@RequestMapping("/resource")
public class DownloadController {

	@Resource
	private ImageService imageService;

	/**
	 * 获取头像资源
	 *
	 * @param file     文件名
	 * @param response 响应头
	 */
	@GetMapping("/avatar/{file}")
	public void getAvatar(@PathVariable String file, HttpServletResponse response) {
		imageService.getAvatarImage(file, response);
	}

	/**
	 * 获取普通图片资源，封面图和内容里面的图片都用这一个
	 *
	 * @param file 文件名
	 */
	@GetMapping("/image/{file}")
	public void getImage(@PathVariable String file, HttpServletResponse response) {
		imageService.getGeneralImage(file, response);
	}

}
