package sum25.group03.patientservice.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sum25.group03.patientservice.dtos.request.UserSnapshotRequest;
import sum25.group03.patientservice.dtos.response.UserSnapshotResponse;
import sum25.group03.patientservice.entities.UserSnapshotEntity;
import sum25.group03.patientservice.feign.IAMFeignClient;
import sum25.group03.patientservice.feign.dtos.FeignUserDTO;
import sum25.group03.patientservice.feign.dtos.FeignUserResponseWrapper;
import sum25.group03.patientservice.feign.dtos.UserFilterUpdate;
import sum25.group03.patientservice.mapper.UserSnapshotMapper;
import sum25.group03.patientservice.repositories.postgres.UserSnapshotRepository;
import sum25.group03.patientservice.services.interfaces.UserSnapshotService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserSnapshotServiceImpl implements UserSnapshotService {

    private final UserSnapshotRepository repository;
    private final UserSnapshotMapper mapper;
    private final IAMFeignClient userFeignClient;
    private final JdbcTemplate jdbcTemplate;


    @Transactional
    public void syncByPage(List<FeignUserDTO> updatedList, List<UserSnapshotEntity> allUserSnapshots) {
        if (updatedList == null || updatedList.isEmpty()) return;

        // look up map by externalUserId and proper entity
        Map<Long, UserSnapshotEntity> userSnapshotMap = allUserSnapshots.stream()
                .collect(Collectors.toMap(UserSnapshotEntity::getExternalUserId, e -> e));

        // map to UserFilterUpdate DTOs
        List<UserFilterUpdate> updateInfoDTOs = mapper.toUpdateInfoDTOs(updatedList);
        for (UserFilterUpdate updateInfo: updateInfoDTOs) {
            UserSnapshotEntity entity = userSnapshotMap.get(updateInfo.getId()); // search by externalUserId
            if (entity == null) continue;
            if (updateInfo.getRoles() != null) entity.setRoles(updateInfo.getRoles());
            if (updateInfo.getEmail() != null) entity.setEmail(updateInfo.getEmail());
            if (updateInfo.getPhoneNumber() != null) entity.setPhoneNumber(updateInfo.getPhoneNumber());
            if (updateInfo.getFullName() != null) entity.setFullName(updateInfo.getFullName());
        }
    }

    // sync user information from IAM service
    // More optimize: IAM Service has an 'isUpdated' flag to filter only updated users since last sync.
    @Override
    public void syncUserSnapshots() {
        int size = 5;
        int number = 0; // page
        boolean last = false;

        // load all users entities:
        List<UserSnapshotEntity> allUserSnapshots = repository.findAll();
        if (allUserSnapshots == null || allUserSnapshots.isEmpty()) {
            throw new RuntimeException("No user snapshots found in the database.");
        }

        do {
            // fetch data from IAM service with pagination
            FeignUserResponseWrapper usersWrapper = userFeignClient.fetchUsersInfo(number, size);
            System.out.println("usersWrapper={}\n" + usersWrapper);

            List<FeignUserDTO> updatedList = usersWrapper.getContent();

            // sync data
            syncByPage(updatedList, allUserSnapshots);

            last = usersWrapper.getLast();
            number++;

        } while(!last);

        log.info("Finished syncing all user snapshots from IAM service.");
    }

    @Override
    public UserSnapshotResponse create(UserSnapshotRequest request) {
        UserSnapshotEntity entity = mapper.toEntity(request);
        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public UserSnapshotResponse update(Long id, UserSnapshotRequest request) {
        UserSnapshotEntity existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("User snapshot not found"));
        existing.setExternalUserId(request.getExternalUserId());
        return mapper.toResponse(repository.save(existing));
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public UserSnapshotResponse getById(Long id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new RuntimeException("User snapshot not found"));
    }

    @Override
    public UserSnapshotResponse getByExternalUserId(Long externalUserId) {
        return repository.findByExternalUserId(externalUserId)
                .map(mapper::toResponse)
                .orElseThrow(() -> new RuntimeException("User snapshot not found"));
    }

    @Override
    public List<UserSnapshotResponse> getAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }
}
