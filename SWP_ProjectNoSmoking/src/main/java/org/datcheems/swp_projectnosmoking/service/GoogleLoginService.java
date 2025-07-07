package org.datcheems.swp_projectnosmoking.service;

import com.nimbusds.jose.shaded.gson.Gson;
import org.datcheems.swp_projectnosmoking.dto.response.AuthenticationResponse;
import org.datcheems.swp_projectnosmoking.dto.response.NeedUsernameResponse;
import org.datcheems.swp_projectnosmoking.dto.response.ResponseObject;
import org.datcheems.swp_projectnosmoking.entity.Member;
import org.datcheems.swp_projectnosmoking.entity.Role;
import org.datcheems.swp_projectnosmoking.entity.User;
import org.datcheems.swp_projectnosmoking.repository.MemberRepository;
import org.datcheems.swp_projectnosmoking.repository.RoleRepository;
import org.datcheems.swp_projectnosmoking.repository.UserRepository;
import org.datcheems.swp_projectnosmoking.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class GoogleLoginService {


    private String googleClientId = "82302107538-mjprlclm2pvioc2ojv5q0mjjibkbpdni.apps.googleusercontent.com";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private MemberRepository memberRepository;


    private static final String GOOGLE_USER_INFO_URL = "https://www.googleapis.com/oauth2/v3/tokeninfo?access_token=";

    public ResponseObject<?> authenticateWithGoogle(String googleAccessToken) {
        ResponseObject<?> response = new ResponseObject<>();

        try {
            String userInfoJson = verifyGoogleToken(googleAccessToken);
            Map<String, Object> userInfo = parseGoogleUserInfo(userInfoJson);

            Optional<User> optionalUser = userRepository.findByEmail((String) userInfo.get("email"));
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                String token = jwtUtils.generateToken(user);

                ResponseObject<AuthenticationResponse> successResponse = new ResponseObject<>();
                successResponse.setStatus("success");
                successResponse.setMessage("Google authentication successful");
                successResponse.setData(new AuthenticationResponse(token, true));
                return successResponse;
            } else {
                NeedUsernameResponse data = new NeedUsernameResponse(
                        (String) userInfo.get("email"),
                        (String) userInfo.get("name")
                );

                ResponseObject<NeedUsernameResponse> needUsernameResponse = new ResponseObject<>();
                needUsernameResponse.setStatus("need_username");
                needUsernameResponse.setMessage("First time login, please choose a username.");
                needUsernameResponse.setData(data);

                return needUsernameResponse;
            }

        } catch (RuntimeException e) {
            response.setStatus("error");
            response.setMessage(e.getMessage());
            response.setData(null);
        }

        return response;
    }

    public ResponseObject<AuthenticationResponse> createUserWithGoogle(String email, String username) {
        ResponseObject<AuthenticationResponse> response = new ResponseObject<>();

        // Check username existed
        if (userRepository.existsByUsername(username)) {
            response.setStatus("error");
            response.setMessage("Username already exists!");
            response.setData(null);
            return response;
        }

        // Create user
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setFullName(""); // optional: lưu name lúc đầu cũng được
        newUser.setPassword("defaultPassword");

        Role defaultRole = roleRepository.findByName(Role.RoleName.MEMBER)
                .orElseThrow(() -> new RuntimeException("Default role not found"));
        newUser.getRoles().add(defaultRole);
        newUser.setStatus(User.Status.ACTIVE);

        userRepository.save(newUser);

        Member member = new Member();
        member.setUser(newUser);
        memberRepository.save(member);

        // Generate JWT
        String token = jwtUtils.generateToken(newUser);

        response.setStatus("success");
        response.setMessage("Account created successfully");
        response.setData(new AuthenticationResponse(token, true));

        return response;
    }




    public String verifyGoogleToken(String googleAccessToken) {
        // Gửi yêu cầu tới Google để xác minh Access Token
        RestTemplate restTemplate = new RestTemplate();
        String url = GOOGLE_USER_INFO_URL + googleAccessToken;
        return restTemplate.getForObject(url, String.class);
    }

    public Map<String, Object> parseGoogleUserInfo(String userInfoJson) {
        Gson gson = new Gson();
        return gson.fromJson(userInfoJson, HashMap.class);
    }
}
