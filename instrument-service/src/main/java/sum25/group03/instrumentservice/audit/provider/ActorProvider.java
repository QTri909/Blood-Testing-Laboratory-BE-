package sum25.group03.instrumentservice.audit.provider;

import sum25.group03.instrumentservice.audit.model.ActorContext;

public interface ActorProvider {
    ActorContext getCurrentActor();
}
