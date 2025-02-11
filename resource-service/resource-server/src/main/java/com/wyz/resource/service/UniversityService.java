package com.wyz.resource.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wyz.resource.pojo.City;
import com.wyz.resource.pojo.Province;
import com.wyz.resource.pojo.University;

import java.util.List;

/**
 * 学校信息服务接口
 */
public interface UniversityService extends IService<University> {

	/**
	 * 获取省份信息
	 *
	 * @return 省份信息列表
	 */
	List<Province> getProvinceList();

	/**
	 * 获取城市信息
	 *
	 * @param provinceId 省份id
	 * @return 城市信息列表
	 */
	List<City> getCityList(int provinceId);

	/**
	 * 获取高校信息
	 *
	 * @param cityId 城市id
	 * @return 高校信息列表
	 */
	List<University> getUniversityList(int cityId);

	/**
	 * 获取高校名称
	 *
	 * @param schoolCode 院校代码
	 * @return 高校名称
	 */
	String getUniversityName(int schoolCode);

}
