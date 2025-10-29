package sum25.group03.instrumentservice.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import sum25.group03.instrumentservice.client.WarehouseServiceClient;
import sum25.group03.instrumentservice.client.response.ReagentResponse;
import sum25.group03.instrumentservice.common.InstalledReagentStatus;
import sum25.group03.instrumentservice.common.InstrumentStatus;
import sum25.group03.instrumentservice.controller.request.BloodTestingRequest;
import sum25.group03.instrumentservice.controller.response.RawTestResultResponse;
import sum25.group03.instrumentservice.event.TestResultPublishedEvent;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;

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


    @Override
    @Async("taskExecutor")
    public CompletableFuture<RawTestResultResponse> startTest(BloodTestingRequest request) {
        try {
            if (instrumentRepository.existsByIdAndStatusIsNot(request.getInstrumentId(), InstrumentStatus.READY)) {
                String errorMessage = "Instrument ID: " + request.getInstrumentId() + " is not READY";
                log.warn(errorMessage);
                throw new InstrumentNotReadyException(errorMessage);
            }
            log.info("Starting MEK-6510 simulator for barcode: {} on instrument: {}",
                    request.getBarcode(), request.getInstrumentId());

            List<InstalledReagent> installedReagents = installedReagentRepository
                    .findByInstrumentIdAndStatusIsNot(request.getInstrumentId(), InstalledReagentStatus.REMOVED);
            for (InstalledReagent installedReagent : installedReagents) {
                log.info("Reagent: {}", installedReagent.getReagentName());
            }
            if (installedReagents.isEmpty()) {
                log.info("No installed reagents found for instrument ID: {}", request.getInstrumentId());
            }
            log.info("Found {} installed reagents", installedReagents.size());
            List<ReagentResponse> listReagentResponses = warehouseServiceClient.reagentResponseReagentList();
            if (!ReagentValidator.validateReagentVolume(installedReagents,listReagentResponses)) {
                log.warn("Insufficient reagent volume for barcode: {}", request.getBarcode());
                publishFailureEvent(request, "INSUFFICIENT_REAGENT");
                throw new InsufficientReagentException(
                        "Insufficient reagent volume for barcode: " + request.getBarcode());
            }
            long simulationTime = 15_000 + (long) (Math.random() * 5_000);
            Thread.sleep(simulationTime);

            Map<String, Double> allRawResults = new HashMap<>();
            List<String> allObxResults = new ArrayList<>();

            if (request.getTestTypes() != null) {
                for (String testType : request.getTestTypes()) {
                    switch (testType.toUpperCase()) {
                        case "CBC":
                            Map<String, Double> rawCbc = generateRawCbcResults();
                            allRawResults.putAll(rawCbc);
                            allObxResults.addAll(analyzeCbcResults(rawCbc));
                            break;
                        case "CHEMISTRY":
                            Map<String, Double> rawChem = generateRawChemistryResults();
                            allRawResults.putAll(rawChem);
                            allObxResults.addAll(analyzeChemistryResults(rawChem));
                            break;
                        case "COAGULATION":
                            Map<String, Double> rawCoag = generateRawCoagulationResults();
                            allRawResults.putAll(rawCoag);
                            allObxResults.addAll(analyzeCoagulationResults(rawCoag));
                            break;
                        default:
                            break;
                    }
                }
            }

            String rawDataJson = objectMapper.writeValueAsString(allRawResults);
            String finalHl7Message = buildHl7Message(request.getBarcode(), allObxResults);

            Instrument instrument = instrumentRepository.findById(request.getInstrumentId())
                    .orElseThrow(() -> new RuntimeException("Instrument not found with ID: " + request.getInstrumentId()));

            RawTestResult newResult = RawTestResult.builder()
                    .testOrderId(request.getTestOrderId())
                    .instrument(instrument)
                    .rawData(rawDataJson)
                    .hl7Message(finalHl7Message)
                    .isSentToMonitoring(false)
                    .isSynced(false)
                    .createdAt(LocalDateTime.now())
                    .build();

            RawTestResult savedResult = rawTestResultRepository.save(newResult);
            log.info("Raw test result saved with ID: {} for barcode: {}", savedResult.getResultId(), request.getBarcode());

            TestResultPublishedEvent event = TestResultPublishedEvent.builder()
                    .testOrderId(request.getTestOrderId())
                    .instrumentId(request.getInstrumentId())
                    .barcode(request.getBarcode())
                    .hl7Message(finalHl7Message)
                    .rawData(rawDataJson)
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
                    .rawData(allRawResults)
                    .hl7Message(finalHl7Message)
                    .isSentToMonitoring(savedResult.getIsSentToMonitoring())
                    .isSynced(savedResult.getIsSynced())
                    .createdAt(savedResult.getCreatedAt())
                    .build();

            return CompletableFuture.completedFuture(response);

        } catch (InterruptedException e) {
            log.error("Simulation thread interrupted for barcode: {}", request.getBarcode());
            publishFailureEvent(request, "INTERRUPTED");
            Thread.currentThread().interrupt();
            return CompletableFuture.failedFuture(e);
        } catch (InsufficientReagentException e) {
            log.error("Insufficient reagent for barcode: {}", request.getBarcode());
            return CompletableFuture.failedFuture(e);
        } catch (Exception e) {
            log.error("Critical error during simulation for barcode: {} | Error: {}",
                    request.getBarcode(), e.getMessage(), e);
            publishFailureEvent(request, "ERROR");
            return CompletableFuture.failedFuture(e);
        }
    }


    private void publishFailureEvent(BloodTestingRequest request, String reason) {
        try {
            TestResultPublishedEvent failureEvent = TestResultPublishedEvent.builder()
                    .testOrderId(request.getTestOrderId())
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
        rawResults.put("WBC", 4_000 + (10_000 - 4_000) * rand.nextDouble());
        rawResults.put("RBC", 4.2 + (6.1 - 4.2) * rand.nextDouble());
        rawResults.put("HGB", 12.0 + (18.0 - 12.0) * rand.nextDouble());
        rawResults.put("HCT", 37.0 + (52.0 - 37.0) * rand.nextDouble());
        rawResults.put("MCV", 80.0 + (100.0 - 80.0) * rand.nextDouble());
        rawResults.put("MCH", 27.0 + (33.0 - 27.0) * rand.nextDouble());
        rawResults.put("MCHC", 32.0 + (36.0 - 32.0) * rand.nextDouble());
        rawResults.put("PLT", 150_000 + (350_000 - 150_000) * rand.nextDouble());
        return rawResults;
    }

    public static Map<String, Double> generateRawChemistryResults() {
        Map<String, Double> rawResults = new HashMap<>();
        rawResults.put("GLUCOSE", 70 + (100 - 70) * rand.nextDouble());
        rawResults.put("ALT", 10 + (40 - 10) * rand.nextDouble());
        rawResults.put("CREATININE", 0.6 + (1.2 - 0.6) * rand.nextDouble());
        return rawResults;
    }

    public static Map<String, Double> generateRawCoagulationResults() {
        Map<String, Double> rawResults = new HashMap<>();
        rawResults.put("PT", 11.0 + (13.5 - 11.0) * rand.nextDouble());
        rawResults.put("INR", 0.8 + (1.1 - 0.8) * rand.nextDouble());
        return rawResults;
    }
    public static List<String> analyzeCbcResults(Map<String, Double> rawResults) {
        List<String> obxSegments = new ArrayList<>();
        obxSegments.add(String.format("OBX||NM|WBC||%.1f|cells/µL|4000-10000||||F", rawResults.get("WBC")));
        obxSegments.add(String.format("OBX||NM|RBC||%.2f|million/µL|4.2-6.1||||F", rawResults.get("RBC")));
        obxSegments.add(String.format("OBX||NM|HGB||%.1f|g/dL|12.0-18.0||||F", rawResults.get("HGB")));
        obxSegments.add(String.format("OBX||NM|HCT||%.1f|%%|37-52||||F", rawResults.get("HCT")));
        obxSegments.add(String.format("OBX||NM|MCV||%.1f|fL|80-100||||F", rawResults.get("MCV")));
        obxSegments.add(String.format("OBX||NM|MCH||%.1f|pg|27-33||||F", rawResults.get("MCH")));
        obxSegments.add(String.format("OBX||NM|MCHC||%.1f|g/dL|32-36||||F", rawResults.get("MCHC")));
        obxSegments.add(String.format("OBX||NM|PLT||%.0f|cells/µL|150000-350000||||F", rawResults.get("PLT")));
        return obxSegments;
    }

    public static List<String> analyzeChemistryResults(Map<String, Double> rawResults) {
        List<String> obxSegments = new ArrayList<>();
        obxSegments.add(String.format("OBX||NM|GLUCOSE||%.0f|mg/dL|70-100||||F", rawResults.get("GLUCOSE")));
        obxSegments.add(String.format("OBX||NM|ALT||%.0f|U/L|10-40||||F", rawResults.get("ALT")));
        obxSegments.add(String.format("OBX||NM|CREATININE||%.2f|mg/dL|0.6-1.2||||F", rawResults.get("CREATININE")));
        return obxSegments;
    }

    public static List<String> analyzeCoagulationResults(Map<String, Double> rawResults) {
        List<String> obxSegments = new ArrayList<>();
        obxSegments.add(String.format("OBX||NM|PT||%.1f|seconds|11.0-13.5||||F", rawResults.get("PT")));
        obxSegments.add(String.format("OBX||NM|INR||%.2f||0.8-1.1||||F", rawResults.get("INR")));
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
}
