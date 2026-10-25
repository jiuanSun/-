package frame.server.service;

import com.github.pagehelper.PageInfo;
import frame.server.domain.Supplier;
import frame.server.vo.consumables.CSupplierController.CAddSupplierModel;
import frame.server.vo.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SuppliceService {
    PageInfo<Supplier> getSuppliers(Page page);
    List<Supplier> getSuppliers(String warehouseId);
    Supplier getSupplier(String warehouseId, String supplierId);
    boolean isSupplierExist(String warehouseId, String supplierId);
    boolean isSupplerNameRepeat(String warehouseId, String name);
    boolean isConsumableBoundSupplier(String supplierId);
    @Transactional
    void addSupplier(CAddSupplierModel model);
    @Transactional
    void updateSupplier(CAddSupplierModel model,String supplierId);
    @Transactional
    void deleteSupplier(String warehouseId, String supplierId);
}
