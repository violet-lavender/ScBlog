package com.wyz.blog.utils;

import com.wyz.blog.pojo.domain.Blog;
import com.wyz.blog.service.BlogService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.wyz.blog.sdk.BlogMqConstants.BLOG_REFRESH_ES_KEY;
import static com.wyz.blog.sdk.BlogMqConstants.BLOG_TOPIC_EXCHANGE;

/**
 * 博客内容工具类
 */
@Slf4j
@Component
public class BlogContentUtils {

	@Resource
	BlogService blogService;

	@Resource
	RabbitTemplate rabbitTemplate;

	/**
	 * 重置所有ES中的博客数据
	 */
	public void refreshBlogs() {
		// 先获取所有博客数据
		List<Blog> blogList = blogService.list();
		log.info("共获取到{}条博客数据", blogList.size());
		// 发送MQ消息
		rabbitTemplate.convertAndSend(BLOG_TOPIC_EXCHANGE, BLOG_REFRESH_ES_KEY, blogList);
	}

}
