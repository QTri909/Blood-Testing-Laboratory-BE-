package sum25.group03.warehouseservice.audit.provider;

import sum25.group03.warehouseservice.audit.model.ActorContext;

public interface ActorProvider {
    ActorContext getCurrentActor();
}
