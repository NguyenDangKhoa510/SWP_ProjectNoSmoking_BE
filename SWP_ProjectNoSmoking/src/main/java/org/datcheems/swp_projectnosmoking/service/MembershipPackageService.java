package org.datcheems.swp_projectnosmoking.service;

import lombok.RequiredArgsConstructor;
import org.datcheems.swp_projectnosmoking.dto.request.MembershipPackageRequest;
import org.datcheems.swp_projectnosmoking.dto.response.MembershipPackageResponse;
import org.datcheems.swp_projectnosmoking.entity.MembershipPackage;
import org.datcheems.swp_projectnosmoking.repository.MembershipPackageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MembershipPackageService {

    private final MembershipPackageRepository membershipPackageRepository;

    public List<MembershipPackageResponse> getAllPackages() {
        return membershipPackageRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public MembershipPackageResponse getPackageById(Long id) {
        MembershipPackage pkg = membershipPackageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Membership package not found with id: " + id));
        return mapToResponse(pkg);
    }

    public MembershipPackageResponse createPackage(MembershipPackageRequest request) {
        MembershipPackage pkg = mapToEntity(request);
        MembershipPackage saved = membershipPackageRepository.save(pkg);
        return mapToResponse(saved);
    }

    public MembershipPackageResponse updatePackage(Long id, MembershipPackageRequest request) {
        MembershipPackage existing = membershipPackageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Membership package not found with id: " + id));

        existing.setName(request.getName());
        existing.setDescription(request.getDescription());
        existing.setPrice(request.getPrice());
        existing.setReleaseDate(request.getReleaseDate());
        existing.setEndDate(request.getEndDate());

        MembershipPackage updated = membershipPackageRepository.save(existing);
        return mapToResponse(updated);
    }

    public void deletePackage(Long id) {
        if (!membershipPackageRepository.existsById(id)) {
            throw new RuntimeException("Membership package not found with id: " + id);
        }
        membershipPackageRepository.deleteById(id);
    }

    // Mapping methods
    private MembershipPackageResponse mapToResponse(MembershipPackage pkg) {
        return MembershipPackageResponse.builder()
                .Id(pkg.getId())
                .Name(pkg.getName())
                .Description(pkg.getDescription())
                .Price(pkg.getPrice())
                .ReleaseDate(pkg.getReleaseDate())
                .EndDate(pkg.getEndDate())
                .build();
    }

    private MembershipPackage mapToEntity(MembershipPackageRequest request) {
        MembershipPackage pkg = new MembershipPackage();
        pkg.setName(request.getName());
        pkg.setDescription(request.getDescription());
        pkg.setPrice(request.getPrice());
        pkg.setReleaseDate(request.getReleaseDate());
        pkg.setEndDate(request.getEndDate());
        return pkg;
    }
}
