package com.example.pojo;

import org.springframework.stereotype.Component;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author liyu
 *
 */
@Component
public class Message {
	/**
	 * 自定义错误码
	 */
	@ApiModelProperty(value="错误码")
	private Integer code;
	/**
	 * 给用户看的错误提示
	 */
	@ApiModelProperty(value="给用户看的错误提示")
	private String msg;
	/**
	 * 程序内部错误追查信息
	 */
	@ApiModelProperty(value="内部错误追查信息")
	private String description;

	/**
	 * 后端关联日志的追踪号
	 */
	@ApiModelProperty("后端关联日志的追踪号")
	private String trackId;

	public String getTrackId() {
		return trackId;
	}

	public void setTrackId(String trackId) {
		this.trackId = trackId;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
