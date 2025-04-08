package com.wyz.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wyz.blog.pojo.bo.BlogCountBO;
import com.wyz.blog.pojo.bo.BlogSaveBO;
import com.wyz.blog.pojo.domain.Blog;
import com.wyz.blog.pojo.domain.BlogContent;
import com.wyz.blog.pojo.domain.BlogUserGeneral;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 博客服务
 */
public interface BlogService extends IService<Blog> {

	/**
	 * 保存博客
	 *
	 * @param blog 博客
	 */
	Integer saveBlog(BlogSaveBO blog);

	/**
	 * 获取作者博客数量
	 *
	 * @param authorId 作者id
	 * @return 博客数量信息
	 */
	BlogCountBO getBlogCount(int authorId);

	/**
	 * 上传博客图片，自动命名为文件的md5值，返回图片路径
	 *
	 * @param coverImage 封面图
	 * @return 资料链接
	 */
	String uploadImage(MultipartFile coverImage);

	/**
	 * 获取博客md，用于作者编辑博客内容
	 *
	 * @param blogId 博客id
	 * @param userId 用户id
	 * @return 博客内容
	 */
	BlogContent getBlogContent(Integer blogId, Integer userId);

    /**
     * 删除博客（将博客存入回收站）
     *
     * @param blogId 博客id
     * @param userId 用户id
     * @return 操作是否成功
     */
    Boolean deleteBlog(Integer blogId, Integer userId);

    /**
     * 恢复博客（将博客从回收站还原）
     *
     * @param blogId 博客id
     * @param userId 用户id
     * @return 操作是否成功
     */
    Boolean recoveryBlog(Integer blogId, Integer userId);

    /**
     * 完全删除博客
     *
     * @param blogId 博客id
     * @param userId 用户id
     * @return 操作是否成功
     */
    Boolean completelyDeleteBlog(Integer blogId, Integer userId);

	/**
	 * 查询用户发表的各博客行为统计
	 *
	 * @param userIds 用户id
	 * @return 博客行为数据
	 */
	List<BlogUserGeneral> getUserBlogGeneral(Integer[] userIds);

}
