package org.datcheems.swp_projectnosmoking.service;

import com.nimbusds.jose.shaded.gson.Gson;
import org.datcheems.swp_projectnosmoking.dto.response.AuthenticationResponse;
import org.datcheems.swp_projectnosmoking.entity.Role;
import org.datcheems.swp_projectnosmoking.entity.User;
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


    private static final String GOOGLE_USER_INFO_URL = "https://www.googleapis.com/oauth2/v3/tokeninfo?access_token=";

    public AuthenticationResponse authenticateWithGoogle(String googleAccessToken) {
        // Xác minh Access Token từ Google
        String userInfoJson = verifyGoogleToken(googleAccessToken);

        // Lấy thông tin người dùng từ Google
        Map<String, Object> userInfo = parseGoogleUserInfo(userInfoJson);

        // Kiểm tra người dùng trong hệ thống
        User user = userRepository.findByUsername((String) userInfo.get("email"))
                .orElseGet(() -> {
                    // Nếu người dùng không tồn tại, tạo mới người dùng
                    User newUser = new User();
                    newUser.setUsername((String) userInfo.get("email"));  // Email từ Google sẽ là tên đăng nhập
                    newUser.setEmail((String) userInfo.get("email"));
                    newUser.setFullName((String) userInfo.get("name"));  // Tên đầy đủ từ Google

                    // Mã hóa mật khẩu mặc định hoặc tự tạo mật khẩu (nếu cần)
                    // Có thể tạo mật khẩu mặc định hoặc để trống vì Google đã xác thực người dùng
                    newUser.setPassword("defaultPassword");  // Mật khẩu mặc định (hãy mã hóa nó trước khi lưu)

                    // Gán vai trò cho người dùng (ví dụ gán role USER)
                    Role defaultRole = roleRepository.findByName(Role.RoleName.MEMBER)
                            .orElseThrow(() -> new RuntimeException("Default role not found"));
                    newUser.getRoles().clear();  // Xóa các vai trò cũ (nếu có)
                    newUser.getRoles().add(defaultRole);  // Thêm vai trò cho người dùng

                    // Lưu người dùng vào cơ sở dữ liệu
                    userRepository.save(newUser);

                    return newUser; // Trả lại người dùng mới đã được lưu
                });

        // Tạo JWT token
        String token = jwtUtils.generateToken(user);

        return new AuthenticationResponse(token, true);
    }

    private String verifyGoogleToken(String googleAccessToken) {
        // Gửi yêu cầu tới Google để xác minh Access Token
        RestTemplate restTemplate = new RestTemplate();
        String url = GOOGLE_USER_INFO_URL + googleAccessToken;
        return restTemplate.getForObject(url, String.class);
    }

    private Map<String, Object> parseGoogleUserInfo(String userInfoJson) {
        // Parse thông tin người dùng từ JSON trả về từ Google
        Gson gson = new Gson();
        return gson.fromJson(userInfoJson, HashMap.class);
    }
}
