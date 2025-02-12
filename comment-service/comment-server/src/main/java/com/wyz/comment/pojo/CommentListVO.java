package com.wyz.comment.pojo;

import com.wyz.common.result.ListVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 评论list视图对象VO
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CommentListVO extends ListVO<CommentVO> {

	/**
	 * 总评论数量（包括二级评论）
	 */
	@SuppressWarnings("AlibabaPojoNoDefaultValue")
	Long allCount = 0L;

}
