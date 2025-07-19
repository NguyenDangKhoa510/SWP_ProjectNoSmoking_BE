package org.datcheems.swp_projectnosmoking.service;

import lombok.RequiredArgsConstructor;
import org.datcheems.swp_projectnosmoking.dto.request.UserMembershipRequest;
import org.datcheems.swp_projectnosmoking.dto.response.ResponseObject;
import org.datcheems.swp_projectnosmoking.dto.response.UserMembershipResponse;
import org.datcheems.swp_projectnosmoking.entity.Member;
import org.datcheems.swp_projectnosmoking.entity.MembershipPackage;
import org.datcheems.swp_projectnosmoking.entity.UserMembership;
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

    private UserMembershipResponse toResponse(UserMembership entity) {
        UserMembershipResponse response = UserMembershipResponse.builder()
                .membershipId(entity.getMembershipId())
                .userId(entity.getMember().getUserId())
                .userName(entity.getMember().getUser().getFullName())
                .membershipPackageId(entity.getMembershipPackageId() .getId())
                .membershipPackageName(entity.getMembershipPackageId().getName())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .status(entity.getStatus())
                .build();
        return response;
    }

    public ResponseObject<List<UserMembershipResponse>> getAll() {
        List<UserMembership> list = userMembershipRepository.findAll();
        List<UserMembershipResponse> responseList = new ArrayList<>();

        for (UserMembership entity : list) {
            responseList.add(toResponse(entity));
        }

        ResponseObject<List<UserMembershipResponse>> response = new ResponseObject<>();
        response.setStatus("success");
        response.setMessage("Lấy danh sách thành công");
        response.setData(responseList);
        return response;
    }


//    public ResponseObject<Double> getTotalRevenue() {
//        List<UserMembership> records = userMembershipRepository.findAll();
//        double totalRevenue = 0.0;
//
//        for (UserMembership record : records) {
//            MembershipPackage pack = record.getMembershipPackageId();
//            if (pack != null && record.getStatus() != null && record.getStatus().equalsIgnoreCase("ACTIVE")) {
//                totalRevenue += pack.getPrice() != null ? pack.getPrice() : 0.0;
//            }
//        }
//
//        ResponseObject<Double> response = new ResponseObject<>();
//        response.setStatus("success");
//        response.setMessage("Tổng doanh thu tính thành công");
//        response.setData(totalRevenue);
//        return response;
//    }



    public ResponseObject<UserMembershipResponse> getById(Long id) {
        Optional<UserMembership> optional = userMembershipRepository.findById(id);
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
        if (request.getTransactionId() != null && !request.getTransactionId().isEmpty()) {
            if (userMembershipRepository.existsByTransactionId(request.getTransactionId())) {
                response.setStatus("already_processed");
                response.setMessage("Giao dịch này đã được xử lý. Membership đã tồn tại với transactionId này.");
                response.setData(null);
                return response;
            }
        }
        List<UserMembership> memberships = userMembershipRepository.findByMember_UserId(request.getUserId());
        boolean hasActive = memberships.stream().anyMatch(m ->
                "ACTIVE".equalsIgnoreCase(m.getStatus()) &&
                        m.getEndDate() != null &&
                        !m.getEndDate().isBefore(LocalDate.now())
        );

        if (hasActive) {
            response.setStatus("fail");
            response.setMessage("Người dùng đã có gói thành viên đang hoạt động");
            response.setData(null);
            return response;
        }
        boolean isDuplicate = memberships.stream().anyMatch(m ->
                m.getMembershipPackageId().getId() == request.getMembershipPackageId() &&
                        m.getStartDate().equals(request.getStartDate()) &&
                        m.getEndDate().equals(request.getEndDate())
        );

        if (isDuplicate) {
            response.setStatus("fail");
            response.setMessage("Người dùng đã đăng ký gói này trong khoảng thời gian tương ứng");
            response.setData(null);
            return response;
        }

        UserMembership entity = new UserMembership();
        entity.setMember(memberOpt.get());
        entity.setMembershipPackageId(packageOpt.get());
        entity.setStartDate(request.getStartDate());
        entity.setEndDate(request.getEndDate());
        entity.setStatus(request.getStatus());
        entity.setTransactionId(request.getTransactionId());

        try {
            UserMembership saved = userMembershipRepository.save(entity);
            response.setStatus("success");
            response.setMessage("Tạo bản ghi thành công");
            response.setData(toResponse(saved));
        } catch (Exception ex) {
            if (ex.getMessage() != null && ex.getMessage().contains("duplicate key")) {
                response.setStatus("already_processed");
                response.setMessage("Giao dịch đã được xử lý (DB unique). Membership đã tồn tại với transactionId này.");
                response.setData(null);
            } else {
                response.setStatus("error");
                response.setMessage("Lỗi hệ thống: " + ex.getMessage());
                response.setData(null);
            }
        }
        return response;
    }

    public ResponseObject<UserMembershipResponse> update(Long id, UserMembershipRequest request) {
        ResponseObject<UserMembershipResponse> response = new ResponseObject<>();

        Optional<UserMembership> existingOpt = userMembershipRepository.findById(id);
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

        UserMembership entity = existingOpt.get();
        entity.setMember(memberOpt.get());
        entity.setMembershipPackageId(packageOpt.get());
        entity.setStartDate(request.getStartDate());
        entity.setEndDate(request.getEndDate());
        entity.setStatus(request.getStatus());

        UserMembership updated = userMembershipRepository.save(entity);

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

        List<UserMembership> memberships = userMembershipRepository.findByMember_UserId(userId);
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
            List<UserMembership> memberships = userMembershipRepository.findByMember_UserId(userId);

            // Tìm membership active và chưa hết hạn
            Optional<UserMembership> activeMembership = memberships.stream()
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

    public ResponseObject<Double> getTotalRevenue() {
        List<UserMembership> records = userMembershipRepository.findAll();
        double totalRevenue = 0.0;

        for (UserMembership record : records) {
            MembershipPackage pack = record.getMembershipPackageId();
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

    public void updateExpiredMembershipStatuses() {
        List<UserMembership> memberships = userMembershipRepository.findAll();

        for (UserMembership membership : memberships) {
            if ("ACTIVE".equalsIgnoreCase(membership.getStatus())
                    && membership.getEndDate() != null
                    && membership.getEndDate().isBefore(LocalDate.now())) {
                membership.setStatus("EXPIRED");
                userMembershipRepository.save(membership);
            }
        }
    }
}
