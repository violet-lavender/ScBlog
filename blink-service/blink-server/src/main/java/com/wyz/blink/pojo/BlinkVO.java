package com.wyz.blink.pojo;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.wyz.user.dto.UserDTO;
import lombok.Data;

/**
 * BlinkVO, 事实上是BlinkView和UserDTO的组合
 */
@Data
public class BlinkVO {

	@JsonUnwrapped
	BlinkView blink;

	UserDTO user;

	// 是否点赞
	Boolean isLike;
}
