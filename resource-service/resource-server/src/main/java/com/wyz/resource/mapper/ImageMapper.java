package com.wyz.resource.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wyz.resource.pojo.Image;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

/**
 * 图片Mapper
 */
@Mapper
public interface ImageMapper extends BaseMapper<Image> {

	/**
	 * 浏览量 +1
	 *
	 * @param url 访问的资源
	 */
	@Update("update resource.image set visit = visit + 1 where url = #{blogId};")
	void increaseVisit(String url);

}
