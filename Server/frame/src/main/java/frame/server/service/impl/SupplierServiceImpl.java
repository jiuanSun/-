package frame.server.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import frame.server.dao.ConsumableDao;
import frame.server.dao.SupplierDao;
import frame.server.domain.Supplier;
import frame.server.service.SuppliceService;
import frame.server.utils.StringUtil;
import frame.server.vo.consumables.CSupplierController.CAddSupplierModel;
import frame.server.vo.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("Supplierservice")
public class SupplierServiceImpl implements SuppliceService {

    @Autowired
    private SupplierDao supplierDao;

    @Autowired
    private ConsumableDao consumableDao;

    @Override
    public PageInfo<Supplier> getSuppliers(Page page) {
        PageHelper.startPage(page.getPageNum(), page.getPageSize());
        JSONObject searchCondition = page.getSearchCondition();
        String warehouseId = searchCondition.getString("warehouseId");
        String searchStr = searchCondition.getString("searchStr");

        if (StringUtil.isBlank(searchStr)) {
            return new PageInfo<>(supplierDao.selectByWarehouseId(warehouseId));
        }
        return new PageInfo<>(supplierDao.selectSupplierAndLikeName(warehouseId, searchStr));
    }

    @Override
    public List<Supplier> getSuppliers(String warehouseId) {
        return supplierDao.selectByWarehouseId(warehouseId);
    }

    @Override
    public Supplier getSupplier(String warehouseId, String supplierId) {
        return supplierDao.selectByWarehouseIdAndSupplierId(warehouseId, supplierId);
    }

    @Override
    public boolean isSupplierExist(String warehouseId, String supplierId) {
        return supplierDao.selectCountByWarehouseIdAndSupplierId(warehouseId, supplierId) > 0;
    }

    @Override
    public boolean isSupplerNameRepeat(String warehouseId, String name) {
        return supplierDao.selectCountByWarehouseIdAndName(warehouseId, name) > 0;
    }

    @Override
    public boolean isConsumableBoundSupplier(String supplierId) {
        return consumableDao.c_selectConsumableCountBySupplierId(supplierId) > 0;
    }

    @Override
    public void addSupplier(CAddSupplierModel model) {
        supplierDao.insertSupplier(model.createSupplier());
    }

    @Override
    public void updateSupplier(CAddSupplierModel model, String supplierId) {
        Supplier supplier = new Supplier();
        supplier.setId(supplierId);
        supplier.setWarehouseId(model.getWarehouseId());
        supplier.setName(model.getName());
        supplier.setContactPerson(model.getContactPerson());
        supplier.setContactPhone(model.getContactPhone());
        supplier.setAddress(model.getAddress());
        supplier.setRemarks(model.getRemarks());
        supplierDao.updateSupplier(supplier);
    }

    @Override
    public void deleteSupplier(String warehouseId, String supplierId) {
        supplierDao.deleteSupplier(warehouseId, supplierId);
    }
}
