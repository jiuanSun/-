<template>
  <div class="login">
    <LoginComponent @doLogin="doLogin" />
  </div>
</template>

<script>
import LoginComponent from "@/components/Login.vue";
import { Message } from "element-ui";
import { loginApi } from "../api/login";

export default {
  name: "login",
  components: {
    LoginComponent
  },
  // inject: ["initWebSocket"],
  data: function() {
    return {};
  },
  methods: {
    doLogin(data) {
      loginApi(data).then(res => {
        Message.success("登录成功");

        // 待办提醒
        if (res.data.pendingTasks) {
          var pt = res.data.pendingTasks;
          var msgs = [];
          if (pt.alarmCount > 0) msgs.push(pt.alarmCount + ' 个消耗品库存不足');
          if (pt.pendingApprovalCount > 0) msgs.push(pt.pendingApprovalCount + ' 个待审批申请');
          if (msgs.length > 0) {
            this.$notify({ title: '待办提醒', message: msgs.join('；'), type: 'warning', duration: 6000 });
          }
        }

        this.$store.commit("initToken", res.data.Authorization);
        this.$store.commit("initUserInfo", res.data.userInfo);
        this.$store.commit("initSystemPermissions", res.data.systemPermissions);
        this.$store.commit("initDefaultLogo", res.data.defaultLogo);
        this.$store.commit("initSystemPermissionsType");

        if (res.data.userInfo.warehouses && res.data.userInfo.warehouses.length > 0) {
          this.$store.commit("initCurWarehouse", res.data.userInfo.warehouses[0]);
        }

        // this.initWebSocket();

        var targetSystemPath;
        switch (res.data.target) {
          case "consumables":
          case "warehouse":
            targetSystemPath = "/" + res.data.target + "/home";
            break;
          case "application":
            targetSystemPath = "/" + res.data.target + "/application";
            break;
        }

        this.$router.push(targetSystemPath);
      });
    }
  }
};
</script>

<style scoped lang="less">
.login {
  height: 100%;
}
</style>