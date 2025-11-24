package sum25.group03.instrumentservice.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import sum25.group03.instrumentservice.audit.service.AuditLogService;
import sum25.group03.instrumentservice.client.TestOrderServiceClient;
import sum25.group03.instrumentservice.client.WarehouseServiceClient;
import sum25.group03.instrumentservice.client.response.CreationTestOrderResponse;
import sum25.group03.instrumentservice.client.response.ReagentResponse;
import sum25.group03.instrumentservice.client.response.TestOrderResponse;
import sum25.group03.instrumentservice.common.InstalledReagentStatus;
import sum25.group03.instrumentservice.common.InstrumentStatus;
import sum25.group03.instrumentservice.controller.request.BloodTestingRequest;
import sum25.group03.instrumentservice.controller.response.RawTestResultResponse;
import sum25.group03.instrumentservice.event.ReagentUsageHistoryEvent;
import sum25.group03.instrumentservice.event.TestResultPublishedEvent;
import sum25.group03.instrumentservice.exception.BarcodeAlreadyTestedException;
import sum25.group03.instrumentservice.exception.InstrumentNotReadyException;
import sum25.group03.instrumentservice.exception.InsufficientReagentException;
import sum25.group03.instrumentservice.model.Instrument;
import sum25.group03.instrumentservice.model.InstalledReagent;
import sum25.group03.instrumentservice.model.RawTestResult;
import sum25.group03.instrumentservice.repository.InstalledReagentRepository;
import sum25.group03.instrumentservice.repository.InstrumentRepository;
import sum25.group03.instrumentservice.repository.RawTestResultRepository;
import sum25.group03.instrumentservice.service.KafkaEventPublisher;
import sum25.group03.instrumentservice.service.SimulatorService;
import sum25.group03.instrumentservice.service.util.ReagentValidator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class SimulatorServiceImpl implements SimulatorService {
    private static final Random rand = new Random();
    private static final DateTimeFormatter HL7_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final RawTestResultRepository rawTestResultRepository;
    private final InstrumentRepository instrumentRepository;
    private final InstalledReagentRepository installedReagentRepository;
    private final KafkaEventPublisher kafkaEventPublisher;
    private final ObjectMapper objectMapper;
    private final WarehouseServiceClient warehouseServiceClient;
    private final TestOrderServiceClient testOrderServiceClient;
    private final AuditLogService auditLogService;

    @Override
//    @Async("taskExecutor")
    public CompletableFuture<RawTestResultResponse> startTest(BloodTestingRequest request) {
        TestOrderResponse testOrderResponse = null;
        try {
            if (instrumentRepository.existsByIdAndStatusIsNot(request.getInstrumentId(), InstrumentStatus.READY)) {
                String errorMessage = "Instrument ID: " + request.getInstrumentId() + " is not READY";
                log.warn(errorMessage);
                throw new InstrumentNotReadyException(errorMessage);
            }
            log.info("Starting Blood Analyser simulator for barcode: {} on instrument: {}",
                    request.getBarcode(), request.getInstrumentId());

//            List<InstalledReagent> installedReagents = installedReagentRepository
//                    .findByInstrumentIdAndStatusIsNot(request.getInstrumentId(), InstalledReagentStatus.REMOVED);

            // only get available reagents
            List<InstalledReagent> installedReagents = installedReagentRepository.findByInstrumentIdAndStatus(
                    request.getInstrumentId(), InstalledReagentStatus.AVAILABLE
            );

            if (installedReagents.isEmpty()) {
                log.info("No installed reagents found for instrument ID: {}", request.getInstrumentId());
                throw new RuntimeException("No installed reagents found for instrument ID");
            }
            for (InstalledReagent installedReagent : installedReagents) {
                log.info("Reagent: {}", installedReagent.getReagentName());
            }


            List<ReagentResponse> listReagentResponses = warehouseServiceClient.reagentResponseReagentList();
            if (!ReagentValidator.validateReagentVolume(installedReagents,listReagentResponses)) {
                throw new InsufficientReagentException(
                        "Insufficient reagent volume for barcode: " + request.getBarcode());
            }

            validateBarcode(request.getBarcode());

            if(rawTestResultRepository.existsByBarcode(request.getBarcode())){
                log.error("BARCODE ALREADY TESTED");
                throw new BarcodeAlreadyTestedException("Barcode has been tested: " +request.getBarcode());
            }

            testOrderResponse =
                    testOrderServiceClient.getTestOrderByBarcode(request.getBarcode());

            if(testOrderResponse==null){
                CreationTestOrderResponse creationTestOrderResponse =
                        testOrderServiceClient.createUnmatchedOrder(request.getBarcode());
                testOrderResponse = TestOrderResponse.builder()
                        .id(creationTestOrderResponse.getId())
                        .barcode(creationTestOrderResponse.getBarcode())
                        .code(creationTestOrderResponse.getCode())
                        .status(creationTestOrderResponse.getStatus())
                        .createdAt(creationTestOrderResponse.getCreatedAt())
                        .build();

            }

            long simulationTime = 10_000 + (long) (Math.random() * 5_000);
            Thread.sleep(simulationTime);

            for(ReagentResponse reagentResponse: listReagentResponses){
                Double usageVolume = reagentResponse.getUsageMin()+ (reagentResponse.getUsageMax()-reagentResponse.getUsageMin())*rand.nextDouble();
                InstalledReagent installedReagent = installedReagentRepository.findByReagentIdAndInstrumentIdAndStatusNot(reagentResponse.getReagentId(), request.getInstrumentId(), InstalledReagentStatus.REMOVED)
                        .orElseThrow(() -> new RuntimeException("Installed reagent not found for reagent ID: " + reagentResponse.getReagentId()));
                Double currentVolume = installedReagent.getCurrentVolume();
                installedReagentRepository.updateCurrentVolumeById(currentVolume-usageVolume,installedReagent.getId());
                ReagentUsageHistoryEvent usageEvent = ReagentUsageHistoryEvent.builder()
                        .instrumentId(request.getInstrumentId())
                        .reagentId(installedReagent.getReagentId())
                        .testOrderId(testOrderResponse.getId())
                        .usageType("TEST_USAGE")
                        .lotReagentId(installedReagent.getLotReagentId())
                        .reagentName(installedReagent.getReagentName())
                        .lotNumber(installedReagent.getLotNumber())
                        .quantityUsed(usageVolume)
                        .unit(installedReagent.getUnit())
                        .usedAt(LocalDate.now())
                        .usedBy(2)
                        .notes("Reagent name: " + installedReagent.getReagentName() +
                                " used for test order ID: " + testOrderResponse.getId() + " | Barcode: " + request.getBarcode()+"Usage volume: "+usageVolume+installedReagent.getUnit())
                        .build();
                kafkaEventPublisher.publishReagentUsageHistoryEvent(usageEvent);
                log.info("Publish reagent usage event for reagent: {} | Barcode: {}",
                        installedReagent.getReagentName(), request.getBarcode());
                log.info("REAGENT: {} | Initial Volume: {} {} | Used: {} {} | Final Volume: {} {}",
                        installedReagent.getReagentName(),
                        currentVolume, installedReagent.getUnit(),
                        usageVolume, installedReagent.getUnit(),
                        currentVolume-usageVolume, installedReagent.getUnit()
                );
            }
            Map<String, Double> rawCbc = generateRawCbcResults();
            List<String> HL7Message =  analyzeCbcResults(rawCbc);

            String rawDataJson = objectMapper.writeValueAsString(rawCbc);
            String finalHl7Message = buildHl7Message(request.getBarcode(), HL7Message);

            Instrument instrument = instrumentRepository.findById(request.getInstrumentId())
                    .orElseThrow(() -> new RuntimeException("Instrument not found with ID: " + request.getInstrumentId()));

            RawTestResult newResult = RawTestResult.builder()
                    .testOrderId(testOrderResponse.getId())
                    .instrument(instrument)
                    .barcode(request.getBarcode())
                    .rawData(rawDataJson)
                    .hl7Message(finalHl7Message)
                    .isSentToMonitoring(false)
                    .isSynced(true)
                    .createdAt(LocalDateTime.now())
                    .build();

            RawTestResult savedResult = rawTestResultRepository.save(newResult);
            log.info("Raw test result saved with ID: {} for barcode: {}", savedResult.getResultId(), request.getBarcode());

            TestResultPublishedEvent event = TestResultPublishedEvent.builder()
                    .testOrderId(testOrderResponse.getId())
                    .instrumentId(request.getInstrumentId())
                    .barcode(request.getBarcode())
                    .rawData(rawDataJson)
                    .hl7Message(finalHl7Message)
                    .timestamp(LocalDateTime.now())
                    .status("SUCCESS")
                    .build();

            kafkaEventPublisher.publishTestResultEvent(event);
            log.info("Test result published via HL7 for barcode: {}", request.getBarcode());

            savedResult.setIsSentToMonitoring(true);
            rawTestResultRepository.save(savedResult);

            RawTestResultResponse response = RawTestResultResponse.builder()
                    .resultId(savedResult.getResultId())
                    .testOrderId(savedResult.getTestOrderId())
                    .instrumentId(savedResult.getInstrument().getId())
                    .rawData(rawCbc)
                    .hl7Message(finalHl7Message)
                    .isSentToMonitoring(savedResult.getIsSentToMonitoring())
                    .isSynced(savedResult.getIsSynced())
                    .createdAt(savedResult.getCreatedAt())
                    .build();

            return CompletableFuture.completedFuture(response);

        } catch (InterruptedException e) {
            log.error("Simulation thread interrupted for barcode: {}", request.getBarcode());
//            publishFailureEvent(request, "INTERRUPTED",testOrderResponse.getId());
            Thread.currentThread().interrupt();
            return CompletableFuture.failedFuture(e);
        } catch (InsufficientReagentException e) {
            log.error("Insufficient reagent for barcode: {}", request.getBarcode());
            return CompletableFuture.failedFuture(e);
        }catch (BarcodeAlreadyTestedException e){
            log.error("Barcode already tested: {}", request.getBarcode());
            return CompletableFuture.failedFuture(e);
        }catch (InstrumentNotReadyException e){
            log.error("Instrument is not ready: {}", request.getBarcode());
            return CompletableFuture.failedFuture(e);
        } catch (Exception e) {
            log.error("Critical error during simulation for barcode: {} | Error: {}",
                    request.getBarcode(), e.getMessage(), e);
//            publishFailureEvent(request, "ERROR",testOrderResponse.getId());
            return CompletableFuture.failedFuture(e);
        }
    }

    private void validateBarcode(String barcode) {
        final String BARCODE_REGEX = "^BC-\\d{6}$";
        if (barcode == null || barcode.isEmpty()) {
            String errorMessage = "Barcode is null or empty";
            log.warn(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        if (!Pattern.matches(BARCODE_REGEX, barcode)) {
            String errorMessage = "Invalid barcode format: " + barcode;
            log.warn(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
    }


    private void publishFailureEvent(BloodTestingRequest request, String reason,Long testOrderId) {
        try {
            TestResultPublishedEvent failureEvent = TestResultPublishedEvent.builder()
                    .testOrderId(testOrderId)
                    .instrumentId(request.getInstrumentId())
                    .barcode(request.getBarcode())
                    .timestamp(LocalDateTime.now())
                    .status("FAILED")
                    .build();

            kafkaEventPublisher.publishTestResultEvent(failureEvent);
            log.info("Failure event published for barcode: {} with reason: {}", request.getBarcode(), reason);
        } catch (Exception e) {
            log.error("Failed to publish failure event: {}", e.getMessage());
        }
    }

    public static Map<String, Double> generateRawCbcResults() {
        Map<String, Double> rawResults = new HashMap<>();

        final int PROB_NORMAL = 80;
        final int PROB_LOW = 10;

        // --- WBC (White Blood Cell) ---
        double wbcNormalMin = 4_000, wbcNormalMax = 10_000;
        double wbcAbnormalLow = 1_000, wbcAbnormalHigh = 30_000;
        int wbcProb = rand.nextInt(100);

        if (wbcProb < PROB_NORMAL) {
            rawResults.put("WBC", randomInRange(wbcNormalMin, wbcNormalMax));
        } else if (wbcProb < (PROB_NORMAL + PROB_LOW)) {
            rawResults.put("WBC", randomInRange(wbcAbnormalLow, wbcNormalMin));
        } else {
            rawResults.put("WBC", randomInRange(wbcNormalMax, wbcAbnormalHigh));
        }

        // --- RBC (Red Blood Cell) ---
        double rbcNormalMin = 4.2, rbcNormalMax = 6.1;
        double rbcAbnormalLow = 2.0, rbcAbnormalHigh = 8.0;
        int rbcProb = rand.nextInt(100);

        if (rbcProb < PROB_NORMAL) {
            rawResults.put("RBC", randomInRange(rbcNormalMin, rbcNormalMax));
        } else if (rbcProb < (PROB_NORMAL + PROB_LOW)) {
            rawResults.put("RBC", randomInRange(rbcAbnormalLow, rbcNormalMin));
        } else {
            rawResults.put("RBC", randomInRange(rbcNormalMax, rbcAbnormalHigh));
        }

        // --- Hb/HGB (Hemoglobin) ---
        double hgbNormalMin = 12.0, hgbNormalMax = 18.0;
        double hgbAbnormalLow = 7.0, hgbAbnormalHigh = 22.0;
        int hgbProb = rand.nextInt(100);

        if (hgbProb < PROB_NORMAL) {
            rawResults.put("Hb/HGB", randomInRange(hgbNormalMin, hgbNormalMax));
        } else if (hgbProb < (PROB_NORMAL + PROB_LOW)) {
            rawResults.put("Hb/HGB", randomInRange(hgbAbnormalLow, hgbNormalMin));
        } else {
            rawResults.put("Hb/HGB", randomInRange(hgbNormalMax, hgbAbnormalHigh));
        }

        // --- HCT (Hematocrit) ---
        double hctNormalMin = 37.0, hctNormalMax = 52.0;
        double hctAbnormalLow = 20.0, hctAbnormalHigh = 65.0;
        int hctProb = rand.nextInt(100);

        if (hctProb < PROB_NORMAL) {
            rawResults.put("HCT", randomInRange(hctNormalMin, hctNormalMax));
        } else if (hctProb < (PROB_NORMAL + PROB_LOW)) {
            rawResults.put("HCT", randomInRange(hctAbnormalLow, hctNormalMin));
        } else {
            rawResults.put("HCT", randomInRange(hctNormalMax, hctAbnormalHigh));
        }

        // --- MCV (Mean Corpuscular Volume) ---
        double mcvNormalMin = 80.0, mcvNormalMax = 100.0;
        double mcvAbnormalLow = 60.0, mcvAbnormalHigh = 115.0;
        int mcvProb = rand.nextInt(100);

        if (mcvProb < PROB_NORMAL) {
            rawResults.put("MCV", randomInRange(mcvNormalMin, mcvNormalMax));
        } else if (mcvProb < (PROB_NORMAL + PROB_LOW)) {
            rawResults.put("MCV", randomInRange(mcvAbnormalLow, mcvNormalMin));
        } else {
            rawResults.put("MCV", randomInRange(mcvNormalMax, mcvAbnormalHigh));
        }

        // --- MCH (Mean Corpuscular Hemoglobin) ---
        double mchNormalMin = 27.0, mchNormalMax = 33.0;
        double mchAbnormalLow = 20.0, mchAbnormalHigh = 40.0;
        int mchProb = rand.nextInt(100);

        if (mchProb < PROB_NORMAL) {
            rawResults.put("MCH", randomInRange(mchNormalMin, mchNormalMax));
        } else if (mchProb < (PROB_NORMAL + PROB_LOW)) {
            rawResults.put("MCH", randomInRange(mchAbnormalLow, mchNormalMin));
        } else {
            rawResults.put("MCH", randomInRange(mchNormalMax, mchAbnormalHigh));
        }

        // --- MCHC (Mean Corpuscular Hemoglobin Concentration) ---
        double mchcNormalMin = 32.0, mchcNormalMax = 36.0;
        double mchcAbnormalLow = 28.0, mchcAbnormalHigh = 40.0;
        int mchcProb = rand.nextInt(100);

        if (mchcProb < PROB_NORMAL) {
            rawResults.put("MCHC", randomInRange(mchcNormalMin, mchcNormalMax));
        } else if (mchcProb < (PROB_NORMAL + PROB_LOW)) {
            rawResults.put("MCHC", randomInRange(mchcAbnormalLow, mchcNormalMin));
        } else {
            rawResults.put("MCHC", randomInRange(mchcNormalMax, mchcAbnormalHigh));
        }

        // --- PLT (Platelet) ---
        double pltNormalMin = 150_000, pltNormalMax = 350_000;
        double pltAbnormalLow = 20_000, pltAbnormalHigh = 750_000;
        int pltProb = rand.nextInt(100);

        if (pltProb < PROB_NORMAL) {
            rawResults.put("PLT", randomInRange(pltNormalMin, pltNormalMax));
        } else if (pltProb < (PROB_NORMAL + PROB_LOW)) {
            rawResults.put("PLT", randomInRange(pltAbnormalLow, pltNormalMin));
        } else {
            rawResults.put("PLT", randomInRange(pltNormalMax, pltAbnormalHigh));
        }

        return rawResults;
    }

    private static double randomInRange(double min, double max) {
        if (min >= max) {
            return min;
        }
        return min + (max - min) * rand.nextDouble();
    }


    public static List<String> analyzeCbcResults(Map<String, Double> rawResults) {
        List<String> obxSegments = new ArrayList<>();
        obxSegments.add(String.format("OBX||NM|WBC||%.1f|cells/µL|4000-10000||||F", rawResults.get("WBC")));
        obxSegments.add(String.format("OBX||NM|RBC||%.2f|million/µL|4.2-6.1||||F", rawResults.get("RBC")));
        obxSegments.add(String.format("OBX||NM|Hb/HGB||%.1f|g/dL|12.0-18.0||||F", rawResults.get("Hb/HGB")));
        obxSegments.add(String.format("OBX||NM|HCT||%.1f|%%|37-52||||F", rawResults.get("HCT")));
        obxSegments.add(String.format("OBX||NM|MCV||%.1f|fL|80-100||||F", rawResults.get("MCV")));
        obxSegments.add(String.format("OBX||NM|MCH||%.1f|pg|27-33||||F", rawResults.get("MCH")));
        obxSegments.add(String.format("OBX||NM|MCHC||%.1f|g/dL|32-36||||F", rawResults.get("MCHC")));
        obxSegments.add(String.format("OBX||NM|PLT||%.0f|cells/µL|150000-350000||||F", rawResults.get("PLT")));
        return obxSegments;
    }




    public static String buildHl7Message(String barcode, List<String> obxSegments) {
        String msh = "MSH|^~\\&|MEK-6510|LAB|INSTRUMENT_SERVICE|LAB|" +
                LocalDateTime.now().format(HL7_DATE_FORMAT) +
                "||ORU^R01|MSG" + System.currentTimeMillis() + "|P|2.5.1";
        String pid = "PID|1||" + barcode + "||Patient^Simulated||20000101|M";
        String obr = "OBR|1||ORDER-" + System.currentTimeMillis() + "|PANEL^CBC_CHEM_COAG|||" +
                LocalDateTime.now().format(HL7_DATE_FORMAT);

        StringBuilder finalMessage = new StringBuilder();
        finalMessage.append(msh).append("\n");
        finalMessage.append(pid).append("\n");
        finalMessage.append(obr).append("\n");

        int obxIndex = 1;
        for (String obxSegment : obxSegments) {
            String indexedObx = obxSegment.replaceFirst("OBX\\|\\|", "OBX|" + obxIndex + "|");
            finalMessage.append(indexedObx).append("\n");
            obxIndex++;
        }

        return finalMessage.toString();
    }

    @Override
    public void logTestCompletion(String barcode, String ipAddress, String userAgent) {
        try {
            auditLogService.logWriteSuccess(
                    "START_BLOOD_TEST",
                    "BloodTest",
                    barcode,
                    ipAddress,
                    userAgent,
                    auditLogService.createFieldChanges(
                            "test_status",
                            "PENDING",
                            "COMPLETED"
                    )
            );
            log.info("Logged test completion for barcode: {}", barcode);
        } catch (Exception e) {
            log.error("Failed to log test completion for barcode: {}", barcode, e);
        }
    }

    @Override
    public void logTestFailure(String barcode, String ipAddress, String userAgent, String errorCode, String errorMessage) {
        try {
            auditLogService.logWriteFailure(
                    "START_BLOOD_TEST",
                    "BloodTest",
                    barcode,
                    ipAddress,
                    userAgent,
                    errorCode,
                    errorMessage
            );
            log.info("Logged test failure for barcode: {} with error: {}", barcode, errorCode);
        } catch (Exception e) {
            log.error("Failed to log test failure for barcode: {}", barcode, e);
        }
    }
}
