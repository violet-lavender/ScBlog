package com.wyz.resource.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wyz.resource.mapper.CityMapper;
import com.wyz.resource.mapper.ProvinceMapper;
import com.wyz.resource.mapper.UniversityMapper;
import com.wyz.resource.pojo.City;
import com.wyz.resource.pojo.Province;
import com.wyz.resource.pojo.University;
import com.wyz.resource.service.UniversityService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 学校服务实现类
 */
@Slf4j
@Service
public class UniversityServiceImpl extends ServiceImpl<UniversityMapper, University> implements UniversityService {

	@Resource
	private ProvinceMapper provinceMapper;

	@Resource
	private CityMapper cityMapper;

	@Resource
	private UniversityMapper universityMapper;

	@Override
	public List<Province> getProvinceList() {
		return provinceMapper.selectList(null);
	}

	@Override
	public List<City> getCityList(int provinceId) {
		LambdaQueryWrapper<City> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(City::getProvinceId, provinceId);
		return cityMapper.selectList(wrapper);
	}

	@Override
	public List<University> getUniversityList(int cityId) {
		LambdaQueryWrapper<University> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(University::getCityId, cityId);
		return universityMapper.selectList(wrapper);
	}

	@Override
	public String getUniversityName(int schoolCode) {
		LambdaQueryWrapper<University> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(University::getCode, schoolCode);
		University university = universityMapper.selectOne(wrapper);
		return university == null ? null : university.getName();
	}

}
