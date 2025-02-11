package com.wyz.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wyz.blog.pojo.domain.BlogContent;
import org.apache.ibatis.annotations.Mapper;

/**
 * 博客内容Mapper
 */
@Mapper
public interface BlogContentMapper extends BaseMapper<BlogContent> {

}
