package com.example.service;

import java.util.Random;

import org.springframework.stereotype.Service;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.example.pojo.Message;
import com.example.utils.RedisUtil;
import com.example.utils.SendSmsUtils;

import redis.clients.jedis.Jedis;

/**
 * @author liyu
 *
 */
@Service
public class SmsMessage {
	// 过期时间
	private final int EXPIRETIME = 60 * 10;
	private String existphoneNumber = "_";

	/**
	 * 发送短信验证码
	 * 
	 * @param phoneNumber  手机号           
	 * @param ip   ip地址          
	 * @return 返回短信验证码的发送结果
	 */
	public Message sendMessage(String phoneNumber, String ip) {
		Jedis jedis = RedisUtil.getJedis();
		Message message=new Message();
		if (jedis == null) {
			message.setCode(501);
			message.setMsg("发送失败");
			message.setDescription("redis未连接");			
			return message;
		}
		if(phoneNumber==""){
			message.setCode(500);
			message.setMsg("发送失败");
			message.setDescription("phoneNumber is null");
			return message;
		}
		
		try {
			
			// 限制发送验证码间隔在60秒
			if (jedis.exists(phoneNumber + "_createTime")) {
				long createTime = Long.valueOf(jedis.get(phoneNumber + "_createTime"));
				long presentTime = System.currentTimeMillis();
				if ((presentTime - createTime) < 60*1000) {
					message.setCode(400);
					message.setMsg("操作频繁");
					message.setDescription("isv.BUSINESS_LIMIT_CONTROL");
					return message;
				}
			}
			// 限制同一个ip发送的次数限制20次
			if (jedis.exists(ip)) {
				int ipCount = Integer.parseInt(jedis.get(ip));
				if (ipCount > 20) {
					message.setCode(401);
					message.setMsg("ip限流");
					message.setDescription("IP_LIMIT");
					return message;
				}
			}
			// 获得6位数字随机验证码
			Random random = new Random();
			String smsVerifyCode = "";
			for (int i = 0; i < 6; i++) {
				smsVerifyCode += random.nextInt(10);
			}

			// 发送验证码
			if (!existphoneNumber.equals(phoneNumber)) {
				existphoneNumber = phoneNumber;
				SendSmsResponse sendSms=null;
				try {
					sendSms = SendSmsUtils.sendSms(phoneNumber, smsVerifyCode);
				} catch (ClientException e) {
					message.setCode(502);
					message.setMsg("客户端异常");
					message.setDescription("The exception of sendSms()");
					existphoneNumber = "_";
					e.printStackTrace();
					return message;
				}
				//获得阿里云
				String smsCode = sendSms.getCode();
				String smsMessage=sendSms.getMessage();				
				// 成功发送
				if ("OK".equals(smsCode)) {
					// 设置验证码有效期10分钟
					jedis.setex(phoneNumber + "_validTime", EXPIRETIME, smsVerifyCode);
					// 保存发送的时间
					jedis.set(phoneNumber + "_createTime", Long.toString(System.currentTimeMillis()));
					
					// 设置ip的请求次数，有效期一天
					if (jedis.exists(ip)) {
						int ipCount = Integer.parseInt(jedis.get(ip));
						long expireTime = jedis.ttl(ip);
						jedis.psetex(ip, expireTime * 1000, String.valueOf(ipCount + 1));
					} else {
						jedis.setex(ip, 60 * 60 * 24, "1");
					}	
					message.setCode(200);
					message.setMsg("短信发送成功");
					message.setDescription("success");
					existphoneNumber = "_";
					return message;
				}else{
					//返回阿里云服务器参数
					message.setDescription(smsCode);
					message.setMsg(smsMessage);
					message.setCode(503);
					existphoneNumber = "_";
					return message;
				}
			} else {
				message.setCode(402);
				message.setMsg("等待");
				message.setDescription("waiting");
				return message;
			}
		} finally {
			jedis.close();
		}
		
	}

	/**
	 * 验证短信验证码
	 * 
	 * @param phoneNumber  手机号          
	 * @param smsVerifyCode 验证码
	 * @return 返回短信验证码的验证结果
	 */

	public Message verifySmsCode(String phoneNumber, String smsVerifyCode) {
		Jedis jedis = RedisUtil.getJedis();
		Message message=new Message();
		if (jedis == null) {
			message.setCode(501);
			message.setMsg("发送失败");
			message.setDescription("redis未连接");
			return message;
		}
		if(phoneNumber==""){
			message.setCode(500);
			message.setMsg("发送失败");
			message.setDescription("phoneNumber is null");
			return message;
		}
		
		try {
			// 判断是否是正确验证码
			if (jedis.exists(phoneNumber + "_validTime")) {
				String pwd = jedis.get(phoneNumber + "_validTime");
				if (pwd.equals(smsVerifyCode)) {
					// 验证成功删除该验证码
					jedis.del(phoneNumber + "_validTime");
					message.setCode(200);
					message.setMsg("短信验证成功");
					message.setDescription("success");
					return message;
				}
			}
		} finally {
			jedis.close();
		}
		message.setMsg("验证码错误或验证码失效");
		message.setCode(403);
		message.setDescription("Is not vaildCode");
		return message;
	}
}
