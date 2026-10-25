import { get, post } from "../../utils/http";

export const getSuppliersApi = p => get("/cSupplier/getSuppliers", p);
export const allSuppliersApi = p => get("/cSupplier/allSuppliers", p);
export const addSupplierApi = p => post("/cSupplier/add", p);
export const updateSupplierApi = p => post("/cSupplier/update", p);
export const deleteSupplierApi = p => get("/cSupplier/delete", p);
