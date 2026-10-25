<template>
  <LayoutComponent :active-menu="menuName" @curWarehouseChanged="curWarehouseChanged">
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item>首页</el-breadcrumb-item>
      <el-breadcrumb-item>供应商管理</el-breadcrumb-item>
    </el-breadcrumb>
    <hr />
    <el-row class="search-row">
      <el-col :span="18">
        <el-input style="width: 300px;" placeholder="输入供应商名称搜索" v-model="searchStr" clearable @keyup.enter.native="doSearch">
          <el-button slot="append" icon="el-icon-search" @click="doSearch"></el-button>
        </el-input>
      </el-col>
      <el-col :span="6" style="text-align: right;">
        <el-button type="primary" icon="el-icon-plus" @click="showAddDialog">新增供应商</el-button>
      </el-col>
    </el-row>
    <el-table :data="suppliers" border stripe style="width: 100%; margin-top: 10px;" height="calc(100% - 150px)">
      <el-table-column prop="name" label="供应商名称" width="180" align="center"></el-table-column>
      <el-table-column prop="contactPerson" label="联系人" width="120" align="center"></el-table-column>
      <el-table-column prop="contactPhone" label="联系电话" width="140" align="center"></el-table-column>
      <el-table-column prop="address" label="地址" min-width="180" align="center"></el-table-column>
      <el-table-column prop="remarks" label="备注" min-width="150" align="center"></el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="160" align="center"></el-table-column>
      <el-table-column label="操作" width="150" align="center">
        <template slot-scope="scope">
          <el-button size="mini" type="primary" icon="el-icon-edit" @click="showEditDialog(scope.row)"></el-button>
          <el-button size="mini" type="danger" icon="el-icon-delete" @click="deleteSupplier(scope.row)"></el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="page-container" style="margin-top: 10px;">
      <el-pagination background layout="total, sizes, prev, pager, next, jumper" :page-sizes="[5,10,30,50]" :page-size="page.pageSize" :total="total" :current-page="page.pageNum" @size-change="s => { page.pageSize = s; getData(); }" @current-change="p => { page.pageNum = p; getData(); }"></el-pagination>
    </div>

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="500px">
      <el-form :model="form" :rules="rules" ref="supplierForm" label-width="90px">
        <el-form-item label="供应商名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入供应商名称"></el-input>
        </el-form-item>
        <el-form-item label="联系人" prop="contactPerson">
          <el-input v-model="form.contactPerson" placeholder="请输入联系人"></el-input>
        </el-form-item>
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="form.contactPhone" placeholder="请输入联系电话"></el-input>
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入地址"></el-input>
        </el-form-item>
        <el-form-item label="备注" prop="remarks">
          <el-input v-model="form.remarks" placeholder="备注信息（选填）"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </span>
    </el-dialog>
  </LayoutComponent>
</template>

<script>
import { Message, MessageBox } from "element-ui";
import LayoutComponent from "@/components/consumables/Layout.vue";
import { getSuppliersApi, addSupplierApi, updateSupplierApi, deleteSupplierApi } from "../../api/consumables/supplier";

export default {
  name: "c_supplier",
  components: { LayoutComponent },
  inject: ["reload"],
  mounted() { this.getData(); },
  data() {
    return {
      menuName: "/consumables/supplier",
      suppliers: [], total: 0, searchStr: "", isEdit: false, editId: null,
      page: { pageNum: 1, pageSize: 10 },
      dialogVisible: false, dialogTitle: "新增供应商",
      form: { name: "", contactPerson: "", contactPhone: "", address: "", remarks: "" },
      rules: {
        name: [{ required: true, message: "请输入供应商名称", trigger: "blur" }, { max: 50, message: "不超过50字符", trigger: "blur" }],
        contactPerson: [{ required: true, message: "请输入联系人", trigger: "blur" }, { max: 20, message: "不超过20字符", trigger: "blur" }],
        contactPhone: [{ max: 20, message: "不超过20字符", trigger: "blur" }],
        address: [{ max: 255, message: "不超过255字符", trigger: "blur" }]
      }
    };
  },
  methods: {
    getData() {
      if (!this.$store.state.curWarehouse || !this.$store.state.curWarehouse.id) return;
      var sc = { warehouseId: this.$store.state.curWarehouse.id, searchStr: this.searchStr };
      this.page.searchCondition = JSON.stringify(sc);
      getSuppliersApi(this.page).then(res => {
        this.suppliers = res.data.list; this.total = res.data.total;
      });
    },
    doSearch() { this.page.pageNum = 1; this.getData(); },
    curWarehouseChanged() { this.reload(); },
    showAddDialog() {
      this.isEdit = false; this.dialogTitle = "新增供应商";
      this.form = { name: "", contactPerson: "", contactPhone: "", address: "", remarks: "", warehouseId: this.$store.state.curWarehouse.id };
      this.dialogVisible = true;
      this.$nextTick(() => this.$refs.supplierForm && this.$refs.supplierForm.clearValidate());
    },
    showEditDialog(row) {
      this.isEdit = true; this.dialogTitle = "编辑供应商"; this.editId = row.id;
      this.form = { name: row.name, contactPerson: row.contactPerson, contactPhone: row.contactPhone, address: row.address, remarks: row.remarks, warehouseId: this.$store.state.curWarehouse.id, id: row.id };
      this.dialogVisible = true;
      this.$nextTick(() => this.$refs.supplierForm && this.$refs.supplierForm.clearValidate());
    },
    submitForm() {
      this.$refs.supplierForm.validate(valid => {
        if (!valid) return;
        var api = this.isEdit ? updateSupplierApi : addSupplierApi;
        api(this.form).then(res => {
          if (res.success) { Message.success(this.isEdit ? "修改成功" : "新增成功"); this.dialogVisible = false; this.getData(); }
          else { Message.error(res.data.msg || res.msg); }
        });
      });
    },
    deleteSupplier(row) {
      MessageBox.confirm("确定删除供应商：「" + row.name + "」？", "提示", { type: "warning" }).then(() => {
        deleteSupplierApi({ warehouseId: this.$store.state.curWarehouse.id, id: row.id }).then(res => {
          if (res.success) { Message.success("删除成功"); this.getData(); }
          else { Message.error(res.data.msg || "删除失败"); }
        });
      }).catch(() => {});
    }
  }
};
</script>

<style scoped lang="less">
.search-row { margin-bottom: 10px; }
.page-container { display: flex; justify-content: flex-end; }
</style>
