package com.wyz.blog.content.pojo;

import com.wyz.common.result.ListVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 博客列表 VO
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BlogListVO extends ListVO<BlogDoc> {

}
