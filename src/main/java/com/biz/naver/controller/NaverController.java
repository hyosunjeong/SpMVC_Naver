package com.biz.naver.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.biz.naver.service.NaverService;

import lombok.extern.slf4j.Slf4j;
/*
 * 컨트롤러의 역할
 * 1. web browser가 보내는 request 정보를 수신
 * 	 	request 정보 = URI, form의 입력데이터 등
 * 2. service를 호출하여 연산, DB 작업등을 수행
 * 3. 생성된 데이터와 views(*.jsp)파일을 Rendering 하여
 * 		생성된 데이터 = service로부터 return 받은 데이터
 * 		Rendering = *.jsp + vo의 결합 
 * 4. HTML 코드로 생성한 후 web browser로 response 송신
 * 
 *  >> 사소한 문자열 1개를 보내려고 해도 반드시 *.jsp파일이 있어야 하고
 *  	Rendering 과정을 거쳐야 한다.
 *  	그래서 spring 3.5 이후에서는 @ResponseBody라는 Annotation을 도입해서 
 *  	단순 문자열로 Rendering 하지 않고 browser로 보낼 수 있게 되었다.
 */


@Slf4j
@Controller
public class NaverController {
	
	@Autowired
	NaverService nService;
	
	private Log logger = LogFactory.getLog(this.getClass());
	
	@RequestMapping(value="naver", method=RequestMethod.POST)
	public String naver(@RequestParam String search,
			@RequestParam String cate, Model model) {
		
		log.debug("Form에서 받은 search:" + search);
		String jsonString = nService.getString(cate,search);
		
		JSONArray ja = nService.getObject(jsonString);
		model.addAttribute("NAVER",ja);
		
		return "home";		
	}
	
	@ResponseBody
	@RequestMapping(value="naver.json", method=RequestMethod.POST, produces="application/json; charset=utf8")
	public String naver_json(@RequestParam String search) {
		// @RequestParam String search("search") String search
		// @RequestParam String search 로 쓸 수 있다.
		// @RequestParam String search는 @param("search") String Search를 대체한다. 
		
		log.debug(" Form이 보낸 search: " + search);
		
		logger.debug("네이버 home 열기");
		log.debug("lombok Log로 메시지 보이기");
		
		String bookString = nService.getString("",search);
		
		//return "home";
		//return " Welcom to Korea";
		return bookString;
		
	}
}
