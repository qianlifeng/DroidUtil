package com.scottqian.droidutil.ad;

/**
 * 积分墙广告接口
 * @author scott
 *
 */
public interface IAdWall extends IAdvertisement
{
	/**
	 * 增加积分
	 * @param point
	 */
	void addAdPoint(long point);
	
	/**
	 * 使用积分
	 * @param point
	 */
	void useAdPoint(long point);
	
	/**
	 * 获得目前已有积分
	 * @return
	 */
	long getTotalPoint();
}
