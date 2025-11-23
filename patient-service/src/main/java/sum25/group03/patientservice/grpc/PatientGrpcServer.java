package sum25.group03.patientservice.grpc;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import sum25.group03.patientservice.dtos.request.GrpcMappingPatientAndCreatorIdRequest;
import sum25.group03.patientservice.dtos.response.GrpcMappingPatientAndCreatorIdResponse;
import sum25.group03.patientservice.mapper.GrpcUserSnapshotMapper;
import sum25.group03.patientservice.repositories.postgres.UserSnapshotRepository;
import sum25.group03.patientservice.entities.UserSnapshotEntity;
import sum25.group03.patientservice.services.interfaces.MedicalRecordService;
import sum25.group03.patientservice.services.interfaces.UserSnapshotService;

@GrpcService
@RequiredArgsConstructor
public class PatientGrpcServer extends PatientServiceGrpc.PatientServiceImplBase {

    private final UserSnapshotRepository userSnapshotRepository;
    private final MedicalRecordService medicalRecordService;
    private final UserSnapshotService userSnapshotService;
    private final GrpcUserSnapshotMapper grpcUserSnapshotMapper;

    @Override
    public void getPatientById(GetPatientByIdRequest request,
                               StreamObserver<GetPatientByIdResponse> responseObserver) {
        UserSnapshotEntity user = userSnapshotRepository
                .findByExternalUserId(request.getExternalUserId())
                .orElse(null);

        if (user == null) {
            responseObserver.onError(new RuntimeException("Patient not found"));
            return;
        }

        GetPatientByIdResponse response = GetPatientByIdResponse.newBuilder()
                .setId(user.getId())
                .setFullName(user.getFullName())
                .setPhoneNumber(user.getPhoneNumber())
                .setDateOfBirth(user.getDateOfBirth() != null ? user.getDateOfBirth().toString() : "")
                .setGender(user.getGender() != null ? user.getGender() : "")
                .setEmail(user.getEmail())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void autoCreateMedicalRecord(
            AutoCreateMedicalRecordRequest request,
            StreamObserver<CreatedMedicalRecordResponse> createdRecordInfo
    ) {
        Long createdMedicalRecordId = medicalRecordService.autoCreateNewMedicalRecordByTestOrder(
                request.getCreatedBy(),
                request.getPatientId()
        );
        CreatedMedicalRecordResponse response = CreatedMedicalRecordResponse.newBuilder()
                .setRecordId(createdMedicalRecordId)
                .build();

        createdRecordInfo.onNext(response);
        createdRecordInfo.onCompleted();
    }

    @Override
    public void assignPatientIdToMedicalRecord(
            AssignPatientIdToMedicalRecordRequest request,
            StreamObserver<EmptyMsg> responseObserver
    ) {
        medicalRecordService.assignPatientIdToMedicalRecord(
                request.getMedicalRecordId(),
                request.getPatientId()
        );

        responseObserver.onNext(EmptyMsg.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void mappingPatientIdAndCreatorIdToTheirName(
            MappingPatientIdAndCreatorIdToTheirNameRequest request,
            StreamObserver<MappingPatientIdAndCreatorIdToTheirNameResponse> responseObserver
    ) {

        GrpcMappingPatientAndCreatorIdRequest javaDtoRequest = grpcUserSnapshotMapper.fromGrpcMappingRequest(request);
        var mapping = userSnapshotService.getGrpcMappingPatientAndCreatorName(javaDtoRequest);

        MappingPatientIdAndCreatorIdToTheirNameResponse grpcResponse = grpcUserSnapshotMapper.toGrpcMappingResponse(mapping);
        responseObserver.onNext(grpcResponse);
        responseObserver.onCompleted();
    }
}
