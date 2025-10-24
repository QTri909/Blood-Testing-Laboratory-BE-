package sum25.group03.testorderservice.component;

import org.springframework.stereotype.Component;

@Component
public class Hl7Parser {

//    private final TestOrderRepository testOrderRepository;
//    private final ParameterRepository parameterRepository;
//
//    public Hl7Parser(TestOrderRepository testOrderRepository, ParameterRepository parameterRepository) {
//        this.testOrderRepository = testOrderRepository;
//        this.parameterRepository = parameterRepository;
//    }
//
//    public List<TestResult> parseHL7(String hl7Message) {
//        List<TestResult> results = new ArrayList<>();
//
//        String[] segments = hl7Message.split("\\r?\\n|\\r");
//
//        TestOrder testOrder = null;
//        Long instrumentId = null;
//
//        for (String segment : segments) {
//            String[] fields = segment.split("\\|");
//
//            switch (fields[0]) {
//                case "OBR":
//                    // OBR-2 = testOrder ID
//                    String orderId = fields[2];
//                    testOrder = testOrderRepository.findById(Long.valueOf(orderId))
//                            .orElseThrow(() -> new RuntimeException("TestOrder not found: " + orderId));
//
//                    // OBR-18 = instrument ID
//                    try {
//                        instrumentId = Long.parseLong(fields[18]);
//                    } catch (Exception e) {
//                        instrumentId = null;
//                    }
//                    break;
//
//                case "OBX":
//                    // OBX-3 = test code
//                    String[] testCodeParts = fields[3].split("\\^");
//                    String testCode = testCodeParts[0];
//
//                    Parameter parameter = parameterRepository.findByAbbreviation(testCode);
//                    if(parameter==null){
//                        throw new EntityNotFoundException("Parameter "+testCode+" not found");
//                    }
//
//                    // OBX-5 = value
//                    Double value = null;
//                    try {
//                        value = Double.parseDouble(fields[5]);
//                    } catch (Exception e) {
//                        value = null;
//                    }
//
//                    // ✅ Determine flagStatus based on min/max from parameter
//                    String flagStatus;
//                    if (value == null) {
//                        flagStatus = "N/A";
//                    } else if (parameter.getMin() != null && value < parameter.getMin()) {
//                        flagStatus = "L"; // Low
//                    } else if (parameter.getMax() != null && value > parameter.getMax()) {
//                        flagStatus = "H"; // High
//                    } else {
//                        flagStatus = "N"; // Normal
//                    }
//
//                    // OBX-3 code → testType
//                    TestType testType = mapTestType(testCode);
//
//                    TestResult result = TestResult.builder()
//                            .testOrder(testOrder)
//                            .instrumentId(instrumentId)
//                            .parameterSnapshotId(null) // tạm null
//                            .flagStatus(flagStatus)
//                            .status(TestResultStatus.COMPLETED)
//                            .value(value)
//                            .createdAt(LocalDateTime.now())
//                            .updatedAt(LocalDateTime.now())
//                            .testType(testType)
//                            .parameter(parameter)
//                            .build();
//
//                    results.add(result);
//                    break;
//            }
//        }
//
//        return results;
    }

//    private TestType mapTestType(String testCode) {
//        if (testCode == null || testCode.isEmpty()) return TestType.CBC;
//
//        try {
//            return TestType.valueOf(testCode.toUpperCase());
//        } catch (IllegalArgumentException e) {
//            // Nếu không có enum nào trùng, map theo nhóm logic
//            if (testCode.startsWith("GLU")) return TestType.GLUCOSE;
//            if (testCode.startsWith("HB")) return TestType.HEMOGLOBIN;
//            if (testCode.startsWith("CHOL")) return TestType.CHOLESTEROL;
//            if (testCode.startsWith("WBC")) return TestType.WBC_DIFF;
//            if (testCode.startsWith("PT")) return TestType.PT;
//            if (testCode.startsWith("INR")) return TestType.INR;
//            if (testCode.startsWith("UR")) return TestType.URINE_ROUTINE;
//            if (testCode.startsWith("CSF")) return TestType.CSF_ANALYSIS;
//            if (testCode.startsWith("HBS")) return TestType.HBSAG;
//            if (testCode.startsWith("HIV")) return TestType.HIV_ANTIBODY;
//
//            return TestType.CBC;
//        }
//    }

