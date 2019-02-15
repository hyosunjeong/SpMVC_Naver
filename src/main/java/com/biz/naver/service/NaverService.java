package com.biz.naver.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import com.biz.naver.config.NaverClient;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class NaverService {
	
	String movieURL = "https://openapi.naver.com/v1/search/movie.json";
	String bookURL = "https://openapi.naver.com/v1/search/book.json";
	String newsURL = "https://openapi.naver.com/v1/search/news.json";
	
	
	private Log logger = LogFactory.getLog(this.getClass());
	
	/*
	 * JSON형태의 문자열을 매개변수로 받아서
	 * JSON Object로 parsing 한 후
	 * List<VO>같은 JavaObject형으로 변환하여
	 * controller로 return 하도록 작성
	 */
	
	public JSONArray getObject(String jsonString) {
		/*
		 * json String을 JSONObject로 변환 작업 실행
		 */
		
		// 1. JSONParser 객체 생성
		JSONParser jp = new JSONParser();
		
		// 2. JSONParser 객체를 경유해서 문자열을
		// 		JSONObject로 일단 변환
		JSONObject jo = null;
		
		try {
			jo = (JSONObject) jp.parse(jsonString);
			
			long longTotal = (long) jo.get("total");
			String lastDate = (String)jo.get("lastBuildDate");
			
			log.debug("요청한 시각: " + lastDate.toString());
			log.debug("수신한 데이터 개수: " + longTotal);
			
			/*
			 * 문자열 중에서 도서정보가 포함된 영역만 추출
			 * 도서정보가 포함된 영역의 key items
			 */
			// items 항목 부분만 추출해서 JSONArray로 변환
			JSONArray items = (JSONArray) jo.get("items");
			int itemsLen = items.size();
			
			// JSONArray는 확장for를 사용할 수 없다.
			for(int i = 0; i < itemsLen; i++) {
				log.debug(i +"번째 데이터" + items.get(i));
			}
			
			return items;
					
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	// 도서정보 검색
	public String getString(String cate, String searchText) {
		
		log.debug("반갑습니다");
		
		// id와 key 세팅
		String clientId = NaverClient.ID;
		String clientKey = NaverClient.KEY;
		
		// 검색할 문자열 하나 선언
		// String text="자바";
		

		
		try {
			// 검색문자열을 Naver로 보내기전에 Encoding을 실시
			String text= URLEncoder.encode(searchText,"UTF-8");
			String apiURL = "https://openapi.naver.com/v1/search/book.json";
			
			if(cate.equalsIgnoreCase("MOVIE")) {
				apiURL= movieURL;
			}
			if(cate.equalsIgnoreCase("NEWS")) {
				apiURL = newsURL;
			}
			
			apiURL += "?query=" + text;
			
			
			// URL 객체로 생성
			URL url = new URL(apiURL);
			
			// HttpRequest로 변환
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			
			
			// 접속정보를 Setting
			conn.setRequestMethod("GET"); // 메소드값을 GET 형식으로 만든다.
			conn.setRequestProperty("X-Naver-Client-Id", clientId); // Property=>변수 X-Naver-Client-Id변수이름으로 clientId를 셋팅 
			conn.setRequestProperty("X-Naver-Client-Secret", clientKey); //X-Naver-Client-Secret변수이름으로 clientKey를 셋팅 
			
			// Naver에게 요청을 보내서
			// 내 요청에 응답할 수 있느냐 라고 묻기
			int resCode = conn.getResponseCode(); //200이라는 숫자를 보여주는데 이 숫자를 resCode에 담아둔다.
			
			BufferedReader buffer;
			if(resCode == 200) {
				// 데이터를 수신할 준비
				InputStreamReader is = new InputStreamReader(conn.getInputStream()); // 데이터링크가 성립이 된 후 naver가 보내는 Data들을 inputStreamReader에 담아둔다
				
				buffer = new BufferedReader(is); // 그 결과를 buffer에 보관
				
			}else {
				// 오류가 무엇인지 분석
				InputStreamReader is = new InputStreamReader(conn.getErrorStream());
				buffer = new BufferedReader(is);
			}
			
			String reader ="";
			String readStrings = "";
			while(true) {
				reader = buffer.readLine();
				if(reader == null) break;
				log.debug(reader);
				
				readStrings += reader; //한개의 문자열로 붙여라
			}
			
			buffer.close();
			return readStrings; //controller로 return
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
		
	}

}
