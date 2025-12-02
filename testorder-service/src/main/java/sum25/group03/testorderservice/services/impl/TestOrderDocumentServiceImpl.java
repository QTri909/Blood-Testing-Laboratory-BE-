package sum25.group03.testorderservice.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sum25.group03.testorderservice.entities.mongodb.TestOrderDocument;
import sum25.group03.testorderservice.repositories.mongodb.TestOrderDocumentRepo;
import sum25.group03.testorderservice.services.interfaces.TestOrderDocumentService;

@Service
@RequiredArgsConstructor
public class TestOrderDocumentServiceImpl implements TestOrderDocumentService {

    private final TestOrderDocumentRepo testOrderDocumentRepo;

    @Override
    public TestOrderDocument getTestOrderDocumentByTestOrderId(Long testOrderId) {
        return testOrderDocumentRepo.findByTestOrderId(testOrderId).orElse(null);
    }
}
