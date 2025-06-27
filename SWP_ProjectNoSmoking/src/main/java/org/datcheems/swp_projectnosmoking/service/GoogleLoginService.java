package org.datcheems.swp_projectnosmoking.service;

import com.nimbusds.jose.shaded.gson.Gson;
import org.datcheems.swp_projectnosmoking.dto.response.AuthenticationResponse;
import org.datcheems.swp_projectnosmoking.dto.response.ResponseObject;
import org.datcheems.swp_projectnosmoking.entity.Member;
import org.datcheems.swp_projectnosmoking.entity.Role;
import org.datcheems.swp_projectnosmoking.entity.User;
import org.datcheems.swp_projectnosmoking.repository.MemberRepository;
import org.datcheems.swp_projectnosmoking.repository.RoleRepository;
import org.datcheems.swp_projectnosmoking.repository.UserRepository;
import org.datcheems.swp_projectnosmoking.uitls.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

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

    public ResponseObject<AuthenticationResponse> authenticateWithGoogle(String googleAccessToken) {
        ResponseObject<AuthenticationResponse> response = new ResponseObject<>();

        try {
            // Xác minh token Google và lấy thông tin người dùng
            String userInfoJson = verifyGoogleToken(googleAccessToken);

            Map<String, Object> userInfo = parseGoogleUserInfo(userInfoJson);

            // Tìm người dùng trong hệ thống hoặc tạo mới nếu không có
            User user = userRepository.findByUsername((String) userInfo.get("email"))
                    .orElseGet(() -> {
                        User newUser = new User();
                        newUser.setUsername((String) userInfo.get("email"));
                        newUser.setEmail((String) userInfo.get("email"));
                        newUser.setFullName((String) userInfo.get("name"));
                        newUser.setPassword("defaultPassword");

                        // Gán role mặc định cho người dùng mới
                        Role defaultRole = roleRepository.findByName(Role.RoleName.MEMBER)
                                .orElseThrow(() -> new RuntimeException("Default role not found"));
                        newUser.getRoles().clear();
                        newUser.getRoles().add(defaultRole);
                        newUser.setStatus(User.Status.ACTIVE);

                        userRepository.save(newUser);

                        Member member = new Member();
                        member.setUser(newUser);
                        memberRepository.save(member);

                        return newUser;
                    });

            // Tạo token JWT
            String token = jwtUtils.generateToken(user);

            // Thiết lập ResponseObject với thông tin thành công
            response.setStatus("success");
            response.setMessage("Google authentication successful");
            response.setData(new AuthenticationResponse(token, true));

        } catch (RuntimeException e) {
            // Trong trường hợp có lỗi, thiết lập ResponseObject lỗi
            response.setStatus("error");
            response.setMessage(e.getMessage());
            response.setData(null);  // Có thể thêm chi tiết lỗi vào đây nếu muốn, như stack trace hoặc code lỗi.
        }

        return response;
    }

    private String verifyGoogleToken(String googleAccessToken) {
        // Gửi yêu cầu tới Google để xác minh Access Token
        RestTemplate restTemplate = new RestTemplate();
        String url = GOOGLE_USER_INFO_URL + googleAccessToken;
        return restTemplate.getForObject(url, String.class);
    }

    private Map<String, Object> parseGoogleUserInfo(String userInfoJson) {
        Gson gson = new Gson();
        return gson.fromJson(userInfoJson, HashMap.class);
    }
}
