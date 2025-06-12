package org.datcheems.swp_projectnosmoking.service;

import jakarta.transaction.Transactional;
import org.datcheems.swp_projectnosmoking.dto.request.SmokingStatusRequest;
import org.datcheems.swp_projectnosmoking.dto.response.SmokingStatusResponse;
import org.datcheems.swp_projectnosmoking.entity.SmokingStatus;
import org.datcheems.swp_projectnosmoking.entity.User;
import org.datcheems.swp_projectnosmoking.exception.ResourceNotFoundException;
import org.datcheems.swp_projectnosmoking.repository.SmokingStatusRepository;
import org.datcheems.swp_projectnosmoking.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class SmokingStatusService {

    private final SmokingStatusRepository smokingStatusRepository;
    private final UserRepository userRepository;

    public SmokingStatusService(SmokingStatusRepository smokingStatusRepository, UserRepository userRepository) {
        this.smokingStatusRepository = smokingStatusRepository;
        this.userRepository = userRepository;
    }

    public SmokingStatusResponse getSmokingStatus(String username) {
        // 1️⃣ Tìm user
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (!optionalUser.isPresent()) {
            throw new ResourceNotFoundException("User not found with username: " + username);
        }
        User user = optionalUser.get();

        // 2️⃣ Tìm SmokingStatus của user
        Optional<SmokingStatus> optionalSmokingStatus = smokingStatusRepository.findByUser(user);
        if (!optionalSmokingStatus.isPresent()) {
            throw new ResourceNotFoundException("Smoking status not found for user: " + username);
        }
        SmokingStatus smokingStatus = optionalSmokingStatus.get();

        // 3️⃣ Map Entity -> Response
        SmokingStatusResponse response = new SmokingStatusResponse();
        response.setCigarettesPerDay(smokingStatus.getCigarettesPerDay());
        response.setFrequency(smokingStatus.getFrequency());
        response.setPricePerPack(smokingStatus.getPricePerPack());
        response.setRecordDate(smokingStatus.getRecordDate());

        return response;
    }

    public void saveOrUpdateSmokingStatus(String username, SmokingStatusRequest request) {
        // 1️⃣ Tìm user
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (!optionalUser.isPresent()) {
            throw new ResourceNotFoundException("User not found with username: " + username);
        }
        User user = optionalUser.get();

        // 2️⃣ Kiểm tra SmokingStatus đã có chưa
        SmokingStatus smokingStatus;
        Optional<SmokingStatus> optionalSmokingStatus = smokingStatusRepository.findByUser(user);
        if (optionalSmokingStatus.isPresent()) {
            // Đã có → cập nhật
            smokingStatus = optionalSmokingStatus.get();
        } else {
            // Chưa có → tạo mới
            smokingStatus = new SmokingStatus();
            smokingStatus.setUser(user);
        }

        // 3️⃣ Set data từ request
        smokingStatus.setCigarettesPerDay(request.getCigarettesPerDay());
        smokingStatus.setFrequency(request.getFrequency());
        smokingStatus.setPricePerPack(request.getPricePerPack());
        smokingStatus.setRecordDate(request.getRecordDate());

        // 4️⃣ Lưu vào DB
        smokingStatusRepository.save(smokingStatus);
    }
}