package com.wyz.gateway.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wyz.gateway.mapper.VisitLogMapper;
import com.wyz.gateway.pojo.VisitRecord;
import org.springframework.stereotype.Service;

/**
 * 访问日志Service类
 */
@Service
public class VisitLogService extends ServiceImpl<VisitLogMapper, VisitRecord> {

}
