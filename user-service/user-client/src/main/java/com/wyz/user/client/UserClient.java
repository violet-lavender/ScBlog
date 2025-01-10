package com.wyz.user.client;

import com.wyz.common.result.RestResult;
import com.wyz.user.dto.UserDTO;
import com.wyz.user.dto.UserGeneralDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 用户客户端
 */
@Import(UserClientResolver.class)
@FeignClient(value = "user-server", fallback = UserClientResolver.class)
public interface UserClient {

	/**
	 * 获取用户公开信息
	 *
	 * @param id 用户id
	 * @return 用户信息
	 */
	@GetMapping("/user")
	RestResult<UserDTO> getByUserId(@RequestParam Integer id);

	/**
	 * 批量获取用户信息
	 *
	 * @param userIdList 用户id列表
	 * @return key为用户id，value为用户信息
	 */
	@PostMapping("/user/list")
	RestResult<Map<Integer, UserDTO>> getUserList(@RequestParam List<Integer> userIdList);

	/**
	 * 批量获取用户信息
	 *
	 * @param userIdList 用户id列表
	 * @return key为用户id，value为用户信息
	 */
	@PostMapping("/user/list")
	RestResult<Map<Integer, UserDTO>> getUserList(@RequestParam Set<Integer> userIdList);

	/**
	 * 获取用户关注列表
	 *
	 * @param userId 用户id
	 * @return 关注列表
	 */
	@GetMapping("/user/followId")
	RestResult<List<Integer>> getFollowIdList(@RequestParam Integer userId);

	/**
	 * 批量查询用户各项数据统计数据
	 *
	 * @param userIdList 用户id列表
	 * @return 批量用户统计数据
	 */
	@GetMapping("/user/general/map")
	RestResult<Map<Integer, UserGeneralDTO>> getUserGeneralList(@RequestParam List<Integer> userIdList);

}
