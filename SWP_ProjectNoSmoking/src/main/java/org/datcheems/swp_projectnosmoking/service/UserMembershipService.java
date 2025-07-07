package org.datcheems.swp_projectnosmoking.service;

import lombok.RequiredArgsConstructor;
import org.datcheems.swp_projectnosmoking.dto.request.UserMembershipRequest;
import org.datcheems.swp_projectnosmoking.dto.response.ResponseObject;
import org.datcheems.swp_projectnosmoking.dto.response.UserMembershipResponse;
import org.datcheems.swp_projectnosmoking.entity.Member;
import org.datcheems.swp_projectnosmoking.entity.MembershipPackage;
import org.datcheems.swp_projectnosmoking.entity.User_Membership;
import org.datcheems.swp_projectnosmoking.repository.MemberRepository;
import org.datcheems.swp_projectnosmoking.repository.MembershipPackageRepository;
import org.datcheems.swp_projectnosmoking.repository.UserMembershipRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserMembershipService {

    private final UserMembershipRepository userMembershipRepository;
    private final MemberRepository memberRepository;
    private final MembershipPackageRepository membershipPackageRepository;

    private UserMembershipResponse toResponse(User_Membership entity) {
        UserMembershipResponse response = UserMembershipResponse.builder()
                .membershipId(entity.getMembershipId())
                .userId(entity.getMember().getUserId())
                .userName(entity.getMember().getUser().getFullName())
                .membershipPackageId(entity.getMembershipPackage().getId())
                .membershipPackageName(entity.getMembershipPackage().getName())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .status(entity.getStatus())
                .build();
        return response;
    }

    public ResponseObject<List<UserMembershipResponse>> getAll() {
        List<User_Membership> list = userMembershipRepository.findAll();
        List<UserMembershipResponse> responseList = new ArrayList<>();

        for (User_Membership entity : list) {
            responseList.add(toResponse(entity));
        }

        ResponseObject<List<UserMembershipResponse>> response = new ResponseObject<>();
        response.setStatus("success");
        response.setMessage("Lấy danh sách thành công");
        response.setData(responseList);
        return response;
    }
    public ResponseObject<Double> getTotalRevenue() {
        List<User_Membership> records = userMembershipRepository.findAll();
        double totalRevenue = 0.0;

        for (User_Membership record : records) {
            MembershipPackage pack = record.getMembershipPackage();
            if (pack != null && record.getStatus() != null && record.getStatus().equalsIgnoreCase("ACTIVE")) {
                totalRevenue += pack.getPrice() != null ? pack.getPrice() : 0.0;
            }
        }

        ResponseObject<Double> response = new ResponseObject<>();
        response.setStatus("success");
        response.setMessage("Tổng doanh thu tính thành công");
        response.setData(totalRevenue);
        return response;
    }


    public ResponseObject<UserMembershipResponse> getById(Long id) {
        Optional<User_Membership> optional = userMembershipRepository.findById(id);
        ResponseObject<UserMembershipResponse> response = new ResponseObject<>();

        if (optional.isPresent()) {
            response.setStatus("success");
            response.setMessage("Tìm thấy bản ghi");
            response.setData(toResponse(optional.get()));
        } else {
            response.setStatus("fail");
            response.setMessage("Không tìm thấy bản ghi với ID = " + id);
            response.setData(null);
        }

        return response;
    }

    public ResponseObject<UserMembershipResponse> create(UserMembershipRequest request) {
        ResponseObject<UserMembershipResponse> response = new ResponseObject<>();

        Optional<Member> memberOpt = memberRepository.findById(request.getUserId());
        Optional<MembershipPackage> packageOpt = membershipPackageRepository.findById(request.getMembershipPackageId());

        if (memberOpt.isEmpty() || packageOpt.isEmpty()) {
            response.setStatus("fail");
            response.setMessage("Người dùng hoặc gói thành viên không tồn tại");
            response.setData(null);
            return response;
        }

        User_Membership entity = new User_Membership();
        entity.setMember(memberOpt.get());
        entity.setMembershipPackage(packageOpt.get());
        entity.setStartDate(request.getStartDate());
        entity.setEndDate(request.getEndDate());
        entity.setStatus(request.getStatus());

        User_Membership saved = userMembershipRepository.save(entity);

        response.setStatus("success");
        response.setMessage("Tạo bản ghi thành công");
        response.setData(toResponse(saved));
        return response;
    }

    public ResponseObject<UserMembershipResponse> update(Long id, UserMembershipRequest request) {
        ResponseObject<UserMembershipResponse> response = new ResponseObject<>();

        Optional<User_Membership> existingOpt = userMembershipRepository.findById(id);
        if (existingOpt.isEmpty()) {
            response.setStatus("fail");
            response.setMessage("Không tìm thấy bản ghi để cập nhật");
            response.setData(null);
            return response;
        }

        Optional<Member> memberOpt = memberRepository.findById(request.getUserId());
        Optional<MembershipPackage> packageOpt = membershipPackageRepository.findById(request.getMembershipPackageId());

        if (memberOpt.isEmpty() || packageOpt.isEmpty()) {
            response.setStatus("fail");
            response.setMessage("Người dùng hoặc gói thành viên không tồn tại");
            response.setData(null);
            return response;
        }

        User_Membership entity = existingOpt.get();
        entity.setMember(memberOpt.get());
        entity.setMembershipPackage(packageOpt.get());
        entity.setStartDate(request.getStartDate());
        entity.setEndDate(request.getEndDate());
        entity.setStatus(request.getStatus());

        User_Membership updated = userMembershipRepository.save(entity);

        response.setStatus("success");
        response.setMessage("Cập nhật bản ghi thành công");
        response.setData(toResponse(updated));
        return response;
    }

    public ResponseObject<String> delete(Long id) {
        ResponseObject<String> response = new ResponseObject<>();

        if (userMembershipRepository.existsById(id)) {
            userMembershipRepository.deleteById(id);
            response.setStatus("success");
            response.setMessage("Xóa thành công");
            response.setData("Đã xóa bản ghi có ID = " + id);
        } else {
            response.setStatus("fail");
            response.setMessage("Không tìm thấy bản ghi để xóa");
            response.setData(null);
        }

        return response;
    }
    public ResponseObject<Boolean> checkUserHasActiveMembership(Long userId) {
        ResponseObject<Boolean> response = new ResponseObject<>();

        List<User_Membership> memberships = userMembershipRepository.findByMember_UserId(userId);
        boolean hasActive = memberships.stream().anyMatch(m ->
                "ACTIVE".equalsIgnoreCase(m.getStatus()) &&
                        m.getEndDate() != null &&
                        !m.getEndDate().isBefore(LocalDate.now())
        );
        response.setStatus("success");
        response.setMessage("Kiểm tra thành công");
        response.setData(hasActive);

        return response;
    }

    public ResponseObject<UserMembershipResponse> checkUserMembership(Long userId) {
        ResponseObject<UserMembershipResponse> response = new ResponseObject<>();

        try {
            List<User_Membership> memberships = userMembershipRepository.findByMember_UserId(userId);

            // Tìm membership active và chưa hết hạn
            Optional<User_Membership> activeMembership = memberships.stream()
                    .filter(m -> "ACTIVE".equalsIgnoreCase(m.getStatus()))
                    .filter(m -> m.getEndDate() != null && !m.getEndDate().isBefore(LocalDate.now()))
                    .findFirst();

            if (activeMembership.isPresent()) {
                response.setStatus("success");
                response.setMessage("User has active membership");
                response.setData(toResponse(activeMembership.get()));
            } else {
                response.setStatus("error");
                response.setMessage("No active membership found");
                response.setData(null);
            }

        } catch (Exception e) {
            response.setStatus("error");
            response.setMessage("Failed to check membership: " + e.getMessage());
            response.setData(null);
        }
        return response;
    }
}
