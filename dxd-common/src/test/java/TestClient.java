import cn.com.topnetwork.dxd.system.entity.SysUserVo;
import com.alibaba.fastjson.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

public class TestClient {

    private static Map<String, String> map = new HashMap<String, String>();

    @Before
    public void testToken() throws Exception {
        RestTemplate restTemplate = new RestTemplate();

        SysUserVo user = new SysUserVo();
        user.setUsername("admin");
        user.setPassword("11a254dab80d52bc4a347e030e54d861a9d2cdb2af2185a9ca4a7318e830d04d");
        String jsonUser = JSONObject.toJSONString(user);
        System.out.println(jsonUser);

        String url = "http://127.0.0.1:8088/login";

        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        HttpEntity<String> formEntity = new HttpEntity<String>(jsonUser, headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, formEntity, String.class);

        //System.out.println("body:"+responseEntity.getBody());
        //System.out.println("statusCode:"+responseEntity.getStatusCode());
        //System.out.println("headers:"+responseEntity.getHeaders());

        HttpHeaders responseHeaders = responseEntity.getHeaders();
        if (HttpStatus.OK == responseEntity.getStatusCode()) {
            String token = responseHeaders.get("token").get(0);
            System.out.println("token is :" + token);
            map.put("token",token);
        }
    }

    @Test
    public void testHello() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        String token = map.get("token");
        String url = "http://127.0.0.1:8088/hello";

        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        headers.add("token", token);
        HttpEntity<String> formEntity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, formEntity, String.class);

        System.out.println("body:"+responseEntity.getBody());
        System.out.println("statusCode:"+responseEntity.getStatusCode());
        System.out.println("headers:"+responseEntity.getHeaders());
    }

}
