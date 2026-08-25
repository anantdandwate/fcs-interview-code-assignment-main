package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import java.util.List;
import org.junit.jupiter.api.Test;

public class ArchiveWarehouseUseCaseTest {

    @Test
    public void archiveShouldSetArchiveTimestampAndUpdateStore() {
        Warehouse warehouse = new Warehouse();
        warehouse.businessUnitCode = "MWH.001";

        TrackingWarehouseStore warehouseStore = new TrackingWarehouseStore();
        ArchiveWarehouseUseCase useCase = new ArchiveWarehouseUseCase(warehouseStore);

        useCase.archive(warehouse);

        assertNotNull(warehouse.archivedAt);
        assertNotNull(warehouseStore.updatedWarehouse);
        assertNotNull(warehouseStore.updatedWarehouse.archivedAt);
    }

    private static class TrackingWarehouseStore implements WarehouseStore {
        private Warehouse updatedWarehouse;

        @Override
        public List<Warehouse> getAll() {
            return List.of();
        }

        @Override
        public void create(Warehouse warehouse) {
            // Not needed for this test.
        }

        @Override
        public void update(Warehouse warehouse) {
            updatedWarehouse = warehouse;
        }

        @Override
        public void remove(Warehouse warehouse) {
            // Not needed for this test.
        }

        @Override
        public Warehouse findByBusinessUnitCode(String buCode) {
            return null;
        }
    }
}
