package com.wyz.common.redis.utils;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Redis数据对象
 */
@Data
public class RedisData {

	LocalDateTime expireTime;

	Object data;

}
