package za.co.hailer.aortacore.controller;

import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import za.co.hailer.aortacore.model.AccessToken;
import za.co.hailer.aortacore.model.RegistrationResponse;

import java.util.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private RestTemplate restTemplate;
    public UserController(
            RestTemplate restTemplate
    ){
        this.restTemplate = restTemplate;
    }

    @PostMapping("/login")
    public ResponseEntity login(
            @RequestParam("grant_type") String grantType,
            @RequestParam("client_id") String clientId,
            @RequestParam("client_secret") String clientSecret,
            @RequestParam("username") String username,
            @RequestParam("password") String password
    ){
        String url = "http://keycloak-http/auth/realms/Aorta/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("grant_type", grantType);
        map.add("client_id", clientId);
        map.add("client_secret", clientSecret);
        map.add("username", username);
        map.add("password", password);

        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(map, headers);

        return this.restTemplate.postForEntity(url, entity, AccessToken.class);
    }

    @PostMapping("/register")
    public ResponseEntity register(
            @RequestParam("username")String username,
            @RequestParam("password")String password,
            @RequestParam("email")String email,
            @RequestParam("first_name")String firstName,
            @RequestParam("last_name")String lastName
    ){
        AccessToken adminAccessToken = loginAsAdmin();
        String url = "http://keycloak-http/auth/admin/realms/Aorta/users";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(adminAccessToken.getAccess_token());

        JSONObject credential = new JSONObject();
        credential.put("type", "password");
        credential.put("value", password);

        JSONObject userData = new JSONObject();
        userData.put("username", username);
        userData.put("firstName", firstName);
        userData.put("lastName", lastName);
        userData.put("email", email);
        userData.put("realmRoles", Collections.singletonList("app-user"));
        userData.put("enabled", true);
        userData.put("credentials", Collections.singletonList(credential));

        HttpEntity<String> entity = new HttpEntity<>(userData.toString(), headers);

        return this.restTemplate.postForEntity(url, entity, RegistrationResponse.class);
    }

    private AccessToken loginAsAdmin(){
        String url = "http://keycloak-http/auth/realms/master/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "password");
        map.add("client_id", "admin-cli");
        map.add("username", "admin");
        map.add("password", "Lionelmessih");

        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(map, headers);
        return this.restTemplate.postForEntity(url, entity, AccessToken.class).getBody();
    }

}
