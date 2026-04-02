package org.acme.vehicle.application.port;

import java.util.List;
import org.acme.vehicle.domain.model.FipeBrand;

public interface BrandIngestPublisher {

    void publishAll(List<FipeBrand> brands);
}
