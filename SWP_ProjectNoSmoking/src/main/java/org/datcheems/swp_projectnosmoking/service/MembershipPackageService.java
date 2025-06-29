package org.datcheems.swp_projectnosmoking.service;

import lombok.RequiredArgsConstructor;
import org.datcheems.swp_projectnosmoking.dto.request.MembershipPackageRequest;
import org.datcheems.swp_projectnosmoking.dto.response.MembershipPackageResponse;
import org.datcheems.swp_projectnosmoking.dto.response.ResponseObject;
import org.datcheems.swp_projectnosmoking.entity.MembershipPackage;
import org.datcheems.swp_projectnosmoking.repository.MembershipPackageRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MembershipPackageService {

    private final MembershipPackageRepository repository;

    private MembershipPackageResponse toResponse(MembershipPackage pkg) {
        return MembershipPackageResponse.builder()
                .Id(pkg.getId())
                .Name(pkg.getName())
                .Description(pkg.getDescription())
                .Duration(pkg.getDuration())
                .Price(pkg.getPrice())
                .ReleaseDate(pkg.getReleaseDate())
                .EndDate(pkg.getEndDate())
                .build();
    }

    public ResponseObject<List<MembershipPackageResponse>> getAllPackages() {
        List<MembershipPackage> packages = repository.findAll();
        List<MembershipPackageResponse> responseList = new ArrayList<>();
        for (MembershipPackage pkg : packages) {
            responseList.add(toResponse(pkg));
        }

        ResponseObject<List<MembershipPackageResponse>> response = new ResponseObject<>();
        response.setStatus("success");
        response.setMessage("Lấy danh sách thành công");
        response.setData(responseList);
        return response;
    }

    public ResponseObject<MembershipPackageResponse> getPackageById(Long id) {
        Optional<MembershipPackage> optional = repository.findById(id);
        ResponseObject<MembershipPackageResponse> response = new ResponseObject<>();
        if (optional.isPresent()) {
            response.setStatus("success");
            response.setMessage("Tìm thấy gói thành viên");
            response.setData(toResponse(optional.get()));
        } else {
            response.setStatus("fail");
            response.setMessage("Không tìm thấy gói thành viên với ID = " + id);
            response.setData(null);
        }
        return response;
    }

    public ResponseObject<MembershipPackageResponse> createPackage(MembershipPackageRequest request) {
        MembershipPackage pkg = new MembershipPackage();
        pkg.setName(request.getName());
        pkg.setDescription(request.getDescription());
        pkg.setDuration(request.getDuration());
        pkg.setPrice(request.getPrice());
        pkg.setReleaseDate(request.getReleaseDate());
        pkg.setEndDate(request.getEndDate());

        MembershipPackage saved = repository.save(pkg);
        ResponseObject<MembershipPackageResponse> response = new ResponseObject<>();
        response.setStatus("success");
        response.setMessage("Tạo gói thành viên thành công");
        response.setData(toResponse(saved));
        return response;
    }

    public ResponseObject<MembershipPackageResponse> updatePackage(Long id, MembershipPackageRequest request) {
        Optional<MembershipPackage> optional = repository.findById(id);
        ResponseObject<MembershipPackageResponse> response = new ResponseObject<>();
        if (optional.isPresent()) {
            MembershipPackage pkg = optional.get();
            pkg.setName(request.getName());
            pkg.setDescription(request.getDescription());
            pkg.setDuration(request.getDuration());
            pkg.setPrice(request.getPrice());
            pkg.setReleaseDate(request.getReleaseDate());
            pkg.setEndDate(request.getEndDate());

            MembershipPackage updated = repository.save(pkg);

            response.setStatus("success");
            response.setMessage("Cập nhật gói thành viên thành công");
            response.setData(toResponse(updated));
        } else {
            response.setStatus("fail");
            response.setMessage("Không tìm thấy gói thành viên để cập nhật");
            response.setData(null);
        }
        return response;
    }

    public ResponseObject<String> deletePackage(Long id) {
        ResponseObject<String> response = new ResponseObject<>();
        if (repository.existsById(id)) {
            repository.deleteById(id);
            response.setStatus("success");
            response.setMessage("Xóa thành công");
            response.setData("Gói thành viên đã bị xóa");
        } else {
            response.setStatus("fail");
            response.setMessage("Không tìm thấy gói thành viên để xóa");
            response.setData(null);
        }
        return response;
    }
}
