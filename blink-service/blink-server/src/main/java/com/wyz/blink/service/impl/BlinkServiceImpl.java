package com.wyz.blink.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wyz.blink.mapper.BlinkGeneralMapper;
import com.wyz.blink.mapper.BlinkMapper;
import com.wyz.blink.pojo.Blink;
import com.wyz.blink.pojo.BlinkGeneral;
import com.wyz.blink.pojo.SaveBlinkBO;
import com.wyz.blink.pojo.UpdateBlinkBO;
import com.wyz.blink.service.BlinkService;
import com.wyz.common.exception.MapperException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

/**
 * 动态服务实现类
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class BlinkServiceImpl extends ServiceImpl<BlinkMapper, Blink> implements BlinkService {

	@Resource
	private BlinkMapper blinkMapper;

	@Resource
	private BlinkGeneralMapper blinkGeneralMapper;

	@Override
	public void create(SaveBlinkBO blinkBO) {
		Blink blink = new Blink();
		BeanUtils.copyProperties(blinkBO, blink);
		blink.setCreateTime(new Timestamp(System.currentTimeMillis()));
		blinkMapper.insert(blink);
		BlinkGeneral general = new BlinkGeneral();
		general.setBlinkId(blink.getId());
		general.setScore(getRating(blink));
		blinkGeneralMapper.insert(general);
	}

	@Override
	public void remove(int id, int userId) {
		LambdaQueryWrapper<Blink> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(Blink::getId, id).eq(Blink::getUserId, userId);
		if (blinkMapper.delete(wrapper) != 1) {
			throw new MapperException("动态删除失败", "id -> " + id);
		}
		blinkGeneralMapper.deleteById(id);
	}

	@Override
	public void update(UpdateBlinkBO blinkBO) {
		LambdaUpdateWrapper<Blink> wrapper = new LambdaUpdateWrapper<>();
		wrapper.eq(Blink::getId, blinkBO.getId()).eq(Blink::getUserId, blinkBO.getUserId());
		wrapper.set(Blink::getContent, blinkBO.getContent());
		if (blinkMapper.update(null, wrapper) != 1) {
			throw new MapperException("动态更新失败", blinkBO);
		}
	}

	@Override
	public void viewBlink(Integer blinkId) {
		if (blinkId != null) {
			blinkGeneralMapper.viewBlink(blinkId);
		}
	}

	// 计算评分, 字数开方作为初始评分
	private double getRating(Blink blink) {
		double rating = Math.sqrt(blink.getContent().length());
		return Math.round(rating * 100.0) / 100.0;
	}

}
