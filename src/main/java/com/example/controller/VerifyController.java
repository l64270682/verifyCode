package com.example.controller;

import com.example.pojo.Message;
import com.example.service.ImageService;
import com.example.service.SmsMessage;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @Author by liyu cdx
 */

@RestController
@RequestMapping("/verification_code/v1")
public class VerifyController {
    @Autowired
    private SmsMessage smsMessage;
    @Autowired
    private ImageService imageService;


    /**
     * 获取图形验证码
     *
     * @param imageStyle 验证码类型
     * （staticCode 静态验证码、arithmeticCode 算数验证码、sortCode 排序验证码）
     */
    @ApiOperation(value="请求发送图形验证码", notes="使用arithmeticCode、sortCode 分别获取算术验证码、排序验证码、其他则获取静态验证码")
    @ApiImplicitParam(name = "imageStyle", value = "图形验证码风格", required = true, dataType = "String",paramType = "path")
    @GetMapping(value = "/requestCaptcha/{imageStyle}")
    public  void requestCaptcha(@PathVariable String imageStyle, HttpServletResponse response, HttpSession session)
            throws IOException {
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");
        // 生成随机字串
        String verifyImage = imageService.getImageCode(imageStyle, response.getOutputStream());

        // 存入会话session
        session.setAttribute("verifyImage", verifyImage);
    }

    /**
     * 验证图形验证码
     *
     * @param requestCode 用户输入验证码
     * @return 返回验证结果
     */
    @ApiOperation(value="验证图形验证码", notes="验证图形验证码")
    @ApiImplicitParam(name="requestCode",value="图形验证码",required = true, dataType = "String",paramType = "path")
    
    @PostMapping(value = "/verifyCaptcha/{requestCode}")
    public ResponseEntity<?> verifyCaptcha(@PathVariable String requestCode, HttpSession session) {
        String verifyImage = (String) (session.getAttribute("verifyImage"));
        Message message = imageService.verifyImageCode(verifyImage, requestCode);
        return message;
    }

    /**
     * 发送短信验证码
     *
     * @param phoneNumber 手机号
     * @return 返回发送结果
     */
    @ApiOperation(value="请求发送短信验证码", notes="请求发送短信验证码")
    @ApiImplicitParam(name="phoneNumber",value="目标手机号码",required = true, dataType = "String", paramType = "query")
    @GetMapping(value = "/requestSmsCode")
    public   Message requestSmsCode(String phoneNumber, HttpServletRequest request, HttpSession session) {
        String ip = null;
        if (request.getHeader("x-forwarded-for") == null) {
            ip = request.getRemoteAddr();
        } else {
            ip = request.getHeader("x-forwarded-for");
        }
        Message message = smsMessage.sendMessage(phoneNumber, ip);
        return message;

    }

    /**
     * 验证短信验证码
     *
     * @param phoneNumber 手机号
     * @param smsCode     验证码
     * @return 返回验证结果
     */
    
   
    @ApiOperation(value="验证短信验证码", notes="验证短信验证码")
    @ApiImplicitParams({
    @ApiImplicitParam(name="phoneNumber",value="用户手机号码",required = true, dataType = "String",paramType = "query"),
    @ApiImplicitParam(name="smsCode",value="短信验证码",required = true, dataType = "String",paramType = "path")
    })
    @PostMapping(value = "/verifySmsCode/{smsCode}")
    public  @ResponseBody Message verifySmsCode(String phoneNumber, @PathVariable String smsCode, HttpSession session) {
        Message message = smsMessage.verifySmsCode(phoneNumber, smsCode);
        session.removeAttribute("verifyMessage");
        session.setAttribute("verifyMessage", message.getCode());
        return message;
    }


}
