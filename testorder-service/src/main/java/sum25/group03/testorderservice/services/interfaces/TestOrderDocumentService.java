package sum25.group03.testorderservice.services.interfaces;

import sum25.group03.testorderservice.entities.mongodb.TestOrderDocument;

public interface TestOrderDocumentService {
    // get by id
    TestOrderDocument getTestOrderDocumentByTestOrderId(Long testOrderId);
}