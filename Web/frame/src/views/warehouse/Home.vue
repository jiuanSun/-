<template>
  <LayoutComponent :active-menu="menuName">
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item>首页</el-breadcrumb-item>
    </el-breadcrumb>
    <hr />
    <el-container class="custom-el-container">
      <el-main class="custom-el-main">
        <el-scrollbar style="height: 100%">
          <div class="home">
            <el-alert
              :title="welcomeMsg"
              type="success"
              :closable="false"
              show-icon
              style="margin-bottom: 15px;"
            ></el-alert>
            <el-row :gutter="10" class="count-info-container">
              <el-col :span="4" v-for="(info, index) in countInfos" :key="index" style="margin-bottom: 5px;">
                <div class="count-main" :class="info.bg" @click="handleCountInfoClick(info.pageInfo)">
                  <div class="left-icon-container"><i :class="info.icon"></i></div>
                  <div class="right-info-container">
                    <span class="title">{{ info.name }}</span>
                    <CountTo class="count" :startVal="0" :endVal="info.count" :duration="2000"></CountTo>
                  </div>
                </div>
              </el-col>
            </el-row>
            <el-row :gutter="10" style="margin-top: 10px;">
              <el-col :span="12">
                <el-card>
                  <div slot="header"><i class="el-icon-pie-chart"></i> 消耗品分类占比</div>
                  <div ref="pieChart" style="width: 100%; height: 280px;"></div>
                </el-card>
              </el-col>
              <el-col :span="12">
                <el-card>
                  <div slot="header"><i class="el-icon-time"></i> 最近操作记录</div>
                  <el-timeline v-if="recentOperations.length > 0">
                    <el-timeline-item v-for="(item, i) in recentOperations" :key="i" :timestamp="item.createTime" placement="top" :color="i === 0 ? '#409EFF' : '#909399'">
                      {{ item.operation }}
                    </el-timeline-item>
                  </el-timeline>
                  <el-empty v-else description="暂无操作"></el-empty>
                </el-card>
              </el-col>
            </el-row>
            <el-row :gutter="10" style="margin-top: 10px;">
              <el-col :span="24">
                <el-card>
                  <div slot="header"><i class="el-icon-data-line"></i> 近6月出入库走势</div>
                  <div ref="trendChart" style="width: 100%; height: 280px;"></div>
                </el-card>
              </el-col>
            </el-row>
            <el-row :gutter="10" style="margin-top: 10px;">
              <el-col :span="12">
                <el-card>
                  <div slot="header"><i class="el-icon-shopping-cart-2"></i> 🤖 智能补货建议</div>
                  <el-table :data="restockList" size="small" max-height="250">
                    <el-table-column prop="name" label="消耗品" width="100"></el-table-column>
                    <el-table-column prop="currentCount" label="库存" width="60" align="center"></el-table-column>
                    <el-table-column prop="safetyCount" label="安全线" width="60" align="center"></el-table-column>
                    <el-table-column prop="suggestBuy" label="建议采购" width="80" align="center">
                      <template slot-scope="s"><el-tag type="warning">{{ s.row.suggestBuy }}</el-tag></template>
                    </el-table-column>
                  </el-table>
                  <el-empty v-if="restockList.length===0" description="库存充足，无需补货" :image-size="40"></el-empty>
                </el-card>
              </el-col>
              <el-col :span="12">
                <el-card>
                  <div slot="header"><i class="el-icon-warning-outline"></i> 🚨 消耗异常检测</div>
                  <div v-if="anomalyList.length===0" style="text-align:center; padding:30px; color:#67c23a;">
                    <i class="el-icon-success" style="font-size:40px;"></i>
                    <p style="margin-top:10px;">近7天无异常消耗，一切正常 ✓</p>
                  </div>
                  <el-timeline v-else>
                    <el-timeline-item v-for="(a,i) in anomalyList" :key="i" color="#f56c6c" :timestamp="a.consumableName">
                      本周消耗 {{ a.currentWeek }}，上周 {{ a.previousWeek }}，增长 {{ a.increaseRatio }} 倍
                    </el-timeline-item>
                  </el-timeline>
                </el-card>
              </el-col>
            </el-row>
            <el-row :gutter="10" style="margin-top: 10px;">
              <el-col :span="24">
                <el-card>
                  <div slot="header"><i class="el-icon-s-tools"></i> 快捷操作</div>
                  <el-row :gutter="10">
                    <el-col :span="6"><el-button type="primary" icon="el-icon-plus" style="width:100%;" @click="goPath('/consumables/consumables')">消耗品管理</el-button></el-col>
                    <el-col :span="6"><el-button type="success" icon="el-icon-s-order" style="width:100%;" @click="goPath('/consumables/operate')">操作履历</el-button></el-col>
                    <el-col :span="6"><el-button type="warning" icon="el-icon-bell" style="width:100%;" @click="goPath('/consumables/statistics')">数据统计</el-button></el-col>
                    <el-col :span="6"><el-button type="info" icon="el-icon-setting" style="width:100%;" @click="goPath('/warehouse/user')">人员管理</el-button></el-col>
                  </el-row>
                </el-card>
              </el-col>
            </el-row>
          </div>
        </el-scrollbar>
      </el-main>
    </el-container>
  </LayoutComponent>
</template>

<script>
import LayoutComponent from "@/components/warehouse/Layout.vue";
import CountTo from "vue-count-to";
import echarts from "echarts";
import { getInitDataApi, getSmartAnalysisApi } from "../../api/warehouse/home";

export default {
  name: "w_home",
  components: { LayoutComponent, CountTo },
  mounted() { this.getInitData(); },
  computed: {
    welcomeMsg() {
      var uname = this.$store.state.userInfo ? this.$store.state.userInfo.name : "用户";
      var today = new Date().toLocaleDateString("zh-CN", { year: "numeric", month: "long", day: "numeric", weekday: "long" });
      return "欢迎回来，" + uname + "！今天是 " + today;
    }
  },
  data() {
    return {
      menuName: "/warehouse/home",
      recentOperations: [], restockList: [], anomalyList: [],
      countInfos: [
        { name: "人员数", ref: "userCount", icon: "el-icon-s-custom", count: 0, pageInfo: "/warehouse/user", bg: "count-info-bg-user" },
        { name: "仓库数", ref: "warehouseCount", icon: "el-icon-s-management", count: 0, pageInfo: "/warehouse/warehouse", bg: "count-info-bg-warehouse" },
        { name: "消耗品", ref: "consumableCount", icon: "el-icon-s-goods", count: 0, pageInfo: "/consumables/consumables", bg: "count-info-bg-consumable" },
        { name: "预警品", ref: "alarmCount", icon: "el-icon-warning", count: 0, pageInfo: "/consumables/consumables", bg: "count-info-bg-alarm" },
        { name: "待审批", ref: "pendingApprovalCount", icon: "el-icon-s-check", count: 0, pageInfo: "/consumables/application", bg: "count-info-bg-pending" }
      ]
    };
  },
  methods: {
    getInitData() {
      getInitDataApi().then(res => {
        var d = res.data;
        this.countInfos[0].count = d.userCount;
        this.countInfos[1].count = d.warehouseCount;
        this.countInfos[2].count = d.consumableCount;
        this.countInfos[3].count = d.alarmCount;
        this.countInfos[4].count = d.pendingApprovalCount;
        this.recentOperations = d.recentOperations || [];
        this.$nextTick(() => { this.initPieChart(d.typeCounts); this.initTrendChart(d.monthlyTrend);
        this.loadSmartAnalysis(); });
      });
    },
    initPieChart(typeCounts) {
      if (!this.$refs.pieChart) return;
      var chart = echarts.init(this.$refs.pieChart);
      chart.setOption({
        tooltip: { trigger: "item" },
        legend: { orient: "vertical", right: 10, top: 10, textStyle: { fontSize: 11 } },
        series: [{ type: "pie", radius: ["45%", "75%"], center: ["40%", "50%"], data: typeCounts || [] }]
      });
      window.addEventListener("resize", () => chart.resize());
    },
    initTrendChart(trend) {
      if (!trend || !this.$refs.trendChart) return;
      var chart = echarts.init(this.$refs.trendChart);
      chart.setOption({
        tooltip: { trigger: "axis" },
        legend: { data: ["入库", "出库"], top: 5 },
        grid: { left: 50, right: 20, top: 40, bottom: 30 },
        xAxis: { type: "category", data: trend.months || [] },
        yAxis: { type: "value" },
        series: [
          { name: "入库", type: "bar", data: trend.inbound || [], itemStyle: { color: "#4dd599" }, barGap: "20%" },
          { name: "出库", type: "bar", data: trend.outbound || [], itemStyle: { color: "#f56c6c" } }
        ]
      });
      window.addEventListener("resize", () => chart.resize());
    },
    handleCountInfoClick(page) {
      if (!this.$store.state.curWarehouse || !this.$store.state.curWarehouse.id) {
        var whs = this.$store.state.userInfo.warehouses;
        if (whs && whs.length > 0) this.$store.commit("initCurWarehouse", whs[0]);
      }
      this.$router.push(page);
    },
    loadSmartAnalysis() {
      getSmartAnalysisApi().then(res => {
        this.restockList = (res.data.suggestions || []).slice(0, 5);
        this.anomalyList = res.data.anomalies || [];
      });
    },
    goPath(path) { this.$router.push(path); }
  }
};
</script>

<style scoped lang="less">
.home { height: 100%; overflow-x: hidden; overflow-y: auto; }
.count-info-container { margin-bottom: 10px; }
.count-main {
  display: flex; flex-flow: row nowrap; height: 75px; border-radius: 4px; cursor: pointer;
  &:hover .right-info-container .count { font-size: 28px; }
  .left-icon-container { display: flex; justify-content: center; align-items: center; height: 100%; width: 55px; font-size: 38px; color: aliceblue; }
  .right-info-container { flex: 1; display: flex; flex-flow: column nowrap; justify-content: center; align-items: center; height: 100%; color: aliceblue; }
  .title { font-size: 11px; }
  .count { font-size: 22px; }
}
.count-info-bg-user { background-color: #4f81c7; }
.count-info-bg-warehouse { background-color: #4dd599; }
.count-info-bg-consumable { background-color: #e6a23c; }
.count-info-bg-alarm { background-color: #f56c6c; }
.count-info-bg-pending { background-color: #909399; }
</style>
