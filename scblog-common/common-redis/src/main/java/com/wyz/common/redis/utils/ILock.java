package com.wyz.common.redis.utils;

/**
 * 锁接口
 */
public interface ILock {

	/**
	 * 尝试获取锁
	 *
	 * @param timeout 锁持有的超时时间，过期后自动释放，防止服务器宕机导致死锁
	 * @return true代表获取锁成功; false代表获取锁失败
	 */
	boolean tryLock(long timeout);

	/**
	 * 释放锁
	 */
	void unlock();

}
