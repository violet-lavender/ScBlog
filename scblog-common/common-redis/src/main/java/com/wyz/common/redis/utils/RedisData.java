package com.wyz.common.redis.utils;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author 阿杆
 */
@Data
public class RedisData {

	LocalDateTime expireTime;

	Object data;

}
