package com.example.service;

import java.io.OutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.pojo.Message;
import com.example.utils.RandomGraphic;

@Service

public class ImageService {
	@Autowired
	 public Message message;
	
	public  String getImageCode(String i, OutputStream stream) {
		try {
			if (i.equals("arithmeticCode")) {
				return RandomGraphic.createInstance(6).drawAlpha2(RandomGraphic.GRAPHIC_PNG, stream);
			}
			if (i.equals("sortCode")) {
				return RandomGraphic.createInstance(6).drawAlpha3(RandomGraphic.GRAPHIC_PNG, stream);
			}else{
				//返回静态验证码
				return RandomGraphic.createInstance(6).drawAlpha(RandomGraphic.GRAPHIC_PNG, stream);
			}
		} catch (Exception e) {		
			e.printStackTrace();
		}
		return  null;	
	}
	public Message verifyImageCode(String verifyImage,String requestCode){
		if (verifyImage != null && requestCode !=null) {
			requestCode=requestCode.toLowerCase();
			if (verifyImage.equals(requestCode)) {
				Message message=new Message();
				message.setCode(200);
				message.setMsg("验证成功");
				message.setDescription("success");
				return message;
			}
		}	
		message.setCode(403);
		message.setMsg("图片验证码错误或不存在");
		message.setDescription("picture is not valid");
		return message;
	}
}
