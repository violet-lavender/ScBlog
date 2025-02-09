package com.wyz.gateway.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wyz.gateway.pojo.VisitRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 访问日志 Mapper 接口
 */
@Mapper
public interface VisitLogMapper extends BaseMapper<VisitRecord> {

}
