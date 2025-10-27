package sum25.group03.instrumentservice.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import sum25.group03.instrumentservice.controller.request.BloodTestingRequest;
import sum25.group03.instrumentservice.model.Instrument;
import sum25.group03.instrumentservice.model.RawTestResult;
import sum25.group03.instrumentservice.repository.InstrumentRepository;
import sum25.group03.instrumentservice.repository.RawTestResultRepository;
import sum25.group03.instrumentservice.service.SimulatorService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;

//@Service
//public class SimulatorServiceImpl implements SimulatorService {
//    private static Random rand = new Random();
//    private final RawTestResultRepository rawTestResultRepository;
//    private final InstrumentRepository instrumentRepository;
//
//
//    @Override
//    @Async
//    public void startTest(BloodTestingRequest request) {
//        try {
//            long simulationTime = 15_000 + (long) (Math.random() * 5_000);
//            Thread.sleep(simulationTime);
//
//            Map<String, Double> allRawResults = new HashMap<>();
//            List<String> allObxResults = new ArrayList<>();
//
//            if (request.getTestTypes() != null) {
//                for (String testType : request.getTestTypes()) {
//                    switch (testType.toUpperCase()) {
//                        case "CBC": {
//                            Map<String, Double> rawCbc = generateRawCbcResults();
//                            allRawResults.putAll(rawCbc);
//                            allObxResults.addAll(analyzeCbcResults(rawCbc));
//                            break;
//                        }
//                        case "CHEMISTRY": {
//                            Map<String, Double> rawChem = generateRawChemistryResults();
//                            allRawResults.putAll(rawChem);
//                            allObxResults.addAll(analyzeChemistryResults(rawChem));
//                            break;
//                        }
//                        case "COAGULATION": {
//                            Map<String, Double> rawCoag = generateRawCoagulationResults();
//                            allRawResults.putAll(rawCoag);
//                            allObxResults.addAll(analyzeCoagulationResults(rawCoag));
//                            break;
//                        }
//                        default:
//                            break;
//                    }
//                }
//            }
//
////            String rawDataJson = objectMapper.writeValueAsString(allRawResults);
//
//            String finalHl7Message = buildHl7Message(request.getBarcode(), allObxResults);
//
//            Instrument instrument = instrumentRepository.findById(request.getInstrumentId())
//                    .orElseThrow(() -> new RuntimeException("Lỗi giả lập: Không tìm thấy Instrument với ID: " + request.getInstrumentId()));
//            RawTestResult newResult = new RawTestResult();
//            newResult.setTestOrderId(request.getTestOrderId());
//            newResult.setInstrument(instrument);
////            newResult.setRawData(rawDataJson);
//            newResult.setHl7Message(finalHl7Message);
//            newResult.setIsSentToMonitoring(false);
//            newResult.setIsSynced(false);
//
//            rawTestResultRepository.save(newResult);
//
//
//        } catch (InterruptedException e) {
//            System.err.println("Luồng giả lập bị gián đoạn cho barcode: " + request.getBarcode());
//            Thread.currentThread().interrupt();
//        } catch (Exception e) {
//
//            System.err.println("Lỗi nghiêm trọng trong quá trình giả lập cho barcode: " + request.getBarcode() + " | Lỗi: " + e.getMessage());
//        }
//    }
//
//
//    public static Map<String, Double> generateRawCbcResults() {
//        Map<String, Double> rawResults = new HashMap<>();
//        rawResults.put("WBC", 4_000 + (10_000 - 4_000) * rand.nextDouble());
//        rawResults.put("RBC", 4.2 + (6.1 - 4.2) * rand.nextDouble());
//        rawResults.put("HGB", 12.0 + (18.0 - 12.0) * rand.nextDouble());
//        rawResults.put("PLT", 150_000 + (350_000 - 150_000) * rand.nextDouble());
//        return rawResults;
//    }
//
//
//    public static Map<String, Double> generateRawChemistryResults() {
//        Map<String, Double> rawResults = new HashMap<>();
//        rawResults.put("GLUCOSE", 70 + (100 - 70) * rand.nextDouble());
//        rawResults.put("ALT", 10 + (40 - 10) * rand.nextDouble());
//        rawResults.put("CREATININE", 0.6 + (1.2 - 0.6) * rand.nextDouble());
//        return rawResults;
//    }
//
//
//    public static Map<String, Double> generateRawCoagulationResults() {
//        Map<String, Double> rawResults = new HashMap<>();
//        rawResults.put("PT", 11.0 + (13.5 - 11.0) * rand.nextDouble());
//        rawResults.put("INR", 0.8 + (1.1 - 0.8) * rand.nextDouble());
//        return rawResults;
//    }
//
//
//    public static List<String> analyzeCbcResults(Map<String, Double> rawResults) {
//        String obxWbc = String.format("OBX||NM|WBC||%.1f|cells/µL|4000-10000||||F", rawResults.get("WBC"));
//        String obxRbc = String.format("OBX||NM|RBC||%.2f|million/µL|4.2-6.1||||F", rawResults.get("RBC"));
//        String obxHgb = String.format("OBX||NM|HGB||%.1f|g/dL|12.0-18.0||||F", rawResults.get("HGB"));
//        String obxPlt = String.format("OBX||NM|PLT||%.0f|cells/µL|150000-350000||||F", rawResults.get("PLT"));
//
//        return Arrays.asList(obxWbc, obxRbc, obxHgb, obxPlt);
//    }
//
//
//    public static List<String> analyzeChemistryResults(Map<String, Double> rawResults) {
//        String obxGlucose = String.format("OBX||NM|GLUCOSE||%.0f|mg/dL|70-100||||F", rawResults.get("GLUCOSE"));
//        String obxAlt = String.format("OBX||NM|ALT||%.0f|U/L|10-40||||F", rawResults.get("ALT"));
//        String obxCreatinine = String.format("OBX||NM|CREATININE||%.2f|mg/dL|0.6-1.2||||F", rawResults.get("CREATININE"));
//
//        return Arrays.asList(obxGlucose, obxAlt, obxCreatinine);
//    }
//
//    public static List<String> analyzeCoagulationResults(Map<String, Double> rawResults) {
//        String obxPt = String.format("OBX||NM|PT||%.1f|seconds|11.0-13.5||||F", rawResults.get("PT"));
//        String obxInr = String.format("OBX||NM|INR||%.2f||0.8-1.1||||F", rawResults.get("INR"));
//
//        return Arrays.asList(obxPt, obxInr);
//    }
//
//
//    public static String buildHl7Message(String barcode, List<String> obxSegments) {
//        String msh = "MSH|^~\\&|SIMULATOR_v3|LAB|INSTRUMENT_SERVICE|LAB|20251027092200||ORU^R01|MSG00001|P|2.5.1";
//        String pid = "PID|1||" + barcode + "||Patient^Fake||20000101|M";
//        String obr = "OBR|1||ORDER-789|PANEL^CBC_CHEM_COAG|||20251027092200";
//
//        StringBuilder finalMessage = new StringBuilder();
//        finalMessage.append(msh).append("\n");
//        finalMessage.append(pid).append("\n");
//        finalMessage.append(obr).append("\n");
//
//        int obxIndex = 1;
//        for (String obxSegment : obxSegments) {
//            String indexedObx = obxSegment.replaceFirst("OBX\\|\\|", "OBX|" + obxIndex + "|");
//            finalMessage.append(indexedObx).append("\n");
//            obxIndex++;
//        }
//
//        return finalMessage.toString();
//    }
//}