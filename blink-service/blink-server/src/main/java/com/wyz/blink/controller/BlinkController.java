package com.wyz.blink.controller;

import com.wyz.blink.pojo.BlinkView;
import com.wyz.blink.pojo.BlinkViewListVO;
import com.wyz.blink.pojo.SaveBlinkBO;
import com.wyz.blink.pojo.UpdateBlinkBO;
import com.wyz.blink.service.BlinkService;
import com.wyz.blink.service.BlinkViewService;
import com.wyz.blink.service.LikeBlinkService;
import com.wyz.common.exception.BusinessException;
import com.wyz.common.web.auth.AuthHelper;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 动态服务相关接口
 */
@Slf4j
@RestController
@RequestMapping("/blink")
@Validated
public class BlinkController {

	private static final int PAGE_SIZE = 20;

	@Resource
	private BlinkViewService blinkViewService;

	@Resource
	private BlinkService blinkService;

	@Resource
	private LikeBlinkService likeBlinkService;

	/**
	 * 获取动态内容 TODO, 博客的浏览量统计问题，感觉不好解决
	 *
	 * @param id 动态id
	 */
	@GetMapping
	public BlinkView getBlink(@NotNull Integer id) {
		return blinkViewService.getById(id);
	}

	/**
	 * 创建动态
	 *
	 * @param content    动态内容
	 * @param schoolCode 院校代码，从cookie中获取
	 */
	@PostMapping
	public void createBlink(@NotNull String content, @CookieValue(required = false) Integer schoolCode) {
		Integer id = AuthHelper.getCurrentUserIdOrExit();
		SaveBlinkBO saveBlinkBO = new SaveBlinkBO();
		saveBlinkBO.setContent(content);
		saveBlinkBO.setUserId(id);
		saveBlinkBO.setSchoolCode(schoolCode);
		blinkService.create(saveBlinkBO);
	}

	/**
	 * 修改动态内容
	 *
	 * @param blinkBO 动态内容
	 */
	@PutMapping
	public void updateBlink(@Validated UpdateBlinkBO blinkBO) {
		Integer id = AuthHelper.getCurrentUserIdOrExit();
		blinkBO.setUserId(id);
		blinkService.update(blinkBO);
	}

	/**
	 * 点赞动态
	 *
	 * @param blinkId 动态id
	 */
	@PostMapping("/like")
	public Map<String, Object> likeBlog(@NotNull Integer blinkId) {
		Integer id = AuthHelper.getCurrentUserIdOrExit();
		boolean status = likeBlinkService.likeBlink(id, blinkId);
		Map<String, Object> map = new HashMap<>(1);
		map.put("num", likeBlinkService.getLikeNum(blinkId));
		map.put("status", status);
		return map;
	}

	/**
	 * 删除动态
	 *
	 * @param blinkId 动态id
	 */
	@DeleteMapping
	public void deleteBlink(@NotNull Integer blinkId) {
		Integer id = AuthHelper.getCurrentUserIdOrExit();
		blinkService.remove(blinkId, id);
	}

	/**
	 * 获取分数动态列表
	 *
	 * @param page       第几页
	 * @param schoolCode 院校代码，哪个学校的 TODO 这里的院校代码问题
	 */
	@GetMapping("/list")
	public BlinkViewListVO getList(@RequestParam(defaultValue = "1") Integer page, Integer schoolCode) {
		Integer userId = AuthHelper.getCurrentUserId();
		return blinkViewService.getListByScore(page, PAGE_SIZE, schoolCode, userId);
	}

	/**
	 * 获取动态列表
	 *
	 * @param page 第几页
	 */
	@GetMapping("/list/self")
	public BlinkViewListVO getSelfList(
			@RequestParam(defaultValue = "1") Integer page,
			Integer userId) {
		if (userId == null) {
			userId = AuthHelper.getCurrentUserId();
			if (userId == null) {
				throw new BusinessException("参数异常");
			}
		}
		return blinkViewService.getSelfList(userId, page, PAGE_SIZE);
	}

}
